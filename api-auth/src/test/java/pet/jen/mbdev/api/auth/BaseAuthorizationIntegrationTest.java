package pet.jen.mbdev.api.auth;

import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.ClassRule;
import org.junit.Rule;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;

import java.util.Collections;

/**
 * For the local integration tests WireMock is used. No specific mappings are done programmatically.
 * The `resources/mappings/` directory contains proxied recorded requests which mimic the production API.
 *
 * The mock is running on port 8887.
 */
public class BaseAuthorizationIntegrationTest {

    protected static final int MOCK_PORT = 8887;

    protected static final String MOCK_URL = "http://localhost:" + MOCK_PORT;

    @ClassRule
    public static WireMockClassRule mockClassRule = new WireMockClassRule(
            WireMockConfiguration.options().port(MOCK_PORT).notifier(new Slf4jNotifier(true)));
    @Rule
    public WireMockClassRule mockRule = mockClassRule;

    protected OAuthConfig createConfig() {
        return getDefaultBuilder()
                .build();
    }

    protected OAuthConfig.OAuthConfigBuilder getDefaultBuilder() {
        return OAuthConfig.builder()
                .authorizationBaseUrl(MOCK_URL)
                .loginBaseUrl(MOCK_URL)
                .clientId("client-id")
                .clientSecret("client-secret")
                .redirectUri("http://localhost")
                .scopes(Collections.singletonList("mb:vehicle:status:general"));
    }

}
