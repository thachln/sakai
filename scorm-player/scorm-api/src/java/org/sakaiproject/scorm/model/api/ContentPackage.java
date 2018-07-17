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

public class ContentPackage implements Serializable {

	private static final int NUMBER_OF_TRIES_UNLIMITED = -1;

	private static final long serialVersionUID = 1L;

	private Long contentPackageId;

	private String context;

	private String title;

	/**
	 * The id of the resource representing the content package
	 */
	private String resourceId;

	private Serializable manifestId;

	private String manifestResourceId;

	private String url;

	private Date releaseOn;

	private Date dueOn;

	private Date acceptUntil;

	private Date createdOn;

	private String createdBy;

	private Date modifiedOn;

	private String modifiedBy;

	private int numberOfTries = NUMBER_OF_TRIES_UNLIMITED;

	private boolean isDeleted;
	
	private Boolean startNewAttempt;
	
	private Integer viewSortOrder;
	
	private Boolean getHighestResult;
	
	private String category;
	
	private String learningStatus;
	
	private Double score;
	
	private String groupsPermission;
	// Require completed scorm-packages
	private String prerequisite;
	
	// Next content package
	private String nextResourceId;
	
	/** All prerequisite other packages. */
	private boolean passPrerequisite;

	public ContentPackage() {
		this.isDeleted = false;
		this.viewSortOrder = 0;
		this.getHighestResult = true;
	}

	public ContentPackage(String title, long contentPackageId) {
		this();
		this.title = title;
		this.contentPackageId = contentPackageId;
	}

	public ContentPackage(String title, String resourceId) {
		this();
		this.title = title;
		this.resourceId = resourceId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContentPackage other = (ContentPackage) obj;
		if (contentPackageId == null) {
			if (other.contentPackageId != null)
				return false;
		} else if (!contentPackageId.equals(other.contentPackageId))
			return false;
		return true;
	}

	public Date getAcceptUntil() {
		return acceptUntil;
	}

	public Long getContentPackageId() {
		return contentPackageId;
	}

	public String getContext() {
		return context;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public boolean getDeleted() {
		return isDeleted;
	}

	public Date getDueOn() {
		return dueOn;
	}

	public Serializable getManifestId() {
		return manifestId;
	}

	public String getManifestResourceId() {
		return manifestResourceId;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public int getNumberOfTries() {
		return numberOfTries;
	}

	public Date getReleaseOn() {
		return releaseOn;
	}

	public String getResourceId() {
		return resourceId;
	}

	public String getStatus() {
		return "Open";
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contentPackageId == null) ? 0 : contentPackageId.hashCode());
		return result;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public boolean isReleased() {
		Date now = new Date();

		return now.after(releaseOn);
	}

	public void setAcceptUntil(Date acceptUntil) {
		this.acceptUntil = acceptUntil;
	}

	public void setContentPackageId(Long contentPackageId) {
		this.contentPackageId = contentPackageId;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getViewSortOrder() {
		return viewSortOrder;
	}

	public void setViewSortOrder(Integer viewSortOrder) {
		this.viewSortOrder = viewSortOrder;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setDueOn(Date dueOn) {
		this.dueOn = dueOn;
	}

	public void setManifestId(Serializable manifestId) {
		this.manifestId = manifestId;
	}

	public void setManifestResourceId(String manifestResourceId) {
		this.manifestResourceId = manifestResourceId;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setNumberOfTries(int numberOfTries) {
		this.numberOfTries = numberOfTries;
	}

	public void setReleaseOn(Date releaseOn) {
		this.releaseOn = releaseOn;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the startNewAttempt
	 */
	public Boolean getStartNewAttempt() {
		return startNewAttempt;
	}

	/**
	 * @param startNewAttempt the startNewAttempt to set
	 */
	public void setStartNewAttempt(Boolean startNewAttempt) {
		this.startNewAttempt = startNewAttempt;
	}

	/**
	 * @return the getHighestResult
	 */
	public Boolean getGetHighestResult() {
		return getHighestResult;
	}

	/**
	 * @param getHighestResult the getHighestResult to set
	 */
	public void setGetHighestResult(Boolean getHighestResult) {
		this.getHighestResult = getHighestResult;
	}

    /**
    * Get value of category.
    * [TODO] To be updated
    * @return the category
    */
    public String getCategory() {
//        String[] sampleCats = {"Java core", "AngularJS", "Communication Skill"};
//        int randCat =  ((int) (Math.random() * 100)) % 3;
//        
//        return sampleCats[randCat];
        
        return category;
    }

    /**
     * Set the value for category.
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

	public String getLearningStatus() {
		return learningStatus;
	}

	public void setLearningStatus(String learningStatus) {
		this.learningStatus = learningStatus;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getGroupsPermission() {
		return groupsPermission;
	}

	public void setGroupsPermission(String groupsPermission) {
		this.groupsPermission = groupsPermission;
	}

	public String getPrerequisite() {
		return prerequisite;
	}

	public void setPrerequisite(String prerequisite) {
		this.prerequisite = prerequisite;
	}

    /**
    * Get value of passPrerequisite.
    * @return the passPrerequisite
    */
    public boolean isPassPrerequisite() {
        return passPrerequisite;
    }

    /**
     * Set the value for passPrerequisite.
     * @param passPrerequisite the passPrerequisite to set
     */
    public void setPassPrerequisite(boolean passPrerequisite) {
        this.passPrerequisite = passPrerequisite;
    }

	public String getNextResourceId() {
		return nextResourceId;
	}

	public void setNextResourceId(String nextResourceId) {
		this.nextResourceId = nextResourceId;
	}

}
