package pet.jen.mbdev.api.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import pet.jen.mbdev.api.auth.client.TokenApi;
import pet.jen.mbdev.api.auth.domain.TokenInformation;
import pet.jen.mbdev.api.auth.persistence.TokenRepository;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class OAuthTokenProviderTest extends BaseAuthorizationTest {

    @Mock
    private TokenApi tokenApi;

    private OAuthTokenProvider provider;

    @Test(expected = IllegalArgumentException.class)
    public void testBuild_whenConfigIsMissing_shouldThrowException() {
        OAuthTokenProvider.builder().build();
    }

    @Test
    public void testInitialize_whenAuthCodeIsProvided_shouldInitiallyRetrieveTokens() {
        mockAuthCodeCall(0);
        provider = OAuthTokenProvider.builder()
                .tokenApi(tokenApi)
                .config(createDefaultConfig())
                .authCode("auth-code").build();
        assertThat(provider.getTokenInfo()).isNotNull();
        assertThat(provider.getTokenInfo().getTimestamp()).isGreaterThan(1L);
        assertThat(provider.getTokenInfo().getAccessToken()).isNotNull();
        assertThat(provider.getTokenInfo().getRefreshToken()).isNotNull();
        Mockito.verify(tokenApi, Mockito.times(1)).retrieve(anyString(), anyString(), anyString());
    }

    @Test
    public void testInitialize_whenAuthCodeIsNotProvidedAndRepositoryIsNotEmptyAndValid_shouldReturnProviderWithRepositoryInformation() throws Throwable {
        TokenRepository repository = Mockito.mock(InMemoryTokenRepository.class);
        Mockito.when(repository.get()).thenReturn(createTokens(0, 0, new Date().getTime()));
        provider = OAuthTokenProvider.builder()
                .tokenApi(tokenApi)
                .config(createDefaultConfig())
                .tokenRepository(repository).build();
        assertThat(provider.getTokenInfo().getAccessToken()).isEqualToIgnoringCase("access-token-0");
        assertThat(provider.getTokenInfo().getTimestamp()).isGreaterThan(0L);
        Mockito.verifyZeroInteractions(tokenApi);
    }

    @Test(expected = IllegalStateException.class)
    public void testInitialize_whenAuthCodeIsNotProvidedAndRepositoryIsNotEmptyAndInvalid_shouldThrowException() throws Throwable {
        TokenRepository repository = Mockito.mock(InMemoryTokenRepository.class);
        Mockito.when(repository.get()).thenReturn(createTokens(0, 0, 0L));
        provider = OAuthTokenProvider.builder()
                .tokenApi(tokenApi)
                .config(createDefaultConfig())
                .tokenRepository(repository).build();
    }

    @Test(expected = IllegalStateException.class)
    public void testInitialize_whenNeitherAuthCodeNorAValidRepositoryIsPresent_shouldThrowException() throws Exception {
        provider = OAuthTokenProvider.builder()
                .tokenApi(tokenApi)
                .config(createDefaultConfig()).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitialize_whenTokenApiReturnsInvalidInformation_shouldThrowException() {
        Mockito.when(tokenApi.retrieve(
                eq("authorization_code"),
                eq("auth-code"),
                eq("http://localhost"))).thenReturn(new TokenInformation());
        provider = OAuthTokenProvider.builder()
                .tokenApi(tokenApi)
                .config(createDefaultConfig())
                .authCode("auth-code").build();
    }

    @Test
    public void testRefreshTokens_shouldRetrieveNewTokenInformationAndUpdateAccordingAttributes() throws Exception {
        mockAuthCodeCall(0);
        provider = OAuthTokenProvider.builder()
                .tokenApi(tokenApi)
                .config(createDefaultConfig())
                .authCode("auth-code").build();
        TimeUnit.MILLISECONDS.sleep(1);
        Long oldUpdate = provider.getTokenInfo().getTimestamp();
        TokenInformation oldTokens = provider.getTokenInfo();
        Mockito.when(tokenApi.refresh(
                eq("refresh_token"),
                eq("refresh-token-0"))).thenReturn(createTokens(1, 0));
        String accessToken = provider.refreshTokens();
        assertThat(accessToken).isNotEqualToIgnoringCase(oldTokens.getAccessToken());
        assertThat(accessToken).isEqualTo("access-token-1");
        assertThat(provider.getTokenInfo().getRefreshToken()).isNotEqualToIgnoringCase(oldTokens.getRefreshToken());
        assertThat(provider.getTokenInfo().getTimestamp()).isGreaterThan(oldUpdate);
    }

    @Test
    public void testGetAccessToken_whenTokensAreExpired_shouldRefreshTokensAndReturnNewAccessToken() throws Exception {
        mockAuthCodeCall(0);
        provider = OAuthTokenProvider.builder()
                .tokenApi(tokenApi)
                .config(createDefaultConfig())
                .authCode("auth-code").build();
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
        provider = OAuthTokenProvider.builder()
                .tokenApi(tokenApi)
                .config(createDefaultConfig())
                .authCode("auth-code").build();
        assertThat(provider.getAccessToken()).isEqualTo("access-token-0");
        Mockito.verify(tokenApi, Mockito.times(0)).refresh(anyString(), anyString());
    }

    private void mockAuthCodeCall(int expiry) {
        Mockito.when(tokenApi.retrieve(
                eq("authorization_code"),
                eq("auth-code"),
                eq("http://localhost"))).thenReturn(createTokens(0, expiry));
    }

    private TokenInformation createTokens(int id, int expiry, long timestamp) {
        TokenInformation tokenInformation = createTokens(id, expiry);
        tokenInformation.setTimestamp(timestamp);
        return tokenInformation;
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