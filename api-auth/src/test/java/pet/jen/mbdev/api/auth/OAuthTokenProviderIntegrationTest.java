package pet.jen.mbdev.api.auth;

import org.junit.Before;
import org.junit.Test;
import pet.jen.mbdev.api.auth.domain.TokenInformation;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class OAuthTokenProviderIntegrationTest extends BaseAuthorizationIntegrationTest {

    private OAuthTokenProvider tokenProvider;

    @Before
    public void setup() {
        tokenProvider = OAuthTokenProvider.builder()
                .config(createConfig())
                .authCode("28231715-ef6c-4742-9914-498d4aa391bf").build();
    }

    @Test
    public void testGetAccessToken_whenNoAccessTokenPresent_shouldRetrieveTokensInitiallyUsingAuthCode() {
        // public causes call to api
        String accessToken = tokenProvider.getAccessToken();

        assertThat(accessToken).isNotNull();
        assertThat(accessToken).isNotBlank();
        assertThat(accessToken).isEqualTo("5957805d-e0e8-4ddc-b59f-b7c35ae1cdc2");

        // package visibility just for checks
        TokenInformation tokenInfo = tokenProvider.getTokenInfo();
        assertThat(tokenInfo.getRefreshToken()).isEqualTo("a89b41e5-15a6-48b3-b959-a03098b3b0e0");
        assertThat(tokenInfo.getExpiresIn()).isEqualTo(3600000);
        assertThat(tokenInfo.getTokenType()).isEqualTo("Bearer");
        assertThat(tokenInfo.getScope()).isEqualTo("mb:vehicle:status:general mb:user:pool:reader");
    }

    @Test
    public void testRefreshTokens_shouldRetrieveCompletelyNewTokens() {
        tokenProvider.getAccessToken();
        TokenInformation oldTokenInfo = tokenProvider.getTokenInfo();
        // forced refresh
        tokenProvider.refreshTokens();
        TokenInformation newTokenInfo = tokenProvider.getTokenInfo();

        assertThat(newTokenInfo.getRefreshToken()).isNotEqualToIgnoringCase(oldTokenInfo.getRefreshToken());
        assertThat(newTokenInfo.getAccessToken()).isNotEqualToIgnoringCase(oldTokenInfo.getAccessToken());
        assertThat(newTokenInfo.getAccessToken()).isEqualTo("d38ee318-6982-4871-a798-27ae3d3b8de9");
        assertThat(newTokenInfo.getRefreshToken()).isEqualTo("0a7a708c-424b-4987-a94a-a614b4677e48");
    }
}