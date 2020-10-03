package m.k.s.eng.sakai.model;

import java.util.ArrayList;
import java.util.Map;

/**
 * Description: Customized Question Bean For TOEIC
 * @author MINH MAN
 *
 */
public class QuestionDataToeic {
    Long questionId;
    QuestionContent content;
    double maxPoint;
    String objective;
    private Double score;
    private Double discount;
    private Integer duration;
    //private String key;
    private String feedback;
    ArrayList<AnswerToeic> answers;
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public QuestionDataToeic() {
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

    public QuestionContent getContent() {
        return content;
    }

    public void setContent(QuestionContent content) {
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

    public ArrayList<AnswerToeic> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<AnswerToeic> answers) {
        this.answers = answers;
    }

}
