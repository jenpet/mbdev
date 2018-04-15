package pet.jen.mbdev.api.exception;

/**
 * Thrown in case the HTTP API response status is 401 (Unauthorized).
 * According to the API description this error will occur in case the service "Failed to resolve API Key query parameter"
 * or there is an "Invalid API Key".
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class UnauthorizedException extends MBDevApiException {
    public UnauthorizedException(String message, Exception cause) {
        super(message, cause);
    }
}
