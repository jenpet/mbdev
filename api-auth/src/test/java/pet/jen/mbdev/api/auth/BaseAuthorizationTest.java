package pet.jen.mbdev.api.auth;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;

import java.io.IOException;
import java.util.Arrays;

public class BaseAuthorizationTest {

    protected OAuthConfig.OAuthConfigBuilder getDefaultBuilder() {
        return OAuthConfig.builder()
                .authorizationBaseUrl("http://localhost")
                .loginBaseUrl("http://localhost")
                .clientId("client-id")
                .clientSecret("client-secret")
                .redirectUri("http://localhost")
                .scopes(Arrays.asList("scope1", "scope2"))
                .tokenExpiryBuffer(1);
    }

    protected OAuthConfig createDefaultConfig() {
        return getDefaultBuilder().build();
    }

    protected String resourceAsString(String filename) throws IOException {
        return Resources.toString(
                Resources.getResource(filename), Charsets.UTF_8);
    }
}
