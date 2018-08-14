package pet.jen.mbdev.api.auth;

import org.junit.Before;
import org.junit.Test;
import pet.jen.mbdev.api.auth.domain.TokenInformation;
import pet.jen.mbdev.api.auth.exception.TokenPersistenceException;
import pet.jen.mbdev.api.auth.persistence.TokenRepository;

import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class InMemoryTokenRepositoryTest {

    private TokenRepository repository;

    @Before
    public void setup() {
        repository = new InMemoryTokenRepository();
    }

    @Test(expected = TokenPersistenceException.class)
    public void testSave_whenTokensAreInvalid_shouldThrowException() throws Throwable {
        repository.save(new TokenInformation());
    }

    @Test
    public void testGet_whenTokensAreSaved_shouldReturnTokens() throws Throwable {
        repository.save(createTokenInformation("0"));
        assertThat(repository.get().getAccessToken()).isEqualToIgnoringCase("access-token-0");
    }

    @Test
    public void testSave_whenTokensAreSaved_shouldReplace() throws Throwable {
        repository.save(createTokenInformation("0"));
        assertThat(repository.get().getAccessToken()).isEqualToIgnoringCase("access-token-0");
        repository.save(createTokenInformation("1"));
        assertThat(repository.get().getAccessToken()).isEqualToIgnoringCase("access-token-1");
    }

    @Test
    public void testClear_whenTokensAreSaved_shouldClear() throws Throwable {
        repository.save(createTokenInformation("0"));
        repository.clear();
        assertThat(repository.get()).isNull();
    }

    @Test
    public void testHasValidEntry_shouldReturnAccordingly() throws Throwable {
        assertThat(repository.isEmpty()).isFalse();
        repository.save(createTokenInformation("0"));
        assertThat(repository.isEmpty()).isTrue();
    }

    private TokenInformation createTokenInformation(String suffix) {
        TokenInformation tokenInformation = new TokenInformation();
        tokenInformation.setExpiresIn(1);
        tokenInformation.setAccessToken("access-token-" +  suffix);
        tokenInformation.setRefreshToken("refresh-token-" +  suffix);
        tokenInformation.setScope("scope");
        tokenInformation.setTokenType("token-type");
        tokenInformation.setTimestamp(new Date().getTime());
        return tokenInformation;
    }

}