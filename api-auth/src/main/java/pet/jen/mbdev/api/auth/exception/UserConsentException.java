package pet.jen.mbdev.api.auth.exception;

/**
 * Indicates that auth code could not be retrieved within the OAuth flow. The issues can be technical
 * or use case dependent.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class UserConsentException extends RuntimeException {
    public UserConsentException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserConsentException(String message) {
        super(message);
    }
}
