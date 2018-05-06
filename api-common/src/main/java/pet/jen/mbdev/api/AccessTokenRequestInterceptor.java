package pet.jen.mbdev.api;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import pet.jen.mbdev.api.exception.UnauthorizedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Request interceptor for feign clients which adds an OAuth authorization bearer header to the current existing ones
 * for API access.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
class AccessTokenRequestInterceptor implements RequestInterceptor {

    private TokenProvider tokenProvider;

    AccessTokenRequestInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void apply(RequestTemplate template) {
        try {
            Map<String, Collection<String>> headers = new HashMap<>(template.headers());
            Collection<String> authHeaders = headers.get("Authorization");
            if(authHeaders == null) {
                authHeaders = new ArrayList<>();
            }
            authHeaders.add("Bearer " + this.tokenProvider.getAccessToken());
            headers.put("Authorization", authHeaders);
            template.headers(headers);
        } catch (Exception e) {
            throw new UnauthorizedException("Could not retrieve access token from token provider.", e);
        }
    }
}
