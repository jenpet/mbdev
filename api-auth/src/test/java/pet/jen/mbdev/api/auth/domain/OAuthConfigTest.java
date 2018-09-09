package pet.jen.mbdev.api.auth.domain;

import org.junit.Test;
import pet.jen.mbdev.api.auth.BaseAuthorizationTest;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class OAuthConfigTest extends BaseAuthorizationTest {

    @Test
    public void testIsValid_whenAllParametersAreSet_shouldReturnTrue() {
        // config without PKCE
        assertThat(createDefaultConfig().isValid()).isTrue();

        // config with PKCE
        assertThat(getDefaultBuilder().usePKCE(true).build().isValid()).isTrue();
    }

    @Test
    public void testIsValid_whenNotAllParametersAreNotSet_shouldReturnFalse() {
        assertThat(OAuthConfig.builder().build().isValid()).isFalse();
    }

    @Test
    public void testGetTokenExpiryBuffer_shouldReturnInMilliseconds() {
        OAuthConfig config = createDefaultConfig();
        assertThat(config.getTokenExpiryBuffer()).isEqualTo(1000);
    }
}