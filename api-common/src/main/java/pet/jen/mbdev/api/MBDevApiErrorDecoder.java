package pet.jen.mbdev.api;

import feign.Response;
import feign.codec.ErrorDecoder;
import pet.jen.mbdev.api.exception.MBDevApiException;
import pet.jen.mbdev.api.exception.QuotaExceededException;
import pet.jen.mbdev.api.exception.UnauthorizedException;

/**
 * Default error decoder for the Mercedes Benz Developer API. In case the feign client returns a
 * {@link feign.FeignException} based on unexpected behavior of the API the error decoder wraps the responses
 * into exceptions which are easier to handle.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class MBDevApiErrorDecoder extends ErrorDecoder.Default {
    @Override
    public Exception decode(String methodKey, Response response) {
        Exception exception = super.decode(methodKey, response);
        switch (response.status()) {
            case 401:
                return new UnauthorizedException(getUnauthorizedMessage(methodKey, response, exception), exception);
            case 429:
                return new QuotaExceededException(getQuotaExceededMessage(methodKey, response, exception), exception);
            default:
                return new MBDevApiException(getDefaultMessage(methodKey, response, exception), exception);
        }
    }

    private static String getUnauthorizedMessage(String methodKey, Response response, Exception e) {
        return String.format("Api method call `%s` was apparently not authorized. %n%s",
                methodKey, formatResult(response, e));
    }

    private static String getQuotaExceededMessage(String methodKey, Response response, Exception e) {
        return String.format("Api method call `%s` was exceeding the quota limit. %n%s",
                methodKey, formatResult(response, e));
    }

    private static String getDefaultMessage(String methodKey, Response response, Exception e) {
        return String.format("An error occurred during api method call `%s`. %n%s",
                methodKey, formatResult(response, e));
    }

    static String formatResult(Response response, Exception e) {
        StringBuilder builder = new StringBuilder();

        if(response.request() != null) {
            builder.append("Request: " + response.request().method() + " - " + response.request().url() + "\n");
        }

        builder.append("Response Error: " + e.getMessage());
        return builder.toString();
    }
}
