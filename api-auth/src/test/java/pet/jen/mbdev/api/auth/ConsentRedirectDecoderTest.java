package pet.jen.mbdev.api.auth;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pet.jen.mbdev.api.auth.exception.AuthCodeException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ConsentRedirectDecoderTest {

    private ConsentRedirectDecoder decoder;

    @Before
    public void setup() {
        decoder = new ConsentRedirectDecoder();
    }

    @Test
    public void testDecode_whenResponseOnConsentRequestRedirectsToErrorHost_shouldExtractAuthorizationCode() {
        Request request = mockRequest("http://localhost/oidc10/auth/oauth/v2/authorize/consent", createLocationHeader());
        Response response = Response.builder().status(302).headers(createLocationHeader()).request(request).build();
        Exception exception = decoder.decode("method", response);
        assertThat(exception).isInstanceOf(AuthCodeException.class);
        AuthCodeException authCodeException = (AuthCodeException) exception;
        assertThat(authCodeException.getAuthorizationCode()).isEqualTo("9c0174a7-6355-4adb-9626-206638c21022");
    }

    @Test
    public void testDecode_whenRequestUrlWasNotConsent_shouldReturnFeignException() {
        Request request = mockRequest("http://localhost/oidc10/auth/oauth/v2/authorize", null);
        Response response = Response.builder().status(302).headers(createLocationHeader()).request(request).build();
        Exception exception = decoder.decode("method", response);
        assertThat(exception).isInstanceOf(FeignException.class);
    }

    @Test
    public void testDecode_whenResponseStatusIsNot302_shouldReturnFeignException() {
        Request request = mockRequest("http://localhost/oidc10/auth/oauth/v2/authorize/consent", new HashMap<>());
        Response response = Response.builder().headers(new HashMap<>()).status(400).request(request).build();
        Exception exception = decoder.decode("method", response);
        assertThat(exception).isInstanceOf(FeignException.class);
    }

    private Map<String, Collection<String>> createLocationHeader() {
        HashMap<String, Collection<String>> headers = new HashMap<>();
        headers.put("location", Arrays.asList("http://localhost?code=9c0174a7-6355-4adb-9626-206638c21022"));
        return headers;
    }

    private Request mockRequest(String url, Map<String, Collection<String>> headers) {
        Request request = Mockito.mock(Request.class);
        Mockito.when(request.url()).thenReturn(url);
        Mockito.when(request.headers()).thenReturn(headers);
        return request;
    }
}