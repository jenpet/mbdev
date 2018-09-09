package pet.jen.mbdev.api.auth.service;

import com.google.common.base.Strings;
import pet.jen.mbdev.api.auth.client.AuthorizationApiClient;
import pet.jen.mbdev.api.auth.client.BaseClient;
import pet.jen.mbdev.api.auth.client.WebFormClient;
import pet.jen.mbdev.api.auth.domain.BaseConsent;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;
import pet.jen.mbdev.api.auth.exception.UserConsentException;
import pet.jen.mbdev.api.auth.exception.UserLoginFailedException;

import java.util.List;

public class WebFormHandler {

    private WebFormClient client;

    public WebFormHandler(OAuthConfig config) {
        this.client = new WebFormClient(config);
    }

    WebFormHandler(WebFormClient client) {
        this.client = client;
    }

    /**
     * Triggers the login of a user with his credentials and returning a derivate of {@link BaseConsent} depending
     * on the permissions the user has given (or not given) to the application identified by the app id.
     *
     * @param appId received from previous redirects from the {@link AuthorizationApiClient}
     * @param sessionId of the current authentication attempt
     * @param sessionData of the current authentication attempt
     * @param username of the user
     * @param password of the user
     * @param cookieStorage where cookies are stored after login to save a step in case consent requires user approval
     * @return either a plain consent in case no further user approvals are required or a scope approval consent
     * @throws UserLoginFailedException in case the communication failed or a creation of a consent is not possible
     */
    public BaseConsent login(String appId, String sessionId, String sessionData, String username, String password, BaseClient.CookieStorage cookieStorage) throws UserLoginFailedException {
        String result = this.client.login(appId, sessionId, sessionData, username, password, cookieStorage);
        if(Strings.isNullOrEmpty(result)) {
            throw new UserLoginFailedException("Failed to extract necessary consent information from the login request since the returned result was null or empty.");
        }
        try {
            return ConsentFactory.fromResponse(result);
        } catch(IllegalArgumentException e) {
            throw new UserLoginFailedException("Failed to extract necessary consent information from the login request.", e);
        }
    }

    /**
     * Simulates a user approving a list of scopes for the requesting application.
     *
     * @param appId identifier of the requesting application
     * @param sessionId of the current authentication attempt
     * @param sessionData of the current authentication attempt
     * @param approvedScopes scopes which the user want's to approved
     * @param cookieStorage which contains the cookie from a successful login submission. If there is no valid cookie present
     *                      there will be another redirect to a login form which will cause an error in the flow.
     * @return consent which will be most likely a plain consent in case of success
     * @throws UserConsentException in case the communication failed or a creation of a consent is not possible
     */
    public BaseConsent consent(String appId, String sessionId, String sessionData, List<String> approvedScopes, BaseClient.CookieStorage cookieStorage) throws UserConsentException {
        String result = this.client.consent(appId, sessionId, sessionData, approvedScopes, cookieStorage);
        if(Strings.isNullOrEmpty(result)) {
            throw new UserConsentException("Failed to submit the user consent with the provided scopes since the returned result was null or empty.");
        }
        try {
            return ConsentFactory.fromResponse(result);
        } catch(IllegalArgumentException e) {
            throw new UserConsentException("Failed to submit the user consent with the provided scopes.", e);
        }
    }
}
