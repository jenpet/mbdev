package pet.jen.mbdev.api.auth.domain;

import lombok.Data;

/**
 * Holds all necessary information about a consent confirmation of a user.
 *
 * @author Jens Petersohn <me@jen.pet>
 * TODO: Inheritence?
 */
@Data
public abstract class BaseConsent {
    private String sessionID;
    private String sessionData;
}
