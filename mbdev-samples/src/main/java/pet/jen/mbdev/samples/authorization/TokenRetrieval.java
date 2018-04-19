package pet.jen.mbdev.samples.authorization;

import pet.jen.mbdev.api.TokenProvider;
import pet.jen.mbdev.api.auth.AuthorizationFlowHandler;
import pet.jen.mbdev.samples.config.ConfigParser;
import pet.jen.mbdev.samples.config.SampleConfig;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

public class TokenRetrieval {
    public static void main(String args[]) throws Exception {
        trustAllHosts();
        SampleConfig config = ConfigParser.getConfig();
        AuthorizationFlowHandler handler = AuthorizationFlowHandler.setup(config.getOAuthConfig());
        TokenProvider tokenProvider = handler.authorize(config.getUsername(), config.getPassword());
        System.out.println("Received access token is : " + tokenProvider.getAccessToken());
    }

    // http://www.rgagnon.com/javadetails/java-fix-certificate-problem-in-HTTPS.html
    private static void trustAllHosts() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {  }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
}
