package pet.jen.mbdev.api.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import pet.jen.mbdev.api.auth.client.TokenApi;
import pet.jen.mbdev.api.auth.domain.TokenInformation;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class OAuthTokenProviderTest extends BaseAuthorizationTest {

    @Mock
    private TokenApi tokenApi;

    private OAuthTokenProvider provider;

    @Test
    public void testInitialize_whenAccessCodeIsProvided_shouldInitiallyRetrieveTokens() {
        mockAuthCodeCall(0);
        provider = new OAuthTokenProvider(tokenApi, createDefaultConfig(), "auth-code");
        assertThat(provider.getLastUpdate()).isNotNull();
        assertThat(provider.getTokenInfo()).isNotNull();
        assertThat(provider.getTokenInfo().getAccessToken()).isNotNull();
        assertThat(provider.getTokenInfo().getRefreshToken()).isNotNull();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAccessToken_whenProviderIsNotInitialized_shouldThrowIllegalStateException() {
        provider = new OAuthTokenProvider(tokenApi, createDefaultConfig(), "auth-code");
        provider.getAccessToken();
    }

    @Test
    public void testRefreshTokens_shouldRetrieveNewTokenInformationAndUpdateAccordingAttributes() throws Exception {
        mockAuthCodeCall(0);
        provider = new OAuthTokenProvider(tokenApi, createDefaultConfig(), "auth-code");
        TimeUnit.MILLISECONDS.sleep(1);
        Long oldUpdate = provider.getLastUpdate();
        TokenInformation oldTokens = provider.getTokenInfo();
        Mockito.when(tokenApi.refresh(
                eq("refresh_token"),
                eq("refresh-token-0"))).thenReturn(createTokens(1, 0));
        String accessToken = provider.refreshTokens();
        assertThat(accessToken).isNotEqualToIgnoringCase(oldTokens.getAccessToken());
        assertThat(accessToken).isEqualTo("access-token-1");
        assertThat(provider.getTokenInfo().getRefreshToken()).isNotEqualToIgnoringCase(oldTokens.getRefreshToken());
        assertThat(provider.getLastUpdate()).isGreaterThan(oldUpdate);
    }

    @Test
    public void testGetAccessToken_whenTokensAreExpired_shouldRefreshTokensAndReturnNewAccessToken() throws Exception {
        mockAuthCodeCall(0);
        provider = new OAuthTokenProvider(tokenApi, createDefaultConfig(), "auth-code");
        TimeUnit.SECONDS.sleep(1);
        Mockito.when(tokenApi.refresh(
                eq("refresh_token"),
                eq("refresh-token-0"))).thenReturn(createTokens(2, 0));

        String accessToken = provider.getAccessToken();
        assertThat(accessToken).isEqualTo("access-token-2");
    }

    @Test
    public void testGetAccessToken_whenTokensAreNotExpired_shouldJustReturnTheCurrentAccessToken() {
        mockAuthCodeCall(5);
        provider = new OAuthTokenProvider(tokenApi, createDefaultConfig(), "auth-code");
        assertThat(provider.getAccessToken()).isEqualTo("access-token-0");
        Mockito.verify(tokenApi, Mockito.times(0)).refresh(anyString(), anyString());
    }

    private void mockAuthCodeCall(int expiry) {
        Mockito.when(tokenApi.retrieve(
                eq("authorization_code"),
                eq("auth-code"),
                eq("http://localhost"))).thenReturn(createTokens(0, expiry));
    }

    private TokenInformation createTokens(int id, int expiry) {
        TokenInformation tokenInformation = new TokenInformation();
        tokenInformation.setAccessToken("access-token-" + id);
        tokenInformation.setRefreshToken("refresh-token-" + id);
        tokenInformation.setExpiresIn(expiry); // 0 seconds expiry time to check the refresh mechanism
        tokenInformation.setScope("scope");
        tokenInformation.setTokenType("Bearer");
        return tokenInformation;
    }
}