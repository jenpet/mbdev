package pet.jen.mbdev.api.auth.persistence;

import pet.jen.mbdev.api.auth.domain.TokenInformation;
import pet.jen.mbdev.api.auth.exception.TokenPersistenceException;

/**
 * ...
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public interface TokenRepository {
    boolean isEmpty();
    void save(TokenInformation tokenInformation) throws TokenPersistenceException;
    TokenInformation get();
    void clear();
}
