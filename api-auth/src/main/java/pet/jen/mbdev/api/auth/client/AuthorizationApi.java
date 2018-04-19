package pet.jen.mbdev.api.auth.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * Split into different clients would have been possible to have it cleaner (or as clean as it can possibly get) but
 * the retrieveSession request needs a separate regular decoder and the consent request requires an individual error decoder.
 * Thus both of the calls are within one client having two different decoders (regular and error).
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public interface AuthorizationApi extends IAMApi {

    /**
     * Authorize request which will end up being redirected to a login form. The HTML content of the redirect is relevant
     * for further processing.
     *
     * @param clientId of the registered application
     * @param redirectUri from the developer console (should be `http://localhost`)
     * @param scope or scopes which should be used for authorization
     * @return plain HTML body as string containing relevant session data.
     */
    @RequestLine("GET " + BASE_PATH + "/authorize?response_type=code&client_id={clientId}&redirect_uri={redirectUri}&scope={scope}")
    String authorize(@Param("clientId") String clientId, @Param("redirectUri") String redirectUri, @Param("scope") String scope);

    /**
     * Performs a post request to grant a user's consent.
     *
     * Attention: this function has no return value on purpose since the flow requires it to fail in a
     * {@link feign.FeignException} which will be handled individually.
     *
     * @param action is usually 'Grant'
     * @param sessionId from the current session
     * @param sessionData from the current session
     */
    @RequestLine("POST " + BASE_PATH + "/authorize/consent")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    void postConsent(@Param("action") String action, @Param("sessionID") String sessionId, @Param("sessionData") String sessionData);
}
