package m.k.s.eng.sakai.model;

public class AnswerToeicGrading {
    private Long itemId;
    private Long answerId;
    private String poolName;
    private Long itemGradingId;
    private String answerText;
    private Long assessmentGradingId;
    
    /**
    * Get value of itemId.
    * @return the itemId
    */
    public Long getItemId() {
        return itemId;
    }
    /**
    * Get value of assessmentGradingId.
    * @return the assessmentGradingId
    */
    public Long getAssessmentGradingId() {
        return assessmentGradingId;
    }
    /**
     * Set the value for assessmentGradingId.
     * @param assessmentGradingId the assessmentGradingId to set
     */
    public void setAssessmentGradingId(Long assessmentGradingId) {
        this.assessmentGradingId = assessmentGradingId;
    }
    /**
    * Get value of answerId.
    * @return the answerId
    */
    public Long getAnswerId() {
        return answerId;
    }
    /**
    * Get value of poolName.
    * @return the poolName
    */
    public String getPoolName() {
        return poolName;
    }
    /**
     * Set the value for itemId.
     * @param itemId the itemId to set
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    /**
     * Set the value for answerId.
     * @param answerId the answerId to set
     */
    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }
    /**
     * Set the value for poolName.
     * @param poolName the poolName to set
     */
    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }
    public Long getItemGradingId() {
        return itemGradingId;
    }
    public void setItemGradingId(Long itemGradingId) {
        this.itemGradingId = itemGradingId;
    }
    public String getAnswerText() {
        return answerText;
    }
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    
    
}
