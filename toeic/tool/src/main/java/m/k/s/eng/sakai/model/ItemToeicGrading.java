package m.k.s.eng.sakai.model;

import java.util.List;

public class ItemToeicGrading {
    private long assessmentId;
    private String assessmentTitle;
    private Long assessmentGradingId;
    private Integer timeElapsed;
    private List<AnswerToeicGrading> items;
    private Integer lastVisitedPart;
    private Integer lastVisitedQuestion;

    public List<AnswerToeicGrading> getItems() {
        return items;
    }
    public void setItems(List<AnswerToeicGrading> items) {
        this.items = items;
    }
    public long getAssessmentId() {
        return assessmentId;
    }
    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }
    public Long getAssessmentGradingId() {
        return assessmentGradingId;
    }
    public void setAssessmentGradingId(Long assessmentGradingId) {
        this.assessmentGradingId = assessmentGradingId;
    }
    public Integer getTimeElapsed() {
        return timeElapsed;
    }
    public void setTimeElapsed(Integer timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
    public Integer getLastVisitedPart() {
        return lastVisitedPart;
    }
    public void setLastVisitedPart(Integer lastVisitedPart) {
        this.lastVisitedPart = lastVisitedPart;
    }
    public Integer getLastVisitedQuestion() {
        return lastVisitedQuestion;
    }
    public void setLastVisitedQuestion(Integer lastVisitedQuestion) {
        this.lastVisitedQuestion = lastVisitedQuestion;
    }
    public String getAssessmentTitle() {
        return assessmentTitle;
    }
    public void setAssessmentTitle(String assessmentTitle) {
        this.assessmentTitle = assessmentTitle;
    }
}
