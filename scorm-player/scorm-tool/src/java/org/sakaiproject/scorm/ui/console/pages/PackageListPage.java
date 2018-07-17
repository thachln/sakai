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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.PageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.PropertyResolver;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.scorm.api.ScormConstants;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.scorm.service.api.ScormResultService;
import org.sakaiproject.scorm.ui.console.components.CollapsiblePanel;
import org.sakaiproject.scorm.ui.console.components.DecoratedDatePropertyColumn;
import org.sakaiproject.scorm.ui.player.pages.PlayerPage;
import org.sakaiproject.scorm.ui.player.util.Utils;
import org.sakaiproject.scorm.ui.reporting.pages.LearnerResultsPage;
import org.sakaiproject.scorm.ui.reporting.pages.ResultsListPage;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.wicket.markup.html.link.BookmarkablePageLabeledLink;
import org.sakaiproject.wicket.markup.html.repeater.data.table.Action;
import org.sakaiproject.wicket.markup.html.repeater.data.table.ActionColumn;
import org.sakaiproject.wicket.markup.html.repeater.data.table.BasicDataTable;
import org.sakaiproject.wicket.markup.html.repeater.data.table.ImageLinkColumn;

public class PackageListPage extends ConsoleBasePage implements ScormConstants {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(PackageListPage.class);

	private static final ResourceReference PAGE_ICON = new ResourceReference(PackageListPage.class, "res/table.png");
	private static final ResourceReference DELETE_ICON = new ResourceReference(PackageListPage.class, "res/delete.png");
	private static final ResourceReference UP_ICON = new ResourceReference(PackageListPage.class, "res/sortascending.gif");
	private static final ResourceReference DOWN_ICON = new ResourceReference(PackageListPage.class, "res/sortdescending.gif");
	@SpringBean
	LearningManagementSystem lms;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormContentService")
	ScormContentService contentService;
	
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormResultService")
	ScormResultService resultService;
	
    private String currentLeanerRole = "";
	private String currentLearnerId = "";

