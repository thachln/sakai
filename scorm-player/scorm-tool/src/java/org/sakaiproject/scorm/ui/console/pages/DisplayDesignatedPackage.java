/*
 * #%L
 * SCORM Tool
 * %%
 * Copyright (C) 2007 - 2016 Sakai Project
 * %%
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *             http://opensource.org/licenses/ecl2
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.sakaiproject.scorm.ui.console.pages;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.PageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.scorm.api.ScormConstants;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.scorm.ui.player.pages.PlayerPage;
import org.sakaiproject.scorm.ui.reporting.pages.LearnerResultsPage;
import org.sakaiproject.scorm.ui.reporting.pages.ResultsListPage;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.wicket.markup.html.SakaiPortletWebPage;

public class DisplayDesignatedPackage extends SakaiPortletWebPage implements IHeaderContributor, ScormConstants {

	private static Log log = LogFactory.getLog(DisplayDesignatedPackage.class);

	public static final String CFG_PACKAGE_NAME = "scorm.package";
	public static final String CFG_PACKAGE_NAME_UNDEFINED = "undefined";

	@SpringBean
	LearningManagementSystem lms;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormContentService")
	ScormContentService contentService;
	
	public DisplayDesignatedPackage() {
		log.debug("DisplayDesignatedPackage page entered...");

		ContentPackage contentPackage = getDesignatedPackage();

		// add components
		add(new FeedbackPanel("feedback"));
		add(new Label("page.title", contentPackage.getTitle()));
		log.debug("Invoke DisplayDesignatedPackage()");
		addActionLinksForPackage(contentPackage);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.renderCSSReference(new ResourceReference(DisplayDesignatedPackage.class, "DisplayDesignatedPackage.css"));
	}

	private ContentPackage getDesignatedPackage() {
		ContentPackage designatedPackage = null;
		String resourceId = null;
		log.debug("Invoke getDesignatedPackage()");
		ToolManager toolManager = (ToolManager) ComponentManager.get(ToolManager.class);
		if (null != toolManager) {
			Properties cfgPlacement = toolManager.getCurrentPlacement().getPlacementConfig();
			resourceId = cfgPlacement.getProperty(CFG_PACKAGE_NAME);
			// check that the property was set in the tool, otherwise show a configuration error
			if ((null != resourceId) && (!CFG_PACKAGE_NAME_UNDEFINED.equals(resourceId))) {
				log.debug("Package name " + resourceId + " was found in configuration.");
				designatedPackage = this.contentService.getContentPackageByResourceId(resourceId);
			}
			else {
				String msg = getLocalizer().getString("designated.resource.notconfigured", this, CFG_PACKAGE_NAME);
				error(msg);
				// log.warn("This SCORM player is NOT YET configured to play a designated package. Please to the tool properties and configure the property named 'scorm.package'");
			}
		}
		else {
			log.warn("toolManager could not be obtained");
		}

		if (designatedPackage == null) {
			String msg = getLocalizer().getString("designated.resource.notfound", this, resourceId);
			error(msg);
			// log.debug("The configured package " +packageFilename+ " is not available as a site resource.");
		}
		
		return designatedPackage;
	}

	protected PageParameters getParametersForPackage(ContentPackage pkg) {
	    log.debug("Invoke getParametersForPackage" );
	    log.debug("Data of contentPackage : { id :"+pkg.getContentPackageId() +" resourceID : "+pkg.getResourceId()+" title : "+pkg.getTitle()+" }");
		PageParameters params = new PageParameters();
		params.add("contentPackageId", "" + pkg.getContentPackageId());
		params.add("resourceId", pkg.getResourceId());
		String title = pkg.getTitle();
		params.add("title", title);
		return params;
	}

	protected PageParameters getParametersForPersonalResults(ContentPackage pkg) {
		PageParameters params = new PageParameters();
		params.add("contentPackageId", "" + pkg.getContentPackageId());
		params.add("learnerId", lms.currentLearnerId());
		params.add("no-toolbar", "true");

		return params;
	}

	protected PageParameters getParametersForResultsList(ContentPackage pkg) {
		PageParameters params = new PageParameters();
		params.add("contentPackageId", "" + pkg.getContentPackageId());
		params.add("no-toolbar", "true");

		return params;
	}

	/**
	 * @param pkg
	 */
	@SuppressWarnings("deprecation")
	protected void addActionLinksForPackage(final ContentPackage pkg) {
        if (log.isDebugEnabled()) {
            log.debug("Invoke addActionLinksForPackage()");
            log.debug("file: " + pkg.getTitle());
            log.debug("BaoNQ, DuyDPD: DisplayDesignatedPackage - call PlayerPage");
        }
		PlayerPage playerPage = new PlayerPage(getParametersForPackage(pkg));
		Link lnkGo = new PageLink("lnk_go", playerPage);

		// Link lnkGo = new BookmarkablePageLink("lnk_go", PlayerPage.class, getParametersForPackage(pkg) );
		if (StringUtils.isNotBlank(pkg.getTitle())) {

			String title = pkg.getTitle();

			PopupSettings popupSettings = new PopupSettings(PageMap.forName(title), PopupSettings.RESIZABLE);
			popupSettings.setWidth(1020);
			popupSettings.setHeight(740);

			popupSettings.setWindowName(title);

			lnkGo.setPopupSettings(popupSettings);
		}

		lnkGo.setEnabled(true);
		lnkGo.setVisible(true);

		final PageParameters params = getParametersForPackage(pkg);
		params.add("no-toolbar", "true");

		String context = lms.currentContext();
		final boolean canConfigure = lms.canConfigure(context);
		final boolean canViewResults = lms.canViewResults(context);
		final boolean canGrade = lms.canGrade(context);
		
		Link<?> lnkConfigure = new Link("lnk_configure") {
			@Override
			public void onClick() {
				setResponsePage(new PackageConfigurationPage(params));
			}
		};
		lnkConfigure.setVisible(canConfigure);

		// the following link points to the results page for the designated package
		/*
		 * if (canViewResults) { actionColumn.addAction(new Action(new StringResourceModel("column.action.grade.label", this, null), LearnerResultsPage.class, paramPropertyExpressions)); }
		 */
		Link<?> lnkResults = new Link("lnk_results") {
			@Override
			public void onClick() {
				Page resultsPage = new LearnerResultsPage(getParametersForPersonalResults(pkg));
				if (canGrade) {
					resultsPage = new ResultsListPage(getParametersForResultsList(pkg));
				}
				setResponsePage(resultsPage);
			}
		};
		lnkResults.setVisible(canViewResults || canGrade);

		// add links to page
		add(lnkGo);
		add(lnkConfigure);
		add(lnkResults);
	}

} // class
