/*
 * #%L
 * SCORM API
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
package org.sakaiproject.scorm.service.api;

import java.util.List;

import org.sakaiproject.authz.api.Member;
import org.sakaiproject.scorm.model.api.ActivityReport;
import org.sakaiproject.scorm.model.api.ActivitySummary;
import org.sakaiproject.scorm.model.api.Attempt;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.Interaction;
import org.sakaiproject.scorm.model.api.Learner;
import org.sakaiproject.scorm.model.api.LearnerExperience;
import org.sakaiproject.site.api.Group;

public interface ScormResultService {

	public boolean existsActivityReport(long contentPackageId, String learnerId, long attemptNumber, String scoId);

	public ActivityReport getActivityReport(long contentPackageId, String learnerId, long attemptNumber, String scoId);

	public List<ActivitySummary> getActivitySummaries(long contentPackageId, String learnerId, long attemptNumber);

	public Attempt getAttempt(long id);

	public Attempt getAttempt(long contentPackageId, String learnerId, long attemptNumber);

	public List<Attempt> getAttempts(long contentPackageId);

	public List<Attempt> getAttempts(long contentPackageId, String learnerId);
	
	public Attempt getNewstAttempt(long contentPackageId, String learnerId);
	
	public int countAttempts(long contentPackageId, String learnerId);

	public List<Attempt> getAttempts(String courseId, String learnerId);

	public Interaction getInteraction(long contentPackageId, String learnerId, long attemptNumber, String scoId, String interactionId);

	public List<LearnerExperience> getLearnerExperiences(long contentPackageId, String learnerFilter,
			ContentPackage contentPackage, String currentLearnerRole, 
			String currentLearnerId,List<String> lstMemberInGroup, boolean isBasicSummary);
	
	public List<LearnerExperience> getLearnerExperiencesExportData(long contentPackageId, String learnerFilter,
            ContentPackage contentPackage, String currentLearnerRole, 
            String currentLearnerId,List<String> lstMemberInGroup, boolean isBasicSummary, String siteId);

	public List<Learner> getLearners(long contentPackageId);

	public int getNumberOfAttempts(long contentPackageId, String learnerId);

	public String[] getSiblingIds(long contentPackageId, String learnerId, long attemptNumber, String scoId, String interactionId);

	public void saveAttempt(Attempt attempt);
	// Hoctdy add start
	public List<Group> getAllGroupInSite(String context);
	public List<Member> getAllMemberInGroup(String id, String context);
	/**
	 * get Max Result in Current Attempts
	 * @param contentPackageId
	 * @param currentLearnerId
	 * @return
	 */
	public Object[] getMaxResultOfLearner(long contentPackageId, String currentLearnerId);
	// Hoctdy add end

}
