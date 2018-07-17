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
package org.sakaiproject.scorm.ui.console.components;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.scorm.dao.api.ContentPackageManifestDao;
import org.sakaiproject.scorm.exceptions.LearnerNotDefinedException;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.ContentPackageManifest;
import org.sakaiproject.scorm.model.api.Learner;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.ui.console.pages.PackageConfigurationPage.AssessmentSetup;
import org.sakaiproject.scorm.ui.console.pages.PackageConfigurationPage.GradebookSetup;
import org.sakaiproject.service.gradebook.shared.GradebookExternalAssessmentService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.wicket.model.DecoratedPropertyModel;
import org.sakaiproject.wicket.model.SimpleDateFormatPropertyModel;

public class ContentPackageDetailPanel extends Panel {
	@SpringBean(name = "org.sakaiproject.service.gradebook.GradebookExternalAssessmentService")
	GradebookExternalAssessmentService gradebookExternalAssessmentService;
	

	@SpringBean(name = "org.sakaiproject.scorm.dao.api.ContentPackageManifestDao")
	ContentPackageManifestDao contentPackageManifestDao;

	private static final long serialVersionUID = 1L;

	@SpringBean
	LearningManagementSystem lms;
	
	
	public ContentPackageDetailPanel(String id, ContentPackage contentPackage) {		
		super(id, new TypeAwareCompoundPropertyModel(contentPackage));
		final GradebookSetup gradebookSetup = getAssessmentSetup(contentPackage);
		String createdByName = "Unknown";
		String modifiedByName = "Unknown";
		
		if (contentPackage != null) {
			try {
				createdByName = getLearnerDisplay(lms.getLearner(contentPackage.getCreatedBy()));
				modifiedByName = getLearnerDisplay(lms.getLearner(contentPackage.getModifiedBy()));
			} catch (LearnerNotDefinedException e) {
				// Doesn't matter.
			}
		}
		
		add(new Label("title"));
		add(new Label("releaseOn"));
//		add(new Label("numberOffPoints"));
//		add(new Label("synchronizeSCOWithGradebook"));
		add(new Label("dueOn"));
		add(new Label("acceptUntil"));
		add(new Label("numberOfTries", new TriesDecoratedPropertyModel(contentPackage, "numberOfTries")));
		add(new Label("createdBy", new Model(createdByName)));
		add(new Label("createdOn"));
		add(new Label("modifiedBy", new Model(modifiedByName)));
		add(new Label("modifiedOn"));
		
		// hoctdy add attemptNumber and synchronizeSCOWithGradebook
		CheckBox checkbox = new CheckBox("startNewAttempt", new PropertyModel<Boolean>(contentPackage, "startNewAttempt"));
		checkbox.setVisible(true);
		checkbox.setDefaultModel(Model.of(contentPackage.getStartNewAttempt()));
		add(checkbox);
		
		ListView scos;
		add(scos = new ListView("scos", gradebookSetup.getAssessments()) {

			private static final long serialVersionUID = 965550162166385688L;

			@Override
			protected void populateItem(final ListItem item) {
				Label label = new Label("itemTitle", new PropertyModel(contentPackage, "title"));
				item.add(label);
				final WebMarkupContainer verifySyncWithGradebook = new WebMarkupContainer( "verifySyncWithGradebook" );
				verifySyncWithGradebook.setOutputMarkupId( true );
				verifySyncWithGradebook.setOutputMarkupPlaceholderTag( true );
				verifySyncWithGradebook.setVisible( false );
				item.add( verifySyncWithGradebook );

				AjaxCheckBox synchronizeSCOWithGradebook = new AjaxCheckBox("synchronizeSCOWithGradebook", new PropertyModel(item.getModelObject(), 
						"synchronizeSCOWithGradebook") )
				{
					@Override
					protected void onUpdate( AjaxRequestTarget target ){						
					}
				};
				item.add(synchronizeSCOWithGradebook);
			}
		});
		scos.setVisible(gradebookSetup.isGradebookDefined() && !gradebookSetup.getAssessments().isEmpty());
		
	}
	private GradebookSetup getAssessmentSetup(ContentPackage contentPackage) {
		final GradebookSetup gradebookSetup = new GradebookSetup();
		String context = getContext();
		boolean isGradebookDefined = gradebookExternalAssessmentService.isGradebookDefined(context);
		gradebookSetup.setGradebookDefined(isGradebookDefined);
		gradebookSetup.setContentPackage(contentPackage);
		if (isGradebookDefined) {
			ContentPackageManifest contentPackageManifest = contentPackageManifestDao.load(contentPackage.getManifestId());
			gradebookSetup.setContentPackageManifest(contentPackageManifest);
			List<AssessmentSetup> assessments = gradebookSetup.getAssessments();
			for (AssessmentSetup as : assessments) {
				String assessmentExternalId = getAssessmentExternalId(gradebookSetup, as);
				boolean has = gradebookExternalAssessmentService.isExternalAssignmentDefined(getContext(), assessmentExternalId);
				as.setsynchronizeSCOWithGradebook(has);
			}
		}
		return gradebookSetup;
	}
	private static String getAssessmentExternalId(final GradebookSetup gradebook, AssessmentSetup assessment) {
		String assessmentExternalId = "" + gradebook.getContentPackageId() + ":" + assessment.getLaunchData().getItemIdentifier();
		return assessmentExternalId;
	}
	
	
	private String getLearnerDisplay(Learner learner) {
		return new StringBuilder(learner.getDisplayName()).append(" (").append(learner.getDisplayId())
			.append(")").toString();
	}
	

	private Label newPropertyLabel(ContentPackage contentPackage, String expression) {
		return new Label(expression, new PropertyModel(contentPackage, expression));
	}
	
	private Label newDatePropertyLabel(ContentPackage contentPackage, String expression) {
		return new Label(expression, new SimpleDateFormatPropertyModel(contentPackage, expression));
	}
	
	private Label newTriesPropertyLabel(ContentPackage contentPackage, String expression) {
		return new Label(expression, new TriesDecoratedPropertyModel(contentPackage, expression));
	}
	
	public class TriesDecoratedPropertyModel extends DecoratedPropertyModel {

		private static final long serialVersionUID = 1L;

		public TriesDecoratedPropertyModel(Object modelObject, String expression) {
			super(modelObject, expression);
		}

		@Override
		public Object convertObject(Object object) {
			String str = String.valueOf(object);
			
			if (str.equals("-1"))
				return "Unlimited";
			
			return str;
		}
		
	}
	
	protected String getContext() {
		Placement placement = ToolManager.getCurrentPlacement();
		String context = placement.getContext();
		return context;
	}
	
}
