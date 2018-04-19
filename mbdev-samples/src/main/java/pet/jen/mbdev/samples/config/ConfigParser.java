package pet.jen.mbdev.samples.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;

import java.io.IOException;
import java.util.Arrays;

public class ConfigParser {
    private static ObjectMapper mapper = new ObjectMapper();

    /*
     * Might appear ugly but OAuthConfig attributes should stay private. Thus the SampleConfig class
     * embeds an OAuthConfig POJO.
     */
    public static SampleConfig getConfig()  throws Exception {
        String jsonConfig = Resources.toString(Resources.getResource("mbdev-config.json"), Charsets.UTF_8);
        SampleConfig config = mapper.readValue(jsonConfig, SampleConfig.class);
        config.setOAuthConfig(getOAuthConfig(jsonConfig));
        return config;
    }

    private static OAuthConfig getOAuthConfig(String jsonConfig) throws IOException {
        JsonNode tree = mapper.readTree(jsonConfig);
        OAuthConfig config = OAuthConfig.builder()
                .clientId(tree.get("clientId").asText())
                .clientSecret(tree.get("clientSecret").asText())
                .scopes(Arrays.asList(tree.get("scopes").asText().split(" "))).build();
        if(!config.isValid()) {
            throw new IllegalArgumentException("Provided config file mbdev-config.json could not get parsed into valid OAuth config file.");
        }
        return config;
    }
}
