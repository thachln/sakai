package m.k.s.eng.sakai.model;

public class AssessmentModel {
    private String assessmentId;
    private Boolean isPractice;
    private long startDate;
    private String description;
    private Integer totalQuestion;
    private Integer answered;
    private Integer correctAnswered;

    public Boolean getIsPractice() {
        return isPractice;
    }
    public void setIsPractice(Boolean isPractice) {
        this.isPractice = isPractice;
    }
    public String getAssessmentId() {
        return assessmentId;
    }
    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }
    public long getStartDate() {
        return startDate;
    }
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getTotalQuestion() {
        return totalQuestion;
    }
    public void setTotalQuestion(Integer totalQuestion) {
        this.totalQuestion = totalQuestion;
    }
    public Integer getAnswered() {
        return answered;
    }
    public void setAnswered(Integer answered) {
        this.answered = answered;
    }
    public Integer getCorrectAnswered() {
        return correctAnswered;
    }
    public void setCorrectAnswered(Integer correctAnswered) {
        this.correctAnswered = correctAnswered;
    }
}
