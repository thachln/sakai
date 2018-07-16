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

public class ActivitySummary implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String COMPLETED_STATUS = "Completed";
	public static final String IMCOMPLETE_STATUS = "Incomplete";

	// General data
	private String scoId;

	private String title;

	private long contentPackageId;

	private String learnerId;

	private long attemptNumber;

	// Progress data
	private double progressMeasure;

	private double completionThreshold;

	private String completionStatus;

	private String successStatus;

	private String learnerLocation;

	private long maxSecondsAllowed;

	private String totalSessionSeconds;
	
	private String totalSessionSecondsDisplay;

	// Score data
	private double scaled;

	private double raw;

	private double min;

	private double max;

	private double scaledToPass;
	
	private Date startDate;
	
	// Hoctdy add start
	/**
	 * compare two Activity Summary
	 * @param as1
	 * @param as2
	 * @return
	 */
	public int compareActivitySummary(ActivitySummary as2) {
		if((COMPLETED_STATUS.equals(this.getCompletionStatus())
				&& COMPLETED_STATUS.equals(as2.getCompletionStatus()))
				||(IMCOMPLETE_STATUS.equals(this.getCompletionStatus())
						&& IMCOMPLETE_STATUS.equals(as2.getCompletionStatus()))){
			if(this.getScaled() < as2.getScaled()){
				return -1;
			} else if(this.getScaled() > as2.getScaled()){
				return 1;
			} else {
				return 0;
			}
		} else if(COMPLETED_STATUS.equals(this.getCompletionStatus())
				&& IMCOMPLETE_STATUS.equals(as2.getCompletionStatus())){
			return 1;
		} else if(IMCOMPLETE_STATUS.equals(this.getCompletionStatus())
				&& COMPLETED_STATUS.equals(as2.getCompletionStatus())){
			return -1;
		} else {
			return 0;
		}
	}
	
	private String toEmptyObject(String s){
		if(null != s && !"".equals(s)) {
			return s;
		} else {
			return "";
		}
	}
	public int compareActivityDuration(ActivitySummary as2){
		
		String duration1 = toEmptyObject(this.totalSessionSeconds);
		String duration2 = toEmptyObject(as2.totalSessionSeconds);
		
		return duration1.compareTo(duration2);
	}
	// Hoctdy add end
	public long getAttemptNumber() {
		return attemptNumber;
	}

	public String getCompletionStatus() {
		return completionStatus;
	}

	public double getCompletionThreshold() {
		return completionThreshold;
	}

	public long getContentPackageId() {
		return contentPackageId;
	}

	public String getLearnerId() {
		return learnerId;
	}

	public String getLearnerLocation() {
		return learnerLocation;
	}

	public double getMax() {
		return max;
	}

	public long getMaxSecondsAllowed() {
		return maxSecondsAllowed;
	}

	public double getMin() {
		return min;
	}

	public double getProgressMeasure() {
		return progressMeasure;
	}

	public double getRaw() {
		return raw;
	}

	public double getScaled() {
		return scaled;
	}

	public double getScaledToPass() {
		return scaledToPass;
	}

	public String getScoId() {
		return scoId;
	}

	public String getSuccessStatus() {
		return successStatus;
	}

	public String getTitle() {
		return title;
	}

	public String getTotalSessionSeconds() {
		return totalSessionSeconds;
	}

	public void setAttemptNumber(long attemptNumber) {
		this.attemptNumber = attemptNumber;
	}

	public void setCompletionStatus(String completionStatus) {
		this.completionStatus = completionStatus;
	}

	public void setCompletionThreshold(double completionThreshold) {
		this.completionThreshold = completionThreshold;
	}

	public void setContentPackageId(long contentPackageId) {
		this.contentPackageId = contentPackageId;
	}

	public void setLearnerId(String learnerId) {
		this.learnerId = learnerId;
	}

	public void setLearnerLocation(String learnerLocation) {
		this.learnerLocation = learnerLocation;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public void setMaxSecondsAllowed(long maxSecondsAllowed) {
		this.maxSecondsAllowed = maxSecondsAllowed;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public void setProgressMeasure(double progressMeasure) {
		this.progressMeasure = progressMeasure;
	}

	public void setRaw(double raw) {
		this.raw = raw;
	}

	public void setScaled(double scaled) {
		this.scaled = scaled;
	}

	public void setScaledToPass(double scaledToPass) {
		this.scaledToPass = scaledToPass;
	}

	public void setScoId(String scoId) {
		this.scoId = scoId;
	}

	public void setSuccessStatus(String successStatus) {
		this.successStatus = successStatus;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTotalSessionSeconds(String totalSessionSeconds) {
		this.totalSessionSeconds = totalSessionSeconds;
	}

	public String getTotalSessionSecondsDisplay() {
		return totalSessionSecondsDisplay;
	}

	public void setTotalSessionSecondsDisplay(String totalSessionSecondsDisplay) {
		this.totalSessionSecondsDisplay = totalSessionSecondsDisplay;
	}

}
