package org.sakaiproject.tool.assessment.data.dao.grading;

public class ToeicDetailFeedback {

    private Long id;
    /**
     * 0: listening
     * 1: reading
     */
    private Integer kind;
    private Integer levelPoint;
    private String strength;
    private String weakness;
    /**
    * Get value of id.
    * @return the id
    */
    public Long getId() {
        return id;
    }
    /**
    * Get value of kind.
    * @return the kind
    */
    public Integer getKind() {
        return kind;
    }
    /**
    * Get value of levelPoint.
    * @return the levelPoint
    */
    public Integer getLevelPoint() {
        return levelPoint;
    }
    /**
    * Get value of strength.
    * @return the strength
    */
    public String getStrength() {
        return strength;
    }
    /**
    * Get value of weakness.
    * @return the weakness
    */
    public String getWeakness() {
        return weakness;
    }
    /**
     * Set the value for id.
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * Set the value for kind.
     * @param kind the kind to set
     */
    public void setKind(Integer kind) {
        this.kind = kind;
    }
    /**
     * Set the value for levelPoint.
     * @param levelPoint the levelPoint to set
     */
    public void setLevelPoint(Integer levelPoint) {
        this.levelPoint = levelPoint;
    }
    /**
     * Set the value for strength.
     * @param strength the strength to set
     */
    public void setStrength(String strength) {
        this.strength = strength;
    }
    /**
     * Set the value for weakness.
     * @param weakness the weakness to set
     */
    public void setWeakness(String weakness) {
        this.weakness = weakness;
    }
    
    
}
