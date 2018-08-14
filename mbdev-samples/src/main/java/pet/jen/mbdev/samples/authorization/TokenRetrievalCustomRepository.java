package pet.jen.mbdev.samples.authorization;

import pet.jen.mbdev.api.TokenProvider;
import pet.jen.mbdev.api.auth.AuthorizationFlowHandler;
import pet.jen.mbdev.api.auth.domain.TokenInformation;
import pet.jen.mbdev.api.auth.exception.TokenPersistenceException;
import pet.jen.mbdev.api.auth.persistence.TokenRepository;
import pet.jen.mbdev.samples.SampleBase;
import pet.jen.mbdev.samples.config.ConfigParser;
import pet.jen.mbdev.samples.config.SampleConfig;

public class TokenRetrievalCustomRepository extends SampleBase {
    public static void main(String args[]) throws Exception {
        trustAllHosts();

        SampleConfig config = ConfigParser.getConfig();
        SampleTokenRepository repository = new SampleTokenRepository();

        // first retrieve token info the "initial" way
        AuthorizationFlowHandler handler = AuthorizationFlowHandler.setup(config.getOAuthConfig(), repository);
        TokenProvider initialTokenProvider = handler.authorize(config.getUsername(), config.getPassword());
        System.out.println("Received access token with username and password is : " + initialTokenProvider.getAccessToken());

        TokenProvider repositoryTokenProvider = AuthorizationFlowHandler.fromRepository(config.getOAuthConfig(), repository);
        System.out.println("Refreshed access token using a repository: " + repositoryTokenProvider.refreshTokens());
    }

    private static class SampleTokenRepository implements TokenRepository {

        private TokenInformation tokenInformation;

        @Override
        public boolean isEmpty() {
            return tokenInformation == null;
        }

        @Override
        public void save(TokenInformation tokenInformation) throws TokenPersistenceException {
            this.tokenInformation = tokenInformation;
        }

        @Override
        public TokenInformation get() {
            return tokenInformation;
        }

        @Override
        public void clear() {
            tokenInformation = null;
        }
    }
}
