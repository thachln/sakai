package m.k.s.eng.sakai.model;

import java.util.List;

import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBeanie;

public class BasicAssessmentModel {
    private List<DeliveryBeanie> takeableList;
    private List<DeliveryBeanie> assessmentList;
    private List<AssessmentModel> additionalInfo;
    /**
     * Get value of takeableList.
     * @return the takeableList
     */
    public List<DeliveryBeanie> getTakeableList() {
        return takeableList;
    }
    /**
     * Get value of assessmentList.
     * @return the assessmentList
     */
    public List<DeliveryBeanie> getAssessmentList() {
        return assessmentList;
    }
    /**
     * Get value of additionalInfo.
     * @return the additionalInfo
     */
    public List<AssessmentModel> getAdditionalInfo() {
        return additionalInfo;
    }
    /**
     * Set the value for takeableList.
     * @param takeableList the takeableList to set
     */
    public void setTakeableList(List<DeliveryBeanie> takeableList) {
        this.takeableList = takeableList;
    }
    /**
     * Set the value for assessmentList.
     * @param assessmentList the assessmentList to set
     */
    public void setAssessmentList(List<DeliveryBeanie> assessmentList) {
        this.assessmentList = assessmentList;
    }
    /**
     * Set the value for additionalInfo.
     * @param additionalInfo the additionalInfo to set
     */
    public void setAdditionalInfo(List<AssessmentModel> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
