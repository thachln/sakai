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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.adl.validator.contentpackage.LaunchData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.WildcardListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.collections.MicroMap;
import org.sakaiproject.scorm.dao.api.ContentPackageManifestDao;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.ContentPackageManifest;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.scorm.service.api.ScormResultService;
import org.sakaiproject.scorm.ui.NameValuePair;
import org.sakaiproject.service.gradebook.shared.GradebookExternalAssessmentService;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.wicket.markup.html.form.CancelButton;
import org.sakaiproject.wicket.model.DecoratedPropertyModel;
import org.sakaiproject.wicket.model.SimpleDateFormatPropertyModel;

public class PackageConfigurationPage extends ConsoleBasePage {

	public static class AssessmentSetup implements Serializable{

		private static final long serialVersionUID = 1L;
		LaunchData launchData;
		Double numberOffPoints = 100d;
		boolean synchronizeSCOWithGradebook;

		public AssessmentSetup() {
			super();
		}

		public AssessmentSetup(LaunchData launchData) {
			super();
			this.launchData = launchData;
		}

		public String getItemIdentifier() {
			return launchData.getItemIdentifier();
		}
		public String getItemTitle() {
			return launchData.getItemTitle();
		}

		public LaunchData getLaunchData() {
			return launchData;
		}

		public Double getNumberOffPoints() {
			return numberOffPoints;
		}

		public boolean issynchronizeSCOWithGradebook() {
			return synchronizeSCOWithGradebook;
		}

		public void setLaunchData(LaunchData launchData) {
			this.launchData = launchData;
		}

		public void setNumberOffPoints(Double numberOffPoints) {
			this.numberOffPoints = numberOffPoints;
		}

		public void setsynchronizeSCOWithGradebook(boolean synchronizeSCOWithGradebook) {
			this.synchronizeSCOWithGradebook = synchronizeSCOWithGradebook;
		}
	}

	public class DisplayNamePropertyModel extends DecoratedPropertyModel implements Serializable{

		private static final long serialVersionUID = 1L;

		public DisplayNamePropertyModel(Object modelObject, String expression) {
			super(modelObject, expression);
		}

		@Override
		public Object convertObject(Object object) {
			String userId = String.valueOf(object);

			return lms.getLearnerName(userId);
		}
	}

	public static class GradebookSetup implements Serializable{
		private static final long serialVersionUID = 1L;
		boolean isGradebookDefined;
		ContentPackage contentPackage;

		public ContentPackage getContentPackage() {
			return contentPackage;
		}

		public void setContentPackage(ContentPackage contentPackage) {
			this.contentPackage = contentPackage;
		}

		ContentPackageManifest contentPackageManifest;
		List<AssessmentSetup> assessments = new ArrayList<PackageConfigurationPage.AssessmentSetup>();

		public List<AssessmentSetup> getAssessments() {
			return assessments;
		}

		public String getContentPackageId() {
			return "" + contentPackage.getContentPackageId();
		}

		public ContentPackageManifest getContentPackageManifest() {
			return contentPackageManifest;
		}

		public boolean isGradebookDefined() {
			return isGradebookDefined;
		}

		public void setContentPackageManifest(ContentPackageManifest contentPackageManifest) {
			this.contentPackageManifest = contentPackageManifest;
			assessments.clear();
			@SuppressWarnings("unchecked")
			List<LaunchData> launchDatas = contentPackageManifest.getLaunchData();
			for (LaunchData launchData : launchDatas) {
				String scormType = launchData.getSCORMType();
				if ("sco".equalsIgnoreCase(scormType)) {
					AssessmentSetup assessment = buildAssessmentSetup(launchData);
					assessments.add(assessment);
				}
			}
		}

		protected AssessmentSetup buildAssessmentSetup(LaunchData launchData) {
			AssessmentSetup assessment = new AssessmentSetup(launchData);
			return assessment;
		}

		public void setGradebookDefined(boolean isGradebookDefined) {
			this.isGradebookDefined = isGradebookDefined;
		}
	}

	public class TryChoiceRenderer extends ChoiceRenderer implements Serializable{
		private static final long serialVersionUID = 1L;

		public TryChoiceRenderer() {
			super();
		}

