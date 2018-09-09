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
        String jsonConfig = Resources.toString(Resources.getResource(getConfigFileName()), Charsets.UTF_8);
        SampleConfig config = mapper.readValue(jsonConfig, SampleConfig.class);
        config.setOAuthConfig(getOAuthConfig(jsonConfig));
        return config;
    }

    private static String getConfigFileName() {
        String suffix = System.getProperty("configSuffix", "");
        StringBuilder builder = new StringBuilder();
        builder.append("mbdev-config");
        if(suffix != "") {
            builder.append("-");
            builder.append(suffix);
        }
        return builder.append(".json").toString();
    }

    private static OAuthConfig getOAuthConfig(String jsonConfig) throws IOException {
        JsonNode tree = mapper.readTree(jsonConfig);
        OAuthConfig.OAuthConfigBuilder builder = OAuthConfig.builder();
        if(tree.has("clientId")) {
            builder.clientId(tree.get("clientId").asText());
        }
        if(tree.has("clientSecret")) {
            builder.clientSecret(tree.get("clientSecret").asText());
        }
        if(tree.has("authorizationBaseUrl")) {
            builder.authorizationBaseUrl(tree.get("authorizationBaseUrl").asText());
        }
        if(tree.has("loginBaseUrl")) {
            builder.loginBaseUrl(tree.get("loginBaseUrl").asText());
        }
        if(tree.has("usePKCE")) {
            builder.usePKCE(tree.get("usePKCE").asBoolean());
        }
        if(tree.has("trustAllSslHosts")) {
            builder.trustAllSslHosts(tree.get("trustAllSslHosts").asBoolean());
        }
        if(tree.has("scopes")) {
            builder.scopes(Arrays.asList(tree.get("scopes").asText().split(" ")));
        }
        OAuthConfig config = builder.build();

        if(!config.isValid()) {
            throw new IllegalArgumentException("Provided config file mbdev-config.json could not get parsed into valid OAuth config file.");
        }
        return config;
    }
}
