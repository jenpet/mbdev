package pet.jen.mbdev.api.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds all necessary information about a session required to authenticate the user targeting a special session.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionInformation {
    private String sessionID;
    private String sessionData;
    private String appId;

    /**
     * @param responseBody containing all relevant information for the session.
     * @return object providing extracted session information.
     */
    public static SessionInformation fromResponse(String responseBody) {
        HtmlBody htmlBody = new HtmlBody(responseBody);
        SessionInformation sessionInformation = new SessionInformation();
        sessionInformation.setAppId(htmlBody.extractInputFieldValue("app-id"));
        sessionInformation.setSessionID(htmlBody.extractInputFieldValue("sessionID"));
        sessionInformation.setSessionData(htmlBody.extractInputFieldValue("sessionData"));
        return sessionInformation;
    }
}