		@Override
		public Object getDisplayValue(Object object) {
			Integer n = (Integer) object;

			if (n == -1)
			{
				return unlimitedMessage;
			}

			return object;
		}
	}

	private static final long serialVersionUID = 1L;
	private static ResourceReference PAGE_ICON = new ResourceReference(PackageConfigurationPage.class, "res/table_edit.png");
	@SpringBean
	LearningManagementSystem lms;

	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormContentService")
	ScormContentService contentService;
	
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormResultService")
	ScormResultService resultService;

	@SpringBean(name = "org.sakaiproject.service.gradebook.GradebookExternalAssessmentService")
	GradebookExternalAssessmentService gradebookExternalAssessmentService;	


	@SpringBean(name = "org.sakaiproject.scorm.dao.api.ContentPackageManifestDao")
	ContentPackageManifestDao contentPackageManifestDao;
	// hoctdy add start
	private static Log log = LogFactory.getLog(PackageConfigurationPage.class);

	private String unlimitedMessage;
	private List<NameValuePair> selectedList = new ArrayList<>();
	private List<NameValuePair> selectedRequiredPackageList = new ArrayList<>();
	private NameValuePair selectedNextPackage = null;

	public NameValuePair getSelectedNextPackage() {
		return selectedNextPackage;
	}

