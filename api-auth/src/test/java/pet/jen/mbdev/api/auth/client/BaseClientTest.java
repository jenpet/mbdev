package pet.jen.mbdev.api.auth.client;

import com.google.common.net.HttpHeaders;
import okhttp3.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BaseClientTest {

    @Mock
    private OkHttpClient client;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @InjectMocks
    private BaseClient baseClient;

    @Before
    public void setup() throws IOException {
        Mockito.when(client.newCall(Mockito.any(Request.class))).thenReturn(mockCall);
        Mockito.when(mockCall.execute()).thenReturn(mockResponse);
    }

    @Test
    public void testExecuteAndGetBody_whenClientReturnsNoBody_shouldReturnEmptyString() {
        Mockito.when(mockResponse.body()).thenReturn(null);
        assertThat(baseClient.executeAndGetBody(client, Mockito.mock(Request.class))).isEqualToIgnoringCase("");
    }

    @Test
    public void testExecuteAndGetBody_whenClientThrowsIOException_shouldReturnEmptyString() throws IOException {
        Mockito.doThrow(IOException.class).when(mockCall).execute();
        assertThat(baseClient.executeAndGetBody(client, Mockito.mock(Request.class))).isEqualToIgnoringCase("");
    }

    @Test
    public void testExecuteAndGetBody_whenResponseBodyStringThrowsIOException_shouldReturnEmptyString() throws IOException {
        ResponseBody body = Mockito.mock(ResponseBody.class);
        Mockito.when(mockResponse.body()).thenReturn(body);
        Mockito.when(body.string()).thenThrow(IOException.class);
        assertThat(baseClient.executeAndGetBody(client, Mockito.mock(Request.class))).isEqualToIgnoringCase("");
    }

    @Test
    public void testExecute_whenCookieStorageIsProvided_shouldStoreResponsesCookiesInAuthorization() {
        Mockito.when(mockResponse.headers(HttpHeaders.SET_COOKIE)).thenReturn(Arrays.asList("cookie-1", "cookie-2"));
        BaseClient.CookieStorage cookieStorage = new TestCookieStorage();
        baseClient.execute(client, Mockito.mock(Request.class), cookieStorage);
        assertThat(cookieStorage.load()).contains("cookie-1", "cookie-2");
    }

    @Test
    public void testExecute_whenCookieStorageIsProvidedButNoCookiesAreSet_shouldStoreNothing() {
        BaseClient.CookieStorage storage = Mockito.mock(BaseClient.CookieStorage.class);
        baseClient.execute(client, Mockito.mock(Request.class), storage);
        Mockito.verifyZeroInteractions(storage);
    }

    private class TestCookieStorage extends ArrayList<String> implements BaseClient.CookieStorage {

        @Override
        public void store(List<String> cookies) {
            this.addAll(cookies);
        }

        @Override
        public List<String> load() {
            return this;
        }
    }
}