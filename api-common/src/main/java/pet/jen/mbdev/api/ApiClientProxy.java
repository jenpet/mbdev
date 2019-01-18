package pet.jen.mbdev.api;

import feign.RetryableException;
import pet.jen.mbdev.api.exception.MBDevApiException;
import pet.jen.mbdev.api.exception.UnauthorizedException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an object, preferably a feign client to provide an automatic token refresh mechanism in case
 * access tokens are invalid.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
public class ApiClientProxy implements InvocationHandler {

    private Object target;
    private TokenProvider tokenProvider;

    // attempts which should be performed
    private static final int TOKEN_REFRESH_TRY = 1;

    final Map<String, Method> targetMethods = new HashMap<>();

    public ApiClientProxy(Object target, TokenProvider tokenProvider) {
        this.target = target;
        this.tokenProvider = tokenProvider;

        for(Method method: this.target.getClass().getDeclaredMethods()) {
            this.targetMethods.put(method.getName(), method);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return this.invoke(method, args, 0);
    }

    /**
     * Invokes the target method and handles {@link MBDevApiException}s. These exceptions will be
     * unwrapped since they are unchecked by default if the feign interfaces is not written accordingly.
     *
     * A {@link UnauthorizedException} will cause the proxy to retry the call with refreshed tokens within the
     * provided {@link TokenProvider}. If the retry count is reached the exception will just be rethrown.
     *
     * All other exceptions which derive from {@link MBDevApiException} will be thrown plain.
     *
     * @param method which should be called on the target
     * @param args which should be passed
     * @param refreshes amount of refreshes required for {@link UnauthorizedException} handling
     * @return object received from the target
     * @throws Throwable inherited from {@link Object}.invoke()
     */
    private Object invoke(Method method, Object[] args, int refreshes) throws Throwable {
        try {
            return targetMethods.get(method.getName()).invoke(target, args);
        } catch (InvocationTargetException e) {
            // unwrap the actual exception and retry if applicable
            if (e.getCause() instanceof UnauthorizedException && refreshes < TOKEN_REFRESH_TRY) {
                // refresh the token so that another request with new tokens can be made
                this.tokenProvider.refreshTokens();
                return invoke(method, args, ++refreshes);
            } else if (e.getCause() instanceof MBDevApiException) {
                throw e.getCause();
            } else if (e.getCause() instanceof RetryableException) {
                throw new MBDevApiException("Retryable error while requesting MBDevApi", (RetryableException) e.getCause());
            }
            throw e.getCause();
        }
    }
}
