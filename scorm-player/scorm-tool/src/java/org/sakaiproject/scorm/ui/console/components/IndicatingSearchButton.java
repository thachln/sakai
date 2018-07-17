package org.sakaiproject.scorm.ui.console.components;

import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.markup.html.form.Button;

public class IndicatingSearchButton extends Button implements IAjaxIndicatorAware {

    private AjaxIndicatorAppender indicatorAppender = new AjaxIndicatorAppender();
    
    public IndicatingSearchButton(String id) {
        super(id);
        add(indicatorAppender);
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        return indicatorAppender.getMarkupId();
    }
    
}