	public void setSelectedNextPackage(NameValuePair selectedNextPackageList) {
		this.selectedNextPackage = selectedNextPackageList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PackageConfigurationPage(PageParameters params) {
		super(params);
		long contentPackageId = params.getLong("contentPackageId");

		final ContentPackage contentPackage = contentService.getContentPackage(contentPackageId);
		final GradebookSetup gradebookSetup = getAssessmentSetup(contentPackage);

		final Class pageSubmit;
		final Class pageCancel;
		final CheckBox checkboxAttempTracking;
		final CheckBox checkboxHighestResult;
		final RadioGroup groupNextPackage;
		

		// @NOTE this is a hack that allows us to change the destination we
		// are redirected to after form submission depending on where we come from
		// I'm sure there's a more reliable way to do this is Wicket but it's not trivial to figure it out.
		if(params.getBoolean("no-toolbar")) {
			pageSubmit = DisplayDesignatedPackage.class;
			pageCancel = DisplayDesignatedPackage.class;
		} else {
			pageSubmit = PackageListPage.class;
			pageCancel = PackageListPage.class;
		}
		
		final Component feedbackPanel = new FeedbackPanel("feedback").setOutputMarkupPlaceholderTag(true);

		Form form = new Form("configurationForm") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit() {
				if (gradebookSetup.isGradebookDefined()) {
					List<AssessmentSetup> assessments = gradebookSetup.getAssessments();
					for (AssessmentSetup assessmentSetup : assessments) {
						boolean on = assessmentSetup.issynchronizeSCOWithGradebook();
						String assessmentExternalId = getAssessmentExternalId(gradebookSetup, assessmentSetup);
						String context = getContext();
						boolean has = gradebookExternalAssessmentService.isExternalAssignmentDefined(context, assessmentExternalId);
						String fixedTitle = contentPackage.getTitle();
						if (has && on) { 
							gradebookExternalAssessmentService.updateExternalAssessment(context, assessmentExternalId, null, fixedTitle, assessmentSetup.numberOffPoints, gradebookSetup.getContentPackage().getDueOn()); 
							} else if (!has && on) { 
								gradebookExternalAssessmentService.addExternalAssessment(context, assessmentExternalId, null, fixedTitle, assessmentSetup.numberOffPoints, gradebookSetup.getContentPackage().getDueOn(), "SCORM player"); 
							} else if (has && !on) { 
								gradebookExternalAssessmentService.removeExternalAssessment(context, assessmentExternalId);
							}
						
					}
				}
				
				String idsGroup = getIDsOfGroup(selectedList);
				contentPackage.setGroupsPermission(idsGroup);
				
				// Save required packages
				String idsRequiredPackages = getIDsOfRequiredPackage(selectedRequiredPackageList);
                contentPackage.setPrerequisite(idsRequiredPackages);
				
				contentPackage.setStartNewAttempt(((CheckBox)this.get("startNewAttempt")).getConvertedInput());				
				contentPackage.setGetHighestResult(((CheckBox)this.get("getHighestResult")).getConvertedInput());
				
				// get next package
				/*WildcardListModel<NameValuePair> lstNextPackage = (WildcardListModel<NameValuePair>) ((RadioGroup)this.get("groupNextPackage")).get("listNextPackages").getDefaultModel();
				if(null != lstNextPackage){
					for(NameValuePair tmp: lstNextPackage.getObject()){
						if(tmp.getSelected()){
							contentPackage.setNextResourceId(tmp.getValue());
							break;
						}
					}
				}	*/
				contentPackage.setNextResourceId(selectedNextPackage.getValue());
				
				contentService.updateContentPackage(contentPackage);
				
				setResponsePage(pageSubmit);
			}

            //FHD Start
			@Override
		    protected void onValidate() {
		        super.onValidate();
		        
		        Date releaseOnDTF = ((DateTimeField) this.get("releaseOnDTF")).getConvertedInput();
		        Date dueOnDTF = ((DateTimeField) this.get("dueOnDTF")).getConvertedInput();
		        Date acceptUntilDTF = ((DateTimeField) this.get("acceptUntilDTF")).getConvertedInput();
		        
		        Map vars = new MicroMap();
		        if (null != dueOnDTF && null != releaseOnDTF && dueOnDTF.compareTo(releaseOnDTF) <= 0) {
		        	vars.put("validateDate", "Due Date");
		        	error(getString("duedate.errorValidator", Model.ofMap(vars)));
		        }
		        
		        if (null != acceptUntilDTF && null != releaseOnDTF && acceptUntilDTF.compareTo(releaseOnDTF) <= 0) {
		        	vars.put("validateDate", "Accept Until");
		        	error(getString("acceptuntil.errorValidator", Model.ofMap(vars)));
		        }
		        
		        if (null != acceptUntilDTF && null == dueOnDTF) {
		        	vars.put("validateDate", "Due Date Blank");
		        	error(getString("duedate.errorRequired", Model.ofMap(vars)));
		        }
		        
		        String idsRequiredPackages = getIDsOfRequiredPackage(selectedRequiredPackageList);
		        if(!"".equals(selectedNextPackage.getValue()) && idsRequiredPackages.contains(selectedNextPackage.getValue())){
		        	vars.put("validateNextPackage", "Next Package Content");
		        	error(getString("nextpackage.errorloop", Model.ofMap(vars)));
		        }
		        
		    }
			//FHD End

			protected String getItemTitle(AssessmentSetup assessmentSetup, String context) {
				String fixedTitle = assessmentSetup.getItemTitle();
				int count = 1;
				while (gradebookExternalAssessmentService.isAssignmentDefined(context, fixedTitle)) {
					fixedTitle = assessmentSetup.getItemTitle() + " (" + count++ + ")";
				}
				return fixedTitle;
			}
		};

		List<Integer> tryList = new LinkedList<Integer>();

		tryList.add(-1);
		for (int i = 1; i <= 10; i++) {
			tryList.add(i);
		}

		this.unlimitedMessage = getLocalizer().getString("unlimited", this);

		TextField categoryNameField = new TextField("categoryName", new PropertyModel(contentPackage, "category"));
		categoryNameField.setRequired(true);
		form.add(categoryNameField);
		
		// create list group
		List<Group> allGroup = resultService.getAllGroupInSite(lms.currentContext());
		List<NameValuePair> groupsPairValue = new LinkedList<NameValuePair>();
		
		if ((allGroup != null) && (allGroup.size() > 0)) {
			for(Group tmp: allGroup){
				NameValuePair nameValuePair = new NameValuePair(tmp.getId(),tmp.getTitle());
				
				// Unselect as default
				nameValuePair.setSelected(false);
				groupsPairValue.add(nameValuePair );
			}
		}
		
		ListView lstview = createListViewComponent(groupsPairValue);
		getSelectedGroupValue(contentPackage, lstview);

		form.add(lstview);
		

        // Create list package prerequisites
        List<ContentPackage> allPackages = contentService.getContentPackages();
        List<NameValuePair> packagesPairValue = new LinkedList<NameValuePair>();
        
        if ((allPackages != null) && (allPackages.size() > 0)) {
            // Skip current package
            for (ContentPackage sormPackage : allPackages) {
                if (!contentPackage.getResourceId().equals(sormPackage.getResourceId())) {
                    NameValuePair nameValuePair = new NameValuePair(sormPackage.getResourceId(),
                            sormPackage.getTitle());
                    
                    // Unselect as default
                    nameValuePair.setSelected(false);
                    packagesPairValue.add(nameValuePair);
                    
                    if(null != contentPackage.getNextResourceId() 
                    		&& !"".equals(contentPackage.getNextResourceId())
                    		&& contentPackage.getNextResourceId().equals(sormPackage.getResourceId())){
                    	selectedNextPackage = new NameValuePair(sormPackage.getResourceId(), sormPackage.getTitle());
                    }
                }
            }
        }

        ListView lstviewPackages = createListViewPackagesComponent(packagesPairValue);
        getSelectedRequiredPackageValue(contentPackage, lstviewPackages);
        form.add(lstviewPackages);
        
        List<NameValuePair> nextPackages = new ArrayList<>();
        nextPackages.add(new NameValuePair("", ""));
        nextPackages.addAll(packagesPairValue);
        // set default value
        if(null == selectedNextPackage){
        	selectedNextPackage = nextPackages.get(0);
        }
        
        DropDownChoice groupsChoice = new DropDownChoice("groupNextPackage", new PropertyModel(this, "selectedNextPackage"), (List) nextPackages);
		groupsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget request) {
				}
			});	
        form.add(groupsChoice);
		
		TextField nameField = new TextField("packageName", new PropertyModel(contentPackage, "title"));
		nameField.setRequired(true);
		

		form.add(nameField);
		DateTimeField releaseOnDTF = new DateTimeField("releaseOnDTF", new PropertyModel<Date>(contentPackage, "releaseOn"));
		releaseOnDTF.setRequired(true);		
		
		
		form.add(releaseOnDTF);
		form.add(new DateTimeField("dueOnDTF", new PropertyModel(contentPackage, "dueOn")));
		form.add(new DateTimeField("acceptUntilDTF", new PropertyModel(contentPackage, "acceptUntil")));
		form.add(new DropDownChoice("numberOfTries", new PropertyModel(contentPackage, "numberOfTries"), tryList, new TryChoiceRenderer()));
		form.add(new Label("createdBy", new DisplayNamePropertyModel(contentPackage, "createdBy")));
		form.add(new Label("createdOn", new SimpleDateFormatPropertyModel(contentPackage, "createdOn")));
		form.add(new Label("modifiedBy", new DisplayNamePropertyModel(contentPackage, "modifiedBy")));
		form.add(new Label("modifiedOn", new SimpleDateFormatPropertyModel(contentPackage, "modifiedOn")));
		
		//Hoctdy add start
		checkboxAttempTracking = new CheckBox("startNewAttempt", new PropertyModel<Boolean>(contentPackage, "startNewAttempt"));
		checkboxAttempTracking.setVisible(true);
		checkboxAttempTracking.setDefaultModel(Model.of(contentPackage.getStartNewAttempt()));
		form.add(checkboxAttempTracking);
		
		checkboxHighestResult = new CheckBox("getHighestResult", new PropertyModel<Boolean>(contentPackage, "getHighestResult"));
		checkboxHighestResult.setVisible(true);
		checkboxHighestResult.setDefaultModel(Model.of(contentPackage.getGetHighestResult()));
		form.add(checkboxHighestResult);
		
		final WebMarkupContainer verifySyncWithGradebook = new WebMarkupContainer( "verifySyncWithGradebook" );
		verifySyncWithGradebook.setOutputMarkupId( true );
		verifySyncWithGradebook.setOutputMarkupPlaceholderTag( true );
		verifySyncWithGradebook.setVisible( false );
		form.add( verifySyncWithGradebook );
		//Hoctdy add end
		
		ListView scos;
		form.add(scos = new ListView("scos", gradebookSetup.getAssessments()) {

			private static final long serialVersionUID = 965550162166385688L;

			@Override
			protected void populateItem(final ListItem item) {
				Label label = new Label("itemTitle", new PropertyModel(contentPackage, "title"));
				item.add(label);		
				
				@SuppressWarnings("serial")
				AjaxCheckBox synchronizeSCOWithGradebook = new AjaxCheckBox("synchronizeSCOWithGradebook", new PropertyModel(item.getModelObject(), 
						"synchronizeSCOWithGradebook") )
				{
					@Override
					protected void onUpdate( AjaxRequestTarget target )
					{
						AssessmentSetup as = (AssessmentSetup) item.getModelObject();
						String assessmentExternalId = getAssessmentExternalId( gradebookSetup, as );
						boolean hasGradebookSync = gradebookExternalAssessmentService.isExternalAssignmentDefined( getContext(), assessmentExternalId );
						boolean isChecked = this.getModelObject();
						verifySyncWithGradebook.setVisible( hasGradebookSync && !isChecked );
						target.addComponent( verifySyncWithGradebook );
					}
				};

				item.add(synchronizeSCOWithGradebook);
			}
		});
		scos.setVisible(gradebookSetup.isGradebookDefined() && !gradebookSetup.getAssessments().isEmpty());

		form.add(new CancelButton("cancel", pageCancel));
		form.add(feedbackPanel);
		if(lms.canConfigure(lms.currentContext())){
			add(form);
			add(new Label( "config.not.Permission",new ResourceModel( "page.notAllowed" ) ));
		} else {
			add(form.setVisible(false));
			add(new Label( "config.not.Permission",new ResourceModel( "page.notAllowed" ) ));
		}
		
	}
	
	
    /**
	 * Create List View Packages
	 * @param packagesPairValue
	 * @return
	 */
    private ListView createListViewPackagesComponent(List<NameValuePair> packagesPairValue) {
        List<NameValuePair> lstListViewData = packagesPairValue;
        @SuppressWarnings("unchecked")
        ListView listView = new ListView("listPackages", lstListViewData) {
            protected void populateItem(ListItem item) {
                NameValuePair wrapper = (NameValuePair) item.getModelObject();
                item.add(new CheckBox("checkPackage", new PropertyModel(wrapper, "selected")) {
                    protected boolean wantOnSelectionChangedNotifications() {
                        return true;
                    }
                });
                item.add(new Label("namePackage", wrapper.getName()));
            }
        };
        listView.setReuseItems(true);
        return listView;
    }
	
	/**
	 * Create List View Group
	 * @param groupsPairValue
	 * @return
	 */
	private ListView createListViewComponent(List<NameValuePair> groupsPairValue) {
		List<NameValuePair> lstListViewData = groupsPairValue;
		@SuppressWarnings("unchecked")
		ListView listView = new ListView("listGroup", lstListViewData)
	    {
	        protected void populateItem(ListItem item)
	        {
	        	 NameValuePair wrapper = (NameValuePair)item.getModelObject();
	        	 item.add(new CheckBox("check", new PropertyModel(wrapper, "selected")){
	        		 protected boolean wantOnSelectionChangedNotifications() {
	        		        return true;
	        		    }
	        	 });
	        	 
	             item.add(new Label("name", wrapper.getName()));	        
	        }
	         
	    };
        
        listView.setReuseItems(true);
        return listView;
		
	}
	
	private void getSelectedGroupValue(ContentPackage contentPackage, ListView listView){
		String idsGroup = contentPackage.getGroupsPermission();	
		selectedList = listView.getModelObject();
		if(null != idsGroup && "" != idsGroup){
			String[] ids = idsGroup.split(",");		

			if(null != ids && ids.length > 0){
				 for(int j = 0; j < selectedList.size(); j++){
					int size = ids.length;
					boolean isExist = false;
					NameValuePair tmp = selectedList.get(j);
					for(int i = 0; i < size ; i++) {
						 String id = (String)ids[i];
						 if(tmp.getValue().equals(id)){
							 isExist = true;
							 break;
						 }
					}
					if(isExist){
						selectedList.get(j).setSelected(true);
					} else {
						selectedList.get(j).setSelected(false);
					}
				 }	
			}	
		} 
		
	}
	
    private void getSelectedRequiredPackageValue(ContentPackage contentPackage, ListView lstviewPackages) {
        String idsRequiredPackage = contentPackage.getPrerequisite();
        selectedRequiredPackageList = lstviewPackages.getModelObject();
        if (null != idsRequiredPackage && "" != idsRequiredPackage) {
            String[] ids = idsRequiredPackage.split(",");

            if (null != ids && ids.length > 0) {
                for (int j = 0; j < selectedRequiredPackageList.size(); j++) {
                    int size = ids.length;
                    boolean isExist = false;
                    NameValuePair tmp = selectedRequiredPackageList.get(j);
                    for (int i = 0; i < size; i++) {
                        String id = (String) ids[i];
                        if (tmp.getValue().equals(id)) {
                            isExist = true;
                            break;
                        }
                    }
                    if (isExist) {
                        selectedRequiredPackageList.get(j).setSelected(true);
                    } else {
                        selectedRequiredPackageList.get(j).setSelected(false);
                    }
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
	private void getSelectedNextPackageValue(ContentPackage contentPackage, ListView lstviewPackages) {
        String nextContentPackageId = contentPackage.getNextResourceId();
        if(null != lstviewPackages.getModelObject()){
	        if (null != nextContentPackageId && "" != nextContentPackageId) {
	        	for (int j = 0; j < lstviewPackages.getModelObject().size(); j++) {
	                NameValuePair tmp = (NameValuePair) lstviewPackages.getModelObject().get(j);                
	                if (nextContentPackageId.equals(tmp.getValue())) {
	                	tmp.setSelected(true);
	                	selectedNextPackage = tmp;
	                } 
	            }
	        } else {
	        	selectedNextPackage = (NameValuePair) lstviewPackages.getModelObject().get(0);
	        	selectedNextPackage.setSelected(true);
	        }
        }
    }


	/**
	 * Get list IDs Group has permission
	 * @param selectedNameValuePairs
	 * @return
	 */
	private String getIDsOfGroup(List<NameValuePair> selectedNameValuePairs){
		StringBuffer idsGroup = new StringBuffer(); 
		if(null != selectedNameValuePairs && !selectedNameValuePairs.isEmpty()) {
			List<NameValuePair> selected = new ArrayList<>();
			for(NameValuePair tmp : selectedNameValuePairs) {
				if(tmp.getSelected()){
					selected.add(tmp);
				}
			}
			if(null != selected && !selected.isEmpty()){
				int i = 0;
				for(NameValuePair tmp: selected){
					i++;
					idsGroup.append(tmp.getValue());
					if(i < selected.size()){
						idsGroup.append(",");
					}					
				}
			}	
		}
		return idsGroup.toString();
	}

    private String getIDsOfRequiredPackage(List<NameValuePair> selectedRequiredPackageNameValuePairs) {
        StringBuffer idsRequiredPackage = new StringBuffer();
        if (null != selectedRequiredPackageNameValuePairs && !selectedRequiredPackageNameValuePairs.isEmpty()) {
            List<NameValuePair> selected = new ArrayList<>();
            for (NameValuePair tmp : selectedRequiredPackageNameValuePairs) {
                if (tmp.getSelected()) {
                    selected.add(tmp);
                }
            }
            if (null != selected && !selected.isEmpty()) {
                int i = 0;
                for (NameValuePair tmp : selected) {
                    i++;
                    idsRequiredPackage.append(tmp.getValue());
                    if (i < selected.size()) {
                        idsRequiredPackage.append(",");
                    }
                }
            }
        }
        return idsRequiredPackage.toString();
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

	protected String getContext() {
		Placement placement = ToolManager.getCurrentPlacement();
		String context = placement.getContext();
		return context;
	}

	@Override
	protected ResourceReference getPageIconReference() {
		return PAGE_ICON;
	}

	private static String getAssessmentExternalId(final GradebookSetup gradebook, AssessmentSetup assessment) {
		String assessmentExternalId = "" + gradebook.getContentPackageId() + ":" + assessment.getLaunchData().getItemIdentifier();
		return assessmentExternalId;
	}
}
