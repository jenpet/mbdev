package pet.jen.mbdev.api;

import feign.RequestTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import pet.jen.mbdev.api.exception.UnauthorizedException;

import java.util.Collection;
import java.util.Iterator;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenRequestInterceptorTest {

    private RequestTemplate requestTemplate;

    @Mock
    private TokenProvider tokenProvider;

    private AccessTokenRequestInterceptor requestInterceptor;

    @Before
    public void setup() {
        requestTemplate = new RequestTemplate();
        requestInterceptor = new AccessTokenRequestInterceptor(tokenProvider);
    }

    @Test
    public void testApply_whenTokenProviderReturnsAccessToken_shouldAddAuthorizationHeaderAndKeepExistingOnes() {
        requestTemplate.header("Authorization", "EXISTING");
        Mockito.when(tokenProvider.getAccessToken()).thenReturn("access-token");
        requestInterceptor.apply(requestTemplate);
        assertThat(requestTemplate.headers()).hasSize(1);
        Collection<String> authorizationHeader = requestTemplate.headers().get("Authorization");
        assertThat(authorizationHeader).isNotNull();
        Iterator<String> iterator = authorizationHeader.iterator();
        // existing header
        iterator.next();
        assertThat(iterator.next()).isEqualTo("Bearer access-token");
    }

    @Test(expected = UnauthorizedException.class)
    public void testApply_whenTokenProviderThrowsException_shouldWrapIntoUnauthorizedException() {
        Mockito.when(tokenProvider.getAccessToken()).thenThrow(new RuntimeException());
        requestInterceptor.apply(requestTemplate);
    }
}