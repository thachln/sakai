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

import static org.sakaiproject.scorm.api.ScormConstants.COMPLETED;
import static org.sakaiproject.scorm.api.ScormConstants.GRADED;
import static org.sakaiproject.scorm.api.ScormConstants.INCOMPLETE;
import static org.sakaiproject.scorm.api.ScormConstants.NOT_ACCESSED;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.scorm.model.api.ActivityReport;
import org.sakaiproject.scorm.model.api.ActivitySummary;
import org.sakaiproject.scorm.model.api.Interaction;
import org.sakaiproject.scorm.model.api.LearnerExperience;
import org.sakaiproject.scorm.model.api.Objective;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormResultService;
import org.sakaiproject.scorm.ui.Icon;
import org.sakaiproject.scorm.ui.console.components.BreadcrumbPanel;
import org.sakaiproject.scorm.ui.console.components.SakaiFeedbackPanel;
import org.sakaiproject.scorm.ui.player.util.Utils;
import org.sakaiproject.scorm.ui.reporting.pages.AttemptDataProvider;
import org.sakaiproject.scorm.ui.upload.pages.UploadPage;
import org.sakaiproject.scorm.ui.validation.pages.ValidationPage;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.wicket.markup.html.SakaiPortletWebPage;
import org.sakaiproject.wicket.markup.html.link.NavIntraLink;


public class ConsoleBasePage extends SakaiPortletWebPage implements IHeaderContributor {

	private static final long serialVersionUID = 1L;
	
	private static final Log LOG = LogFactory.getLog(ConsoleBasePage.class);
	
	private static ResourceReference CONSOLE_CSS = new CompressedResourceReference(ConsoleBasePage.class, "res/scorm_console.css");
	private static ResourceReference LIST_ICON = new ResourceReference(ConsoleBasePage.class, "res/table.png");
	private static ResourceReference UPLOAD_ICON = new ResourceReference(ConsoleBasePage.class, "res/table_add.png");
	private static ResourceReference VALIDATE_ICON = new ResourceReference(ConsoleBasePage.class, "res/table_link.png");

    private static final String SAK_PROP_ENABLE_MENU_BUTTON_ICONS = "scorm.menuButton.icons";
    @SpringBean( name = "org.sakaiproject.component.api.ServerConfigurationService" )
    ServerConfigurationService serverConfigurationService;
	
	// The feedback panel component displays dynamic messages to the user
	protected FeedbackPanel feedback;
	private BreadcrumbPanel breadcrumbs;
	
	
	@SpringBean
	LearningManagementSystem lms;
	@SpringBean
	public ToolManager toolManager;

	
	public ConsoleBasePage() {
		this(null);
	}
	
	public ConsoleBasePage(PageParameters params) {
		
		final String context = lms.currentContext();
		final boolean canUpload = lms.canUpload(context);
		final boolean canValidate = lms.canValidate(context);
		final boolean canGrade = lms.canGrade(context);
		
		WebMarkupContainer wmc = new MaydayWebMarkupContainer("toolbar-administration");
		if (isSinglePackageTool()) {
	        wmc.setVisible(false);
		}
		
        NavIntraLink listLink = new NavIntraLink("listLink", new ResourceModel("link.list"), PackageListPage.class);
        NavIntraLink uploadLink = new NavIntraLink("uploadLink", new ResourceModel("link.upload"), UploadPage.class);
        //NavIntraLink validateLink = new NavIntraLink("validateLink", new ResourceModel("link.validate"), ValidationPage.class);
        NavIntraLink summaryLink = new NavIntraLink("summaryLink", new ResourceModel("link.summary"), SummaryResultPage.class);
        
        WebMarkupContainer listContainer = new WebMarkupContainer( "listContainer" );
        WebMarkupContainer uploadContainer = new WebMarkupContainer( "uploadContainer" );
        //WebMarkupContainer validateContainer = new WebMarkupContainer( "validateContainer" );
        WebMarkupContainer summaryResultContainer = new WebMarkupContainer( "summaryResultContainer" );
        
        listContainer.add( listLink );
        uploadContainer.add( uploadLink );
        //validateContainer.add( validateLink );
        summaryResultContainer.add(summaryLink);

        SimpleAttributeModifier className = new SimpleAttributeModifier( "class", "current" );
        if( listLink.linksTo( getPage() ) )
        {
            listContainer.add( className );
            listLink.add( className );
        }
        else if( uploadLink.linksTo( getPage() ) )
        {
            uploadContainer.add( className );
            uploadLink.add( className );
        } else if(summaryLink.linksTo(getPage())){
        	summaryResultContainer.add( className );
        	summaryLink.add( className );
        }
        //else if( validateLink.linksTo( getPage() ) )
        //{
        //    validateContainer.add( className );
        //    validateLink.add( className );
        //}
        if(!(canUpload || canValidate)){
        	listContainer.setVisible(false);
        }
        if(!canUpload){
        	uploadContainer.setVisible(false);
        }
        if(!canGrade){
        	summaryLink.setVisible(false);
        }
        listLink.setVisible(canUpload || canValidate);
        uploadLink.setVisible(canUpload);
        summaryLink.setVisible(canGrade);
        // SCO-107 - hide the validate link (interface is currently unimplemented)
        //validateLink.setVisible(canValidate);
        //validateLink.setVisibilityAllowed(false);
        
        Icon listIcon = new Icon("listIcon", LIST_ICON);
        Icon uploadIcon = new Icon("uploadIcon", UPLOAD_ICON);
        //Icon validateIcon = new Icon("validateIcon", VALIDATE_ICON);
        Icon summaryIcon = new Icon("listIcon", LIST_ICON);
        
        // SCO-109 - conditionally show the icons in the menu bar buttons
        boolean enableMenuBarIcons = serverConfigurationService.getBoolean( SAK_PROP_ENABLE_MENU_BUTTON_ICONS, true );
        if( enableMenuBarIcons )
        {
            listIcon.setVisible(canUpload || canValidate);
            uploadIcon.setVisible(canUpload);
            summaryIcon.setVisible(true);

            // SCO-107 hide the validate link (interface is currently unimplemented)
            //validateIcon.setVisible(canValidate);
            //validateIcon.setVisibilityAllowed(false);
        }
        else
        {        	
            listIcon.setVisibilityAllowed( false );
            uploadIcon.setVisibilityAllowed( false );
            summaryIcon.setVisibilityAllowed(false);            
            //validateIcon.setVisibilityAllowed( false );
        }
        
        listContainer.add(listIcon);
        uploadContainer.add(uploadIcon);
        //validateContainer.add(validateIcon);
        summaryResultContainer.add(summaryIcon);

        wmc.add( listContainer );
        wmc.add( uploadContainer );
        //wmc.add( validateContainer );
        wmc.add(summaryResultContainer);

        // add the toolbar container
        add(wmc);
        
		add(newPageTitleLabel(params));
		add(feedback = new SakaiFeedbackPanel("feedback"));
		add(breadcrumbs = new BreadcrumbPanel("breadcrumbs"));
		
		Icon pageIcon = new Icon("pageIcon", getPageIconReference());
		pageIcon.setVisible(getPageIconReference() != null);
		add(pageIcon);
	}
	
