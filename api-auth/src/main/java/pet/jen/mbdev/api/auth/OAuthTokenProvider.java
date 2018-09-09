package pet.jen.mbdev.api.auth;

import com.google.common.base.Strings;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import lombok.Builder;
import pet.jen.mbdev.api.TokenProvider;
import pet.jen.mbdev.api.auth.client.TokenApi;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;
import pet.jen.mbdev.api.auth.domain.TokenInformation;
import pet.jen.mbdev.api.auth.exception.TokenPersistenceException;
import pet.jen.mbdev.api.auth.persistence.TokenRepository;

import java.util.Date;

/**
 * Responsible for the {@link TokenInformation} which is relevant to perform authenticated requests.
 * Might be initialized following two scenarios:
 * 1. An authorization code is available and will be used to retrieve token information (access and refresh code)
 * 2. A populated {@link TokenRepository} already containing all the necessary information.
 *
 * The `getAccessToken` function ensures that there is always a valid access token returned in the method. In case
 * the returned access token is invalid a forced refresh can be triggered by using `refreshTokens`.

 * @author Jens Petersohn <me@jen.pet>
 */
class OAuthTokenProvider implements TokenProvider {

    private TokenApi tokenApi;

    // the overall OAuth configuration
    private OAuthConfig config;

    // repository which should be used to save and store token information
    private TokenRepository tokenRepository;

    /**
     * The builder is the only public method to create a token provider. When calling `build()` this private constructor
     * will be called.
     *
     * The two scenarios mentioned above are handled by checking for the auth code. If that one is not provided but a token
     * repository it will use the token repositories and skip an initial token retrieval. If none of the two is available
     * an {@link IllegalStateException} will be thrown.
     *
     * @param tokenApi might be null; when not set the default token api will be created
     * @param config mandatory; OAuth details required for token handling
     * @param tokenRepository which should be used for the token information
     * @param authCode code to retrieve tokens initially
     * @param codeVerifier in case the authentication process used the PKCE flow it will be used to retrieve the initially
     */
    @Builder
    private OAuthTokenProvider(TokenApi tokenApi, OAuthConfig config, TokenRepository tokenRepository, String authCode, String codeVerifier) {
        if(config == null || !config.isValid()) {
            throw new IllegalArgumentException("Required parameter config is null or invalid.");
        }
        this.config = config;

        // if no token api client set default to the internal one
        this.tokenApi = tokenApi != null ? tokenApi : createTokenApiClient(config);

        // if no repository is set default to in-memory
        this.tokenRepository = tokenRepository != null ? tokenRepository : new InMemoryTokenRepository();

        // if an auth code is present assume that the tokens were not received yet
        if(!Strings.isNullOrEmpty(authCode)) {
            initTokens(authCode, codeVerifier);
            return;
        }

        // If no auth code is provided and no initialization of the tokens happened check whether
        // the repository has valid data. In case it does not throw an exception.
        if(tokenRepository == null || tokenRepository.isEmpty() || !tokenRepository.get().isValid()) {
            throw new IllegalStateException("No auth code available or provided repository was either empty or had invalid data.");
        }
    }

    /**
     * Initializes the tokens for the token provider. For this step an auth code is mandatory, the code verifier
     * depends on the initialization of the process. If the issuing authority received a code challenge earlier
     * a non null code verifier indicates that the request should have an appended client id and code verifier.
     *
     * @param authCode mandatory in every scenario to retrieve tokens
     * @param codeVerifier depending on the previous process required when PKCE is enabled
     */
    private void initTokens(String authCode, String codeVerifier) {
        TokenInformation tokenInformation;
        if(config.isUsePKCE()) {
            if(codeVerifier == null) {
                throw new IllegalStateException("Usage of PKCE mechanism is enabled for the authorization flow but code verifier for token retrieval is not provided.");
            }
            tokenInformation = tokenApi.retrieve(
                    "authorization_code",
                    authCode,
                    config.getClientId(),
                    codeVerifier,
                    config.getRedirectUri());
        } else {
            tokenInformation = tokenApi.retrieve(
                    "authorization_code",
                    authCode,
                    null,
                    null,
                    config.getRedirectUri());
        }
        tokenInformation.setTimestamp(new Date().getTime());
        saveTokenInformation(tokenInformation);
    }

    @Override
    public String getAccessToken() {
        // check for the token buffer first and if necessary refresh
        if(getTokenInfo().getTimestamp() + getTokenInfo().getExpiresIn() - config.getTokenExpiryBuffer() <= new Date().getTime()) {
            refreshTokens();
        }
        return this.getTokenInfo().getAccessToken();
    }

    @Override
    public String refreshTokens() {
        String refreshToken = this.getTokenInfo().getRefreshToken();
        TokenInformation tokenInformation = tokenApi.refresh("refresh_token", refreshToken);
        tokenInformation.setTimestamp(new Date().getTime());
        saveTokenInformation(tokenInformation);
        return tokenInformation.getAccessToken();
    }

    TokenInformation getTokenInfo() {
        return this.tokenRepository.get();
    }

    private void saveTokenInformation(TokenInformation tokenInformation) {
        try {
            tokenRepository.save(tokenInformation);
        } catch (TokenPersistenceException e) {
            throw new IllegalArgumentException("Could not persist token information.", e);
        }
    }

    private static TokenApi createTokenApiClient(OAuthConfig config) {
        Feign.Builder builder = Feign.builder()
                .encoder(new FormEncoder())
                .decoder(new JacksonDecoder());
        if(!config.isUsePKCE()) {
            builder.requestInterceptor(new BasicAuthRequestInterceptor(config.getClientId(), config.getClientSecret()));
        }
        return builder.target(TokenApi.class, config.getAuthorizationBaseUrl());
    }
}