	public PackageListPage(PageParameters params) {
        if (log.isDebugEnabled()) {
            log.debug("BaoNQ.Debug PackageListPage");
        }
        
		final String context = lms.currentContext();
		final boolean canConfigure = lms.canConfigure(context);
		final boolean canGrade = lms.canGrade(context);
		final boolean canViewResults = lms.canViewResults(context);
		final boolean canDelete = lms.canDelete(context);
		
		List<ContentPackage> contentPackages = getContentPackagePermission(context);

		List<IColumn<ContentPackage>> columns = new LinkedList<IColumn<ContentPackage>>();
		String[] paramPropertyExpressions = {"contentPackageId", "resourceId", "title"};
		// Hoctdy start
		if(canConfigure) {			
			columns.add(new ImageLinkColumn(new Model(""), UpPage.class, paramPropertyExpressions, UP_ICON, "up"));
			columns.add(new ImageLinkColumn(new Model(""), DownPage.class, paramPropertyExpressions, DOWN_ICON, "down"));
		}
		// Hoctdy end
		
		ActionColumn actionColumn = new ActionColumn(new StringResourceModel("column.header.content.package.name", this, null), "title", "title");

		Action launchAction = new Action("title", PlayerPage.class, paramPropertyExpressions){
			private static final long serialVersionUID = 1L;
			
			/**
			 * Create hyperlink for title of the lesson.
			 * @param id
			 * @param bean
			 * @return
			 * @see org.sakaiproject.wicket.markup.html.repeater.data.table.Action#newLink(java.lang.String, java.lang.Object)
			 */
			@Override
			public Component newLink(String id, Object bean) {
				IModel<String> labelModel;
				if (displayModel != null) {
					labelModel = displayModel;
				} else {
					String labelValue = String.valueOf(PropertyResolver.getValue(labelPropertyExpression, bean));
					labelModel = new Model<String>(labelValue);
				}

                ContentPackage currenPackage = (ContentPackage) bean;
                Link link;

                if (currenPackage.isPassPrerequisite()) {
                    PageParameters params = buildPageParameters(paramPropertyExpressions, bean);
                    link = new BookmarkablePageLabeledLink(id, labelModel, pageClass, params);

                    if (popupWindowName != null) {
                        PopupSettings popupSettings = new PopupSettings(PageMap.forName(popupWindowName), PopupSettings.RESIZABLE);
                        popupSettings.setWidth(1020);
                        popupSettings.setHeight(740);

                        popupSettings.setWindowName(popupWindowName);
                        link.setPopupSettings(popupSettings);
                    }
    				link.setEnabled(isEnabled(bean) && lms.canLaunch(currenPackage));
    				link.setVisible(isVisible(bean));
                } else {
                    link = new BookmarkablePageLabeledLink(id, labelModel, pageClass);
                    link.setEnabled(false);
                    link.setVisible(isVisible(bean));
                }

				return link;
			}
		};

        actionColumn.addAction(launchAction);
		

		if (lms.canLaunchNewWindow()) {
			launchAction.setPopupWindowName("ScormPlayer");
		}

		if (canConfigure)
		{
			actionColumn.addAction(new Action(new ResourceModel("column.action.edit.label"), PackageConfigurationPage.class, paramPropertyExpressions));
		}

		if (canGrade) {
			 actionColumn.addAction(new Action(new StringResourceModel("column.action.grade.label", this, null), ResultsListPage.class, paramPropertyExpressions));
		}
		else if (canViewResults) {
			// actionColumn.addAction(new Action(new StringResourceModel("column.action.grade.label", this, null), LearnerResultsPage.class, paramPropertyExpressions));
	          Action resultLink = new Action(new StringResourceModel("column.action.grade.label", this, null), LearnerResultsPage.class, paramPropertyExpressions) {
	                @Override
	                public Component newLink(String id, Object bean) {
	                    ContentPackage currenPackage = (ContentPackage) bean;
	                    Component defaultLink = super.newLink(id, bean);
	                    if (currenPackage.isPassPrerequisite()) {
	                        return defaultLink;
	                    } else {
	                        // If Prerequisite lessons
	                        defaultLink.setEnabled(false);
	                        return defaultLink;
	                    }
	                }
	            };
	            actionColumn.addAction(resultLink);
		}

		columns.add(actionColumn);

		columns.add(new StatusColumn(new StringResourceModel("column.header.status", this, null), "status"));

		columns.add(new DecoratedDatePropertyColumn(new StringResourceModel("column.header.releaseOn", this, null), "releaseOn", "releaseOn"));

		columns.add(new DecoratedDatePropertyColumn(new StringResourceModel("column.header.dueOn", this, null), "dueOn", "dueOn"));
		
		columns.add(new LearningStatusColumn(new StringResourceModel("column.header.learn.status", this, null), "learningStatus"));
		columns.add(new ScoreColumn(new StringResourceModel("column.header.score", this, null), "score"));

		if (canDelete)
		{
			columns.add(new ImageLinkColumn(new Model("Remove"), PackageRemovePage.class, paramPropertyExpressions, DELETE_ICON, "delete"));
		}

		// Prepair value
		Map<String,List<ContentPackage>> mapResult = new HashMap<>();
		List<String> categoryNames = new ArrayList<>();
		boolean passPrequisitePackage;
		for (ContentPackage contentPackage : contentPackages) {
		    // Check prerequisite packages
		    passPrequisitePackage = checkCompletedPrequisitePackages(contentPackage, contentPackages);
		    contentPackage.setPassPrerequisite(passPrequisitePackage);
		    
			String category = contentPackage.getCategory();
			if(mapResult.containsKey(category)){
				mapResult.get(category).add(contentPackage);
			} else {
				List<ContentPackage> tmp = new ArrayList<>();
				tmp.add(contentPackage);
				mapResult.put(category, tmp);
				categoryNames.add(category);
			}
		}

		ListView<String> panelList = new ListView<String>("panelList", categoryNames) {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<String> item) {
				boolean isOpen = false;
				if(categoryNames.indexOf(item.getModelObject())== 0){
					isOpen = true;
				}
				item.add(new CollapsiblePanel("collapsiblePanel", item.getModelObject(), isOpen) {
					private static final long serialVersionUID = 1L;

					@Override
					protected Panel getInnerPanel(String markupId) {						
						return new BasicDataTable(markupId, columns, mapResult.get(item.getModelObject()));
					}
				});
			}
		}; 
		
