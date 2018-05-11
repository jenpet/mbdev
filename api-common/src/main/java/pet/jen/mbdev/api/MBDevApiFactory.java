package pet.jen.mbdev.api;

import feign.Feign;
import feign.jackson.JacksonDecoder;

import java.lang.reflect.Proxy;

/**
 * Normalizes the creation of any feign client which calls the Mercedes Benz Developer API(s) by adding the same
 * settings and configurations for a client. The created clients are wrapped into a {@link ApiClientProxy} which
 * handles authorization issues.
 *
 * If used without a base url (regular case) it points straight to the production environment.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class MBDevApiFactory {

    private static String BASE_URL = "https://api.mercedes-benz.com";

    public static <T> T create(Class<T> apiType, String basePath, TokenProvider tokenProvider) {
        return create(apiType, BASE_URL, basePath, tokenProvider);
    }

    public static <T> T create(Class<T> apiType, String baseUrl, String basePath, TokenProvider tokenProvider) {
        T client = Feign.builder()
                .decoder(new JacksonDecoder())
                .errorDecoder(new MBDevApiErrorDecoder())
                .requestInterceptor(new AccessTokenRequestInterceptor(tokenProvider))
                .target(apiType, baseUrl + basePath);
        return wrapClient(apiType, client, tokenProvider);
    }

    /**
     * Wraps a feign client into a proxy class which handles the potential invalidity of tokens gracefully.
     *
     * @param apiType class type of the target class
     * @param feignClient target object itself
     * @param tokenProvider token provider required to handle authorization issues
     * @param <T> target class type
     * @return wrapped feign client
     */
    private static <T> T wrapClient(Class<T> apiType, Object feignClient, TokenProvider tokenProvider) {
        return (T) Proxy.newProxyInstance(
                MBDevApiFactory.class.getClassLoader(),
                new Class[] {apiType},
                new ApiClientProxy(feignClient, tokenProvider));
    }
}
