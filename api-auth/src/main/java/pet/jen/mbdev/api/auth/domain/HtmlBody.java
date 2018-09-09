package pet.jen.mbdev.api.auth.domain;


import com.google.common.base.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a HTML body which is mainly only needed to extract input field values as strings.
 * To ease the parsing the jerry library is used which is part of the jodd framework (https://jodd.org/).
 *
 * This could be replaced in the future with a more light weight parsing library.
 *
 * @author Jens Petersohn <me@jen.pet>
 */
class HtmlBody {

    private Document doc;

    HtmlBody(String html) {
        doc = Jsoup.parse(html);
    }

    public String extractInputFieldValueByName(String fieldName) {
        Elements fields = this.doc.select("input[name=" + fieldName+ "]");

        if(fields.size() != 1) {
            throw new IllegalArgumentException("Could not extract field `" + fieldName + "` since there were more than one" +
                    " or no fields at all within the given body.");
        }
        if(Strings.isNullOrEmpty(fields.attr("value"))) {
            throw new IllegalArgumentException("Could not extract field `" + fieldName + "` since attribute named value is not available.");
        }
        return fields.attr("value");
    }

    public boolean containsId(String id) {
        return !this.doc.select("#" + id).isEmpty();
    }

    public List<String> extractInputCheckboxNamesByFormId(String formId) {
        List<String> names = new ArrayList<>();
        for(Element checkbox : this.doc.select("#" + formId + " input[type=checkbox]")) {
            if(Strings.isNullOrEmpty(checkbox.attr("name"))) {
                throw new IllegalArgumentException("Retrieved checkbox element from form with id " + formId + " with empty name.");
            }
            names.add(checkbox.attr("name"));
        }
        return names;
    }
}
