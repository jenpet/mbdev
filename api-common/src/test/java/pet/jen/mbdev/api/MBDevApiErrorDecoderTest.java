package pet.jen.mbdev.api;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import pet.jen.mbdev.api.exception.MBDevApiException;
import pet.jen.mbdev.api.exception.QuotaExceededException;
import pet.jen.mbdev.api.exception.UnauthorizedException;

import java.nio.charset.Charset;
import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MBDevApiErrorDecoderTest {

    private static MBDevApiErrorDecoder apiErrorDecoder;

    @BeforeClass
    public static void setup() {
        apiErrorDecoder = new MBDevApiErrorDecoder();
    }

    @Test
    public void testDecode_whenResponseStatusIs401_shouldThrowUnauthorizedException() throws Exception {
        Response response = Response.builder().status(401).headers(Collections.EMPTY_MAP).build();
        Exception exception = apiErrorDecoder.decode("api#vehicles", response);
        assertThat(exception).isInstanceOf(UnauthorizedException.class);

        UnauthorizedException unauthorizedException = (UnauthorizedException) exception;
        assertThat(unauthorizedException.getCause()).isInstanceOf(FeignException.class);
    }

    @Test
    public void testDecode_whenResponseStatusIs429_shouldThrowQuotaExceededExeption() throws Exception {
        Response response = Response.builder().status(429).headers(Collections.EMPTY_MAP).build();
        Exception exception = apiErrorDecoder.decode("api#vehicles", response);
        assertThat(exception).isInstanceOf(QuotaExceededException.class);

        QuotaExceededException quotaExceededException = (QuotaExceededException) exception;
        assertThat(quotaExceededException.getCause()).isInstanceOf(FeignException.class);
    }

    @Test
    public void testDecode_shouldWrapFeignExceptionWithGenericMBDevApiException() throws Exception {
        Request request = Request.create("method", "url", Collections.EMPTY_MAP, null, Charset.defaultCharset());
        Response response = Response.builder().status(400).body("test", Charset.defaultCharset())
                .request(request).headers(Collections.EMPTY_MAP).build();
        Exception exception = apiErrorDecoder.decode("api#vehicles", response);
        assertThat(exception).isInstanceOf(MBDevApiException.class);

        MBDevApiException mbDevApiException = (MBDevApiException) exception;
        assertThat(mbDevApiException.getCause()).isInstanceOf(FeignException.class);
        assertThat(mbDevApiException.getMessage()).isNotBlank();
    }
}