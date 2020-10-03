package m.k.s.eng.sakai.model;

public class ItemResult {
    private String itemId;
    private String question;
    private String part;
    private String answerText;
    private String userAnswer;
    private String answer;
    private Boolean isCorrect;

    private String category;

    public ItemResult() {

    }

    /**
     * @param question
     * @param part
     * @param answerText
     * @param userAnswer
     * @param isCorrect
     */
    public ItemResult(String itemId, String question, String part, String answerText, String userAnswer, boolean isCorrect,
            String answer) {
        super();
        this.itemId = itemId;
        this.question = question;
        this.part = part;
        this.answerText = answerText;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
        this.answer = answer;
    }
    
    
    /**
     * @param question
     * @param part
     * @param userAnswer
     * @param answer
     * @param isCorrect
     */
    public ItemResult(String question, String part, String answer, String userAnswer, boolean isCorrect, String itemId) {
        super();
        this.question = question;
        this.part = part;
        this.userAnswer = userAnswer;
        this.answer = answer;
        this.isCorrect = isCorrect;
        this.itemId = itemId;
    }
    
    public ItemResult(String question, String answer, String userAnswer, boolean isCorrect) {
      super();
      this.question = question;
      this.userAnswer = userAnswer;
      this.answer = answer;
      this.isCorrect = isCorrect;
    }
    /**
     * Get value of question.
     * @return the question
     */
    public String getQuestion() {
        return question;
    }
    /**
     * Get value of part.
     * @return the part
     */
    public String getPart() {
        return part;
    }
    /**
     * Get value of answerText.
     * @return the answerText
     */
    public String getAnswerText() {
        return answerText;
    }
    /**
     * Get value of userAnswer.
     * @return the userAnswer
     */
    public String getUserAnswer() {
        return userAnswer;
    }
    /**
     * Set the value for question.
     * @param question the question to set
     */
    public void setQuestion(String question) {
        this.question = question;
    }
    /**
     * Set the value for part.
     * @param part the part to set
     */
    public void setPart(String part) {
        this.part = part;
    }
    /**
     * Set the value for answerText.
     * @param answerText the answerText to set
     */
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    /**
     * Set the value for userAnswer.
     * @param userAnswer the userAnswer to set
     */
    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public String getItemId() {
        return itemId;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public Boolean getIsCorrect() {
      return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
      this.isCorrect = isCorrect;
    }

    /**
    * Get value of category.
    * @return the category
    */
    public String getCategory() {
        return category;
    }

    /**
     * Set the value for category.
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }
}
