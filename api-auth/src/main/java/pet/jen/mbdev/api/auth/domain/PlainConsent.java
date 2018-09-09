package pet.jen.mbdev.api.auth.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A consent which indicates that the authorization flow requires no user interaction to proceed.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
@Data
@NoArgsConstructor
public class PlainConsent extends BaseConsent {

    private String action;

    /**
     * Creates a new consent object containing the action which should be passed later in the process based
     * on a response body (html) received during the authorization flow.
     * @param responseBody which contains the relevant information.
     */
    public PlainConsent(String responseBody) {
        HtmlBody htmlBody = new HtmlBody(responseBody);
        this.setSessionID(htmlBody.extractInputFieldValueByName("sessionID"));
        this.setSessionData(htmlBody.extractInputFieldValueByName("sessionData"));
        this.setAction(htmlBody.extractInputFieldValueByName("action"));
    }
}
