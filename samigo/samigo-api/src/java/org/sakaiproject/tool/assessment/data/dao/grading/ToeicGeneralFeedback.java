package org.sakaiproject.tool.assessment.data.dao.grading;

public class ToeicGeneralFeedback {
    private Long id;
    private Integer minPoint;
    private Integer maxPoint;
    private String cefrLevel;
    private String levelText;
    private String levelDescription;
    
    /**
    * Get value of id.
    * @return the id
    */
    public Long getId() {
        return id;
    }
    /**
    * Get value of minPoint.
    * @return the minPoint
    */
    public Integer getMinPoint() {
        return minPoint;
    }
    /**
    * Get value of maxPoint.
    * @return the maxPoint
    */
    public Integer getMaxPoint() {
        return maxPoint;
    }
    /**
    * Get value of cefrLevel.
    * @return the cefrLevel
    */
    public String getCefrLevel() {
        return cefrLevel;
    }
    /**
    * Get value of levelText.
    * @return the levelText
    */
    public String getLevelText() {
        return levelText;
    }
    /**
    * Get value of levelDescription.
    * @return the levelDescription
    */
    public String getLevelDescription() {
        return levelDescription;
    }
    /**
     * Set the value for id.
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * Set the value for minPoint.
     * @param minPoint the minPoint to set
     */
    public void setMinPoint(Integer minPoint) {
        this.minPoint = minPoint;
    }
    /**
     * Set the value for maxPoint.
     * @param maxPoint the maxPoint to set
     */
    public void setMaxPoint(Integer maxPoint) {
        this.maxPoint = maxPoint;
    }
    /**
     * Set the value for cefrLevel.
     * @param cefrLevel the cefrLevel to set
     */
    public void setCefrLevel(String cefrLevel) {
        this.cefrLevel = cefrLevel;
    }
    /**
     * Set the value for levelText.
     * @param levelText the levelText to set
     */
    public void setLevelText(String levelText) {
        this.levelText = levelText;
    }
    /**
     * Set the value for levelDescription.
     * @param levelDescription the levelDescription to set
     */
    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
    }
    
}
