package pet.jen.mbdev.api.auth.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import pet.jen.mbdev.api.auth.BaseAuthorizationTest;
import pet.jen.mbdev.api.auth.client.AuthorizationApiClient;
import pet.jen.mbdev.api.auth.exception.SessionRetrievalException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationApiHandlerTest extends BaseAuthorizationTest {

    @Mock
    private AuthorizationApiClient client;

    private AuthorizationApiHandler authorizationApiHandler;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Test
    public void testInitializeAuth_whenPKCEShouldBeUsedAndCodeChallengeProvided_shouldCallClientAccordingly() throws Exception {
        Mockito.when(client.authorize(eq("client-id"), eq("http://localhost"), eq("scope"), stringCaptor.capture()))
            .thenReturn(resourceAsString("__files/login/redirected_login_form.html"));
        authorizationApiHandler = new AuthorizationApiHandler(getDefaultBuilder().usePKCE(true).build(), client);
        authorizationApiHandler.initializeAuth("client-id", "http://localhost", "scope");
        assertThat(stringCaptor.getValue()).isNotNull();
    }

    @Test
    public void testInitializeAuth_whenPKCEShouldNotBeUsed_shouldCallClientAccordingly() throws Exception {
        Mockito.when(client.authorize(eq("client-id"), eq("http://localhost"), eq("scope")))
                .thenReturn(resourceAsString("__files/login/redirected_login_form.html"));
        authorizationApiHandler = new AuthorizationApiHandler(createDefaultConfig(), client);
        authorizationApiHandler.initializeAuth("client-id", "http://localhost", "scope");
        Mockito.verify(client, times(1)).authorize(eq("client-id"), eq("http://localhost"), eq("scope"));
    }

    @Test(expected = SessionRetrievalException.class)
    public void testInitializeAuth_whenClientReturnsEmptyString_shouldThrowException() throws Exception {
        Mockito.when(client.authorize(eq("client-id"), eq("http://localhost"), eq("scope"))).thenReturn("");
        authorizationApiHandler = new AuthorizationApiHandler(createDefaultConfig(), client);
        authorizationApiHandler.initializeAuth("client-id", "http://localhost", "scope");
    }
}