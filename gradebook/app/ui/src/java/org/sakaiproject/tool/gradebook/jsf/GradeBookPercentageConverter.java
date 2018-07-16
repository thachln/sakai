/**
 * 
 */
package org.sakaiproject.tool.gradebook.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.NumberConverter;

import org.sakaiproject.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hiepnhse61627
 * The standard JSF number formatters only round values. We generally need
 * them truncated.
 * This converter truncates the input value (probably a double) to two
 * decimal places, and then returns it as an integer percentage.
 */
public class GradeBookPercentageConverter extends NumberConverter {
    private static final Logger log = LoggerFactory.getLogger(GradeBookPercentageConverter.class);

    public GradeBookPercentageConverter() {
        setType("percent");
        setIntegerOnly(true);
        ResourceLoader rl = new ResourceLoader();
        setLocale(rl.getLocale());
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (log.isDebugEnabled()) log.debug("getAsString(" + context + ", " + component + ", " + value + ")");

        String formattedScore = "";
        if (value == null) {
            formattedScore = FacesUtil.getLocalizedString("score_null_placeholder");
        } else {
            if (value instanceof Number) {
                // Truncate to 2 decimal places.
                double doubleValue = new Double(FacesUtil.getRoundDown(((Number)value).doubleValue(), 2));
                long result = Math.round(doubleValue);
                formattedScore = "" + result + " %";
            }
        }

        return formattedScore;
    }
}
