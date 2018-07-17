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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.scorm.model.api.ActivityReport;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.ScoBean;
import org.sakaiproject.scorm.model.api.SessionBean;
import org.sakaiproject.scorm.navigation.INavigable;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormApplicationService;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.scorm.service.api.ScormResourceService;
import org.sakaiproject.scorm.service.api.ScormResultService;
import org.sakaiproject.scorm.service.api.ScormSequencingService;
import org.sakaiproject.scorm.ui.ResourceNavigator;
import org.sakaiproject.scorm.ui.UISynchronizerPanel;
import org.sakaiproject.scorm.ui.console.pages.DisplayDesignatedPackage;
import org.sakaiproject.scorm.ui.player.behaviors.SjaxCall;
import org.sakaiproject.scorm.ui.player.util.Utils;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;

public class SjaxContainer extends WebMarkupContainer implements IHeaderContributor {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(SjaxContainer.class);
	
	private static final ResourceReference SJAX = new JavascriptResourceReference(SjaxContainer.class, "res/scorm-sjax.js");
	
	@SpringBean
	LearningManagementSystem lms;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormApplicationService")
	ScormApplicationService applicationService;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormResourceService")
	ScormResourceService resourceService;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormSequencingService")
	ScormSequencingService sequencingService;
	
	@SpringBean(name = "org.sakaiproject.scorm.service.api.ScormResultService")
	ScormResultService resultService;
	
	@SpringBean(name = "org.sakaiproject.scorm.service.api.ScormContentService")
	ScormContentService contentService;
	
	
	private UISynchronizerPanel synchronizerPanel;
	private SjaxCall[] calls = new SjaxCall[8]; 
	private HiddenField[] components = new HiddenField[8];
	
	public SjaxContainer(String id, final SessionBean sessionBean, final UISynchronizerPanel synchronizerPanel) {
		super(id, new Model(sessionBean));
		this.synchronizerPanel = synchronizerPanel;
		
		this.setOutputMarkupId(true);
		this.setMarkupId("sjaxContainer");
		log.debug("KHACPC SJAXCONTAINER IS RUNNING");
		
		calls[0] = new ScormSjaxCall("Commit", 1);
		
		calls[1] = new ScormSjaxCall("GetDiagnostic", 1);
	
		calls[2] = new ScormSjaxCall("GetErrorString", 1);
	
		calls[3] = new ScormSjaxCall("GetLastError", 0);
		
		calls[4] = new ScormSjaxCall("GetValue", 1);

		calls[5] = new ScormSjaxCall("Initialize", 1) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String callMethod(ScoBean blankScoBean, AjaxRequestTarget target, Object... args) {
				
				ScoBean scoBean = applicationService().produceScoBean("undefined", getSessionBean());
				
				String result = super.callMethod(scoBean, target, args);
				
				synchronizerPanel.synchronizeState(sessionBean, target);
				
				ActivityTree tree = synchronizerPanel.getTree();
				if (tree != null && !tree.isEmpty()) {				
					tree.selectNode();
					tree.updateTree(target);
				}
				
				return result;
			}
		};

		calls[6] = new ScormSjaxCall("SetValue", 2);

