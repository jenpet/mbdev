package pet.jen.mbdev.api.auth.domain;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class TokenInformationTest {

    @Test
    public void testGetExpiresIn_shouldReturnMilliseconds() {
        TokenInformation tokenInformation = new TokenInformation();
        tokenInformation.setExpiresIn(1);
        assertThat(tokenInformation.getExpiresIn()).isEqualTo(1000);
    }
}