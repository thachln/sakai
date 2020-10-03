package m.k.s.eng.sakai.service;

import java.util.List;

import org.sakaiproject.tool.assessment.data.dao.grading.ToeicDetailFeedback;
import org.sakaiproject.tool.assessment.data.dao.grading.ToeicGeneralFeedback;

import m.k.s.eng.sakai.model.FeedbackModel;

public interface FeedbackService {

    public ToeicGeneralFeedback getGeneralFeedback(int point);

    public List<ToeicGeneralFeedback> getListGeneralFeedback();

    public void saveOrUpdateGeneralFeedback(FeedbackModel feedback);

    public List<ToeicDetailFeedback> getListToeicDetailFeedback();

    public void saveOrUpdateDetailFeedback(FeedbackModel feedback);

}
