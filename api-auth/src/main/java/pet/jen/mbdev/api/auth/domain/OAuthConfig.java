package pet.jen.mbdev.api.auth.domain;

import com.google.common.base.Strings;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Holds all information which is necessary for the OAuth flow.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
@Builder
public class OAuthConfig {

    // Mandatory
    @Getter
    @Builder.Default
    private String authorizationBaseUrl = "https://api.secure.mercedes-benz.com";

    // Mandatory
    @Getter
    @Builder.Default
    private String loginBaseUrl = "https://login.secure.mercedes-benz.com";

    // Mandatory
    @Getter
    private String clientId;

    // Mandatory
    @Getter
    private String clientSecret;

    // Mandatory
    @Getter
    @Builder.Default
    private String redirectUri = "http://localhost";

    // Mandatory
    private List<String> scopes;

    /**
     * If the required scopes for the application are not yet approved by the user this flag indicates whether they
     * are approved by default. In a "legal" nutshell: it explicitly disables a user's involvement into his scope management
     * and accepts everything which is upcoming.
     */
    @Getter
    @Builder.Default
    private boolean defaultApproveMissingScopes = true;

    @Getter
    @Builder.Default
    private boolean trustAllSslHosts = false;

    @Builder.Default
    private int tokenExpiryBuffer = 300;

    /**
     * Proof Key for Code Exchange mechanism (https://tools.ietf.org/html/rfc7636) enablement.
     * Attention: Client has to be explicitly setup to use it this way, client secret is not needed in this case.
     */
    @Getter
    @Builder.Default
    private boolean usePKCE = false;

    public boolean isValid() {
        boolean valid = !(Strings.isNullOrEmpty(authorizationBaseUrl)
                || Strings.isNullOrEmpty(loginBaseUrl)
                || Strings.isNullOrEmpty(clientId)
                || Strings.isNullOrEmpty(redirectUri)
                || scopes == null);
        if(!valid) {
            return false;
        }

        // the API only requires a secret when PKCE is not used
        if(!usePKCE) {
            valid = !Strings.isNullOrEmpty(clientSecret);
        }
        return valid;
    }

    /**
     * Joins the scopes array by separating them with a blank ` ` space.
     * @return formatted scopes for further usage on API level.
     */
    public String getScopes() {
        return StringUtils.collectionToDelimitedString(this.scopes, " ");
    }

    public long getTokenExpiryBuffer() {
        return this.tokenExpiryBuffer * 1000;
    }
}
