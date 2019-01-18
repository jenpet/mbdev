package pet.jen.mbdev.api;

import feign.RetryableException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import pet.jen.mbdev.api.exception.MBDevApiException;
import pet.jen.mbdev.api.exception.UnauthorizedException;

import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class ApiClientProxyTest {

    @Mock
    private TestClient testClient;

    @Mock
    private TokenProvider tokenProvider;

    private TestClient proxiedTestClient;

    @Before
    public void setup() {
        proxiedTestClient = (TestClient) Proxy.newProxyInstance(
                ApiClientProxyTest.class.getClassLoader(),
                new Class[] {TestClient.class},
                new ApiClientProxy(testClient, tokenProvider)
        );
    }

    @Test
    public void testInstantiate_shouldExtractTargetsMethods() {
        ApiClientProxy proxy = new ApiClientProxy(testClient, tokenProvider);
        assertThat(proxy.targetMethods.containsKey("testMethod")).isTrue();
    }

    @Test
    public void testInvoke_shouldInvokeAccordingly() {
        proxiedTestClient.testMethod();
        Mockito.verify(testClient, Mockito.times(1)).testMethod();
    }


    @Test
    public void testInvoke_whenTargetThrowsUnauthorizedException_shouldTriggerTokenProviderAndRetry() {
        Mockito.when(testClient.testMethod())
                .thenThrow(new UnauthorizedException(null, null))
                .thenReturn("Test");
        proxiedTestClient.testMethod();
        Mockito.verify(tokenProvider, Mockito.times(1)).refreshTokens();
        Mockito.verify(testClient, Mockito.times(2)).testMethod();
    }

    @Test(expected = MBDevApiException.class)
    public void testInvoke_whenTargetThrowsMBDevApiException_shouldForward() throws Exception {
        Mockito.when(testClient.testMethod()).thenThrow(new MBDevApiException(null, null));
        proxiedTestClient.testMethod();
    }

    @Test
    public void testInvoke_whenTargetConstantlyThrowsUnauthorizedException_shouldOnlyRefreshTokensOnceThenThrowException() {
        UnauthorizedException exception = null;
        Mockito.when(testClient.testMethod()).thenThrow(new UnauthorizedException(null, null));
        try {
            proxiedTestClient.testMethod();
        } catch (UnauthorizedException e) {
            exception = e;
        }
        assertThat(exception).isNotNull();
        Mockito.verify(tokenProvider, Mockito.times(1)).refreshTokens();
        Mockito.verify(testClient, Mockito.times(2)).testMethod();
    }

    @Test(expected = MBDevApiException.class)
    public void testInvoke_whenRetryableExceptionOccurs_shouldShouldWrap() {
        Mockito.when(testClient.testMethod()).thenThrow(new RetryableException("", new Date()));
        proxiedTestClient.testMethod();
    }

    @Test(expected = RuntimeException.class)
    public void testInvoke_whenDifferentExceptionOccurs_shouldNotHandleException() {
        Mockito.when(testClient.testMethod()).thenThrow(new RuntimeException());
        proxiedTestClient.testMethod();
    }

    private interface TestClient {
        String testMethod();
    }
}