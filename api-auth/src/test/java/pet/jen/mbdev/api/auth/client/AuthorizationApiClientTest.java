package pet.jen.mbdev.api.auth.client;


import okhttp3.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import pet.jen.mbdev.api.auth.BaseAuthorizationTest;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationApiClientTest extends BaseAuthorizationTest {

    @Mock
    private OkHttpClient client;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @Mock
    private ResponseBody mockBody;

    @InjectMocks
    private AuthorizationApiClient apiClient;

    @Before
    public void setup() throws IOException {
        Mockito.when(client.newCall(Mockito.any(Request.class))).thenReturn(mockCall);
        Mockito.when(mockCall.execute()).thenReturn(mockResponse);
        Mockito.when(mockResponse.body()).thenReturn(mockBody);
        apiClient = new AuthorizationApiClient(createDefaultConfig(), client, client);
    }

    @Test
    public void testAuthorize_shouldReturnResponseBodyAsString() throws IOException {
        Mockito.when(mockBody.string()).thenReturn("test-body");
        assertThat(apiClient.authorize(null, null, null, null)).isEqualToIgnoringCase("test-body");
    }

    @Test
    public void testRetrieveAuthCode_whenPresentInLocationHeader_shouldReturnExtractedCode() {
        Mockito.when(mockResponse.headers(eq("Location"))).thenReturn(Collections.singletonList("http://localhost:8887?code=28231715-ef6c-4742-9914-498d4aa391bf"));
        assertThat(apiClient.retrieveAuthCode("Grant", "session-id", "session-data")).isEqualTo("28231715-ef6c-4742-9914-498d4aa391bf");
    }

    @Test
    public void testRetrieveAuthCode_whenNotPresentInLocationHeader_shouldReturnEmptyString() {
        Mockito.when(mockResponse.headers(eq("Location"))).thenReturn(Collections.<String>emptyList());
        assertThat(apiClient.retrieveAuthCode("Grant", "session-id", "session-data")).isEqualTo("");
    }

    @Test
    public void testCreateAuthorizeUrl_whenCodeChallengeProvided_shouldAppendToUrlAccordingly() {
        HttpUrl url = apiClient.createAuthorizeUrl("client-id", "http://localhost", "scope-1", "code-challenge");
        assertThat(url.queryParameter("code_challenge")).isEqualTo("code-challenge");
        assertThat(url.queryParameter("code_challenge_method")).isEqualTo("S256");
    }
}