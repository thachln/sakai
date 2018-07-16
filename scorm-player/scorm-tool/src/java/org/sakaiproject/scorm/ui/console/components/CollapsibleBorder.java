package org.sakaiproject.scorm.ui.console.components;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;

/**
 * Wicket Border that can be opened and closed 
 * 
 * @author VuDQ4
 */
public class CollapsibleBorder extends Border {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct of Border
	 * 
	 * @param id Border id
	 * @param title Display title of Link
	 */
	public CollapsibleBorder(String id, String title) {
		super(id, new Model<Boolean>());
		setCollapsed(true);

		WebMarkupContainer body = new WebMarkupContainer("body") {
			/**  . */
			private static final long serialVersionUID = 1L;

			protected void onConfigure() {
				setVisible(!isCollapsed());
			}
		};
		add(body);
		body.add(getBodyContainer());

		Link<CollapsibleBorder> toggle = new Link<CollapsibleBorder>("toggle") {
			/**  . */
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setCollapsed(!isCollapsed());
			}
		};
		add(toggle);

		toggle.add(new Label("caption", title));
	}

	public void setCollapsed(boolean collapsed) {
		setDefaultModelObject(collapsed);
	}

	public boolean isCollapsed() {
		return Boolean.TRUE.equals(getDefaultModelObject());
	}

}
