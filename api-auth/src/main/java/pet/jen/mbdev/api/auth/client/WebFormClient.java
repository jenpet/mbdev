package pet.jen.mbdev.api.auth.client;

import com.google.common.net.HttpHeaders;
import okhttp3.*;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;

import java.util.List;

/**
 * Handles all of the calls targeting HTML forms returned / provided by the Mercedes me portal.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class WebFormClient extends BaseClient {

    private OkHttpClient client;

    private OAuthConfig config;


    public WebFormClient(OAuthConfig config) {
        this(config, createClient(config));
    }

    WebFormClient(OAuthConfig config, OkHttpClient client) {
        this.config = config;
        this.client = client;
    }

    /**
     * Performs a HTTP POST request as a form request mimicking a user who actually enters his credentials.
     * Cookies will be stored in the cookie storage for later usage.
     *
     * @param appId received from previous redirects from the {@link AuthorizationApiClient}
     * @param sessionId of the current authentication attempt
     * @param sessionData of the current authentication attempt
     * @param username of the user
     * @param password of the user
     * @param cookieStorage where cookies are stored after login to save a step in case consent requires user approval
     * @return HTML form for user consent in case of success otherwise an empty string
     */
    public String login(String appId, String sessionId, String sessionData, String username, String password, CookieStorage cookieStorage) {
        RequestBody body = createLoginFormBody(appId, sessionId, sessionData, username, password);
        Request request = new Request.Builder()
                .url(getLoginUrl())
                .post(body)
                .build();
        return executeAndGetBody(client, request, cookieStorage);
    }

    /**
     * Performs a HTTP POST request as a form request mimicking a user who approves missing scopes to the client application.
     * @param appId received from previous redirects from the {@link AuthorizationApiClient}
     * @param sessionId of the current authentication attempt
     * @param sessionData of the current authentication attempt
     * @param approvedScopes scopes which should be approved when calling the API
     * @param cookieStorage used to retrieve the cookies from the session to avoid getting redirected to another screen
     * @return
     */
    public String consent(String appId, String sessionId, String sessionData, List<String> approvedScopes, CookieStorage cookieStorage) {
        RequestBody body = createConsentFormBody(appId, sessionId, sessionData, approvedScopes);
        Request.Builder builder = new Request.Builder()
                .url(getConsentUrl())
                .post(body);
        Request request = addCookies(builder, cookieStorage.load()).build();
        return executeAndGetBody(client, request);
    }

    private HttpUrl getLoginUrl() {
        return HttpUrl.get(config.getLoginBaseUrl() + "/wl/login");
    }

    private FormBody createLoginFormBody(String appId, String sessionId, String sessionData, String username, String password) {
        return new FormBody.Builder()
                .add("app-id", appId)
                .add("sessionID", sessionId)
                .add("sessionData", sessionData)
                .add("username", username)
                .add("password", password).build();
    }

    private HttpUrl getConsentUrl() {
        return HttpUrl.get(config.getLoginBaseUrl() + "/wl/consent");
    }

    private FormBody createConsentFormBody(String appId, String sessionId, String sessionData, List<String> approvedScopes) {
        FormBody.Builder builder = new FormBody.Builder()
                .add("app-id", appId)
                .add("sessionID", sessionId)
                .add("sessionData", sessionData);
        // adding scopes which are approved
        for(String scope : approvedScopes) {
            builder.add(scope, "on");
        }
        return builder.build();
    }

    /**
     * Creates a client for all web form interactions. This client always sets the Content-Type to `application/x-www-form-urlencoded; charset=utf-8`
     * for every request using an interceptor.
     * @param config required to build a base client
     * @return basic client for all HTTP form requests which have to be made.
     */
    private static OkHttpClient createClient(OAuthConfig config) {
        OkHttpClient.Builder builder = BaseClientBuilder.get(config)
                .addInterceptor(BaseClientBuilder.createFormHeaderInterceptor());
        return builder.build();
    }

    private Request.Builder addCookies(Request.Builder builder, List<String> cookies) {
        for(String cookie : cookies) {
            builder.addHeader(HttpHeaders.COOKIE, cookie);
        }
        return builder;
    }
}
