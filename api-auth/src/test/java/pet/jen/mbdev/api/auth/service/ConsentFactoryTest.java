package pet.jen.mbdev.api.auth.service;

import org.junit.Test;
import pet.jen.mbdev.api.auth.BaseAuthorizationTest;
import pet.jen.mbdev.api.auth.domain.PlainConsent;
import pet.jen.mbdev.api.auth.domain.ScopeApprovalConsent;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ConsentFactoryTest extends BaseAuthorizationTest{
    @Test
    public void testFromResponse_whenResponseDoesNotRequireUserApproval_shouldReturnPlainConsent() throws IOException {
        assertThat(ConsentFactory.fromResponse(resourceAsString("__files/login/simple_consent_form.html"))).isInstanceOf(PlainConsent.class);
    }

    @Test
    public void testFromResponse_whenResponseDoesRequiresUserApproval_shouldReturnScopeApprovalConsent() throws IOException {
        assertThat(ConsentFactory.fromResponse(resourceAsString("__files/login/user_consent_form.html"))).isInstanceOf(ScopeApprovalConsent.class);
    }
}