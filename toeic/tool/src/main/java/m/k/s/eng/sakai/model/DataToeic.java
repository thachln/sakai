package m.k.s.eng.sakai.model;

public class DataToeic {
    private AssessmentDataToeic assessment;
    private ItemToeicGrading savedAnswer;
    public AssessmentDataToeic getAssessment() {
        return assessment;
    }
    public void setAssessment(AssessmentDataToeic assessment) {
        this.assessment = assessment;
    }
    public ItemToeicGrading getSavedAnswer() {
        return savedAnswer;
    }
    public void setSavedAnswer(ItemToeicGrading savedAnswer) {
        this.savedAnswer = savedAnswer;
    }
}
