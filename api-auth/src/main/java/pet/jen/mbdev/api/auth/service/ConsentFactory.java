package pet.jen.mbdev.api.auth.service;

import pet.jen.mbdev.api.auth.domain.BaseConsent;
import pet.jen.mbdev.api.auth.domain.PlainConsent;
import pet.jen.mbdev.api.auth.domain.ScopeApprovalConsent;

public class ConsentFactory {

    public static BaseConsent fromResponse(String responseBody) {
        if(containsMissingScopesForm(responseBody)) {
            return new ScopeApprovalConsent(responseBody);
        }

        return new PlainConsent(responseBody);
    }

    private static boolean containsMissingScopesForm(String responseBody) {
        return responseBody.contains(ScopeApprovalConsent.SCOPES_FORM_ID);
    }
}
