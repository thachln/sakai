package m.k.s.eng.sakai.service;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.authz.api.Member;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.dao.grading.ItemGradingData;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBean;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBeanie;

import m.k.s.eng.sakai.model.AnswerToeicGrading;
import m.k.s.eng.sakai.model.AssessmentDataToeic;
import m.k.s.eng.sakai.model.BasicAssessmentModel;
import m.k.s.eng.sakai.model.ItemResult;
import m.k.s.eng.sakai.model.ItemToeicGrading;
import m.k.s.eng.sakai.model.ToeicFeedback;

/**
 * @author MINH MAN
 */
public interface AssessmentToeicService {

    /**
     * Get all assessment with basic information
     * @param request
     * @param response
     * @return
     */
    public BasicAssessmentModel getAllBasicAssessment(HttpServletRequest request, HttpServletResponse response);

    /**
     * Get the assessment by id
     * @param id
     * @param request
     * @param response
     * @return json
     */
    public AssessmentDataToeic getAssessmentToeicById(String id, HttpServletRequest request,
            HttpServletResponse response);

    public ItemToeicGrading getSavedAnswer(String assessmentGradingIdString);

    public List<Member> getMembersInSite();

    public List<Member> getStudentsInSite(List<Member> members);

    public String grading(ItemToeicGrading itemToeicGrading, Boolean isSubmit, Boolean isAutoSubmit);

    public List<AssessmentGradingData> getAllAssessmentGradingData(Long pubAssessmentId);

    public List<AssessmentGradingData> getAllAssessmentGradingDataByIdAndAgent(Long pubAssessmentId);

    public List<Object[]> getResultList(Long assessmentGradingId);

    public String getDecryptPassword(Long publishedAssessmentId);

    public void saveItemGradingList(List<AnswerToeicGrading> list);

    public Integer calculateTimeLimit(PublishedAssessmentFacade publishesAssessment);

    public ToeicFeedback calculateScores(Long assessmentGradingId);

    public void saveItemGrading(AnswerToeicGrading data, Set<ItemGradingData> itemSet, Set<ItemGradingData> newItemSet);

    public AssessmentDataToeic getToeicData(Long publishedAssessmentId, Long assessmentGradingId,
            DeliveryBean delivery);

    public List<ItemResult> getItemResults(Long assessmentGradingId, Long publishedAssessmentId,
            DeliveryBean deliveryBean);

    public ToeicFeedback calculateScores(List<String> correctAnswerParts);

}
