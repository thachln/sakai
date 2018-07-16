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
package org.sakaiproject.scorm.ui.reporting.pages;

import static org.sakaiproject.scorm.api.ScormConstants.COMPLETED;
import static org.sakaiproject.scorm.api.ScormConstants.GRADED;
import static org.sakaiproject.scorm.api.ScormConstants.INCOMPLETE;
import static org.sakaiproject.scorm.api.ScormConstants.NOT_ACCESSED;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.sakaiproject.scorm.model.api.ActivityReport;
import org.sakaiproject.scorm.model.api.ActivitySummary;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.Interaction;
import org.sakaiproject.scorm.model.api.LearnerExperience;
import org.sakaiproject.scorm.model.api.Objective;
import org.sakaiproject.scorm.model.api.comparator.LearnerExperienceComparator;
import org.sakaiproject.scorm.model.api.comparator.LearnerExperienceComparator.CompType;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.scorm.service.api.ScormResultService;
import org.sakaiproject.scorm.ui.NameValuePair;
import org.sakaiproject.scorm.ui.console.components.AccessStatusColumn;
import org.sakaiproject.scorm.ui.console.components.AttemptNumberAction;
import org.sakaiproject.scorm.ui.console.components.ContentPackageDetailPanel;
import org.sakaiproject.scorm.ui.console.components.DecoratedDatePropertyColumn;
import org.sakaiproject.scorm.ui.console.components.MaxProgressMeasureColumn;
import org.sakaiproject.scorm.ui.console.components.MaxScoreColumn;
import org.sakaiproject.scorm.ui.console.components.ProgressMeasureColumn;
import org.sakaiproject.scorm.ui.console.components.ScoreColumn;
import org.sakaiproject.scorm.ui.console.pages.ConsoleBasePage;
import org.sakaiproject.scorm.ui.player.util.Utils;
import org.sakaiproject.wicket.markup.html.repeater.data.presenter.EnhancedDataPresenter;
import org.sakaiproject.wicket.markup.html.repeater.data.table.Action;
import org.sakaiproject.wicket.markup.html.repeater.data.table.ActionColumn;
import org.sakaiproject.wicket.markup.html.repeater.util.EnhancedDataProvider;

public class ResultsListPage extends ConsoleBasePage {

	private static final long serialVersionUID = 1L;

	private static final ResourceReference PAGE_ICON = new ResourceReference(LearnerResultsPage.class, "res/report.png");

	@SuppressWarnings("unused")
	private static final Log LOG = LogFactory.getLog(ResultsListPage.class);

	@SpringBean
	LearningManagementSystem lms;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormContentService")
	ScormContentService contentService;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormResultService")
	ScormResultService resultService;

	private static final String NEW_LINE			= "\n";
	private static final String DOUBLE_QUOTE		= "\"";
	private static final String CSV_DELIMITER		= ",";
	private static final String EMPTY_CELL			= DOUBLE_QUOTE + DOUBLE_QUOTE + CSV_DELIMITER;
	private static final String SUMMARY_INDENT		= EMPTY_CELL + EMPTY_CELL + EMPTY_CELL + EMPTY_CELL ;
	private static final String INTERACTION_INDENT	= SUMMARY_INDENT + SUMMARY_INDENT + EMPTY_CELL;
	private static final String OBJECTIVE_INDENT	= INTERACTION_INDENT + INTERACTION_INDENT + EMPTY_CELL;
	private static		 String CSV_HEADERS;
	private static final String all = "0";
	private static final String admin = "1";
	private static final String student = "2";
	private NameValuePair selectedValue = null;
	private String currentLeanerRole = "";
	private String currentLearnerId = "";

