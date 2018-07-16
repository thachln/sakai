package org.sakaiproject.scorm.ui.reporting.pages;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.LearnerExperience;
import org.sakaiproject.scorm.model.api.comparator.LearnerExperienceComparator;
import org.sakaiproject.scorm.model.api.comparator.LearnerExperienceComparator.CompType;
import org.sakaiproject.scorm.service.api.ScormResultService;
import org.sakaiproject.scorm.ui.NameValuePair;
import org.sakaiproject.wicket.markup.html.repeater.util.EnhancedDataProvider;

public class AttemptDataProvider extends EnhancedDataProvider {
	

	private static final long serialVersionUID = 1L;
	private final List<LearnerExperience> learnerExperiences;
	private final String contentPackageName;
	private final LearnerExperienceComparator comp = new LearnerExperienceComparator();

	public AttemptDataProvider(long contentPackageId, String learnerFilter,
								ContentPackage contentPackage, String currentLearnerRole, 
								String currentLearnerId,List<String> lstMemberInGroup, 
								boolean isBasicSummary,ScormResultService resultService) {
		this.learnerExperiences = resultService.getLearnerExperiences(contentPackageId, learnerFilter,
																		contentPackage, currentLearnerRole, 
																		currentLearnerId,lstMemberInGroup, 
																		isBasicSummary);
		contentPackageName = contentPackage.getTitle();
		setSort( "learnerName", true );
	}

	public Iterator<LearnerExperience> iterator(int first, int count) {

		// Get the sort type
		SortParam sort = getSort();
		String sortProp = sort.getProperty();
		boolean sortAsc = sort.isAscending();

		// Set the sort type in the comparator
		if( StringUtils.equals( sortProp, "lastAttemptDate" ) )
		{
			comp.setCompType( CompType.AttemptDate );
		}
		else if( StringUtils.equals( sortProp, "status" ) )
		{
			comp.setCompType( CompType.Status );
		}
		else if( StringUtils.equals( sortProp, "numberOfAttempts" ) )
		{
			comp.setCompType( CompType.NumberOfAttempts );
		}
		else if( StringUtils.equals( sortProp, "progressMeasure" ) )
		{
			comp.setCompType( CompType.progress );
		}
		else if( StringUtils.equals( sortProp, "score" ) )
		{
			comp.setCompType( CompType.score );
		}
		else if( StringUtils.equals( sortProp, "completedStatus" ) )
		{
			comp.setCompType( CompType.completionStatus );
		}
		else
		{
			comp.setCompType( CompType.Learner );
		}

		// Sort using the comparator in the direction requested
		if( sortAsc )
		{
			Collections.sort( learnerExperiences, comp );
		}
		else
		{
			Collections.sort( learnerExperiences, Collections.reverseOrder( comp ) );
		}

		// Return sub list of sorted collection
		return learnerExperiences.subList(first, first + count).iterator();
	}

	public int size() {
		return learnerExperiences.size();
	}

	@Override
	public List<NameValuePair> getFilterList() {
		List<NameValuePair> list = new LinkedList<NameValuePair>();

		list.add(new NameValuePair("All Groups / Sections", "ALL_GROUPS"));

		return list;
	}

	public String getContentPackageName() {
		return contentPackageName;
	}

}