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
package org.sakaiproject.scorm.ui.player.pages;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.SessionBean;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.scorm.service.api.ScormSequencingService;
import org.sakaiproject.scorm.ui.console.pages.PackageListPage;
import org.sakaiproject.scorm.ui.player.behaviors.ActivityAjaxEventBehavior;
import org.sakaiproject.scorm.ui.player.behaviors.CloseWindowBehavior;
import org.sakaiproject.scorm.ui.player.components.ButtonForm;
import org.sakaiproject.scorm.ui.player.components.LaunchPanel;
import org.sakaiproject.scorm.ui.player.components.LazyLaunchPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlayerPage extends BaseToolPage {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(PlayerPage.class);

	@SpringBean
	LearningManagementSystem lms;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormContentService")
	ScormContentService scormContentService;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormSequencingService")
	ScormSequencingService scormSequencingService;
	
	// Components
	private LazyLaunchPanel lazyLaunchPanel;
	private ActivityAjaxEventBehavior closeWindowBehavior;
	private ButtonForm buttonForm;
	
	public PlayerPage() {
		this(new PageParameters());
	}
	
	
	public PlayerPage(final PageParameters pageParams) {
		super();
        if (log.isDebugEnabled()) {
            log.debug("BaoNQ.Debug.PlayerPage");
        }
		long contentPackageId = pageParams.getLong("contentPackageId");
		
		int userNavRequest = -1;
		if (pageParams.containsKey("navRequest"))
			userNavRequest = pageParams.getInt("navRequest");
		
		ContentPackage contentPackage = scormContentService.getContentPackage(contentPackageId);
        if (log.isDebugEnabled()) {
            log.debug("BaoNQ.Debug.PackageID : " + contentPackageId + "contentPackage Title : "
                    + contentPackage.getTitle());
        }
		final SessionBean sessionBean = scormSequencingService.newSessionBean(contentPackage);
		sessionBean.setCompletionUrl(getCompletionUrl());
		log.debug("completionURL: "+getCompletionUrl());
		buttonForm = new ButtonForm("buttonForm", sessionBean, this);
		add(buttonForm);
				
        if (log.isDebugEnabled()) {
            log.debug("DuyDPD: PlayerPage - content package: " + contentPackage.getTitle());
            log.debug("DuyDPD: PlayerPage - START loading resources (new LazyLaunchPanel)");
        }
		add(lazyLaunchPanel = new LazyLaunchPanel("actionPanel", sessionBean, userNavRequest, this));
        if (log.isDebugEnabled()) {
            log.debug("DuyDPD: PlayerPage - END loading resources");
        }
		closeWindowBehavior = new CloseWindowBehavior(sessionBean, lms.canUseRelativeUrls());
		add(closeWindowBehavior);
		
	}
	
	
	private String getCompletionUrl() {
		RequestCycle cycle = getRequestCycle();
		IRequestCodingStrategy encoder = cycle.getProcessor().getRequestCodingStrategy();
		WebRequest webRequest = (WebRequest)getRequest();
		HttpServletRequest servletRequest = webRequest.getHttpServletRequest();
		String toolUrl = servletRequest.getContextPath();
		
		Class<? extends Page> pageClass = PackageListPage.class;
		
		if (lms.canLaunchNewWindow())
			pageClass = CompletionPage.class;
			
		CharSequence completionUrl = encoder.encode(cycle, new BookmarkablePageRequestTarget(pageClass, new PageParameters()));
		AppendingStringBuffer url = new AppendingStringBuffer();
		url.append(toolUrl).append("/").append(completionUrl);
		
		return url.toString();
	}
			
	public void synchronizeState(SessionBean sessionBean, AjaxRequestTarget target) {
		buttonForm.synchronizeState(sessionBean, target);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.renderOnEventJavascript("window", "beforeunload", closeWindowBehavior.getCall());
	}
	
	public ButtonForm getButtonForm() {
		return buttonForm;
	}
	
	public LaunchPanel getLaunchPanel() {
		return lazyLaunchPanel.getLaunchPanel();
	}

	
}