	@SuppressWarnings("serial")
	public ResultsListPage(PageParameters pageParams) {
		super(pageParams);

		final long contentPackageId = pageParams.getLong("contentPackageId");
		final ContentPackage contentPackage = contentService.getContentPackage(contentPackageId);

		// SCO-94 - deny users who do not have scorm.view.results permission
		String context = lms.currentContext();
		boolean canViewResults = lms.canViewResults( context );
		currentLeanerRole = lms.getRoleLearner(lms.currentLearnerId(), context);
		currentLearnerId = lms.currentLearnerId();
		Label heading;
		if(contentPackage.isDeleted()){
			heading = new Label( "heading", new ResourceModel( "page.heading.notAllowed.deleted" ));
			canViewResults = false;
		} else {
			heading = new Label( "heading", new ResourceModel( "page.heading.notAllowed" ) );
		}
		
		add( heading );	
		
		// Hoctdy add start #8755		
		List<NameValuePair> typeUser = new LinkedList<NameValuePair>();
		NameValuePair allItem = new NameValuePair(all,getLocalizer().getString("selected.all", this));
		NameValuePair adminItem = new NameValuePair(admin,getLocalizer().getString("selected.admin", this));
		NameValuePair studentItem = new NameValuePair(student, getLocalizer().getString("selected.student", this));
		typeUser.add(allItem);
		typeUser.add(adminItem);
		typeUser.add(studentItem);
		if(null == selectedValue) {
			selectedValue = allItem;
		}	

		
		final AttemptDataProvider dataProvider = new AttemptDataProvider(contentPackageId, selectedValue.getValue(),
														contentPackage,currentLeanerRole,currentLearnerId,null, false, resultService);
		dataProvider.setFilterConfigurerVisible(true);
		dataProvider.setTableTitle(getLocalizer().getString("table.title", this));		
		
		
		DropDownChoice typeUserChoice = new DropDownChoice("typeUser", new PropertyModel(this, "selectedValue"), (List) typeUser);
		typeUserChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
					AttemptDataProvider dataProvider = new AttemptDataProvider(contentPackageId, selectedValue.getValue(),
													contentPackage,currentLeanerRole,currentLearnerId, null, false, resultService);
					dataProvider.setFilterConfigurerVisible(true);
									
				
					// TODO Auto-generated method stub
					buildExportHeaders();
					AbstractReadOnlyModel<File> fileModel = createCSVDataFile(contentPackage, dataProvider);
					AbstractReadOnlyModel<File> fileModelExcel = createExcelDataFile(contentPackage, dataProvider);
					
					DownloadLink btnExport = new DownloadLink( "btnExport", fileModel, contentPackage.getTitle() + "_results.csv" );
					btnExport.setDeleteAfterDownload( true );
					btnExport.setOutputMarkupId(true);
					

					DownloadLink btnExportExcel = new DownloadLink( "btnExportExcel", fileModelExcel, contentPackage.getTitle() + "_results.xlsx" );
					btnExportExcel.setDeleteAfterDownload( true );					
					btnExportExcel.setOutputMarkupId(true);
					
					arg0.getPage().get("btnExportExcel").remove();
					arg0.getPage().add(btnExportExcel);
					arg0.getPage().get("btnExport").remove();
					arg0.getPage().add(btnExport);
					
					EnhancedDataPresenter presenterNew = new EnhancedDataPresenter("attemptPresenter", getColumns(), dataProvider);
					presenterNew.setOutputMarkupId(true);
				
					arg0.getPage().remove("attemptPresenter");
					arg0.getPage().add(presenterNew);
					setResponsePage(arg0.getPage());
				}
			});
				
		add(typeUserChoice);
		// Hoctdy add end #8755

		// SCO-127
		buildExportHeaders();
		
		AbstractReadOnlyModel<File> fileModel = createCSVDataFile(contentPackage, dataProvider);
		AbstractReadOnlyModel<File> fileModelExcel = createExcelDataFile(contentPackage, dataProvider);
		
		DownloadLink btnExport = new DownloadLink( "btnExport", fileModel, contentPackage.getTitle() + "_results.csv" );
		
		btnExport.setDeleteAfterDownload( true );
		btnExport.setOutputMarkupId(true);			
		add( btnExport );

		DownloadLink btnExportExcel = new DownloadLink( "btnExportExcel", fileModelExcel, contentPackage.getTitle() + "_results.xlsx" );
		btnExportExcel.setDeleteAfterDownload( true );
		btnExportExcel.setOutputMarkupId(true);	
		add( btnExportExcel );
		
		if(currentLeanerRole.equals("Student")){
			typeUserChoice.setVisible(false);
		}

		if( !canViewResults )
		{
			btnExport.setVisibilityAllowed( false );			
			btnExportExcel.setVisibilityAllowed(false);
			heading.setVisibilityAllowed( true );
			add( new WebMarkupContainer( "attemptPresenter" ) );
			add( new WebMarkupContainer( "details" ) );
		}
		else
		{
			// SCO-94
			heading.setVisibilityAllowed( false );

			addBreadcrumb(new Model(contentPackage.getTitle()), ResultsListPage.class, new PageParameters(), false);
			
			EnhancedDataPresenter presenter = new EnhancedDataPresenter("attemptPresenter", getColumns(), dataProvider);

			add(presenter);

			add(new ContentPackageDetailPanel("details", contentPackage));
		}
		getApplication().getApplicationSettings().setPageExpiredErrorPage(getApplication().getHomePage());
		
	}
	
	private AbstractReadOnlyModel<File> createExcelDataFile(ContentPackage contentPackage, AttemptDataProvider dataProvider ){
		AbstractReadOnlyModel<File> fileModelExcel = new AbstractReadOnlyModel<File>()
		{
			@Override
			public File getObject()
			{
				File tempFile = null;
				try
				{
					tempFile = File.createTempFile( contentPackage.getTitle() + "_results", ".xlsx" );
					generateExportExcel(dataProvider,tempFile);					
				}
				catch( IOException ex )
				{
					LOG.error( "Could not generate results export: ", ex );
				}

				return tempFile;
			}
		};
		return fileModelExcel;
	}
	
	private AbstractReadOnlyModel<File> createCSVDataFile(ContentPackage contentPackage, AttemptDataProvider dataProvider ){
		AbstractReadOnlyModel<File> fileModel = new AbstractReadOnlyModel<File>()
		{
			@Override
			public File getObject()
			{
				File tempFile = null;
				try
				{
					tempFile = File.createTempFile( contentPackage.getTitle() + "_results", ".csv" );
					InputStream data = new ByteArrayInputStream( generateExportCSV( dataProvider ).getBytes() );
					Files.writeTo( tempFile, data );
				}
				catch( IOException ex )
				{
					LOG.error( "Could not generate results export: ", ex );
				}

				return tempFile;
			}
		};
		return fileModel;
	}

	/**
	 * Instantiate the header row for the export, if it hasn't already been instantiated
	 */
	private void buildExportHeaders()
	{
		if( StringUtils.isBlank( CSV_HEADERS ) )
		{
			CSV_HEADERS = DOUBLE_QUOTE + getLocalizer().getString( "export.headers.learner", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.lastAttempt", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.status", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.attempts", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.title", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.progress_measure", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.start_time", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.duration", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.score", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.completionStatus", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.successStatus", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.identifier", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.type", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.weighting", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.latency", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.time", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.result", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.description", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.correctResponse", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.learnerResponse", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.objectiveIdentifier", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.objectiveDescription", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.objectiveCompletionStatus", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.objectiveSuccessStatus", this ) + DOUBLE_QUOTE + CSV_DELIMITER + 
						DOUBLE_QUOTE + getLocalizer().getString( "export.headers.objectiveScore", this ) + DOUBLE_QUOTE + NEW_LINE;
		}
	}

	/**
	 * Export all results for the current module to a CSV file.
	 * 
	 * @param dataProvider used to respect current sort order of the UI
	 * @return string representation of the CSV file
	 */
	private String generateExportCSV( AttemptDataProvider attemptProvider )
	{
		// Create the column headers
		StringBuilder csv = new StringBuilder();
		csv.append( CSV_HEADERS );

		Iterator<LearnerExperience> itr = attemptProvider.iterator( 0, attemptProvider.size() );
		while( itr.hasNext() )
		{
			// Learner info
			LearnerExperience learner = itr.next();
			csv.append( DOUBLE_QUOTE ).append( learner.getLearnerName() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
			csv.append( DOUBLE_QUOTE ).append( Objects.toString( learner.getLastAttemptDate(), "" ) ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
			csv.append( DOUBLE_QUOTE ).append( getStatusLabel( learner.getStatus() ) ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
			csv.append( DOUBLE_QUOTE ).append( learner.getNumberOfAttempts() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );

			// Get the summaries for all attempts for the current user
			List<ActivitySummary> summaries = new ArrayList<>();
			for( int i = 1; i <= learner.getNumberOfAttempts(); i++ )
			{
				summaries.addAll( resultService.getActivitySummaries( learner.getContentPackageId(), learner.getLearnerId(), i ) );
			}

			if( summaries.isEmpty() )
			{
				csv.append( NEW_LINE );
			}

			for( int i = 0; i < summaries.size(); i++ )
			{
				if( i != 0 )
				{
					csv.append( SUMMARY_INDENT );
				}

				// Summary info
				ActivitySummary summary = summaries.get( i );
				csv.append( DOUBLE_QUOTE ).append( summary.getTitle() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
				csv.append( DOUBLE_QUOTE ).append( Utils.getPercentageString(summary.getProgressMeasure()) ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
				csv.append( DOUBLE_QUOTE ).append( Objects.toString( summary.getStartDate() , "" )).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
				csv.append( DOUBLE_QUOTE ).append( summary.getTotalSessionSecondsDisplay()).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
				csv.append( DOUBLE_QUOTE ).append( Utils.getNumberPercententString(summary.getScaled()) ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
				csv.append( DOUBLE_QUOTE ).append( Objects.toString( summary.getCompletionStatus(), "" ) ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
				csv.append( DOUBLE_QUOTE ).append( Objects.toString( summary.getSuccessStatus(), "" ) ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );

				// Get the interactions
				ActivityReport report = resultService.getActivityReport( summary.getContentPackageId(), summary.getLearnerId(), summary.getAttemptNumber(), summary.getScoId() );
				if( report != null )
				{
					List<Interaction> interactions = report.getInteractions();
					if( interactions.isEmpty() )
					{
						csv.append( NEW_LINE );
					}

					for( int j = 0; j < interactions.size(); j++ )
					{
						if( j != 0 )
						{
							csv.append( INTERACTION_INDENT );
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
						csv.append( DOUBLE_QUOTE ).append( interaction.getInteractionId() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
						csv.append( DOUBLE_QUOTE ).append( interaction.getType() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
						csv.append( DOUBLE_QUOTE ).append( interaction.getWeighting() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
						csv.append( DOUBLE_QUOTE ).append( interaction.getLatency() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
						csv.append( DOUBLE_QUOTE ).append( interaction.getTimestamp() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
						csv.append( DOUBLE_QUOTE ).append( interaction.getResult() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
						csv.append( DOUBLE_QUOTE ).append( interaction.getDescription() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
						csv.append( DOUBLE_QUOTE ).append( correctResponses.toString() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
						csv.append( DOUBLE_QUOTE ).append( interaction.getLearnerResponse() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );

						// Objective info
						List<Objective> objectives = interaction.getObjectives();
						for( int k = 0; k < objectives.size(); k++ )
						{
							if( k != 0 )
							{
								csv.append( OBJECTIVE_INDENT );
							}

							Objective objective = objectives.get( k );
							csv.append( DOUBLE_QUOTE ).append( objective.getId() ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
							csv.append( DOUBLE_QUOTE ).append( Objects.toString( objective.getDescription(), "" ) ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
							csv.append( DOUBLE_QUOTE ).append( Objects.toString( objective.getCompletionStatus(), "" ) ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
							csv.append( DOUBLE_QUOTE ).append( Objects.toString( objective.getSuccessStatus(), "" ) ).append( DOUBLE_QUOTE ).append( CSV_DELIMITER );
							csv.append( DOUBLE_QUOTE ).append( Objects.toString( objective.getScore().getScaled(), "" ) ).append( DOUBLE_QUOTE ).append( NEW_LINE );
						}

						if( objectives.isEmpty() )
						{
							csv.append( NEW_LINE );
						}
					}
				}
				else
				{
					csv.append( NEW_LINE );
				}
			}
		}

		return csv.toString();
	}

	// Hoctdy add start 
	private void generateExportExcel(AttemptDataProvider attemptProvider, File tempFile) throws IOException {
		FileOutputStream os = new FileOutputStream(tempFile);
		
		XSSFWorkbook workbook = new XSSFWorkbook();
//		// Create a blank spreadsheet
		XSSFSheet spreadsheet = workbook.createSheet("Results");		
//		// Create row object
		XSSFRow row;
		List<Object[]> lstData = new ArrayList<Object[]>();
		// This Header needs to be written (Object[])
		Object[] header = buildHeaderExportExcel();
		lstData.add(header);
		lstData.addAll(generateExportExcel(resultService, attemptProvider));
		
		// export
		int rowid = 0;
		for (Object[] objLst : lstData) {
			row = spreadsheet.createRow(rowid++);
			int cellid = 0;
			for (Object obj : objLst) {
				Cell cell = row.createCell(cellid++);
				cell.setCellValue((String) obj);				
			}
		}
		int size = header.length;
		for(int i = 0; i< size ; i++){
			spreadsheet.autoSizeColumn(i);
		}
		workbook.write(os);
		
		os.close();
	}
	
	
	// Hoctdy add end
	/**
	 * Determine the proper status label for the give status integer
	 * @param status status integer
	 * @return the String status label corresponding to the status integer given
	 */

	private List<IColumn> getColumns() {
		IModel learnerNameHeader = new ResourceModel("column.header.learner.name");
		IModel attemptedHeader = new ResourceModel("column.header.attempted");
		IModel statusHeader = new ResourceModel("column.header.status");
		IModel numberOfAttemptsHeader = new ResourceModel("column.header.attempt.number");
		IModel progressMeasure = new ResourceModel("column.header.progress");
		IModel score = new ResourceModel("column.header.score");
		IModel completedHeader = new ResourceModel("column.header.completed");
		IModel durationHeader = new ResourceModel("column.header.duration");
		

		List<IColumn> columns = new LinkedList<IColumn>();

		ActionColumn actionColumn = new ActionColumn(learnerNameHeader, "learnerName", "learnerName");

		String[] paramPropertyExpressions = {"contentPackageId", "learnerId"};
		
		

		Action summaryAction = new Action("learnerName", LearnerResultsPage.class, paramPropertyExpressions);
		actionColumn.addAction(summaryAction);
		columns.add(actionColumn);
		
		// Hoctdy add start
		columns.add(new MaxProgressMeasureColumn(progressMeasure, "maxProgress"));
		columns.add(new MaxScoreColumn(score, "maxScore"));
		columns.add(new PropertyColumn(completedHeader, "maxCompletedStatus", "maxCompletedStatus"));
		columns.add(new PropertyColumn(durationHeader, "maxDuration", "maxDuration"));
		// Hoctdy add end
		columns.add(new DecoratedDatePropertyColumn(attemptedHeader, "lastAttemptDate", "lastAttemptDate"));

		columns.add(new AccessStatusColumn(statusHeader, "status"));

		// Hoctdy add hide column
		ActionColumn attemptNumberActionColumn = new ActionColumn(numberOfAttemptsHeader, "numberOfAttempts", "numberOfAttempts");
		attemptNumberActionColumn.addAction(new AttemptNumberAction("numberOfAttempts", LearnerResultsPage.class, paramPropertyExpressions));
		columns.add(attemptNumberActionColumn);
		// Hoctdy add hide column

		return columns;
	}


	@Override
	protected ResourceReference getPageIconReference() {
		return PAGE_ICON;
	}

	public NameValuePair getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(NameValuePair selectedValue) {
		this.selectedValue = selectedValue;
	}
}
