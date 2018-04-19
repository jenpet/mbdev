package pet.jen.mbdev.api.auth.domain;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HtmlBodyTest {

    @Test
    public void testExtractInputFieldValue_whenFieldIsPresent_shouldReturnValue() {
        HtmlBody htmlBody = new HtmlBody("<html><input type=\"hidden\" name=\"test\" value=\"val\"></html>");
        assertThat(htmlBody.extractInputFieldValue("test")).isEqualTo("val");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractInputFieldValue_whenFieldNotPresent_shouldThrowException() {
        HtmlBody htmlBody = new HtmlBody("<html></html>");
        htmlBody.extractInputFieldValue("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractInputFieldValue_whenFieldValueNotPresent_shouldThrowException() {
        HtmlBody htmlBody = new HtmlBody("<html><input type=\"hidden\" name=\"test\"></html>");
        htmlBody.extractInputFieldValue("test");
    }

}