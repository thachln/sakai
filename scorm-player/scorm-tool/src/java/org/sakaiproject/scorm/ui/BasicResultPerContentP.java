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
package org.sakaiproject.scorm.ui;

import java.io.Serializable;
import java.util.Date;

public class BasicResultPerContentP implements Serializable {
	private static final long serialVersionUID = 1L;

	private String completedStatus;
	private String scoreStr;
	private String lastAttempt;
	private String submitTime;
	private long contentPackageId;
	private Date startDate;
	private String startDateStr;
	private String duration;
	// for list view
	
	public BasicResultPerContentP(){
		
	}
	
	public BasicResultPerContentP(long contentPackageId, String completedStatus, String score, String lastAttempt, String submitTime) {
		this.setCompletedStatus(completedStatus);
		this.setScoreStr(score);
		this.setLastAttempt(lastAttempt);
		this.setSubmitTime(submitTime);
		this.contentPackageId = contentPackageId;
	}
	
	/**
    * Get value of lastAttempt.
    * @return the lastAttempt
    */
    public String getLastAttempt() {
        return lastAttempt;
    }

    /**
     * Set the value for lastAttempt.
     * @param lastAttempt the lastAttempt to set
     */
    public void setLastAttempt(String lastAttempt) {
        this.lastAttempt = lastAttempt;
    }

    /**
    * Get value of submitTime.
    * @return the submitTime
    */
    public String getSubmitTime() {
        return submitTime;
    }

    /**
     * Set the value for submitTime.
     * @param submitTime the submitTime to set
     */
    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
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

	/**
	 * @return the contentPackageId
	 */
	public long getContentPackageId() {
		return contentPackageId;
	}

	/**
	 * @param contentPackageId the contentPackageId to set
	 */
	public void setContentPackageId(long contentPackageId) {
		this.contentPackageId = contentPackageId;
	}

	/**
	 * @return the scoreStr
	 */
	public String getScoreStr() {
		return scoreStr;
	}

	/**
	 * @param scoreStr the scoreStr to set
	 */
	public void setScoreStr(String scoreStr) {
		this.scoreStr = scoreStr;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getDuration() {
		return duration;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getStartDateStr() {
		return startDateStr;
	}

	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
	}

}
