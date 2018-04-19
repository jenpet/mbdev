package pet.jen.mbdev.api.auth.domain;

import org.junit.Test;
import pet.jen.mbdev.api.auth.BaseAuthorizationTest;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SessionInformationTest extends BaseAuthorizationTest {

    @Test
    public void testFromResponse_whenAllNecessaryFieldsArePresent_shouldReturnLoginInformation() throws Exception {
        SessionInformation sessionInformation = SessionInformation.fromResponse(resourceAsString("__files/login/redirected_login_form.html"));
        assertThat(sessionInformation.getAppId()).isEqualTo("ONEAPI.PROD");
        assertThat(sessionInformation.getSessionID()).isEqualTo("070027d2-5bd3-4038-8903-e2007a2e14b1");
        assertThat(sessionInformation.getSessionData()).isEqualTo("eyJ0eXAiOiJKV1QiLCJhbGci...KfQ.zoINDytohLmYv1bTKABqnIu8wLS4pETEakcdnnkLF40");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromResponse_whenNecessaryFieldsAreMissing_shouldThrowIllegalArgumentException() throws Exception {
        SessionInformation.fromResponse("<html/>");
    }
}