package pet.jen.mbdev.api.auth.exception;

import lombok.Getter;

/**
 * Helps "violating" the redirection to localhost. Since a non-followable redirect will end up in feign's error decoder
 * this exception embeds the authorization code and allows to access it later from a caller's perspective.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class AuthCodeException extends RuntimeException {
    @Getter
    private String authorizationCode;

    public AuthCodeException(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }
}