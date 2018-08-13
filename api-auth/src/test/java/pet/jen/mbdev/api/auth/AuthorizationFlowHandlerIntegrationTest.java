package pet.jen.mbdev.api.auth;

import org.junit.Test;
import pet.jen.mbdev.api.TokenProvider;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class AuthorizationFlowHandlerIntegrationTest extends BaseAuthorizationIntegrationTest {

    @Test
    public void testInit_whenAllCallsSucceed_shouldReturnTokenProvider() {
        AuthorizationFlowHandler handler = AuthorizationFlowHandler.setup(createConfig());
        TokenProvider tokenProvider = handler.authorize("username", "password");
        assertThat(tokenProvider).isNotNull();
        assertThat(tokenProvider.getAccessToken()).isNotNull();
    }
}