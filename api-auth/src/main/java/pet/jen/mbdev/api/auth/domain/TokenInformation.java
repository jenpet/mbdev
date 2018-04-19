package pet.jen.mbdev.api.auth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO representation of a successful token API endpoint response.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
@NoArgsConstructor
@Data
public class TokenInformation {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String scope;

    /**
     * @return the expiry in milliseconds for easier token expiry handling.
     */
    public long getExpiresIn() {
        return expiresIn * 1000;
    }
}
