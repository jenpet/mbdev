package pet.jen.mbdev.api.auth;

import feign.Feign;
import feign.FeignException;
import feign.RetryableException;
import feign.Retryer;
import feign.form.FormEncoder;
import pet.jen.mbdev.api.TokenProvider;
import pet.jen.mbdev.api.auth.client.AuthorizationApi;
import pet.jen.mbdev.api.auth.client.LoginApi;
import pet.jen.mbdev.api.auth.domain.ConsentInformation;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;
import pet.jen.mbdev.api.auth.domain.SessionInformation;
import pet.jen.mbdev.api.auth.exception.*;

import java.net.HttpRetryException;

/**
 * Performs all of the API calls required to retrieve a valid authorization token. In case a single step of the described
 * OAuth flow fails (https://developer.mercedes-benz.com/content-page/oauth-documentation) a detailed exception will be thrown
 * embedding the according error / exception.
 *
 * The {@link AuthorizationFlowHandler} is not responsible to retrieve the access or refresh tokens. This will be done
 * by the {@link OAuthTokenProvider} which will use the authorization code obtained by a successful flow.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class AuthorizationFlowHandler {

    // api endpoint for authorizations
    private AuthorizationApi authorizationApi;

    // api endpoint for login actions
    private LoginApi loginApi;

    // contains necessary meta data for the oauth flow
    private OAuthConfig config;

    public static AuthorizationFlowHandler setup(OAuthConfig config) {
        if(!config.isValid()) {
            throw new IllegalArgumentException("The provided OAuth configuration was not valid. Please mandatory attributes.");
        }
        return new AuthorizationFlowHandler(
                createAuthorizationApiClient(config),
                createLoginApiClient(config),
                config);
    }

    AuthorizationFlowHandler(AuthorizationApi authorizationApi, LoginApi loginApi, OAuthConfig config) {
        this.authorizationApi = authorizationApi;
        this.loginApi = loginApi;
        this.config = config;
    }

    /**
     * Initializes the OAuth flow and handles every step's result and error gracefully. Eventually it returns a
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
            SessionInformation sessionInfo = this.retrieveSession(
                    this.config.getClientId(),
                    this.config.getRedirectUri(),
                    this.config.getScopes());
            ConsentInformation consentInfo = this.submitUserLogin(
                    sessionInfo,
                    username,
                    password);
            String authCode = this.submitUserConsent(consentInfo);
            return new OAuthTokenProvider(this.config, authCode);
        } catch(SessionRetrievalException | UserLoginFailedException | UserConsentException e) {
            throw new AuthorizationInitializationException("Initialization flow failed can't instantiate a valid token provider", e);
        }
    }

    /**
     * Performs the initial authorization call for an application with certain scopes. The API redirects
     * to a login form where all of the necessary information for a further login can be extracted.
     *
     * @param clientId of the registered application
     * @param redirectUri registered in the console
     * @param scope requested scope for this session
     * @return session information required for further processing.
     */
    SessionInformation retrieveSession(String clientId, String redirectUri, String scope) {
        try {
            String response = authorizationApi.authorize(clientId, redirectUri, scope);
            return SessionInformation.fromResponse(response);
        } catch (FeignException | IllegalArgumentException e) {
            throw new SessionRetrievalException("Failed to extract necessary session information from the authorization request.", e);
        }
    }

    /**
     * Submits the user consent based on the provided consent information. This calls needs the {@link AuthorizationApi}
     * to fail with the according `postConsent` method. It is important that the call with a {@link FeignException} so that
     * the {@link ConsentRedirectDecoder} can extract the authorization code.
     *
     * The handled {@link AuthCodeException} is intentionally thrown by the client. It contains the
     * authorization code. In case this code is null or the feign client itself encountered a different
     * {@link FeignException} a {@link UserConsentException} will be thrown.
     *
     * Note: Check the readme file on how to adjust your settings to cause the client to fail.
     *
     * @param consentInfo information extracted after the login
     * @return the actual authorization code which can be used for further token requests
     */
    String submitUserConsent(ConsentInformation consentInfo) {
        String code = null;
        try {
            authorizationApi.postConsent(
                    consentInfo.getAction(),
                    consentInfo.getSessionID(),
                    consentInfo.getSessionData());
        } catch (AuthCodeException e) {
            code = e.getAuthorizationCode();
        } catch (FeignException e) {
            /*
             * In case the ConsentRedirectDecoder does not kick in due to the HTTP streaming mode
             * the request will fail with feign RetryableException since it tries to connect to the
             * redirect uri `localhost`. In case it is not a RetryableException it will be handled
             * as any other.
            */
            if(e instanceof RetryableException) {
                code = extractCodeFromException((RetryableException) e);
            } else {
                throw new UserConsentException("Client communication while posting consent failed.", e);
            }
        }
        if(code == null) {
            throw new UserConsentException("Retrieved auth code after consent submission was null.");
        }
        return code;
    }

    /**
     * Performs the login action for a specific user having a session. After a login the response
     * contains a consent form that has to be submitted by the client. The return value
     * of this method is an already extracted pojo consent information.
     *
     * In case the request or the extraction of consent information failed a {@link UserLoginFailedException} is thrown.
     *
     * @param sessionInfo which is required for the login process
     * @param username of the user which needs to be logged in
     * @param password of the user which needs to be logged in
     * @return extracted consent information
     */
    ConsentInformation submitUserLogin(SessionInformation sessionInfo, String username, String password) {
        try {
            String responseBody = loginApi.login(sessionInfo.getAppId(),
                    sessionInfo.getSessionID(),
                    sessionInfo.getSessionData(),
                    username,
                    password);
            return ConsentInformation.fromResponse(responseBody);
        } catch (FeignException | IllegalArgumentException e) {
            throw new UserLoginFailedException("Failed to extract necessary consent information from the login request.", e);
        }
    }

    /**
     * Converts the cause of the {@link RetryableException} to a {@link HttpRetryException} to extract
     * the code from the exception's location attribute.
     * @param e {@link RetryableException}which has a {@link HttpRetryException} as a cause.
     * @return the extracted authorization code
     */
    private String extractCodeFromException(RetryableException e) {
        HttpRetryException retryException = (HttpRetryException) e.getCause();
        return AuthCodeExtractor.extract(retryException.getLocation());
    }

    private static AuthorizationApi createAuthorizationApiClient(OAuthConfig config) {
        return Feign.builder()
                .encoder(new FormEncoder())
                .errorDecoder(new ConsentRedirectDecoder()) // custom error decoder which should be only invoked from an invalid redirect
                .retryer(Retryer.Default.NEVER_RETRY)
                .target(AuthorizationApi.class, config.getAuthorizationBaseUrl());
    }

    private static LoginApi createLoginApiClient(OAuthConfig config) {
        return Feign.builder()
                .encoder(new FormEncoder())
                .target(LoginApi.class, config.getLoginBaseUrl());
    }
}
