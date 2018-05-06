package pet.jen.mbdev.api;

import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import feign.RequestLine;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@RunWith(MockitoJUnitRunner.class)
public class MBDevApiFactoryIntegrationTest {
    private static final int MOCK_PORT = 8887;

    private static final String MOCK_URL = "http://localhost:" + MOCK_PORT;

    private TestClient client;

    @Mock
    private TokenProvider tokenProvider;

    @ClassRule
    public static WireMockClassRule mockClassRule = new WireMockClassRule(
            WireMockConfiguration.options().port(MOCK_PORT).notifier(new Slf4jNotifier(true)));
    @Rule
    public WireMockClassRule mockRule = mockClassRule;

    @Before
    public void setup() {
        client = MBDevApiFactory.create(TestClient.class, MOCK_URL, "/", tokenProvider);
    }

    @Test
    public void testCreate_shouldAddRequestInterceptorForAuthorization() {
        String accessToken = "access-token";
        Mockito.when(tokenProvider.getAccessToken()).thenReturn(accessToken);
        mockRule.stubFor(get(urlEqualTo("/test"))
                        .withHeader("Authorization", equalTo("Bearer " +  accessToken))
                .willReturn(aResponse().withStatus(200)));
        client.test();
    }

    @Test
    public void testCreate_shouldWrapApiClientIntoProxy() {
        Mockito.when(tokenProvider.getAccessToken()).thenReturn("access-token").thenReturn("access-token2");
        mockRule.stubFor(get(urlEqualTo("/test"))
                .withHeader("Authorization", equalTo("Bearer access-token"))
                .willReturn(aResponse().withStatus(401)));

        mockRule.stubFor(get(urlEqualTo("/test"))
                .withHeader("Authorization", equalTo("Bearer access-token2"))
                .willReturn(aResponse().withStatus(200)));

        client.test();
    }

    private interface TestClient {
        @RequestLine("GET /test")
        void test();
    }
}
