package m.k.s.eng.sakai.model.vocabulary;

public class FeedBackAnswer {
	private Long id;
	private String text;
	private String type;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public FeedBackAnswer(Long id, String text, String type) {
		super();
		this.id = id;
		this.text = text;
		this.type = type;
	}
	public FeedBackAnswer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
