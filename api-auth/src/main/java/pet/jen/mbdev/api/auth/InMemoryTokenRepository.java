package pet.jen.mbdev.api.auth;

import pet.jen.mbdev.api.auth.domain.TokenInformation;
import pet.jen.mbdev.api.auth.exception.TokenPersistenceException;
import pet.jen.mbdev.api.auth.persistence.TokenRepository;

/**
 * Simple in-memory implementation of a {@link TokenRepository} which holds the information as long
 * as the application context is up and running. It is the default repository which is used in case
 * no other repository is provided to the {@link OAuthTokenProvider}.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
class InMemoryTokenRepository implements TokenRepository {

    private TokenInformation tokenInformation = null;

    @Override
    public boolean isEmpty() {
        return tokenInformation != null && tokenInformation.isValid();
    }

    @Override
    public void save(TokenInformation tokenInformation) throws TokenPersistenceException {
        // checks whether the information which is stored is valid to be sure that we don't end up having
        // malformed data
        if(tokenInformation == null || !tokenInformation.isValid()) {
            throw new TokenPersistenceException("Token information which should be store is not valid.");
        }
        this.tokenInformation = tokenInformation;
    }

    @Override
    public TokenInformation get() {
        return tokenInformation;
    }

    @Override
    public void clear() {
        this.tokenInformation = null;
    }
}
