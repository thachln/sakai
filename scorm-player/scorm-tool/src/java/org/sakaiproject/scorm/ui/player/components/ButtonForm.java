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
package org.sakaiproject.scorm.ui.player.components;

import org.adl.sequencer.SeqNavRequests;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.scorm.model.api.SessionBean;
import org.sakaiproject.scorm.service.api.ScormSequencingService;
import org.sakaiproject.scorm.ui.player.pages.PlayerPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ButtonForm extends Form {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ButtonForm.class);
	private static final String STARTBTN_ROOT_SRC = "/scorm-tool/images/startBtn";
	private static final String PREVBTN_ROOT_SRC = "/scorm-tool/images/prevBtn";
	private static final String NEXTBTN_ROOT_SRC = "/scorm-tool/images/nextBtn";
	private static final String QUITBTN_ROOT_SRC = "/scorm-tool/images/quitBtn";
	private static final String SUSPENDBTN_ROOT_SRC = "/scorm-tool/images/suspendBtn";

	private ActivityAjaxButton prevButton;
	private ActivityAjaxButton nextButton;
	private ActivityAjaxButton startButton;
	private ActivityAjaxButton quitButton;
	private ActivityAjaxButton suspendButton;
	private PlayerPage view;
	
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormSequencingService")
	ScormSequencingService sequencingService;
	
	public ButtonForm(String id, final SessionBean sessionBean, PlayerPage view) {
		super(id);
		this.view = view;
		log.debug("KHACPC BUTTONFORM IS RUNNING");
		prevButton = new ActivityAjaxButton(this, sessionBean, "prevButton", SeqNavRequests.NAV_PREVIOUS, PREVBTN_ROOT_SRC);
		nextButton = new ActivityAjaxButton(this, sessionBean, "nextButton", SeqNavRequests.NAV_CONTINUE, NEXTBTN_ROOT_SRC);
		startButton = new ActivityAjaxButton(this, sessionBean, "startButton", SeqNavRequests.NAV_START, STARTBTN_ROOT_SRC);
		quitButton = new ActivityAjaxButton(this, sessionBean, "quitButton", SeqNavRequests.NAV_EXITALL, QUITBTN_ROOT_SRC);
		suspendButton = new ActivityAjaxButton(this, sessionBean, "suspendButton", SeqNavRequests.NAV_SUSPENDALL, SUSPENDBTN_ROOT_SRC);
		
		add(prevButton);
		add(nextButton);
		add(startButton);
		add(quitButton);
		add(suspendButton);
				
		setOutputMarkupId(true);
		synchronizeState(sessionBean, null);
	}
	
	public void synchronizeState(SessionBean sessionBean, AjaxRequestTarget target) {
	    log.debug("KHACPC ISCONTINUEENABLE IS STARTING");
		boolean isContinueEnabled = sequencingService.isContinueEnabled(sessionBean);
		boolean isContinueExitEnabled = sequencingService.isContinueExitEnabled(sessionBean);
		boolean isPreviousEnabled = sequencingService.isPreviousEnabled(sessionBean);
		boolean isStartEnabled = sequencingService.isStartEnabled(sessionBean);
		boolean isSuspendEnabled = sequencingService.isSuspendEnabled(sessionBean);
		log.debug("KHACPC NEXT BUTTON IS SET VISIBLE");
		setNextButtonVisible(isContinueEnabled, target);
		setPrevButtonVisible(isPreviousEnabled, target);
		setStartButtonVisible(isStartEnabled, target);
		setSuspendButtonVisible(isSuspendEnabled, target);
		setQuitButtonVisible(isContinueExitEnabled, target);
	}

	public void setPrevButtonVisible(boolean isVisible, AjaxRequestTarget target) {
		setButtonVisible(prevButton, isVisible, target);
	}
	
	public void setNextButtonVisible(boolean isVisible, AjaxRequestTarget target) {
	    log.debug("KHACPC SETVISIBLE NEXT BUTTON FUNCTION IS RUNNING");
		setButtonVisible(nextButton, isVisible, target);
	}
	
	public void setStartButtonVisible(boolean isVisible, AjaxRequestTarget target) {
		setButtonVisible(startButton, isVisible, target);
	}
	
	public void setQuitButtonVisible(boolean isVisible, AjaxRequestTarget target) {
		setButtonVisible(quitButton, isVisible, target);
	}
	
	public void setSuspendButtonVisible(boolean isVisible, AjaxRequestTarget target) {
		setButtonVisible(suspendButton, isVisible, target);
	}

	private void setButtonVisible(ActivityAjaxButton button, boolean isEnabled, AjaxRequestTarget target) {
	    log.debug("KHACPC SETVISIBLE SYNC FUNCTION IS RUNNING");
		if (null != button) { 
			boolean wasEnabled = button.isEnabled();
			button.setEnabled(isEnabled);
			
			if (!button.isSyncd() || wasEnabled != isEnabled) {
				if (target != null) {
					target.addComponent(button);
					button.setSyncd(true);
				} else
					button.setSyncd(false);
			}
		}
	}
	
	public LaunchPanel getLaunchPanel() {
		return view.getLaunchPanel();
	}

	public ActivityAjaxButton getQuitButton() {
		return quitButton;
	}

}
