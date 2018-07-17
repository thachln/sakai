package org.sakaiproject.scorm.ui.console.components;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.sakaiproject.scorm.ui.console.pages.PackageListPage;
import org.sakaiproject.wicket.markup.html.repeater.data.table.BasicDataTable;

/**
 * Wicket panel that can be opened and closed
 *
 * @author VuDQ4
 */
public abstract class CollapsiblePanel extends Panel {

	/**  . */
	private static final long serialVersionUID = 1L;
	// private ResourceReference closed = new ResourceReference(CollapsiblePanel.class, "bullet_toggle_plus.png");
	// private ResourceReference open = new ResourceReference(CollapsiblePanel.class, "bullet_toggle_minus.png");
	private static final ResourceReference OPEN_ICON = new ResourceReference(PackageListPage.class, "res/dir_closedplus.gif");
	private static final ResourceReference CLOSE_ICON = new ResourceReference(PackageListPage.class, "res/dir_openminus.gif");
    private boolean visible = false;
    protected Panel innerPanel;

    /**
     * Construct the panel
     *
     * @param id Panel ID
     * @param titleModel Model used to get the panel title
     * @param defaultOpen Is the default state open
     */
    public CollapsiblePanel(String id, String titleModel, boolean defaultOpen){
        super(id);
        this.visible = defaultOpen;
        
        innerPanel = getInnerPanel("innerPanel");
		innerPanel.setVisible(visible);
		innerPanel.setOutputMarkupId(true);
		innerPanel.setOutputMarkupPlaceholderTag(true);
		add(innerPanel);

		final Image showHideImage = new Image("showHideIcon"){
			/**  . */
			private static final long serialVersionUID = 1L;

			@Override
			public ResourceReference getImageResourceReference(){
				return visible ? CLOSE_ICON : OPEN_ICON;
			}
		};
		showHideImage.setOutputMarkupId(true);
		IndicatingAjaxLink showHideLink = new IndicatingAjaxLink("showHideLink"){
			
			/**  . */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				visible = !visible;
				innerPanel.setVisible(visible);
				target.addComponent(innerPanel);
				target.addComponent(showHideImage);
			}

		};
		
		showHideLink.add(showHideImage);
        add(new Label("titlePanel", titleModel));
		add(showHideLink);
    }

    /**
     * Construct the panel contained within the collapsible panel
     *
     * @param markupId ID that should be used for the inner panel
     * @return The inner panel displayed when collapsible is open
     */
	protected abstract Panel getInnerPanel(String markupId);
}
