package pet.jen.mbdev.api.auth.domain;

import com.google.common.base.Strings;
import lombok.Builder;
import lombok.Getter;

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

    @Builder.Default
    private int tokenExpiryBuffer = 300;

    public boolean isValid() {
        return !(Strings.isNullOrEmpty(authorizationBaseUrl)
                || Strings.isNullOrEmpty(loginBaseUrl)
                || Strings.isNullOrEmpty(clientId)
                || Strings.isNullOrEmpty(clientSecret)
                || Strings.isNullOrEmpty(redirectUri)
                || scopes == null);
    }

    /**
     * Joins the scopes array by separating them with a blank ` ` space.
     * @return formatted scopes for further usage on API level.
     */
    public String getScopes() {
        return String.join(" ", this.scopes);
    }


    public long getTokenExpiryBuffer() {
        return this.tokenExpiryBuffer * 1000;
    }
}
