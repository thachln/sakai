/**********************************************************************************
 * $URL: $
 * $Id: $
 * **********************************************************************************
 * <p>
 * Author: David P. Bauer, dbauer1@udayton.edu
 * <p>
 * Copyright (c) 2016, University of Dayton
 * <p>
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.opensource.org/licenses/ECL-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **********************************************************************************/

package org.sakaiproject.lessonbuildertool;

import java.io.Serializable;
import java.util.Date;

public class TrackProgressImpl implements TrackProgress, Serializable {
    private long id;
    private Date lastTimeTracked;
    private long itemId;
    private String userId;
    private String siteId;
    private Double percent;
    /**
    * Get value of id.
    * @return the id
    */
    public long getId() {
        return id;
    }
    /**
     * Set the value for id.
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
    /**
    * Get value of lastTimeTracked.
    * @return the lastTimeTracked
    */
    public Date getLastTimeTracked() {
        return lastTimeTracked;
    }
    /**
     * Set the value for lastTimeTracked.
     * @param lastTimeTracked the lastTimeTracked to set
     */
    public void setLastTimeTracked(Date lastTimeTracked) {
        this.lastTimeTracked = lastTimeTracked;
    }
    /**
    * Get value of itemId.
    * @return the itemId
    */
    public long getItemId() {
        return itemId;
    }
    /**
     * Set the value for itemId.
     * @param itemId the itemId to set
     */
    public void setItemId(long itemId) {
        this.itemId = itemId;
    }
    /**
    * Get value of userId.
    * @return the userId
    */
    public String getUserId() {
        return userId;
    }
    /**
     * Set the value for userId.
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    /**
    * Get value of siteId.
    * @return the siteId
    */
    public String getSiteId() {
        return siteId;
    }
    /**
     * Set the value for siteId.
     * @param siteId the siteId to set
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
    /**
    * Get value of percent.
    * @return the percent
    */
    public Double getPercent() {
        return percent;
    }
    /**
     * Set the value for percent.
     * @param percent the percent to set
     */
    public void setPercent(Double percent) {
        this.percent = percent;
    }

}