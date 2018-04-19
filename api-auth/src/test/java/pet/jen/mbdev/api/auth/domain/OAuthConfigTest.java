package pet.jen.mbdev.api.auth.domain;

import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class OAuthConfigTest {

    @Test
    public void testIsValid_whenAllParametersAreSet_shouldReturnTrue() {
        OAuthConfig config = createTestConfig();
        assertThat(config.isValid()).isTrue();
    }

    @Test
    public void testIsValid_whenNotAllParametersAreNotSet_shouldReturnFalse() {
        assertThat(OAuthConfig.builder().build().isValid()).isFalse();
    }

    @Test
    public void testGetTokenExpiryBuffer_shouldReturnInMilliseconds() {
        OAuthConfig config = createTestConfig();
        // default value in millis
        assertThat(config.getTokenExpiryBuffer()).isEqualTo(300000);
    }

    private OAuthConfig createTestConfig() {
        return OAuthConfig.builder()
                .clientId("client-id")
                .clientSecret("client-secret")
                .scopes(Collections.singletonList("a"))
                .build();
    }
}