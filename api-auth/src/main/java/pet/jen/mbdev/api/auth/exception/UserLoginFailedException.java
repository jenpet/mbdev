package pet.jen.mbdev.api.auth.exception;

/**
 * Indicates that the user couldn't get logged in. The issues can be technical
 * or use case dependent.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class UserLoginFailedException extends Exception {

    public UserLoginFailedException(String message) {
        super(message);
    }

    public UserLoginFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
