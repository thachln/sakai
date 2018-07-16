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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.scorm.exceptions.LearnerNotDefinedException;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.Learner;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.scorm.service.api.ScormResultService;
import org.sakaiproject.scorm.service.api.ScormSequencingService;
import org.sakaiproject.scorm.ui.Icon;
import org.sakaiproject.scorm.ui.console.pages.ConsoleBasePage;
import org.sakaiproject.scorm.ui.player.util.Utils;
import org.sakaiproject.scorm.ui.reporting.components.LearnerDetailsPanel;
import org.sakaiproject.wicket.markup.html.link.BookmarkablePageLabeledLink;
import org.sakaiproject.wicket.markup.html.repeater.data.table.DecoratedPropertyColumn;

public abstract class BaseResultsPage extends ConsoleBasePage {

	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(BaseResultsPage.class);

	private static final ResourceReference NEXT_ICON = new ResourceReference(BaseResultsPage.class, "res/arrow_right.png");
	private static final ResourceReference PREV_ICON = new ResourceReference(BaseResultsPage.class, "res/arrow_left.png");

	@SpringBean
	LearningManagementSystem lms;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormContentService")
	ScormContentService contentService;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormResultService")
	ScormResultService resultService;
	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormSequencingService")
	ScormSequencingService sequencingService;

	private final RepeatingView attemptNumberLinks;

	public BaseResultsPage(PageParameters pageParams) {
		super(pageParams);

		long contentPackageId = pageParams.getLong("contentPackageId");
		String learnerId = pageParams.getString("learnerId");

		if (learnerId == null) { // this is a student coming directly from the package list.
			learnerId = lms.currentLearnerId();
		}

		String learnerName = "[name unavailable]";

		Learner learner;
		boolean learnerFound = false;
		try {
			learner = lms.getLearner(learnerId);
			learnerFound = true;
		} catch (LearnerNotDefinedException lnde) {
			LOG.error("Could not find learner for this id: " + learnerId, lnde);
			learner = new Learner(learnerId, learnerName, "[id unavailable]");
		}

		LearnerDetailsPanel learnerDetailsPanel = new LearnerDetailsPanel("learnerDetails", new Model<Learner>(learner));
		add(learnerDetailsPanel);
		learnerDetailsPanel.setVisible(learnerFound);

		ContentPackage contentPackage = contentService.getContentPackage(contentPackageId);
		String scoId = pageParams.getString("scoId");
		String interactionId = pageParams.getString("interactionId");

		int numberOfAttempts = resultService.getNumberOfAttempts(contentPackageId, learnerId);
		long attemptNumber = 0;

		/** @NOTE hide unnecessary bits */
		if (pageParams.containsKey("attemptNumber")) 
		{
			attemptNumber = pageParams.getLong("attemptNumber");
		}

		if (attemptNumber == 0)
		{
			attemptNumber = numberOfAttempts;
		}

		this.attemptNumberLinks = new RepeatingView("attemptNumberLinks");
		add(attemptNumberLinks);

		for (long i=1;i<=numberOfAttempts;i++) {
			addAttemptNumberLink(i, pageParams, attemptNumberLinks, attemptNumber, contentPackage, scoId, learner);
		}

		initializePage(contentPackage, learner, attemptNumber, pageParams);

		String[] siblingIds = resultService.getSiblingIds(contentPackageId, learnerId, attemptNumber, scoId, interactionId);

		Link previousLink = newPreviousLink(siblingIds[0], pageParams);
		Link nextLink = newNextLink(siblingIds[1], pageParams);

		Icon previousIcon = new Icon("previousIcon", PREV_ICON);
		Icon nextIcon = new Icon("nextIcon", NEXT_ICON);

		previousLink.setVisible(isPreviousLinkVisible(siblingIds));
		nextLink.setVisible(isNextLinkVisible(siblingIds));

		previousIcon.setVisible(previousLink.isVisible());
		nextIcon.setVisible(nextLink.isVisible());

		add(previousLink);
		add(previousIcon);

		add(nextLink);
		add(nextIcon);
	}

	protected Link newPreviousLink(String previousId, PageParameters pageParams) {
		Link link = new PageLink("previousLink", BaseResultsPage.class);
		link.setVisible(false);
		return link;
	}

	protected boolean isPreviousLinkVisible(String[] siblingIds) {
		boolean canGrade = lms.canGrade(lms.currentContext());
		return canGrade && siblingIds[0] != null && !siblingIds[0].equals("");
	}

	protected Link newNextLink(String nextId, PageParameters pageParams) {
		Link link = new PageLink("nextLink", BaseResultsPage.class);
		link.setVisible(false);
		return link;
	}

	protected boolean isNextLinkVisible(String[] siblingIds) {
		boolean canGrade = lms.canGrade(lms.currentContext());
		return canGrade && siblingIds[1] != null && !siblingIds[1].equals("");
	}

	protected abstract void initializePage(ContentPackage contentPackage, Learner learner, long attemptNumber, PageParameters pageParams);

	protected abstract BookmarkablePageLabeledLink newAttemptNumberLink(long i, PageParameters params);

	/*
	 * Copied the basic organization of this method from an Apache Wicket class
	 * 	org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable
	 * originally authored by Igor Vaynberg (ivaynberg)
	 */
	protected void addAttemptNumberLink(long i, PageParameters params, RepeatingView container, long current, ContentPackage contentPackage, String scoId, Learner learner)
	{
		PageParameters newParams = new PageParameters(params);
		newParams.put("attemptNumber", i);

		BookmarkablePageLabeledLink link = newAttemptNumberLink(i, newParams);

		if (i == current) {
			link.setEnabled(false);
		} else {
			link.setVisible(attemptExists(i, scoId, learner.getId(), contentPackage.getContentPackageId()));
		}

		WebMarkupContainer item = new WebMarkupContainer(container.newChildId());
		item.setRenderBodyOnly(true);
		item.add(link);

		container.add(item);
	}

	protected boolean attemptExists(long attemptId, String scoId, String learnerId, long contentPackageId) {
		return true;
	}

	public class PercentageColumn extends DecoratedPropertyColumn {

		private static final long serialVersionUID = 1L;

		public PercentageColumn(IModel displayModel, String sortProperty, String propertyExpression) {
			super(displayModel, sortProperty, propertyExpression);
		}

		@Override
		public Object convertObject(Object object) {
			Double d = (Double)object;
			
			return Utils.getNumberPercententString(d);
		}
	}
}
