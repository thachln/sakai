package m.k.s.eng.sakai.model;

/**
 * Description: Customized Answer Bean For TOEIC
 * 
 * @author MINH MAN
 *
 */
public class AnswerToeic {
    private Long answerId;
    private String text;
    private String label;
    private String feedback;

    public AnswerToeic() {

    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
