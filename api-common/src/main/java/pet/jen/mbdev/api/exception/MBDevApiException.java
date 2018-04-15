package pet.jen.mbdev.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Generic exception which encapsulates a {@link feign.FeignException} when thrown by
 * the according feign client.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
@AllArgsConstructor
public class MBDevApiException extends RuntimeException {
    @Getter
    private String message;
    @Getter
    private Exception cause;
}
