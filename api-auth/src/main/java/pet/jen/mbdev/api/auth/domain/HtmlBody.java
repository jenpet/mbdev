package pet.jen.mbdev.api.auth.domain;

import jodd.jerry.Jerry;

/**
 * Represents a HTML body which is mainly only needed to extract input field values as strings.
 * To ease the parsing the jerry library is used which is part of the jodd framework (https://jodd.org/).
 *
 * This could be replaced in the future with a more light weight parsing library.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
class HtmlBody {

    private Jerry doc;

    HtmlBody(String html) {
        doc = Jerry.jerry(html);
    }

    public String extractInputFieldValue(String fieldName) {
        Jerry field = this.doc.$("input[name=" + fieldName+ "]");
        if(field.length() != 1) {
            throw new IllegalArgumentException("Could not extract field `" + fieldName + "` since there were more than one" +
                    " or no fields at all within the given body.");
        }
        if(field.attr("value") == null) {
            throw new IllegalArgumentException("Could not extract field `" + fieldName + "` since attribute named value is not available.");
        }
        return field.attr("value");
    }
}
