package pet.jen.mbdev.api.auth.exception;

/**
 * Thrown when a persistence of a token information failed using a {@link pet.jen.mbdev.api.auth.persistence.TokenRepository}.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class TokenPersistenceException extends Throwable {
    public TokenPersistenceException(String message) {
        super(message);
    }
}
