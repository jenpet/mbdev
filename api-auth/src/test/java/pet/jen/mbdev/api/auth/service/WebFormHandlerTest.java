package pet.jen.mbdev.api.auth.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import pet.jen.mbdev.api.auth.BaseAuthorizationTest;
import pet.jen.mbdev.api.auth.client.BaseClient;
import pet.jen.mbdev.api.auth.client.WebFormClient;
import pet.jen.mbdev.api.auth.domain.BaseConsent;
import pet.jen.mbdev.api.auth.exception.UserConsentException;
import pet.jen.mbdev.api.auth.exception.UserLoginFailedException;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class WebFormHandlerTest extends BaseAuthorizationTest {

    @Mock
    private WebFormClient client;

    @Mock
    private BaseClient.CookieStorage cookieStorage;

    private WebFormHandler webFormHandler;

    private String consentFormHTML;

    @Before
    public void setup() throws IOException {
        webFormHandler = new WebFormHandler(client);
        consentFormHTML = resourceAsString("__files/login/simple_consent_form.html");
    }

    @Test
    public void testLogin_whenResponseIsValid_shouldReturnAConsent() throws Exception {
        Mockito.when(client.login(anyString(), anyString(), anyString(), anyString(), anyString(), any(BaseClient.CookieStorage.class))).thenReturn(consentFormHTML);
        assertThat(webFormHandler.login("app-id", "session-id", "session-data", "user", "password", cookieStorage))
                .isInstanceOf(BaseConsent.class);
    }

    @Test(expected = UserLoginFailedException.class)
    public void testLogin_whenResponseIsNull_shouldThrowException() throws Exception {
        Mockito.when(client.login(anyString(), anyString(), anyString(), anyString(), anyString(), any(BaseClient.CookieStorage.class))).thenReturn(null);
        webFormHandler.login("app-id", "session-id", "session-data", "user", "password", cookieStorage);
    }

    @Test(expected = UserLoginFailedException.class)
    public void testLogin_whenResponseIsInvalid_shouldThrowException() throws Exception {
        Mockito.when(client.login(anyString(), anyString(), anyString(), anyString(), anyString(), any(BaseClient.CookieStorage.class))).thenReturn("<html>");
        webFormHandler.login("app-id", "session-id", "session-data", "user", "password", cookieStorage);
    }

    @Test
    public void testConsent_whenResponseIsValid_shouldReturnAConsent() throws Exception {
        Mockito.when(client.consent(anyString(), anyString(), anyString(), ArgumentMatchers.<String>anyList(), any(BaseClient.CookieStorage.class)))
                .thenReturn(consentFormHTML);
        assertThat(webFormHandler.consent("app-id", "session-id", "session-data", Arrays.asList("scope-1", "scope-2"), cookieStorage))
                .isInstanceOf(BaseConsent.class);
    }

    @Test(expected = UserConsentException.class)
    public void testConsent_whenResponseIsNull_shouldThrowException() throws Exception {
        Mockito.when(client.consent(anyString(), anyString(), anyString(), ArgumentMatchers.<String>anyList(), any(BaseClient.CookieStorage.class)))
                .thenReturn(null);
        webFormHandler.consent("app-id", "session-id", "session-data", Arrays.asList("scope-1", "scope-2"), cookieStorage);
    }

    @Test(expected = UserConsentException.class)
    public void testConsent_whenResponseIsInvalid_shouldThrowException() throws Exception {
        Mockito.when(client.consent(anyString(), anyString(), anyString(), ArgumentMatchers.<String>anyList(), any(BaseClient.CookieStorage.class)))
                .thenReturn("<html>");
        webFormHandler.consent("app-id", "session-id", "session-data", Arrays.asList("scope-1", "scope-2"), cookieStorage);
    }
}