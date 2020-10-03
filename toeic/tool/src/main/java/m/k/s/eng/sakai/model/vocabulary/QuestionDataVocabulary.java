package m.k.s.eng.sakai.model.vocabulary;

import java.util.ArrayList;

/**
 * Description: Customized Question Bean For TOEIC
 * @author MINH MAN
 *
 */
public class QuestionDataVocabulary {
    Long questionId;
    QuestionContentVocabulary content;
    double maxPoint;
    String objective;
    private Double score;
    private Double discount;
    private Integer duration;
    //private String key;
    private String feedback;
    ArrayList<AnswerVocabulary> answers;

    public QuestionDataVocabulary() {
        // TODO Auto-generated constructor stub
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public QuestionContentVocabulary getContent() {
        return content;
    }

    public void setContent(QuestionContentVocabulary content) {
        this.content = content;
    }

    public double getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(double maxPoint) {
        this.maxPoint = maxPoint;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public ArrayList<AnswerVocabulary> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<AnswerVocabulary> answers) {
        this.answers = answers;
    }

}
