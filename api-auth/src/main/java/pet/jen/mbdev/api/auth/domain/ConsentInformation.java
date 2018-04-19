package pet.jen.mbdev.api.auth.domain;

import lombok.Data;

/**
 * Holds all necessary information about a consent confirmation of a user.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
@Data
public class ConsentInformation {
    private String sessionID;
    private String sessionData;
    private String action;

    /**
     * @param responseBody containing all relevant information for the consent confirmation.
     * @return object providing extracted consent information.
     */
    public static ConsentInformation fromResponse(String responseBody) {
        ConsentInformation consentInformation = new ConsentInformation();
        HtmlBody htmlBody = new HtmlBody(responseBody);
        consentInformation.setAction(htmlBody.extractInputFieldValue("action"));
        consentInformation.setSessionID(htmlBody.extractInputFieldValue("sessionID"));
        consentInformation.setSessionData(htmlBody.extractInputFieldValue("sessionData"));
        return consentInformation;
    }
}
