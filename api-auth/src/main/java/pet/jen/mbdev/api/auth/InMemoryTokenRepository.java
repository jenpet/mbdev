package pet.jen.mbdev.api.auth;

import pet.jen.mbdev.api.auth.domain.TokenInformation;
import pet.jen.mbdev.api.auth.exception.TokenPersistenceException;
import pet.jen.mbdev.api.auth.persistence.TokenRepository;

class InMemoryTokenRepository implements TokenRepository {

    private TokenInformation tokenInformation = null;

    @Override
    public boolean isEmpty() {
        return tokenInformation != null && tokenInformation.isValid();
    }

    @Override
    public void save(TokenInformation tokenInformation) throws TokenPersistenceException {
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
