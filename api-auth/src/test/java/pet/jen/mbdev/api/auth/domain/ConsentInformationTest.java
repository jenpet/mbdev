package pet.jen.mbdev.api.auth.domain;

import org.junit.Test;
import pet.jen.mbdev.api.auth.BaseAuthorizationTest;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class ConsentInformationTest extends BaseAuthorizationTest {

    @Test
    public void testFromResponse_whenAllNecessaryFieldsArePresent_shouldReturnConsentInformation() throws Exception {
        ConsentInformation consentInformation = ConsentInformation.fromResponse(resourceAsString("__files/login/consent_form.html"));
        assertThat(consentInformation.getAction()).isEqualTo("Grant");
        assertThat(consentInformation.getSessionID()).isEqualTo("849cc52b-9ca4-4ec1-b1c6-c26ae9e398ed");
        assertThat(consentInformation.getSessionData()).isEqualTo("eyJjdHkiO..._t7wrqQ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromResponse_whenNecessaryFieldsAreMissing_shouldThrowIllegalArgumentException() throws Exception {
        ConsentInformation.fromResponse("<html/>");
    }
}