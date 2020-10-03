package m.k.s.eng.sakai.service.impl;

import java.util.Collections;
import java.util.List;

import org.sakaiproject.tool.assessment.data.dao.grading.ToeicDetailFeedback;
import org.sakaiproject.tool.assessment.data.dao.grading.ToeicGeneralFeedback;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.springframework.stereotype.Service;

import m.k.s.eng.sakai.model.FeedbackModel;
import m.k.s.eng.sakai.service.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private GradingService gradingService = new GradingService();

    @Override
    public ToeicGeneralFeedback getGeneralFeedback(int point) {

        return gradingService.getToeicGeneralFeedback(point);
    }

    @Override
    public List<ToeicDetailFeedback> getListToeicDetailFeedback() {

        return gradingService.getToeicDetailFeedbackList();
    }

    @Override
    public void saveOrUpdateDetailFeedback(FeedbackModel feedback) {
        if (!feedback.getDeletedIdList().isEmpty()) {
            gradingService.removeToeicDetailFeedback(feedback.getDeletedIdList());
        }

        gradingService.saveOrUpdateToeicDetailFeedback(feedback.getDetailList());
    }

    @Override
    public List<ToeicGeneralFeedback> getListGeneralFeedback() {

        return gradingService.getListToeicGeneralFeedback();
    }

    @Override
    public void saveOrUpdateGeneralFeedback(FeedbackModel feedback) {
        if (!feedback.getDeletedIdList().isEmpty()) {
            gradingService.removeToeicGeneralFeedback(feedback.getDeletedIdList());
        }

        gradingService.saveOrUpdateToeicGeneralFeedback(feedback.getGeneralList());

    }
}
