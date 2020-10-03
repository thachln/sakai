package m.k.s.eng.sakai.model;

import java.util.List;

import org.sakaiproject.tool.assessment.data.dao.grading.ToeicDetailFeedback;
import org.sakaiproject.tool.assessment.data.dao.grading.ToeicGeneralFeedback;

public class FeedbackModel {
    private List<ToeicGeneralFeedback> generalList;
    private List<ToeicDetailFeedback> detailList;
    private List<Long> deletedIdList;
    /**
     * Get value of generalList.
     * @return the generalList
     */
    public List<ToeicGeneralFeedback> getGeneralList() {
        return generalList;
    }
    /**
     * Get value of detailList.
     * @return the detailList
     */
    public List<ToeicDetailFeedback> getDetailList() {
        return detailList;
    }
    /**
     * Get value of deletedIdList.
     * @return the deletedIdList
     */
    public List<Long> getDeletedIdList() {
        return deletedIdList;
    }
    /**
     * Set the value for generalList.
     * @param generalList the generalList to set
     */
    public void setGeneralList(List<ToeicGeneralFeedback> generalList) {
        this.generalList = generalList;
    }
    /**
     * Set the value for detailList.
     * @param detailList the detailList to set
     */
    public void setDetailList(List<ToeicDetailFeedback> detailList) {
        this.detailList = detailList;
    }
    /**
     * Set the value for deletedIdList.
     * @param deletedIdList the deletedIdList to set
     */
    public void setDeletedIdList(List<Long> deletedIdList) {
        this.deletedIdList = deletedIdList;
    }

}
