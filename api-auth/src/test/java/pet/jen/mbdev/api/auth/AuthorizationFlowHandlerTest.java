package pet.jen.mbdev.api.auth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import pet.jen.mbdev.api.TokenProvider;
import pet.jen.mbdev.api.auth.client.BaseClient;
import pet.jen.mbdev.api.auth.domain.*;
import pet.jen.mbdev.api.auth.exception.AuthorizationInitializationException;
import pet.jen.mbdev.api.auth.exception.SessionRetrievalException;
import pet.jen.mbdev.api.auth.exception.UserConsentException;
import pet.jen.mbdev.api.auth.persistence.TokenRepository;
import pet.jen.mbdev.api.auth.service.AuthorizationApiHandler;
import pet.jen.mbdev.api.auth.service.WebFormHandler;

import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationFlowHandlerTest extends BaseAuthorizationTest {

    @Mock
    private WebFormHandler webFormHandler;

    @Mock
    private AuthorizationApiHandler authorizationApiHandler;

    private AuthorizationFlowHandler authorizationFlowHandler;

    private OAuthConfig defaultConfig;

    @Before
    public void setup() {
        defaultConfig = createDefaultConfig();
        authorizationFlowHandler = new AuthorizationFlowHandler(defaultConfig, null, webFormHandler, authorizationApiHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInstantiate_whenOAuthConfigIsNotValid_shouldThrowException() {
        OAuthConfig config = OAuthConfig.builder().build();
        AuthorizationFlowHandler.setup(config);
    }

    @Test(expected = AuthorizationInitializationException.class)
    public void testAuthorize_whenAnyRequiredStepFails_shouldThrowException() throws Exception {
        Mockito.when(authorizationApiHandler.initializeAuth(anyString(), anyString(), anyString())).thenThrow(new SessionRetrievalException("bla", null));
        authorizationFlowHandler.authorize("username", "password");
    }

    @Test
    public void testSubmitUserConsent_whenConsentInformationHasMissingScopesAndDefaultApproveIsEnabled_shouldMakeApprovalCallFirst() throws Exception {
        ScopeApprovalConsent approvalConsent = createScopeApprovalConsent();
        Authorization authorization = createAuthorizationWithoutConsent();
        authorization.setConsent(approvalConsent);

        PlainConsent plainConsent = new PlainConsent();
        plainConsent.setAction("Grant");
        plainConsent.setSessionID(authorization.getSession().getId());
        plainConsent.setSessionData(authorization.getSession().getData());

        Mockito.when(webFormHandler.consent(
                eq("app-id"),
                eq("session-id"),
                eq("session-data"),
                eq(approvalConsent.getMissingScopes()),
                any(BaseClient.CookieStorage.class))).thenReturn(plainConsent);
        Mockito.when(authorizationApiHandler.submitConsent(anyString(), anyString(), anyString())).thenReturn("authCode");

        authorizationFlowHandler.submitUserConsent(authorization);

        assertThat(authorization.getAuthCode()).isEqualTo("authCode");
    }

    @Test(expected = UserConsentException.class)
    public void testSubmitUserConsent_whenMissingScopesAndConfigDoesNotApproveByDefault_shouldThrowException() throws Exception {
        authorizationFlowHandler = new AuthorizationFlowHandler(
                getDefaultBuilder().defaultApproveMissingScopes(false).build(),
                null,
                webFormHandler,
                authorizationApiHandler);
        ScopeApprovalConsent approvalConsent = createScopeApprovalConsent();
        Authorization authorization = createAuthorizationWithoutConsent();
        authorization.setConsent(approvalConsent);

        authorizationFlowHandler.submitUserConsent(authorization);
    }

    @Test(expected = UserConsentException.class)
    public void testSubmitUserConsent_whenReturnedAuthCodeIsEmpty_shouldThrowException() throws Exception {
        ScopeApprovalConsent approvalConsent = createScopeApprovalConsent();
        Authorization authorization = createAuthorizationWithoutConsent();
        authorization.setConsent(approvalConsent);

        PlainConsent plainConsent = new PlainConsent();
        plainConsent.setAction("Grant");
        plainConsent.setSessionID(authorization.getSession().getId());
        plainConsent.setSessionData(authorization.getSession().getData());

        Mockito.when(webFormHandler.consent(
                eq("app-id"),
                eq("session-id"),
                eq("session-data"),
                eq(approvalConsent.getMissingScopes()),
                any(BaseClient.CookieStorage.class))).thenReturn(plainConsent);
        Mockito.when(authorizationApiHandler.submitConsent(anyString(), anyString(), anyString())).thenReturn("");

        authorizationFlowHandler.submitUserConsent(authorization);

    }

    @Test
    public void testFromRepository_whenTokenRepositoryIsNotEmpty_shouldCreateTokenProviderWithGivenRepositoryData() {
        TokenRepository repository = Mockito.mock(TokenRepository.class);
        TokenInformation tokenInformation = Mockito.mock(TokenInformation.class);
        Mockito.when(repository.isEmpty()).thenReturn(false);
        Mockito.when(repository.get()).thenReturn(tokenInformation);
        Mockito.when(tokenInformation.isValid()).thenReturn(true);
        Mockito.when(tokenInformation.getAccessToken()).thenReturn("access-token");
        // so that it does not need to be refreshed set a time which is sensitive for the buffer
        Mockito.when(tokenInformation.getTimestamp()).thenReturn(new Date().getTime() + 10000);
        TokenProvider tokenProvider = AuthorizationFlowHandler.fromRepository(defaultConfig, repository);
        assertThat(tokenProvider.getAccessToken()).isEqualToIgnoringCase("access-token");
    }

    private Authorization createAuthorizationWithoutConsent() {
        Authorization authorization = new Authorization();
        Authorization.Session session = new Authorization.Session();
        session.setId("session-id");
        session.setData("session-data");
        authorization.setSession(session);
        authorization.setAppId("app-id");
        return authorization;
    }

    private ScopeApprovalConsent createScopeApprovalConsent() {
        ScopeApprovalConsent consent = new ScopeApprovalConsent();
        consent.setMissingScopes(Arrays.asList("scope-1", "scope-2"));
        consent.setSessionData("session-data");
        consent.setSessionID("session-id");
        return consent;
    }
}