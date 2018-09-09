package pet.jen.mbdev.api.auth.domain;

import com.google.common.io.BaseEncoding;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pet.jen.mbdev.api.auth.client.BaseClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Object is used to hold all information for the authentication process until we eventually receive an auth code
 * to retrieve the required OAuth tokens.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
@Data
public class Authorization implements BaseClient.CookieStorage {

    private String appId;
    private Session session = new Session();
    private List<String> cookies = new ArrayList<>();
    private BaseConsent consent;
    private String authCode;
    private String codeVerifier;
    private String codeChallenge;
    private List<String> userApprovedScopes = new ArrayList<>();

    public Authorization() {
        this.setCodeVerifier(createCodeVerifier());
        this.setCodeChallenge(createCodeChallenge(this.getCodeVerifier()));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Session {
        private String id;
        private String data;
    }

    public void storeSession(String responseBody) {
        HtmlBody htmlBody = new HtmlBody(responseBody);
        this.setAppId(htmlBody.extractInputFieldValueByName("app-id"));
        this.session.setData(htmlBody.extractInputFieldValueByName("sessionData"));
        this.session.setId(htmlBody.extractInputFieldValueByName("sessionID"));
    }

    @Override
    public void store(List<String> cookies) {
        this.setCookies(cookies);
    }

    @Override
    public List<String> load() {
        return this.getCookies();
    }

    public boolean requiresUserApproval() {
        return this.consent instanceof ScopeApprovalConsent;
    }

    /**
     * When setting a consent the session has also to be updated since retrieving / managing a consent changes
     * the session itself.
     * @param consent that has to be set
     */
    public void setConsent(BaseConsent consent) {
        this.consent = consent;
        this.getSession().setId(consent.getSessionID());
        this.getSession().setData(consent.getSessionData());
    }

    private static String createCodeVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return BaseEncoding.base64Url().omitPadding().encode(code);
    }

    private static String createCodeChallenge(String codeVerifier) {
        byte[] input = getUtf8Bytes(codeVerifier);
        byte[] signature = getSHA256(input);
        return BaseEncoding.base64Url().omitPadding().encode(signature);
    }

    private static byte[] getUtf8Bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] getSHA256(byte[] input) {
        byte[] signature;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input, 0, input.length);
            signature = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not create SHA-256 signature", e);
        }
        return signature;
    }
}
