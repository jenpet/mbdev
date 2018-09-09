package pet.jen.mbdev.api.auth.client;

import com.google.common.net.HttpHeaders;
import okhttp3.*;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;

/**
 * Handles all of the interactions with the authorization API. It requires two different clients to request the
 * respective endpoints. One of the clients has to explicitly suppress following redirects which is essential
 * to retrieve the auth code when calling `retrieveAuthCode`.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class AuthorizationApiClient extends BaseClient implements IAMApi {

    private OkHttpClient redirectClient;
    private OkHttpClient noRedirectClient;

    private OAuthConfig config;

    public AuthorizationApiClient(OAuthConfig config) {
        this(config,
                BaseClientBuilder
                        .get(config)
                        .build(),
                BaseClientBuilder
                        .get(config)
                        .followRedirects(false)
                        .build());
    }

    AuthorizationApiClient(OAuthConfig config, OkHttpClient redirectClient, OkHttpClient noRedirectClient) {
        this.config = config;
        this.redirectClient = redirectClient;
        this.noRedirectClient = noRedirectClient;
    }

    /**
     * Performs a GET call targeting the authorization API's `authorize` endpoint. A client which follows redirect has to be used
     * in order to get redirected to the login form.
     *
     * @param clientId OpenId client id
     * @param redirectUri which has to be used for redirects (i.e. http://localhost).
     * @param scopes List of scopes separated by comma
     * @return login form of the website in case of success
     */
    public String authorize(String clientId, String redirectUri, String scopes, String codeChallenge) {
        Request request = new Request.Builder()
                .url(this.createAuthorizeUrl(clientId, redirectUri, scopes, codeChallenge))
                .get().build();
        return executeAndGetBody(redirectClient, request);
    }

    /**
     * Performs the initial authorize call without a code challenge (i.e. without PKCE relevant mechanisms)
     */
    public String authorize(String clientId, String redirectUri, String scopes) {
        return this.authorize(clientId, redirectUri, scopes, null);
    }

    /**
     * Performs a POST request targeting the autorization API's `consent` endpoint.
     *
     * Background: This request is done after receiving a redirect from the web login when checking a user's
     * consent to another form which instantly posts targeting this API.
     *
     * @param action parsed from the redirected
     * @param sessionId of the session which is used for the user login
     * @param sessionData of the session which is used for the user login
     * @return an authorization code which can be used to retrieve tokens or an empty string in case of any errors
     */
    public String retrieveAuthCode(String action, String sessionId, String sessionData) {
        Request request = new Request.Builder()
                .url(this.createConsentUrl())
                .addHeader(HttpHeaders.CONTENT_TYPE, BaseClientBuilder.MEDIA_TYPE_FORM.toString())
                .post(createConsentFormBody(action, sessionId, sessionData)).build();
        Response response = execute(this.noRedirectClient, request);
        return this.extractAuthCode(response);
    }

    /**
     * Extracts the authorization code from a given response header by parsing the location value. Assumption: there will only
     * be one location header containing the auth code.
     *
     * @param response header which contains the code
     * @return an authorization code which can be used to retrieve tokens or an empty string in case of any errors
     */
    private String extractAuthCode(Response response) {
        if(response != null && !response.headers("Location").isEmpty()) {
            return AuthCodeExtractor.extract(response.headers("Location").get(0));
        }
        return "";
    }

    /**
     * Creates the authorize url containing all of the relevant fields properly encoded (if required). In case
     * the code challenge is present (i.e. PKCE mechanisms should be used) a code challenge method and the code
     * challenge it self will be added to the initial request.
     *
     * @param clientId OpenId client id
     * @param redirectUri which has to be used for redirects (i.e. http://localhost).
     * @param scopes List of scopes separated by comma
     * @param codeChallenge used for PKCE might also be null
     * @return url to call the authorization endpoint
     */
    HttpUrl createAuthorizeUrl(String clientId, String redirectUri, String scopes, String codeChallenge) {
        HttpUrl.Builder builder = baseBuilder()
                .addEncodedPathSegments(BASE_PATH)
                .addEncodedPathSegment("authorize")
                .addQueryParameter("response_type", "code")
                .addQueryParameter("client_id", clientId)
                .addQueryParameter("redirect_uri", redirectUri)
                .addQueryParameter("scope", scopes);
        if(codeChallenge != null) {
            builder.addQueryParameter("code_challenge_method", "S256")
                    .addEncodedQueryParameter("code_challenge", codeChallenge);
        }

        return builder.build();
    }

    private HttpUrl createConsentUrl() {
        return baseBuilder()
                .addEncodedPathSegments(BASE_PATH)
                .addEncodedPathSegment("authorize")
                .addEncodedPathSegment("consent").build();
    }

    /**
     * @return a basic builder to generate requests towards the authorization api.
     */
    private HttpUrl.Builder baseBuilder() {
        HttpUrl base = HttpUrl.get(this.config.getAuthorizationBaseUrl());
        return new HttpUrl.Builder()
                .scheme(base.scheme())
                .host(base.host())
                .port(base.port());
    }

    private RequestBody createConsentFormBody(String action, String sessionId, String sessionData) {
        return new FormBody.Builder()
                .add("action", action)
                .add("sessionID", sessionId)
                .add("sessionData", sessionData)
                .build();
    }
}
