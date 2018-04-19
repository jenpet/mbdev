package pet.jen.mbdev.api.auth.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * Feign interface to perform a simple post login.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public interface LoginApi {
    @RequestLine("POST /wl/login")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    String login(@Param("app-id") String appId,
                 @Param("sessionID") String sessionId,
                 @Param("sessionData") String sessionData,
                 @Param("username") String username,
                 @Param("password") String password);
}
