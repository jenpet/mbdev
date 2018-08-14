package pet.jen.mbdev.api.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * POJO representation of a successful token API endpoint response.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenInformation implements Serializable {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String scope;

    // not returned by the api but still required for the token provider
    private long timestamp;

    /**
     * @return the expiry in milliseconds for easier token expiry handling.
     */
    public long getExpiresIn() {
        return expiresIn * 1000;
    }

    public boolean isValid() {
        return !Strings.isNullOrEmpty(accessToken)
                && !Strings.isNullOrEmpty(tokenType)
                && !Strings.isNullOrEmpty(refreshToken)
                && !Strings.isNullOrEmpty(scope)
                && timestamp > 0L
                && expiresIn >= 0;
    }
}
