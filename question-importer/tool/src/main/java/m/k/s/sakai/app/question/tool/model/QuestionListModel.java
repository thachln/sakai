/**
 * Licensed to MKS under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * MKS Group licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package m.k.s.sakai.app.question.tool.model;

import java.io.Serializable;
import java.util.List;

import org.sakaiproject.tool.assessment.facade.ItemFacade;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Thach N. Le
 */
public class QuestionListModel implements Serializable {
    private Long poolId;
    private List<ItemFacade> questionList;
    private MultipartFile attachment;
    private String[] colHeaderNames;
    
    // More settings
    private boolean setQuestionColor = false;
    private String questionColor = null;
    private boolean setQuestionBold  = false;

    private Integer minDuration; 
    private Integer maxDuration;

    /**
     * Get value of poolId.
     * @return the poolId
     */
    public Long getPoolId() {
        return poolId;
    }

    /**
     * Set the value for poolId.
     * @param poolId the poolId to set
     */
    public void setPoolId(Long poolId) {
        this.poolId = poolId;
    }

    /**
    * Get value of questionList.
    * @return the questionList
    */
    public List<ItemFacade> getQuestionList() {
        return questionList;
    }

    /**
     * Set the value for questionList.
     * @param questionList the questionList to set
     */
    public void setQuestionList(List<ItemFacade> questionList) {
        this.questionList = questionList;
    }

    /**
     * Get value of attachment.
     * @return the attachment
     */
    public MultipartFile getAttachment() {
        return attachment;
    }

    /**
     * Set the value for attachment.
     * @param attachment the attachment to set
     */
    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }


    /**
     * @return the setQuestionColor
     */
    public boolean isSetQuestionColor() {
        return setQuestionColor;
    }

    /**
     * @param setQuestionColor the setQuestionColor to set
     */
    public void setSetQuestionColor(boolean setQuestionColor) {
        this.setQuestionColor = setQuestionColor;
    }

    /**
     * @return the questionColor
     */
    public String getQuestionColor() {
        return questionColor;
    }

    /**
     * @param questionColor the questionColor to set
     */
    public void setQuestionColor(String questionColor) {
        this.questionColor = questionColor;
    }

    /**
     * @return the setQuestionBold
     */
    public boolean isSetQuestionBold() {
        return setQuestionBold;
    }

    /**
     * @param setQuestionBold the setQuestionBold to set
     */
    public void setSetQuestionBold(boolean setQuestionBold) {
        this.setQuestionBold = setQuestionBold;
    }

    /**
    * Get value of colHeaderNames.
    * @return the colHeaderNames
    */
    public String[] getColHeaderNames() {
        return colHeaderNames;
    }

    /**
     * Set the value for colHeaderNames.
     * @param colHeaderNames the colHeaderNames to set
     */
    public void setColHeaderNames(String[] colHeaderNames) {
        this.colHeaderNames = colHeaderNames;
    }

    /**
    * Get value of minDuration.
    * @return the minDuration
    */
    public Integer getMinDuration() {
        return minDuration;
    }

    /**
     * Set the value for minDuration.
     * @param minDuration the minDuration to set
     */
    public void setMinDuration(Integer minDuration) {
        this.minDuration = minDuration;
    }

    /**
    * Get value of maxDuration.
    * @return the maxDuration
    */
    public Integer getMaxDuration() {
        return maxDuration;
    }

    /**
     * Set the value for maxDuration.
     * @param maxDuration the maxDuration to set
     */
    public void setMaxDuration(Integer maxDuration) {
        this.maxDuration = maxDuration;
    }

}
