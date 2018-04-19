package pet.jen.mbdev.api.auth;

import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import pet.jen.mbdev.api.auth.exception.AuthCodeException;

import java.util.List;

/**
 * After a successful login the http client gets redirected to the provided url from the OAuth flow.
 * To have this error decoder working the received location header should not be followed to extract the
 * authorization code. Thus a valid location which accepts the redirect after a consent post request will brake
 * the authentication flow.
 *
 * Easiest possibility is to have a valid redirect uri at the mercedes developer console with `http://localhost`.
 * If a valid redirect should be used the implementation of this module has to change.
 *
 * Example redirect (which contains the code)
 * <pre>
 *  http://localhost?code=0e4181d9-09f2-4dfd-89d9-b1eac47459cX
 * </pre>
 *
 * @author Jens Petersohn <me@jen.pet>
 */
class ConsentRedirectDecoder extends ErrorDecoder.Default {

    @Override
    public Exception decode(String methodKey, Response response) {
        if(isResponseValid(response)) {
            List locations = (List) response.headers().get("Location");
            return new AuthCodeException(AuthCodeExtractor.extract((String) locations.get(0)));
        }
        return super.decode(methodKey, response);
    }

    private boolean isResponseValid(Response response) {
        return isRequestTargetingConsent(response.request())
                && response.status() == 302 && response.headers().containsKey("location");
    }

    /**
     * Checks whether the request was targeting the consent API.
     * @param request which was targeted.
     */
    private boolean isRequestTargetingConsent(Request request) {
        return request != null && request.url().contains("/authorize/consent");
    }
}
