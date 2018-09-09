package pet.jen.mbdev.api.auth.exception;

/**
 * Indicates that session information could not be retrieved from the server(s). The issues can be technical
 * or use case dependent.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class SessionRetrievalException extends Exception {
    public SessionRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
