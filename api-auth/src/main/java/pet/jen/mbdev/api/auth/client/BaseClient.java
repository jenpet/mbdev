package pet.jen.mbdev.api.auth.client;

import com.google.common.net.HttpHeaders;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

/**
 * A basic client required for different kind of interactions with certain endpoints. It abstracts the handling of
 * errors, the return of response bodies as strings and (if applicable) cookie handling.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class BaseClient {
    /**
     * Performs a request using a certain client and handling the possible IOException.
     *
     * @param client which has to be used for the request
     * @param request that has to be executed itself
     * @return response after successful execution or null in case of an error
     */
    Response execute(OkHttpClient client, Request request, CookieStorage cookieStorage) {
        try {
            Response response = client.newCall(request).execute();
            if(cookieStorage!= null && !response.headers(HttpHeaders.SET_COOKIE).isEmpty()) {
                cookieStorage.store(response.headers(HttpHeaders.SET_COOKIE));
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    Response execute(OkHttpClient client, Request request) {
        return execute(client, request, null);
    }

    /**
     * Performs a request and returns it's body string representation.
     * In case any error occurs an empty string will be returned.
     *
     * @param client which has to be used for the request
     * @param request that has to be executed itself
     * @return response body as string after successful execution or empty string in case of an error
     */
    String executeAndGetBody(OkHttpClient client, Request request, CookieStorage cookieStorage) {
        try {
            Response response = execute(client, request, cookieStorage);
            if(response == null || response.body() == null) {
                return "";
            }
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    String executeAndGetBody(OkHttpClient client, Request request) {
        return executeAndGetBody(client, request, null);
    }

    /**
     * Storage which is used to store and load cookies which are returned by different requested targets.
     */
    public interface CookieStorage {
        void store(List<String> cookies);
        List<String> load();
    }
}
