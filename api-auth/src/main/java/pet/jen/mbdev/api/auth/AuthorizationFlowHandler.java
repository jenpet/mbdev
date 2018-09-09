package pet.jen.mbdev.api.auth;

import com.google.common.base.Strings;
import pet.jen.mbdev.api.TokenProvider;
import pet.jen.mbdev.api.auth.domain.*;
import pet.jen.mbdev.api.auth.exception.AuthorizationInitializationException;
import pet.jen.mbdev.api.auth.exception.SessionRetrievalException;
import pet.jen.mbdev.api.auth.exception.UserConsentException;
import pet.jen.mbdev.api.auth.exception.UserLoginFailedException;
import pet.jen.mbdev.api.auth.persistence.TokenRepository;
import pet.jen.mbdev.api.auth.service.AuthorizationApiHandler;
import pet.jen.mbdev.api.auth.service.WebFormHandler;

/**
 * Performs and manages all steps of the authorization flow to eventually retrieve an authorization token which can
 * be used to retrieve the required tokens. In case a single step of the described OAuth flow fails
 * (https://developer.mercedes-benz.com/content-page/oauth-documentation) a detailed exception will be thrown
 * embedding the according error / exception.
 *
 * The {@link AuthorizationFlowHandler} is not responsible to retrieve the access or refresh tokens. This will be done
 * by the {@link OAuthTokenProvider} which will use the authorization code obtained after a successful flow.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class AuthorizationFlowHandler {

    // contains necessary meta data for the oauth flow
    private OAuthConfig config;

    // token repository used to initialize the token provider
    private TokenRepository tokenRepository;

    // handles all interactions with the required html forms
    private WebFormHandler webFormHandler;

    // handles all interactions with the authorization and retrieval of a authorization code
    private AuthorizationApiHandler authorizationApiHandler;

    /**
     * Sets up a new authorization flow handler which performs the necessary steps for a proper initial authentication.
     *
     * Passing a custom {@link TokenRepository} implementation allows to control the storage of the authentication tokens.
     * If a session should be persistent a token repository implementation might be serialized and used as an input later on
     * to create a working {@link TokenProvider}.
     *
     * @param config which holds all the information for the OAuth flow
     * @param tokenRepository which should be used to store and load authentication tokens
     * @return handler which performs necessary steps for authorization.
     */
    public static AuthorizationFlowHandler setup(OAuthConfig config, TokenRepository tokenRepository) {
        if(!config.isValid()) {
            throw new IllegalArgumentException("The provided OAuth configuration was not valid. Please mandatory attributes.");
        }
        return new AuthorizationFlowHandler(config, tokenRepository, new WebFormHandler(config), new AuthorizationApiHandler(config));
    }

    /**
     * Convenience method which automatically uses the default repository of the {@link OAuthTokenProvider}.
     */
    public static AuthorizationFlowHandler setup(OAuthConfig config) {
        return setup(config, null);
    }

    /**
     * Initializes a {@link TokenProvider} based on a config and a given repository. This method should be used
     * when an initial login is already performed using an auth code and a populated repository is available.
     *
     * @param config which holds all the information for the OAuth flow
     * @param tokenRepository which should be used to store and load authentication tokens
     * @return token provider based on the token repository provided
     */
    public static TokenProvider fromRepository(OAuthConfig config, TokenRepository tokenRepository) {
        return OAuthTokenProvider.builder()
                .config(config)
                .tokenRepository(tokenRepository).build();
    }

    AuthorizationFlowHandler(OAuthConfig config, TokenRepository tokenRepository, WebFormHandler webFormHandler, AuthorizationApiHandler authorizationApiHandler) {
        this.config = config;
        this.tokenRepository = tokenRepository;
        this.webFormHandler = webFormHandler;
        this.authorizationApiHandler = authorizationApiHandler;
    }

    /**
     * Initializes the complete OAuth flow and handles every step's result and error gracefully. Eventually it returns a
     * implementation of the {@link TokenProvider} interface which is required to target the corresponding APIs.
     *
     * If any of the performed steps throws a {@link SessionRetrievalException}, {@link UserLoginFailedException} or
     * {@link UserConsentException} a general {@link AuthorizationInitializationException} will be thrown indicating that
     * the flow could not be completed.
     *
     * @param username of the user for which a token provider is required
     * @param password of the user for which a token provider is required
     * @return a valid token provider which can be used to authenticate against the API
     */
    public TokenProvider authorize(String username, String password) {
        try {
            Authorization authorization = authorizationApiHandler.initializeAuth(
                    this.config.getClientId(),
                    this.config.getRedirectUri(),
                    this.config.getScopes()
            );
            this.retrieveConsent(authorization, username, password);
            this.submitUserConsent(authorization);
            return createTokenProvider(authorization);
        } catch(RuntimeException | SessionRetrievalException | UserLoginFailedException | UserConsentException e) {
            throw new AuthorizationInitializationException("Initialization flow failed can't instantiate a valid token provider", e);
        }
    }

    /**
     * Performs the login action for a specific user having a session provided within the authorization object.
     *
     * After a successful operation the passed authorization will be enriched with a consent which is a derivate of {@link BaseConsent}.
     * In case there is no user interaction required after a login the consent will be a {@link PlainConsent} which can be used
     * to interact with the {@link AuthorizationApiHandler}. If the requesting application requires specific scopes
     * from the corresponding user identified by the session the consent will be an instance of {@link ScopeApprovalConsent}
     * which will require special handling (i.e. approving those scopes using `approveScopes()`.
     *
     * @throws UserLoginFailedException in case something fails during the communication or parsing a response to a
     * domain object.
     */
    private void retrieveConsent(Authorization authorization, String username, String password) throws UserLoginFailedException {
        authorization.setConsent(webFormHandler.login(
                authorization.getAppId(),
                authorization.getSession().getId(),
                authorization.getSession().getData(),
                username,
                password,
                authorization));
    }

    /**
     * Based on a given authorization state and an available consent this method enriches the the authorization
     * by an authorization code which can be used to retrieve the token information from the token API's token endpoint.
     *
     * If the authorization contains a {@link ScopeApprovalConsent} there is an additional step to approve the scopes first
     * using the {@link WebFormHandler} before submitting the the consent targeting the authorization API ({@link AuthorizationApiHandler}.
     * The config needs the flag that missing scopes should be approved by default, otherwise this step will fail with an exception.
     *
     * TODO: also handle user input from the outside by not utilizing the default approval config option. What about partial approvals?
     *
     * @param authorization containing all of the information required for a submission of a user's consent.
     * @throws UserConsentException thrown in case scopes can't be approved in the user's name or if there were
     * technical or parsing problems receiving the authorization code.
     */
    void submitUserConsent(Authorization authorization) throws UserConsentException {
        if(authorization.requiresUserApproval()) {
            if(config.isDefaultApproveMissingScopes()) {
                authorization.setUserApprovedScopes(((ScopeApprovalConsent) authorization.getConsent()).getMissingScopes());
                approveScopes(authorization);
            } else {
                throw new UserConsentException("There are missing scopes for a successful user consent handling but missing scopes should not " +
                        "be approved by default according to the provided config.");
            }
        }
        String code = authorizationApiHandler.submitConsent(
                ((PlainConsent) authorization.getConsent()).getAction(),
                authorization.getSession().getId(),
                authorization.getSession().getData());
        if(Strings.isNullOrEmpty(code)) {
            throw new UserConsentException("Retrieved auth code after consent submission was null or empty.");
        }
        authorization.setAuthCode(code);
    }

    /**
     * Handles and submits the form for a user's consent which requires dedicated approval from the user.
     * The scopes passed will be marked as accepted by the user when sending the request.
     *
     * @param authorization containing the user's approved scopes and the required session information
     * @return the resulting consent after submission. Usually this is a simple consent which can be then used
     * to interact with the {@link AuthorizationApiHandler}.
     *
     * @throws UserConsentException in case something fails during the communication or parsing a response to a
     * domain object.
     */
    private void approveScopes(Authorization authorization) throws UserConsentException {
        BaseConsent consent = webFormHandler.consent(
                authorization.getAppId(),
                authorization.getSession().getId(),
                authorization.getSession().getData(),
                authorization.getUserApprovedScopes(),
                authorization);
        authorization.setConsent(consent);
    }

    private TokenProvider createTokenProvider(Authorization authorization) {
        OAuthTokenProvider.OAuthTokenProviderBuilder builder = OAuthTokenProvider.builder()
                .config(this.config)
                .tokenRepository(this.tokenRepository)
                .authCode(authorization.getAuthCode());
        // if PKCE was initially used also pass it to the token provider to have it available when initially retrieving tokens
        if(config.isUsePKCE()) {
            builder.codeVerifier(authorization.getCodeVerifier());
        }
        return builder.build();
    }
}