		calls[7] = new ScormSjaxCall("Terminate", 1) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String callMethod(ScoBean scoBean, AjaxRequestTarget target, Object... args) {
				String result = super.callMethod(scoBean, target, args);
						
				ActivityTree tree = synchronizerPanel.getTree();
				if (tree != null && !tree.isEmpty()) {
					tree.selectNode();
					tree.updateTree(target);
				}
				// Hoctdy add dialog start
		        ActivityReport report = resultService.getActivityReport(sessionBean.getContentPackage().getContentPackageId(), 
		        						sessionBean.getLearnerId(), sessionBean.getAttempt().getAttemptNumber(), scoBean.getScoId() );
		        String completeStatus = "";
		        String status = "";
		        String progress = "";
		        String score = "";
		        if(null != report){
		        	completeStatus = report.getProgress().getCompletionStatus();
		        	status = Utils.getValueAsString(report.getProgress().getSuccessStatus());
		        	progress = Utils.getPercentageString(report.getProgress().getProgressMeasure());
		        	score = Utils.getNumberPercententString(report.getScore().getScaled());
		        }
				if (scoBean != null)
					applicationService.discardScoBean(scoBean.getScoId(), sessionBean, new LocalResourceNavigator());
				StringBuffer js = new StringBuffer();
				
				String url = getUrl(sessionBean.getContentPackage().getNextResourceId());
				// call location
				//js.append(" window.opener.location.reload(true);");
				//js.append(" window.opener.location.href = \"http://localhost:8080/portal/site/cd1016df-6b2a-4dba-9b4c-96e0b8c53746/tool/9950172f-7921-453a-955c-00eb1109bac5/\"");
				//js.append(" window.opener.location.reload(true);");
				js.append("var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;  ");
				js.append("var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;  ");
				js.append("width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width ; ");
				js.append("height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;   ");
				js.append("var left = ((width / 2) - (800 / 2)) + dualScreenLeft; ");
				js.append("var top = ((height / 2) - (450 / 2)) + dualScreenTop; ");		
				js.append(" var myWindow = window.open('', '")
				  .append(sessionBean.getContentPackage().getTitle())
				  .append("\', \'resizable=no,width=800,height=450,top=\'+ top + \',left=\'+ left +\"'\");");
				js.append(" myWindow.focus();");
				
				// print content & css
				StringBuffer css = new StringBuffer();
				css.append("<style>")
				   .append(" .wrap{background: #1a2a40; padding:30px 0px }");				   
				css.append(" .wrap .wrap-content{ background: #e9ebee;width: 85%;margin: 0 auto;padding: 30px}");
				css.append(" .wrap .wrap-content ul{	list-style-type: none; padding-left: 0px;}");
				css.append(" .wrap .wrap-content ul li{display: inline-block;width: 49%; color: #2e4057}");
				css.append(" .wrap .wrap-content ul li.title{text-align:right;padding-right: 5px;font-weight: bold;font-size: 18px; font-style: italic;}</style>");

				// js open next
				StringBuffer jsOpen = new StringBuffer();
				jsOpen.append(" <script>")					
					.append(" function myFunction() {")
					.append(" window.close(); ")
					.append(" window.open(\""+ url + "\",\"" + sessionBean.getContentPackage().getTitle()+ "\",\"_blank\",\"width=1020,height=740\");")
					.append(" }");
				jsOpen.append(" </script>");
				js.append(" myWindow.document.write('<html><head><title>" + sessionBean.getContentPackage().getTitle()  + "</title>');");
				js.append(" myWindow.document.write('" + css.toString() + "');");
				js.append(" myWindow.document.write('" + jsOpen.toString() + "');");
				js.append(" myWindow.document.write('</head><body>');");
								
				// write div
				js.append(" myWindow.document.write('<div class=\"wrap\">');");
				js.append(" myWindow.document.write('<div class=\"wrap-content\">');");
				
				js.append(divClass("This is result of lesson :",sessionBean.getContentPackage().getTitle()));
				
				js.append(divClass("Learner :",sessionBean.getAttempt().getLearnerName()));
				
				js.append(divClass("Attempt Number :", new Long(sessionBean.getAttempt().getAttemptNumber()).toString()));
				
				js.append(divClass("Progress :",progress));
				
				js.append(divClass("Complete Status :",completeStatus));
				
				js.append(divClass("Success Status :",status));
				
				js.append(divClass("Score :",score));

				if(null != url && !"".equals(url)){
					js.append(divClassNextButton());
				}
				
				js.append(" myWindow.document.write('</div>');");
				js.append(" myWindow.document.write('</div>');");
				
				js.append(" myWindow.document.write('</body></html>');");	

				target.appendJavascript(js.toString());		
				// Hoctdy add dialog end
				return result;
			}
		};
		
		
		Form form = new Form("sjaxForm");
		add(form);
		
		components[0] = addSjaxComponent("commitcall", calls[0], form);
		components[1] = addSjaxComponent("getdiagnosticcall", calls[1], form);
		components[2] = addSjaxComponent("geterrorstringcall", calls[2], form);
		components[3] = addSjaxComponent("getlasterrorcall", calls[3], form);
		components[4] = addSjaxComponent("getvaluecall", calls[4], form);
		components[5] = addSjaxComponent("initializecall", calls[5], form);
		components[6] = addSjaxComponent("setvaluecall", calls[6], form);
		components[7] = addSjaxComponent("terminatecall", calls[7], form);
		
	}
	
	public String getUrl(String contentPackageId) {
		
		if(null == contentPackageId 
				|| (null != contentPackageId && "".equals(contentPackageId))){
			return null;
		}
		
		ContentPackage contentPackage = contentService.getContentPackageByResourceId(contentPackageId);
		
		String url = contentService.getToolSiteUrl() + "?wicket:bookmarkablePage=ScormPlayer:org.sakaiproject.scorm.ui.player.pages.PlayerPage";
		url += "&contentPackageId=" + contentPackage.getContentPackageId();
		url += "&resourceId=" + contentPackage.getResourceId();
		url += "&title="+ contentPackage.getTitle();
		
		return url;
	}
	
	private StringBuffer divClass(String title, String value) {
		StringBuffer result = new StringBuffer();
		result.append("myWindow.document.write('<div class=\"row-content\">');");
		result.append("myWindow.document.write('<ul>');");
		result.append("myWindow.document.write('<li class=\"title\">');");
		result.append(" myWindow.document.write('"+ title +"');" );
		
		result.append("myWindow.document.write('<li class=\"content\">');");
		result.append(" myWindow.document.write('"+ value + "');" );
		result.append(" myWindow.document.write('</li>');" );
		 
		result.append("myWindow.document.write('</ul>');");
		result.append("myWindow.document.write('</div>');");
		return result;
	}
	
	private StringBuffer divClassNextButton() {
		StringBuffer result = new StringBuffer();
		result.append(" myWindow.document.write('<div class=\"row-content\">');");
		result.append(" myWindow.document.write('<ul>');");
		result.append(" myWindow.document.write('<li class=\"title\">');");
		result.append(" myWindow.document.write('<button onclick=\"myFunction()\">Next Lesson</button>');");
		result.append(" myWindow.document.write('</li>');" );		 
		result.append(" myWindow.document.write('</ul>');");
		result.append(" myWindow.document.write('</div>');");
		return result;
	}

	@Override
	public void onBeforeRender() {
		super.onBeforeRender();
		
		for (int i=0;i<8;i++) {
			components[i].setModel(new Model(calls[i].getCallUrl().toString()));
		}
	}
	
	
	private HiddenField addSjaxComponent(String callname, SjaxCall call, Form form) {
		HiddenField cc = new HiddenField(callname); 
		form.add(cc);
		cc.setMarkupId(callname);
		cc.add(call);
		
		return cc;
	}
	
	
	public void renderHead(IHeaderResponse response) {
		response.renderJavascriptReference(SJAX);	
		
		
		StringBuffer js = new StringBuffer();
		
		js.append("function APIAdapter() { };\n")	
			.append("var API_1484_11 = APIAdapter;\n")
			.append("var api_result = new Array();\n")
			.append("var call_number = 0;\n");
		
		for (int i=0;i<calls.length;i++) {
			js.append(calls[i].getJavascriptCode()).append("\n");
		}
		
		response.renderJavascript(js.toString(), "SCORM_API");
	}
	
	public class ScormSjaxCall extends SjaxCall {
		
		private static final long serialVersionUID = 1L;
		
		public ScormSjaxCall(String event, int numArgs) {
			super(event, numArgs);
		}
		
		@Override
		protected String callMethod(ScoBean scoBean, AjaxRequestTarget target, Object... args) {
		    log.debug("VinhVC.Debug.callMethod in ScormSjaxCall inside SjaxContainer");
			String result = super.callMethod(scoBean, target, args);
			if (log.isDebugEnabled()) {
				String methodName = getEvent();
				StringBuilder argDisplay = new StringBuilder();
				for (int i=0;i<args.length;i++) {
					argDisplay.append("'").append(args[i]).append("'");
					if (i+1 < args.length)
						argDisplay.append(", ");
				}
				String display = new StringBuilder().append(methodName)
					.append("(")
					.append(argDisplay).append(")").append(" returns ")
					.append("'").append(result).append("'").toString();
				
				log.debug(display);
			}
			
			return result;
		}
		
		@Override
		protected void onEvent(final AjaxRequestTarget target) {
			modelChanging();
			log.debug("=====================================================================");
			log.debug("VinhVC.Debug.onEvent in ScormSjaxCall inside SjaxContainer");
			super.onEvent(target);
			modelChanged();
		}
		
		@Override
		protected SessionBean getSessionBean() {
			return (SessionBean)getDefaultModelObject();
		}
		
		@Override
		protected LearningManagementSystem lms() {
			return lms;
		}
		
		@Override
		protected ScormApplicationService applicationService() {
			return applicationService;
		}

		@Override
		protected ScormResourceService resourceService() {
			return resourceService;
		}
		
		@Override
		protected ScormSequencingService sequencingService() {
			return sequencingService;
		}
		
		@Override
		protected String getChannelName() {
			return "1|s";
		}
		
		@Override
		protected INavigable getNavigationAgent() {
			return new LocalResourceNavigator();
		}
	}
	
	
	public class LocalResourceNavigator extends ResourceNavigator {

		private static final long serialVersionUID = 1L;

		@Override
		protected ScormResourceService resourceService() {
			return SjaxContainer.this.resourceService;
		}
		
		@Override
		public Component getFrameComponent() {
			if (synchronizerPanel != null && synchronizerPanel.getContentPanel() != null) 
				return synchronizerPanel.getContentPanel();
			return null;
		}
		
		@Override
		public boolean useLocationRedirect() {
			return false;
		}
		
	}
}
