package pet.jen.mbdev.api.auth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts the authorization code from a given url string.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
class AuthCodeExtractor {
    private static final Pattern bearerCodePattern = Pattern.compile("(.*?code=)(.*)");

    /**
     * Extracts the bearer token from a given url by using the defined regexp matching above.
     *
     * Example url:
     * <pre>
     *  http://localhost?code=0e4181d9-09f2-4dfd-89d9-b1eac47459cX
     * </pre>
     *
     * @param url which contain the authorization code
     * @return extracted authorization code
     */
    public static String extract(String url) {
        Matcher matcher = bearerCodePattern.matcher(url);
        return matcher.matches() ? matcher.group(2) : null;
    }
}