	public void addBreadcrumb(IModel model, Class<?> pageClass, PageParameters params, boolean isEnabled) {
		breadcrumbs.addBreadcrumb(model, pageClass, params, isEnabled);
	}
	
	protected Label newPageTitleLabel(PageParameters params) {
		return new Label("page.title", new ResourceModel("page.title"));
	}
	
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		// If a feedback message exists, then make the feedback panel visible, otherwise, hide it.
		feedback.setVisible(hasFeedbackMessage());
		breadcrumbs.setVisible(breadcrumbs.getNumberOfCrumbs() > 0);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.renderCSSReference(CONSOLE_CSS);
	}
	
	protected ResourceReference getPageIconReference() {
		return null;
	}
	
	protected boolean isSinglePackageTool() {
		return toolManager != null && 
				toolManager.getCurrentTool() != null && 
				"sakai.scorm.singlepackage.tool".equals(toolManager.getCurrentTool().getId());
	}
	
	public String getStatusLabel( int status )
	{
		switch( status )
		{
			case NOT_ACCESSED:
			{
				return getLocalizer().getString( "access.status.not.accessed", this );
			}
			case INCOMPLETE:
			{
				return getLocalizer().getString( "access.status.incomplete", this );
			}
			case COMPLETED:
			{
				return getLocalizer().getString( "access.status.completed", this );
			}
			case GRADED:
			{
				return getLocalizer().getString( "access.status.graded", this );
			}
		}

		return "";
	}
	
	public Object[] buildHeaderExportExcel() {
		Object[] header = new Object[] { 
				getLocalizer().getString( "export.headers.learner", this ),
				getLocalizer().getString( "export.headers.lastAttempt", this ),
				getLocalizer().getString( "export.headers.status", this ) , 
				getLocalizer().getString( "export.headers.attempts", this ) , 
				getLocalizer().getString( "export.headers.title", this ) ,
				getLocalizer().getString( "export.headers.progress_measure", this ) ,
				getLocalizer().getString( "export.headers.start_time", this ) , 
				getLocalizer().getString( "export.headers.duration", this ) , 
				getLocalizer().getString( "export.headers.score", this ) ,
				getLocalizer().getString( "export.headers.completionStatus", this ) ,
				getLocalizer().getString( "export.headers.successStatus", this ) , 
				getLocalizer().getString( "export.headers.identifier", this ) ,
				getLocalizer().getString( "export.headers.type", this ) ,
				getLocalizer().getString( "export.headers.weighting", this ) ,
				getLocalizer().getString( "export.headers.latency", this ) ,
				getLocalizer().getString( "export.headers.time", this ) , 
				getLocalizer().getString( "export.headers.result", this ) ,
				getLocalizer().getString( "export.headers.description", this ) , 
				getLocalizer().getString( "export.headers.correctResponse", this ) , 
				getLocalizer().getString( "export.headers.learnerResponse", this ) ,
				getLocalizer().getString( "export.headers.objectiveIdentifier", this ) ,
				getLocalizer().getString( "export.headers.objectiveDescription", this ) ,
				getLocalizer().getString( "export.headers.objectiveCompletionStatus", this ) ,
				getLocalizer().getString( "export.headers.objectiveSuccessStatus", this ) , 
				getLocalizer().getString( "export.headers.objectiveScore", this ) 
		};
		return header;
	}
	
	@SuppressWarnings("deprecation")
	public List<Object[]> generateExportExcel( ScormResultService resultService,  AttemptDataProvider attemptProvider )
	{
		// Create the column headers
		List<Object[]> result = new ArrayList<Object[]>();

		Iterator<LearnerExperience> itr = attemptProvider.iterator( 0, attemptProvider.size() );
		while( itr.hasNext() )
		{
			// Learner info			
			LearnerExperience learner = itr.next();
			Object[] tmpObject = new Object[24];
			tmpObject[0] = learner.getLearnerName() ;
			tmpObject[1] = Objects.toString( learner.getLastAttemptDate(), "" );
			tmpObject[2] = getStatusLabel( learner.getStatus() );
			tmpObject[3] = Objects.toString(learner.getNumberOfAttempts(),"");
			
			// Get the summaries for all attempts for the current user
			List<ActivitySummary> summaries = new ArrayList<>();
			for( int i = 1; i <= learner.getNumberOfAttempts(); i++ )
			{
				summaries.addAll( resultService.getActivitySummaries( learner.getContentPackageId(), learner.getLearnerId(), i ) );
			}

			if( summaries.isEmpty() )
			{
				result.add(tmpObject);
			}

			for( int i = 0; i < summaries.size(); i++ )
			{
				if( i != 0 )
				{
					tmpObject = new Object[22];
				}

				// Summary info
				ActivitySummary summary = summaries.get( i );
				tmpObject[4] = summary.getTitle();
				tmpObject[5] = Utils.getPercentageString(summary.getProgressMeasure()) ;
				tmpObject[6] = Objects.toString( summary.getStartDate(), "" );
				tmpObject[7] = summary.getTotalSessionSecondsDisplay();
				tmpObject[8] = Utils.getNumberPercententString(summary.getScaled());
				tmpObject[9] = Objects.toString( summary.getCompletionStatus(), "" );
				tmpObject[10] = Objects.toString( summary.getSuccessStatus(), "" );

				// Get the interactions
				ActivityReport report = resultService.getActivityReport( summary.getContentPackageId(), summary.getLearnerId(), summary.getAttemptNumber(), summary.getScoId() );
				if( report != null )
				{
					List<Interaction> interactions = report.getInteractions();
					if( interactions.isEmpty() )
					{
						result.add(tmpObject);
					}

					for( int j = 0; j < interactions.size(); j++ )
					{
						if( j != 0 )
						{
							tmpObject = new Object[24];
						}

						// Interaction info
						Interaction interaction = interactions.get( j );
						StringBuilder correctResponses = new StringBuilder();
						for( String correctResponse : interaction.getCorrectResponses() )
						{
							if( StringUtils.isNotEmpty( correctResponse ) )
							{
								correctResponses.append( correctResponse );
							}
						}
						tmpObject[11] = interaction.getInteractionId();
						tmpObject[12] = interaction.getType();
						tmpObject[13] = Objects.toString(interaction.getWeighting(),"");
						tmpObject[14] = interaction.getLatency();
						tmpObject[15] = parseStringDatetimeFormat(interaction.getTimestamp());
						tmpObject[16] = interaction.getResult();
						tmpObject[17] = interaction.getDescription();
						tmpObject[18] = correctResponses.toString();
						tmpObject[19] = interaction.getLearnerResponse();
						

						// Objective info
						List<Objective> objectives = interaction.getObjectives();
						
						for( int k = 0; k < objectives.size(); k++ )
						{
							if( k != 0 )
							{
								tmpObject = new Object[22];
							}

							Objective objective = objectives.get( k );
							tmpObject[20] = Objects.toString( objective.getDescription(), "" );
							tmpObject[21] = Objects.toString( objective.getCompletionStatus(), "" );
							tmpObject[22] = Objects.toString( objective.getSuccessStatus(), "" );
							tmpObject[23] = Objects.toString( objective.getScore().getScaled(), "" );
							result.add(tmpObject);							
						}

						if( objectives.isEmpty() )
						{
							result.add(tmpObject);
						}
					}
				}
				else
				{
					result.add(tmpObject);
				}
			}
		}

		return result;
	}
	
	private String parseStringDatetimeFormat(String timestamp) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if ((timestamp == null) || (timestamp.length() == 0)) {
			// return default value ""
			return result;
		}
		Date d1= null;
		try {
			d1 = sdf.parse(timestamp);
		} catch (Exception ex) {
			LOG.warn("Could not get last submit time: maxSubmitTime=" + timestamp);
		}
		if (null != d1) {
			result = output.format(d1);
		}
		return result;
	}
	
}
