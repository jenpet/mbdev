package pet.jen.mbdev.api.auth.service;

import pet.jen.mbdev.api.auth.client.AuthorizationApiClient;
import pet.jen.mbdev.api.auth.domain.Authorization;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;
import pet.jen.mbdev.api.auth.exception.SessionRetrievalException;

public class AuthorizationApiHandler {

    private AuthorizationApiClient client;

    private OAuthConfig config;

    public AuthorizationApiHandler(OAuthConfig config) {
        this(config, new AuthorizationApiClient(config));
    }

    AuthorizationApiHandler(OAuthConfig config, AuthorizationApiClient client) {
        this.config = config;
        this.client = client;
    }

    /**
     * Starts the OAuth process initialization retrieving a session for further handling of the authentication.
     *
     * It has to be differentiated between the usage of PKCE (Proof Key for Code Exchange) requests and 'regular' ones.
     * An {@link Authorization} object will always provide a code challenge and code verifier after instantiation but only
     * in case of a PKCE enhanced flow it will be added to the authorize request.
     *
     * After performing the corresponding request and following the redirect to the web form,
     * the client returns a response which can be used to extract the session information.
     *
     * @param clientId of the registered application
     * @param redirectUri registered in the console
     * @param scope requested scope for this session
     * @return initialized authorization object containing a fresh session.
     *
     * @throws SessionRetrievalException in case there were issues extracting session information from the client's response
     */
    public Authorization initializeAuth(String clientId, String redirectUri, String scope) throws SessionRetrievalException {
        Authorization authorization = new Authorization();
        try {
            if(config.isUsePKCE()) {
                authorization.storeSession(this.client.authorize(clientId, redirectUri, scope, authorization.getCodeChallenge()));
            } else {
                authorization.storeSession(this.client.authorize(clientId, redirectUri, scope));
            }
        } catch (IllegalArgumentException e) {
            throw new SessionRetrievalException("Failed to extract the session from the initial authorize call.", e);
        }
        return authorization;
    }

    /**
     * Submits a consent not containing special information targeting the authorization API. The submission of the consent
     * will return an authorization code which then can be used to retrieve tokens.
     *
     * @param action that
     * @param sessionId of the current authorization flow
     * @param sessionData of the current authorization flow
     * @return authorization code in case of a successful submission
     */
    public String submitConsent(String action, String sessionId, String sessionData) {
        return this.client.retrieveAuthCode(action, sessionId, sessionData);
    }
}
