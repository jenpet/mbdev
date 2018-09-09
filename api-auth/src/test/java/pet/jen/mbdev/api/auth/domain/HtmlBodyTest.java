package pet.jen.mbdev.api.auth.domain;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HtmlBodyTest {

    @Test
    public void testExtractInputFieldValue_whenFieldIsPresent_shouldReturnValue() {
        HtmlBody htmlBody = new HtmlBody("<html><input type=\"hidden\" name=\"test\" value=\"val\"></html>");
        assertThat(htmlBody.extractInputFieldValueByName("test")).isEqualTo("val");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractInputFieldValue_whenFieldNotPresent_shouldThrowException() {
        HtmlBody htmlBody = new HtmlBody("<html></html>");
        htmlBody.extractInputFieldValueByName("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractInputFieldValue_whenFieldValueNotPresent_shouldThrowException() {
        HtmlBody htmlBody = new HtmlBody("<html><input type=\"hidden\" name=\"test\"></html>");
        htmlBody.extractInputFieldValueByName("test");
    }

    @Test
    public void testContainsId_shouldReturnAccordingly() {
        assertThat(new HtmlBody("<html><input id=\"frmConsent\"/></html>").containsId("frmConsent")).isTrue();
        assertThat(new HtmlBody("<html></html>").containsId("frmConsent")).isFalse();
    }

    @Test
    public void testExtractInputCheckboxNamesByFormId_whenPresent_shouldReturnList() {
        HtmlBody htmlBody = new HtmlBody("<html><form id=\"frmConsent\">" +
                "<input type=\"checkbox\"/ name=\"scope-1\" id=\"id-1\"/>" +
                "<input type=\"checkbox\"/ name=\"scope-2\" id=\"id-2\"/>" +
                "</form></html>");
        assertThat(htmlBody.extractInputCheckboxNamesByFormId("frmConsent")).contains("scope-1", "scope-2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractInputCheckboxNamesByFormId_whenCheckboxMissesName_shouldThrowException() {
        HtmlBody htmlBody = new HtmlBody("<html><form id=\"frmConsent\">" +
                "<input type=\"checkbox\"/ id=\"id-1\"/>" +
                "</form></html>");
        htmlBody.extractInputCheckboxNamesByFormId("frmConsent");
    }

}