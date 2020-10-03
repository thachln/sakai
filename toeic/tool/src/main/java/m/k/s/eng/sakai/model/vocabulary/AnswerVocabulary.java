package m.k.s.eng.sakai.model.vocabulary;

import java.util.List;

/**
 * Description: Customized Answer Bean For TOEIC
 * 
 * @author MINH MAN
 *
 */
public class AnswerVocabulary {
    private Long answerId;
    private String text;
    private String label;
    private Boolean isCorrent;
    private List<FeedBackAnswer> feedback;
    

	public List<FeedBackAnswer> getFeedback() {
		return feedback;
	}

	public void setFeedback(List<FeedBackAnswer> feedback) {
		this.feedback = feedback;
	}

	public AnswerVocabulary() {

    }
    
    public Boolean getIsCorrent() {
		return isCorrent;
	}

	public void setIsCorrent(Boolean isCorrent) {
		this.isCorrent = isCorrent;
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
}
