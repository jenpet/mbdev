package pet.jen.mbdev.samples.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleConfig {
    private String username;
    private String password;
    @JsonIgnore
    private OAuthConfig oAuthConfig;
}
