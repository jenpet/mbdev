package pet.jen.mbdev.api.auth.client;

import com.google.common.net.HttpHeaders;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import pet.jen.mbdev.api.auth.BaseAuthorizationTest;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class WebFormClientTest extends BaseAuthorizationTest {

    @Mock
    private OkHttpClient client;

    @Mock
    private BaseClient.CookieStorage mockStorage;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @InjectMocks
    private WebFormClient webFormClient;

    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    @Before
    public void setup() throws IOException {
        webFormClient = new WebFormClient(createDefaultConfig(), client);
        Mockito.when(client.newCall(Mockito.any(Request.class))).thenReturn(mockCall);
        Mockito.when(mockCall.execute()).thenReturn(mockResponse);
    }


    @Test
    public void testConsent_whenCookieStorageIsProvided_shouldAddAvailableCookiesToHeaders() {
        Mockito.when(mockStorage.load()).thenReturn(Arrays.asList("cookie-1", "cookie-2"));
        webFormClient.consent("app-id", "session-id", "session-data", Arrays.asList("a", "b"), mockStorage);
        Mockito.verify(client, Mockito.times(1)).newCall(requestCaptor.capture());
        assertThat(requestCaptor.getValue().headers(HttpHeaders.COOKIE)).contains("cookie-1", "cookie-2");
    }
}