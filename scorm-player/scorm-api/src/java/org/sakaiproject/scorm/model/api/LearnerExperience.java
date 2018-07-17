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
import java.util.Date;

import org.sakaiproject.scorm.api.ScormConstants;

public class LearnerExperience implements Serializable, ScormConstants {

	private static final long serialVersionUID = 1L;

	private String learnerName;

	private String learnerId;

	private long contentPackageId;

	// Hoctdy add start
	private String progress;

	private String score;	

	private String completedStatus ;
	
	private String duration;
	
	private Long progressNumber;
	
	private Long scoreNumber;
	
	// for display result list screen
	private String maxProgress;
	
	private String maxScore;
	
	private String maxCompletedStatus;
	
	private String maxDuration;
	
	// Hoctdy add end

	private int numberOfAttempts;

	private Date lastAttemptDate;

	private int status;

	private String previousLearnerIds;

	private String nextLearnerIds;

	public LearnerExperience(Learner learner, long contentPackageId) {
		this.learnerName = new StringBuilder(learner.getDisplayName()).append(" (").append(learner.getDisplayId()).append(")").toString();
		this.learnerId = learner.getId();
		this.contentPackageId = contentPackageId;
		this.numberOfAttempts = 0;
	}

	public long getContentPackageId() {
		return contentPackageId;
	}

	public Date getLastAttemptDate() {
		return lastAttemptDate;
	}

	public String getLearnerId() {
		return learnerId;
	}

	public String getLearnerName() {
		return learnerName;
	}

	public String getNextLearnerIds() {
		return nextLearnerIds;
	}

	public int getNumberOfAttempts() {
		return numberOfAttempts;
	}

	public String getPreviousLearnerIds() {
		return previousLearnerIds;
	}

	public String getProgress() {
		return progress;
	}

	public String getScore() {
		return score;
	}

	public int getStatus() {
		return status;
	}

	public void setContentPackageId(long contentPackageId) {
		this.contentPackageId = contentPackageId;
	}

	public void setLastAttemptDate(Date lastAttemptDate) {
		this.lastAttemptDate = lastAttemptDate;
	}

	public void setLearnerId(String learnerId) {
		this.learnerId = learnerId;
	}

	public void setLearnerName(String learnerName) {
		this.learnerName = learnerName;
	}

	public void setNextLearnerIds(String nextLearnerId) {
		this.nextLearnerIds = nextLearnerId;
	}

	public void setNumberOfAttempts(int numberOfAttempts) {
		this.numberOfAttempts = numberOfAttempts;
	}

	public void setPreviousLearnerIds(String previousLearnerId) {
		this.previousLearnerIds = previousLearnerId;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the completedStatus
	 */
	public String getCompletedStatus() {
		return completedStatus;
	}

	/**
	 * @param completedStatus the completedStatus to set
	 */
	public void setCompletedStatus(String completedStatus) {
		this.completedStatus = completedStatus;
	}

	public Long getProgressNumber() {
		return progressNumber;
	}

	public String getMaxProgress() {
		return maxProgress;
	}

	public void setMaxProgress(String maxProgress) {
		this.maxProgress = maxProgress;
	}

	public String getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(String maxScore) {
		this.maxScore = maxScore;
	}

	public String getMaxCompletedStatus() {
		return maxCompletedStatus;
	}

	public void setMaxCompletedStatus(String maxCompletedStatus) {
		this.maxCompletedStatus = maxCompletedStatus;
	}

	public void setProgressNumber(Long progressNumber) {
		this.progressNumber = progressNumber;
	}

	public Long getScoreNumber() {
		return scoreNumber;
	}

	public void setScoreNumber(Long scoreNumber) {
		this.scoreNumber = scoreNumber;
	}

	public String getDuration() {
		return duration;
	}

	public String getMaxDuration() {
		return maxDuration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setMaxDuration(String maxDuration) {
		this.maxDuration = maxDuration;
	}


}
