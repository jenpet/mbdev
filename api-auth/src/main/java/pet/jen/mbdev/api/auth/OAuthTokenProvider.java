package pet.jen.mbdev.api.auth;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import pet.jen.mbdev.api.TokenProvider;
import pet.jen.mbdev.api.auth.client.TokenApi;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;
import pet.jen.mbdev.api.auth.domain.TokenInformation;

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

    // auth code for the initial retrieval of the access and refresh token
    private String authCode;

    // the last retrieved token information fromt the token api
    private TokenInformation latestTokenInfo;

    // timestamp when the last update was performed
    private Long lastUpdate;

    /**
     * Creates the token provider based on the configuration of the OAuth flow and the authorization code.
     * @param config OAuth details required for token handling
     * @param authCode code to retrieve tokens initially
     */
    OAuthTokenProvider(OAuthConfig config, String authCode) {
        this(Feign.builder()
                .encoder(new FormEncoder())
                .decoder(new JacksonDecoder())
                .requestInterceptor(new BasicAuthRequestInterceptor(config.getClientId(), config.getClientSecret()))
                .target(TokenApi.class, config.getAuthorizationBaseUrl()),
                config,
                authCode);
    }

    /**
     * Convenience method.
     */
    OAuthTokenProvider(TokenApi tokenApi, OAuthConfig config, String authCode) {
        this.tokenApi = tokenApi;
        this.config = config;
        this.authCode = authCode;
    }

    @Override
    public String getAccessToken() {
        // initial situations - requires a query
        if(this.getLastUpdate() == null || this.getTokenInfo() == null) {
            initTokens();
        } else if(lastUpdate + latestTokenInfo.getExpiresIn() - config.getTokenExpiryBuffer() <= new Date().getTime()) {
            refreshTokens();
        }
        return this.latestTokenInfo.getAccessToken();
    }

    @Override
    public String refreshTokens() {
        this.latestTokenInfo = tokenApi.refresh("refresh_token", this.latestTokenInfo.getRefreshToken());
        updateTimestamp();
        return this.latestTokenInfo.getAccessToken();
    }

    TokenInformation getTokenInfo() {
        return this.latestTokenInfo;
    }

    Long getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * Initializes the tokens using the given authorization code as the required grant type.
     */
    private void initTokens() {
        latestTokenInfo = tokenApi.retrieve("authorization_code", authCode, config.getRedirectUri());
        updateTimestamp();
    }

    /**
     * Updates the internal timestamp of the last update to enable the expiry handling.
     */
    private void updateTimestamp() {
        this.lastUpdate = new Date().getTime();
    }
}
