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
package org.sakaiproject.scorm.ui.player.behaviors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.sakaiproject.scorm.model.api.SessionBean;

public class CloseWindowBehavior extends ActivityAjaxEventBehavior {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(CloseWindowBehavior.class);
	
	private SessionBean sessionBean;
	
	public CloseWindowBehavior(SessionBean sessionBean, boolean isRelativeUrl) {
		super("closeWindow", isRelativeUrl);
		this.sessionBean = sessionBean;
	}
	
	@Override
	protected void onEvent(AjaxRequestTarget target) {
		log.debug("closeWindowBehavior onEvent()");
		if (sessionBean != null && sessionBean.isStarted() && !sessionBean.isEnded()) {
			log.debug("----> Going to exit on next terminate request");
			sessionBean.setCloseOnNextTerminate(true);
		}
	}
	
}