		add(panelList);
	}
	
    /**
     * Check all prerequisite packages are Completed.
     * @return
     */
    protected boolean checkCompletedPrequisitePackages(ContentPackage selectedPackage, List<ContentPackage> allContentPackages) {
        
        if (selectedPackage.getPrerequisite() != null) {
            String[] prerequisitesString = selectedPackage.getPrerequisite().split(",");

            for (int i = 0; i < prerequisitesString.length; i++) {

                String prerequisiteResourceId = prerequisitesString[i];

                for (ContentPackage contentPackage : allContentPackages) {

                    if (contentPackage.getResourceId().equals(prerequisiteResourceId)) {

                        String learningStatus = contentPackage.getLearningStatus();

                        if ((learningStatus == null) || (!Utils.COMPLETED_STATUS.equalsIgnoreCase(learningStatus))) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * If there is a special group "_Everyone" in the site, all scorm lessons will be returned.
     * If the scorm lesson belongs to system group "_Public", it will be added to the returned list.
     * @param context
     * @return
     */
    private List<ContentPackage> getContentPackagePermission(String context) {
        List<ContentPackage> orginal = contentService.getContentPackages();
        List<ContentPackage> result = new ArrayList<>();
        currentLearnerId = lms.currentLearnerId();
        currentLeanerRole = lms.getRoleLearner(currentLearnerId, context);

        String systemPublicGroupId = null;
        // Thach.If there is a special group "_Eveyone", all scorm lessons are visible to the learn.
        List<Group> allGroups = getGroups();
        if (allGroups != null) {
            for (Group group : allGroups) {
                if ("_Everyone".equalsIgnoreCase(group.getTitle())) {
                    result.addAll(orginal);
                    return result;
                } else if ("_Public".equalsIgnoreCase(group.getTitle())) {
                    // Has a special group "_Public" in the site.
                    // Check if the lesson package belongs to the "_Public", every learners of the site can see it.
                    systemPublicGroupId = group.getId();
                }
            }
        }
            
        if ("Student".equals(currentLeanerRole)) {
            // filter by group
            List<String> groups = getListGroupIdOfLearner(context);

            for (ContentPackage lesson : orginal) {

                // Get group of the package belongs to.
                String groupSetting = lesson.getGroupsPermission();

                if (groupSetting != null) {
                    // In case the lesson package belong to "_Public" package, everyone can see it.
                    if (groupSetting.equals(systemPublicGroupId)) {
                        result.add(lesson);
                    } else if (groups != null) { // Group of the learner belongs to
                        for (String group : groups) {
                            if (groupSetting.contains(group)) {
                                result.add(lesson);
                                break;
                            }
                        }
                    } else {
                        log.debug(String.format("User %s has no group.", currentLearnerId));
                    }
                } else {
                    log.debug(String.format("User %s has no permission in lesson '%s'.", currentLearnerId, lesson.getTitle()));
                }
            }
        
        } else {
            result.addAll(orginal);
        }

        return result;
    }
	
	private List<String> getListGroupIdOfLearner(String context){
		List<String> result = new ArrayList<String>();
		
		// Thach.Get Groups in site
//		List<Group> groups = resultService.getAllGroupInSite(context);
		List<Group> groups = getGroups();
		
        if (groups != null) {
            for (Group tmp : groups) {
                List<Member> members = resultService.getAllMemberInGroup(tmp.getId(), context);
                for (Member member : members) {
                    if (member.getUserId().equals(currentLearnerId)) {
                        result.add(tmp.getId());
                        break;
                    }
                }
            }
        }
		return result;
	}

	public class StatusColumn extends AbstractColumn<ContentPackage> {

		private static final long serialVersionUID = 1L;

		public StatusColumn(IModel<String> displayModel, String sortProperty) {
			super(displayModel, sortProperty);
		}

		public void populateItem(Item<ICellPopulator<ContentPackage>> item, String componentId, IModel<ContentPackage> model) {
			item.add(new Label(componentId, createLabelModel(model)));
		}

		protected IModel<String> createLabelModel(IModel<ContentPackage> embeddedModel)
		{
			String resourceId = "status.unknown";
			Object target = embeddedModel.getObject();

			if (target instanceof ContentPackage) {
				ContentPackage contentPackage = (ContentPackage)target;

				int status = contentService.getContentPackageStatus(contentPackage);

				switch (status) {
				case CONTENT_PACKAGE_STATUS_OPEN:
					resourceId = "status.open";
					break;
				case CONTENT_PACKAGE_STATUS_OVERDUE:
					resourceId = "status.overdue";
					break;
				case CONTENT_PACKAGE_STATUS_CLOSED:
					resourceId = "status.closed";
					break;
				case CONTENT_PACKAGE_STATUS_NOTYETOPEN:
					resourceId = "status.notyetopen";
					break;
				}
			}

			return new ResourceModel(resourceId);
		}
	}
	
	public class LearningStatusColumn extends AbstractColumn<ContentPackage> {

		private static final long serialVersionUID = 1L;


		public LearningStatusColumn(IModel displayModel, String sortProperty) {
			super(displayModel, sortProperty);
		}

		public void populateItem(Item<ICellPopulator<ContentPackage>> item, String componentId, IModel<ContentPackage> model) {
			item.add(new Label(componentId, createLabelModel(model)));
		}

		protected IModel createLabelModel(IModel<ContentPackage> embeddedModel)
		{
			String value = "Not available";
			Object target = embeddedModel.getObject();

			if (target instanceof ContentPackage) {
				ContentPackage contentPackage = (ContentPackage)target;
				if(null != contentPackage.getLearningStatus() 
						&& !"".equals(contentPackage.getLearningStatus())) {
					value = contentPackage.getLearningStatus();	
				}
						
			}

			return new Model(value);
		}
	}
	
	public class ScoreColumn extends AbstractColumn<ContentPackage> {

		private static final long serialVersionUID = 1L;

		public ScoreColumn(IModel displayModel, String sortProperty) {
			super(displayModel, sortProperty);
		}

		public void populateItem(Item<ICellPopulator<ContentPackage>> item, String componentId, IModel<ContentPackage> model) {
			item.add(new Label(componentId, createLabelModel(model)));
		}

		protected IModel createLabelModel(IModel<ContentPackage> embeddedModel)
		{
			String value = "Not available";
			Object target = embeddedModel.getObject();

			if (target instanceof ContentPackage) {
				ContentPackage contentPackage = (ContentPackage)target;
				if(null != contentPackage.getScore()){
					value = Utils.getNumberPercententString(contentPackage.getScore().doubleValue());	
				}
			}

			return new Model(value);
		}
	}

	@Override
	protected ResourceReference getPageIconReference() {
		return PAGE_ICON;
	}

   /**
    * Get value of groups.
    * @return the groups
    */
    public List<Group> getGroups() {
        // Load groups in the site;
        final String context = lms.currentContext();
        List<Group> cachedGroups = resultService.getAllGroupInSite(context);

        return cachedGroups;
    }
}
