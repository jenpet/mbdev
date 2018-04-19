package pet.jen.mbdev.api;

/**
 * Common token provider interface which handles authentication and authorization tokens for interactions with the
 * APIs of https://developer.mercedes-benz.com/.
 *
 * The primary goal of `getAccessToken()` is the provision of a valid access token. Handling persistence, expiry times
 * and refreshing of tokens just-in-time is mandatory for this component.
 *
 * In case an external refresh is required `refreshTokens()` will trigger a forced token refresh.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public interface TokenProvider {
    /**
     * @return an always valid access token.
     */
    String getAccessToken();

    /**
     * Forces a refresh of all tokens.
     *
     * @return the newly refreshed access token.
     */
    String refreshTokens();
}
