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
 * Is setup using an authorization code which is used for the initial retrieval of the tokens from the token API.
 * The `getAccessToken` function ensures that there is always a valid access token returned in the method. In case
 * the returned access token is invalid a forced refresh can be triggered by using `refreshTokens`.
 *
 * TODO: Maybe the storage of the tokens could be decrypted in some kind of in-memory cache / db.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
class OAuthTokenProvider implements TokenProvider {

    private TokenApi tokenApi;

    // the overall OAuth configuration
    private OAuthConfig config;

    // repository which should be used to save and store token information
    private TokenRepository tokenRepository;

    /**
     * TODO: Re-write.
     *
     * New authentication
     * Creates the token provider based on the configuration of the OAuth flow and the authorization code.
     *
     * Provider initialization should make the initial token retrieval call since the first invocation of getAccessToken() might
     * beyond the authCode's time of validity which causes the call to fail.
     *
     * @param config OAuth details required for token handling
     * @param authCode code to retrieve tokens initially
     */
    @Builder
    private OAuthTokenProvider(TokenApi tokenApi, OAuthConfig config, TokenRepository tokenRepository, String authCode) {
        if(config == null || !config.isValid()) {
            throw new IllegalArgumentException("Required parameter config is null or invalid.");
        }
        this.config = config;

        // if no token api client set default to the internal one
        this.tokenApi = tokenApi != null ? tokenApi : createTokenApiClient(config);

        // if no repository is set default to in-memory
        this.tokenRepository = tokenRepository != null ? tokenRepository : new InMemoryTokenRepository();

        if(!Strings.isNullOrEmpty(authCode)) {
            // immediately initialize tokens to use fresh auth code
            initTokens(authCode);
            return;
        }

        // If no auth code is provided and no initialization of the tokens happened check whether
        // the repository has valid data. In case it does not throw an exception.
        if(tokenRepository == null || tokenRepository.isEmpty() || !tokenRepository.get().isValid()) {
            throw new IllegalStateException("No auth code available or provided repository was either empty or had invalid data.");
        }
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

    /**
     * Initializes the tokens using the given authorization code as the required grant type.
     */
    private void initTokens(String authCode) {
        TokenInformation tokenInformation = tokenApi.retrieve("authorization_code", authCode, config.getRedirectUri());
        tokenInformation.setTimestamp(new Date().getTime());
        saveTokenInformation(tokenInformation);
    }

    private void saveTokenInformation(TokenInformation tokenInformation) {
        try {
            tokenRepository.save(tokenInformation);
        } catch (TokenPersistenceException e) {
            throw new IllegalArgumentException("Could not persist token information.", e);
        }
    }

    private static TokenApi createTokenApiClient(OAuthConfig config) {
        return Feign.builder()
                .encoder(new FormEncoder())
                .decoder(new JacksonDecoder())
                .requestInterceptor(new BasicAuthRequestInterceptor(config.getClientId(), config.getClientSecret()))
                .target(TokenApi.class, config.getAuthorizationBaseUrl());
    }
}
