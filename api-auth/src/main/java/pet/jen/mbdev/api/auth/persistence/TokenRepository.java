package pet.jen.mbdev.api.auth.persistence;

import pet.jen.mbdev.api.auth.domain.TokenInformation;
import pet.jen.mbdev.api.auth.exception.TokenPersistenceException;

/**
 * Is used by the {@link pet.jen.mbdev.api.auth.OAuthTokenProvider} to persist and read the retrieved values
 * from the token api. It allows a custom handling of the token information when passing it into the {@link pet.jen.mbdev.api.auth.AuthorizationFlowHandler}.
 *
 * The focus of this repository is always just a single object instance of a {@link TokenInformation}.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public interface TokenRepository {
    /**
     * @return boolean indicator whether the repository has any data
     */
    boolean isEmpty();

    /**
     * Saves the information within the repository. In case there is any error a {@link TokenPersistenceException} should be
     * thrown.
     *
     * @param tokenInformation which has to be persisted
     * @throws TokenPersistenceException in case something went wrong during persistence
     */
    void save(TokenInformation tokenInformation) throws TokenPersistenceException;

    /**
     * @return either the stored token information or null if the repository is empty.
     */
    TokenInformation get();

    /**
     * Clears the repository by removing its entry.
     */
    void clear();
}
