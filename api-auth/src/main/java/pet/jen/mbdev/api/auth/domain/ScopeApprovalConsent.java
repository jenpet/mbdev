package pet.jen.mbdev.api.auth.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A consent which indicates that the authorization flow requires user interaction to proceed.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
@Data
@NoArgsConstructor
public class ScopeApprovalConsent extends BaseConsent {

    public static final String SCOPES_FORM_ID = "frmConsent";

    private List<String> missingScopes = new ArrayList<>();

    /**
     * Creates a new consent object containing missing scopes of a user for the requested application based
     * on a response body (html) received during the authorization flow.
     * @param responseBody which contains the relevant information.
     */
    public ScopeApprovalConsent(String responseBody) {
        HtmlBody htmlBody = new HtmlBody(responseBody);
        this.setSessionID(htmlBody.extractInputFieldValueByName("sessionID"));
        this.setSessionData(htmlBody.extractInputFieldValueByName("sessionData"));
        this.setMissingScopes(extractMissingScopes(htmlBody));
    }

    /**
     * Extracts all names of checkboxes within the scopes form.
     * @param htmlBody which contains the form
     * @return list of names which might be empty
     */
    private static List<String> extractMissingScopes(HtmlBody htmlBody) {
        return htmlBody.extractInputCheckboxNamesByFormId(SCOPES_FORM_ID);
    }
}
