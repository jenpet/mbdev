package pet.jen.mbdev.api.auth;

import org.junit.Test;
import pet.jen.mbdev.api.TokenProvider;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class AuthorizationFlowHandlerIntegrationTest extends BaseAuthorizationIntegrationTest {

    @Test
    public void testFullFlow_whenPKCEIsDisabledAndScopesAreAlreadyAccepted_shouldReturnAccessToken() {
        OAuthConfig config = getDefaultBuilder().clientId("eligible-client").build();
        AuthorizationFlowHandler handler = AuthorizationFlowHandler.setup(config);
        TokenProvider tokenProvider = handler.authorize("username", "password");
        assertThat(tokenProvider).isNotNull();
        assertThat(tokenProvider.getAccessToken()).isEqualTo("access-token-1");
    }

    @Test
    public void testFullFlow_whenPKCEIsDisabledAndScopesAreNotAccepted_shouldReturnAccessToken() {
        OAuthConfig config = getDefaultBuilder().clientId("ineligible-client").build();
        AuthorizationFlowHandler handler = AuthorizationFlowHandler.setup(config);
        TokenProvider tokenProvider = handler.authorize("username", "password");
        assertThat(tokenProvider).isNotNull();
        assertThat(tokenProvider.getAccessToken()).isEqualTo("access-token-2");
    }

    @Test
    public void testFullFlow_whenPKCEIsEnabledAndScopesAreAlreadyAccepted_shouldReturnAccessToken() {
        OAuthConfig config = getDefaultBuilder().clientId("eligible-pkce-client").usePKCE(true).build();
        AuthorizationFlowHandler handler = AuthorizationFlowHandler.setup(config);
        TokenProvider tokenProvider = handler.authorize("username", "password");
        assertThat(tokenProvider).isNotNull();
        assertThat(tokenProvider.getAccessToken()).isEqualTo("access-token-3");
    }
}