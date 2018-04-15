package pet.jen.mbdev.api;

import feign.Feign;
import feign.jackson.JacksonDecoder;

/**
 * Normalizes the creation of any feign client which calls the Mercedes Benz Developer API(s) by adding the same
 * settings and configurations for a client.
 *
 * If used without a base url (regular case) it points straight to the production environment.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class MBDevApiFactory {

    private static String BASE_URL = "https://api.mercedes-benz.com";

    public static <T> T create(Class<T> apiType, String basePath) {
        return create(apiType, BASE_URL, basePath);
    }

    public static <T> T create(Class<T> apiType, String baseUrl, String basePath) {
        return Feign.builder()
                .decoder(new JacksonDecoder())
                .errorDecoder(new MBDevApiErrorDecoder())
                .target(apiType, baseUrl + basePath);
    }
}
