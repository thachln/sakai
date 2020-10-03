package m.k.s.eng.sakai.model;

import java.util.List;

public class DataResult {
    private AssessmentDataToeic assessment;
    private List<ItemResult> items;
    public AssessmentDataToeic getAssessment() {
        return assessment;
    }
    public void setAssessment(AssessmentDataToeic assessment) {
        this.assessment = assessment;
    }
    public List<ItemResult> getItems() {
        return items;
    }
    public void setItems(List<ItemResult> items) {
        this.items = items;
    }
}
