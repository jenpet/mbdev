package pet.jen.mbdev.api.exception;

/**
 * Thrown in case the HTTP API response status is 429 (Too Many Requests).
 * According to the API description this error will occur in case the "Quota limit is exceeded".
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class QuotaExceededException extends MBDevApiException {
    public QuotaExceededException(String message, Exception cause) {
        super(message, cause);
    }
}
