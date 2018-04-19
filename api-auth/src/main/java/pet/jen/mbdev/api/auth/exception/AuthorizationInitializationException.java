package pet.jen.mbdev.api.auth.exception;

/**
 * Indicator about a failed OAuth authorization flow. If any of the required steps fails this exception
 * will be thrown containing necessary details.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class AuthorizationInitializationException extends RuntimeException {
    public AuthorizationInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
