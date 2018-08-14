package pet.jen.mbdev.samples.authorization;

import pet.jen.mbdev.api.TokenProvider;
import pet.jen.mbdev.api.auth.AuthorizationFlowHandler;
import pet.jen.mbdev.samples.SampleBase;
import pet.jen.mbdev.samples.config.ConfigParser;
import pet.jen.mbdev.samples.config.SampleConfig;

public class TokenRetrievalSimple extends SampleBase {
    public static void main(String args[]) throws Exception {
        trustAllHosts();
        SampleConfig config = ConfigParser.getConfig();
        AuthorizationFlowHandler handler = AuthorizationFlowHandler.setup(config.getOAuthConfig());
        TokenProvider tokenProvider = handler.authorize(config.getUsername(), config.getPassword());
        System.out.println("Received access token with username and password is : " + tokenProvider.getAccessToken());
    }
}
