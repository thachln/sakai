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
package org.sakaiproject.scorm.model.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ActivityReport implements Serializable {

	private static final long serialVersionUID = 1L;

	private String activityId;

	private String scoId;

	private String title;

	private List<Interaction> interactions;

	private Map<String, Objective> objectives;

	private Progress progress;

	private Score score;

	private List<CMIData> cmiData;

	public ActivityReport() {
		interactions = new LinkedList<Interaction>();
		objectives = new HashMap<String, Objective>();
	}

	public String getActivityId() {
		return activityId;
	}

	public List<CMIData> getCmiData() {
		return cmiData;
	}

	public List<Objective> getInteractionObjectives(Interaction interaction) {
		List<Objective> list = new LinkedList<Objective>();
		for (String objectiveId : interaction.getObjectiveIds()) {
			Objective objective = objectives.get(objectiveId);

			if (objective != null) {
				list.add(objective);
			}

		}

		return list;
	}

	public List<Interaction> getInteractions() {
		return interactions;
	}

	public Map<String, Objective> getObjectives() {
		return objectives;
	}

	public Progress getProgress() {
		return progress;
	}

	public String getScoId() {
		return scoId;
	}

	public Score getScore() {
		return score;
	}

	public String getTitle() {
		return title;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public void setCmiData(List<CMIData> cmiData) {
		this.cmiData = cmiData;
	}

	public void setInteractions(List<Interaction> interactions) {
		this.interactions = interactions;
	}

	public void setObjectives(Map<String, Objective> objectives) {
		this.objectives = objectives;
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
	}

	public void setScoId(String scoId) {
		this.scoId = scoId;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
