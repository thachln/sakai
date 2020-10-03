package m.k.s.eng.sakai.model;

import java.util.List;
import java.util.Map;

import org.sakaiproject.tool.assessment.data.dao.grading.ToeicDetailFeedback;
import org.sakaiproject.tool.assessment.data.dao.grading.ToeicGeneralFeedback;

public class ToeicFeedback {
    private Long assessmentGradingId;
    private Integer listeningScore;
    private Integer readingScore;
    private Integer finalScore;
    private List<Integer> correctParts;
    private ToeicGeneralFeedback toeicGeneralFeedback;
    private List<ToeicDetailFeedback> toeicDetailFeedback;

    /**
    * Get value of finalScore.
    * @return the finalScore
    */
    public Integer getFinalScore() {
        return finalScore;
    }
    /**
    * Get value of toeicGeneralFeedback.
    * @return the toeicGeneralFeedback
    */
    public ToeicGeneralFeedback getToeicGeneralFeedback() {
        return toeicGeneralFeedback;
    }
    /**
    * Get value of toeicDetailFeedback.
    * @return the toeicDetailFeedback
    */
    public List<ToeicDetailFeedback> getToeicDetailFeedback() {
        return toeicDetailFeedback;
    }
    /**
     * Set the value for finalScore.
     * @param finalScore the finalScore to set
     */
    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }
    /**
     * Set the value for toeicGeneralFeedback.
     * @param toeicGeneralFeedback the toeicGeneralFeedback to set
     */
    public void setToeicGeneralFeedback(ToeicGeneralFeedback toeicGeneralFeedback) {
        this.toeicGeneralFeedback = toeicGeneralFeedback;
    }
    /**
     * Set the value for toeicDetailFeedback.
     * @param toeicDetailFeedback the toeicDetailFeedback to set
     */
    public void setToeicDetailFeedback(List<ToeicDetailFeedback> toeicDetailFeedback) {
        this.toeicDetailFeedback = toeicDetailFeedback;
    }
    public List<Integer> getCorrectParts() {
        return correctParts;
    }
    public void setCorrectParts(List<Integer> correctParts) {
        this.correctParts = correctParts;
    }
    public Integer getReadingScore() {
        return readingScore;
    }
    public void setReadingScore(Integer readingScore) {
        this.readingScore = readingScore;
    }
    public Integer getListeningScore() {
        return listeningScore;
    }
    public void setListeningScore(Integer listeningScore) {
        this.listeningScore = listeningScore;
    }
    public Long getAssessmentGradingId() {
        return assessmentGradingId;
    }
    public void setAssessmentGradingId(Long assessmentGradingId) {
        this.assessmentGradingId = assessmentGradingId;
    }
    
    
}
