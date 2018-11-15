package pet.jen.mbdev.api.auth.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import pet.jen.mbdev.api.auth.domain.TokenInformation;

/**
 * Feign interface to retrieve token information (access and refresh token).
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public interface TokenApi extends IAMApi {

    /**
     * Used for initial retrieval of token information. No tokens are currently present. It requires a valid
     * authorization code to succeed.
     * @param grantType will always be set to `authorization_code`
     * @param authorizationCode individual code for the session
     * @param clientId
     * @param codeVerifier
     * @param redirectUri has to be `http://localhost`
     * @return response object providing all token in case of success
     */
    @RequestLine("POST " + BASE_PATH + "/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    TokenInformation retrieve(
            @Param("grant_type") String grantType,
            @Param("code") String authorizationCode,
            @Param("client_id") String clientId,
            @Param("code_verifier") String codeVerifier,
            @Param("redirect_uri") String redirectUri);

    /**
     * Used to refresh tokens once tokens were received. Requires a valid refresh token.
     * @param grantType will always be set to `refresh_token`
     * @param refreshToken which was received in a previous request
     * @param clientId
     * @return response object providing all token in case of success
     */
    @RequestLine("POST " + BASE_PATH + "/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    TokenInformation refresh(
            @Param("grant_type") String grantType,
            @Param("refresh_token") String refreshToken,
            @Param("client_id")String clientId);
}
