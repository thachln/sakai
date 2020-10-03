package m.k.s.eng.sakai.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.collect.Lists;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.samigo.util.SamigoConstants;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.assessment.data.dao.assessment.EventLogData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemText;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedSectionData;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.dao.grading.ItemGradingData;
import org.sakaiproject.tool.assessment.data.dao.grading.StudentGradingSummaryData;
import org.sakaiproject.tool.assessment.data.dao.grading.ToeicDetailFeedback;
import org.sakaiproject.tool.assessment.data.dao.grading.ToeicGeneralFeedback;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AnswerFeedbackIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AnswerIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentAccessControlIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemMetaDataIfc;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.facade.EventLogFacade;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.facade.QuestionPoolFacade;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.services.PersistenceService;
import org.sakaiproject.tool.assessment.services.QuestionPoolService;
import org.sakaiproject.tool.assessment.services.assessment.EventLogService;
import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;
import org.sakaiproject.tool.assessment.ui.bean.delivery.ContentsDeliveryBean;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBean;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBeanie;
import org.sakaiproject.tool.assessment.ui.bean.delivery.ItemContentsBean;
import org.sakaiproject.tool.assessment.ui.bean.delivery.SectionContentsBean;
import org.sakaiproject.tool.assessment.ui.bean.delivery.SelectionBean;
import org.sakaiproject.tool.assessment.ui.bean.select.SelectAssessmentBean;
import org.sakaiproject.tool.assessment.ui.listener.delivery.DeliveryActionListener;
import org.sakaiproject.tool.assessment.ui.listener.delivery.LinearAccessDeliveryActionListener;
import org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.sakaiproject.tool.assessment.ui.web.session.SessionUtil;
import org.sakaiproject.tool.assessment.util.ExtendedTimeDeliveryService;
import org.sakaiproject.tool.assessment.util.SamigoLRSStatements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import m.k.s.eng.sakai.logic.SakaiProxy;
import m.k.s.eng.sakai.model.AnswerToeic;
import m.k.s.eng.sakai.model.AnswerToeicGrading;
import m.k.s.eng.sakai.model.AssessmentDataToeic;
import m.k.s.eng.sakai.model.AssessmentModel;
import m.k.s.eng.sakai.model.BasicAssessmentModel;
import m.k.s.eng.sakai.model.ItemResult;
import m.k.s.eng.sakai.model.ItemToeicGrading;
import m.k.s.eng.sakai.model.PartDataToeic;
import m.k.s.eng.sakai.model.QuestionContent;
import m.k.s.eng.sakai.model.QuestionDataToeic;
import m.k.s.eng.sakai.model.ToeicFeedback;
import m.k.s.eng.sakai.service.AssessmentToeicService;
import m.k.s.eng.sakai.util.AppUtil;

@Service("assessmentToeicService")
public class AssessmentToeicServiceImpl implements AssessmentToeicService {
    private static final String TYPE_EXAM = "type: exam";

    private static final String TYPE_PRACTICE = "type: practice";

    final static protected Log LOG = LogFactory.getLog(AssessmentToeicServiceImpl.class);

    final static private Boolean IS_TOEIC = true;

    private static ResourceBundle eventLogMessages = ResourceBundle
            .getBundle("org.sakaiproject.tool.assessment.bundle.EventLogMessages");

    private final EventTrackingService eventTrackingService = ComponentManager.get(EventTrackingService.class);

    private static List<Integer> arrScoreListening = Arrays.asList(5, 5, 5, 5, 5, 5, 5, 10, 15, 20, 25, 30, 35, 40, 45,
            50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110, 115, 120, 125, 130, 135, 140, 145, 150, 160, 165, 170,
            175, 180, 185, 190, 195, 200, 210, 215, 220, 230, 240, 245, 250, 255, 260, 270, 275, 280, 290, 295, 300,
            310, 315, 320, 325, 330, 340, 345, 350, 360, 365, 370, 380, 385, 390, 395, 400, 405, 410, 420, 425, 430,
            440, 445, 450, 460, 465, 470, 475, 480, 485, 490, 495, 495, 495, 495, 495, 495, 495, 495, 495, 495, 495);
    private static List<Integer> arrScoreReading = Arrays.asList(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 10, 15,
            20, 25, 30, 35, 40, 45, 50, 60, 65, 70, 80, 85, 90, 95, 100, 110, 115, 120, 125, 130, 140, 145, 150, 160,
            165, 170, 175, 180, 190, 195, 200, 210, 215, 220, 225, 230, 235, 240, 250, 255, 260, 265, 270, 280, 285,
            290, 300, 305, 310, 320, 325, 330, 335, 340, 350, 355, 360, 365, 370, 380, 385, 390, 395, 400, 405, 410,
            415, 420, 425, 430, 435, 445, 450, 455, 465, 470, 480, 485, 490, 495, 495, 495, 495);

    @Autowired
    private SakaiProxy sakaiProxy;

    private GradingService gradingService = new GradingService();

    @Override
    public BasicAssessmentModel getAllBasicAssessment(HttpServletRequest request, HttpServletResponse response) {
        SelectAssessmentBean select = null;
        try {
            select = (SelectAssessmentBean) ContextUtil.lookupBeanFromExternalServlet("select", request, response);
        } catch (Throwable th) {
            LOG.error("Could not lookup bean " + select + ". trong home", th);
        }

        if (select == null) {
            return null;
        }

        BasicAssessmentModel result = new BasicAssessmentModel();

        // get service and managed bean
        PublishedAssessmentService publishedAssessmentService = new PublishedAssessmentService();
        select.setHasHighestMultipleSubmission(false); // reset property
        select.setHasAnyAssessmentBeenModified(false);

        SelectActionListener selectActionListener = new SelectActionListener();

        // 1b. get all the published assessmnet available in the site
        // note that agentId is not really used
        String agentString = AgentFacade.getAgentString();
        String takeableOrderBy = selectActionListener.getTakeableOrderBy(select);
        boolean takeableAscending = select.isTakeableAscending();
        String currentSiteId = AgentFacade.getCurrentSiteId();
        List publishedAssessmentList = publishedAssessmentService.getBasicInfoOfAllPublishedAssessments(agentString,
                takeableOrderBy, takeableAscending, currentSiteId, IS_TOEIC);

        Map h = publishedAssessmentService.getTotalSubmissionPerAssessment(agentString, currentSiteId);

        List list = gradingService.getUpdatedAssessmentList(agentString, currentSiteId);

        List updatedAssessmentNeedResubmitList = new ArrayList();
        List updatedAssessmentList = new ArrayList();
        if (list != null && list.size() == 2) {
            updatedAssessmentNeedResubmitList = (List) list.get(0);
            updatedAssessmentList = (List) list.get(1);
        }

        // this list contain all assessment available (also contain not release assessment yet)
        List<PublishedAssessmentFacade> assessmentList = getAssessmentList(publishedAssessmentList, h,
                updatedAssessmentNeedResubmitList, updatedAssessmentList);

        // filter out the one that the given user do not have right to access
        List takeableList = selectActionListener.getTakeableList(publishedAssessmentList, h,
                updatedAssessmentNeedResubmitList, updatedAssessmentList);
        // 1c. prepare delivery bean
        ArrayList<DeliveryBeanie> takeablePublishedList = new ArrayList<DeliveryBeanie>();
        for (int i = 0; i < takeableList.size(); i++) {
            // note that this object carries the min. info to create an index list.
            PublishedAssessmentFacade f = (PublishedAssessmentFacade) takeableList.get(i);
            DeliveryBeanie deliveryBeanie = new DeliveryBeanie();
            setDeliveryBeanie(deliveryBeanie, f, selectActionListener, updatedAssessmentNeedResubmitList,
                    updatedAssessmentList);
            takeablePublishedList.add(deliveryBeanie);
        }

        //
        List<AssessmentModel> additionalInfo = new ArrayList<AssessmentModel>();
        ArrayList<DeliveryBeanie> assessments = new ArrayList<DeliveryBeanie>();
        AssessmentModel model;

        for (PublishedAssessmentFacade d : assessmentList) {
            if (d != null && d.getPublishedAssessmentId() != null) {
                DeliveryBeanie deliveryBeanie = new DeliveryBeanie();
                setDeliveryBeanie(deliveryBeanie, d, selectActionListener, updatedAssessmentNeedResubmitList,
                        updatedAssessmentList);
                model = new AssessmentModel();

                setAssessmentModel(d, model);

                if (d.getStartDate() != null) {
                    model.setStartDate(d.getStartDate().getTime());
                }

                additionalInfo.add(model);
                if (takeablePublishedList.stream()
                        .filter(i -> i.getAssessmentId().equals(deliveryBeanie.getAssessmentId())).findAny()
                        .orElse(null) == null) {
                    assessments.add(deliveryBeanie);
                }
            }
        }

        result.setTakeableList((List<DeliveryBeanie>) takeablePublishedList);
        result.setAdditionalInfo(additionalInfo);
        result.setAssessmentList(assessments);

        return result;
    }

    @Override
    public AssessmentDataToeic getAssessmentToeicById(String id, HttpServletRequest request,
            HttpServletResponse response) {
        DeliveryBean delivery = null;
        try {
            delivery = (DeliveryBean) ContextUtil.lookupBeanFromExternalServlet("delivery", request, response);
        } catch (Throwable th) {
            LOG.error("Could not lookup bean " + delivery + ".", th);
        }
        if (delivery == null) {
            return null;
        }
        delivery.setAssessmentId(id);
        // reference DeliveryBean
        delivery.setActionString("takeAssessment");
        DeliveryActionListener deliveryActionListener = new DeliveryActionListener();
        LinearAccessDeliveryActionListener actionListener = new LinearAccessDeliveryActionListener();
        String publishedItemId = actionListener.getPublishedAssessmentId(delivery);
        // get table of contents
        PublishedAssessmentFacade publishedAssessment = actionListener.getPublishedAssessment(delivery,
                publishedItemId);

        // get parts
        List<PublishedSectionData> parts = publishedAssessment.getSectionArraySorted();
        delivery.setPublishedAssessment(publishedAssessment);

        // Clear elapsed time, set not timed out
        //// actionListener.clearElapsedTime(delivery);

        // set show student score
        actionListener.setShowStudentScore(delivery, publishedAssessment);
        actionListener.setShowStudentQuestionScore(delivery, publishedAssessment);
        //// actionListener.setDeliverySettings(delivery, publishedAssessment);

        String agent = actionListener.getAgentString();
        AssessmentGradingData ag = null;

        // this returns a HashMap with (publishedItemId, itemGrading)
        Map itemGradingHash = gradingService.getLastItemGradingData(publishedItemId, agent);

        boolean isFirstTimeBegin = false;
        if (itemGradingHash != null && itemGradingHash.size() > 0) {
            ag = actionListener.setAssessmentGradingFromItemData(delivery, itemGradingHash, true);
            actionListener.setAttemptDateIfNull(ag);
        } else {
            ag = gradingService.getLastSavedAssessmentGradingByAgentId(publishedItemId, agent);
            if (ag == null) {
                ag = actionListener.createAssessmentGrading(publishedAssessment);
                isFirstTimeBegin = true;
            } else {
                actionListener.setAttemptDateIfNull(ag);
            }
        }
        delivery.setAssessmentGrading(ag);
        PublishedAssessmentService pubService = new PublishedAssessmentService();
        actionListener.populateSubmissionsRemaining(pubService, publishedAssessment, delivery);

        delivery.setAssessmentGradingId(delivery.getAssessmentGrading().getAssessmentGradingId());

        // ag can"t be null beyond this point and must have persisted to DB
        actionListener.setFeedbackMode(delivery);

        actionListener.setTimer(delivery, publishedAssessment, true, isFirstTimeBegin);
        actionListener.setStatus(delivery, pubService, Long.valueOf(id));

        EventLogService eventService = new EventLogService();
        EventLogFacade eventLogFacade = new EventLogFacade();
        String agentEid = AgentFacade.getEid();

        if (agentEid == null || agentEid.equals("")) {
            agentEid = "N/A";
        }
        // set event log data
        EventLogData eventLogData = new EventLogData();
        eventLogData.setAssessmentId(Long.valueOf(id));
        eventLogData.setProcessId(delivery.getAssessmentGradingId());
        eventLogData.setStartDate(new Date());
        eventLogData.setTitle(publishedAssessment.getTitle());
        eventLogData.setUserEid(agentEid);
        String site_id = AgentFacade.getCurrentSiteId();
        // take assessment via url
        if (site_id == null) {
            site_id = pubService.getPublishedAssessmentOwner(Long.valueOf(delivery.getAssessmentId()));
        }
        eventLogData.setSiteId(site_id);
        eventLogData.setErrorMsg(eventLogMessages.getString("no_submission"));
        eventLogData.setEndDate(null);
        eventLogData.setEclipseTime(null);
        eventLogFacade.setData(eventLogData);
        eventService.saveOrUpdateEventLog(eventLogFacade);

        // extend session time out
        SessionUtil.setSessionTimeout(FacesContext.getCurrentInstance(), delivery, true);

        Map publishedAnswerHash = pubService.preparePublishedAnswerHash(publishedAssessment);

        // overload itemGradingHash with the sequence in case renumbering is turned off.
        actionListener.overloadItemData(delivery, itemGradingHash, publishedAssessment);
        // get table of contents
        ContentsDeliveryBean tableOfContents = actionListener.getContents(publishedAssessment, itemGradingHash,
                delivery, publishedAnswerHash);

        String desc = publishedAssessment.getDescription();
        boolean isPractice = false;
        if (desc != null) {
            desc = desc.toLowerCase();
            if (desc.contains(TYPE_PRACTICE)) {
                isPractice = Boolean.TRUE;
            } else if (desc.contains(TYPE_EXAM)) {
                // Official exam
                isPractice = Boolean.FALSE;
            } else {
                // Normal Tests && Quizzes
                LOG.warn("Normal assessment: desc = " + desc);
            }
        } else {
            // No description means TOEIC Exam
            isPractice = Boolean.FALSE;
        }

        if (!isPractice && !isFirstTimeBegin) {
            syncTimeElapsedWithServer(delivery.getAssessmentGrading());
        }

        // get contents of the TOEIC assessment
        AssessmentDataToeic assessment = parseAssessment(tableOfContents, agent, isPractice);
        assessment.setAssessmentGradingId(delivery.getAssessmentGrading().getAssessmentGradingId());
        assessment.setDescription(publishedAssessment.getDescription());
        assessment.setTimeLimit(publishedAssessment.getTimeLimit());
        assessment.setDueDate(publishedAssessment.getDueDate());
        assessment.setAssessmentTitle(publishedAssessment.getTitle());
        assessment.setPractice(isPractice);

        String password = delivery.getSettings().getPassword();
        Set ip = delivery.getSettings().getIpAddresses();
        assessment.setPassword(password);
        // assessment.setIp(ip);
        Map mapMetaData = publishedAssessment.getAssessmentMetaDataMap();

        // Log
        mapMetaData.forEach((k, v) -> LOG.info("Meta data Item : " + k + " Count : " + v));

        return assessment;
    }

    /**
     * Parse assessment bean of Sakai to customized assessment bean for TOEIC
     * @param tableOfContents
     * @param agentId - identifier of the agent
     * @return customized assessment bean
     */
    private AssessmentDataToeic parseAssessment(ContentsDeliveryBean tableOfContents, String agentId,
            boolean isPractice) {
        List<SectionContentsBean> parts = tableOfContents.getPartsContents();
        List<PartDataToeic> newParts = parseParts(parts, agentId, isPractice);

        AssessmentDataToeic aDataToeic = new AssessmentDataToeic(newParts, tableOfContents.getCurrentScore(),
                tableOfContents.getMaxScore());

        return aDataToeic;
    }

    /**
     * Get list of customized parts for TOEIC test
     * @param oldParts - list of parts based on part bean of Sakai
     * @param agentId - identifier of the agent
     * @return customized parts
     */
    private List<PartDataToeic> parseParts(List<SectionContentsBean> oldParts, String agentId, boolean isPractice) {
        List<PartDataToeic> newParts = new ArrayList<>();

        for (SectionContentsBean part : oldParts) {
            newParts.add(parsePart(part, agentId, isPractice));
        }

        return newParts;
    }

    /**
     * Parse bean represented for each part of Sakai to customized part bean
     * @param part - part bean of Sakai
     * @return PartDataToeic - customized bean for each part
     */
    private PartDataToeic parsePart(SectionContentsBean part, String agentId, boolean isPractice) {
        PartDataToeic partDataToeic = new PartDataToeic();
        partDataToeic.setPartId(part.getSectionId());
        partDataToeic.setDescription(part.getDescription());
        partDataToeic.setTitle(part.getTitle());
        partDataToeic.setMaxPoints(part.getMaxPoints());

        if (part.getPoolIdToBeDrawn() != null) {
            Long poolId = part.getPoolIdToBeDrawn();
            partDataToeic.setPartId(poolId.toString());
            QuestionPoolFacade questionPool = new QuestionPoolService().getPool(poolId, agentId);
            String description = questionPool.getDescription();
        }

        partDataToeic.setPoolName(part.getPoolNameToBeDrawn());

        List<ItemContentsBean> itemContents = part.getItemContents();

        List<QuestionDataToeic> questions = parseQuestions(itemContents, isPractice, part.getTitle());
        partDataToeic.setItemContents(questions);

        return partDataToeic;
    }

    /**
     * Get list of customized questions for TOEIC test
     * @param itemContents - list of questions based on question bean of Sakai
     * @return customized questions
     */
    private List<QuestionDataToeic> parseQuestions(List<ItemContentsBean> itemContents, boolean isPractice,
            String partTitle) {
        List<QuestionDataToeic> questions = new ArrayList<>();

        for (ItemContentsBean question : itemContents) {
            questions.add(parseQuestion(question, isPractice, partTitle));
        }

        return questions;
    }

    /**
     * Parse bean represented for each question of Sakai to customized question bean
     * @param itemContent - question bean of Sakai
     * @return QuestionDataToeic - customized bean for each question
     */
    private QuestionDataToeic parseQuestion(ItemContentsBean itemContent, boolean isPractice, String partTitle) {
        QuestionDataToeic question = new QuestionDataToeic();
        question.setDiscount(itemContent.getDiscount());
        question.setDuration(itemContent.getDuration());
        question.setMaxPoint(itemContent.getMaxPoints());

        // Thach. Debug to get Objective in the questions.
        Set<ItemMetaDataIfc> itemMetaDataSet = itemContent.getItemData().getItemMetaDataSet();
        String metaEntry;
        String label;
        ItemDataIfc itemData;
        String objective = "";

        for (ItemMetaDataIfc itemMetaDataIfc : itemMetaDataSet) {
            label = itemMetaDataIfc.getLabel();

            if (ItemMetaDataIfc.OBJECTIVE.equals(label)) {
                objective = metaEntry = itemMetaDataIfc.getEntry();
            }
        }

        question.setQuestionId(itemContent.getItemData().getItemId());
        question.setObjective(objective);
        question.setContent(getContentQuestion((PublishedItemData) itemContent.getItemData()));
        ArrayList<SelectionBean> answers = (ArrayList<SelectionBean>) itemContent.getAnswers();
        question.setAnswers(parseAnswers(answers, isPractice));
        
        // Use objective as category
        question.setCategory(objective);

        return question;
    }

    /**
     * Get detailed content of the question
     * @param itemData - contain data of the question
     * @return
     */
    private QuestionContent getContentQuestion(PublishedItemData itemData) {
        Iterator<PublishedItemText> iterator = itemData.getItemTextSet().iterator();
        // PublishedItemText contentQuestion = iterator.next();
        String questionString = iterator.next().getText();

        Gson gson = new Gson();
        QuestionContent questionContent = new QuestionContent();

        if (questionString.startsWith("\"{") || questionString.startsWith("{")) {
            questionContent = gson.fromJson(questionString, QuestionContent.class);
        } else {
            questionContent.setJustText(questionString);
        }

        return questionContent;
    }

    /**
     * Get list of customized answers of a question for TOEIC test
     * @param answers - list of answers based on answer bean of Sakai
     * @return customized answers
     */
    private ArrayList<AnswerToeic> parseAnswers(ArrayList<SelectionBean> answers, boolean isPractice) {
        ArrayList<AnswerToeic> newanswers = new ArrayList<>();
        for (SelectionBean answer : answers) {
            newanswers.add(parseAnswer(answer, isPractice));
        }

        return newanswers;
    }

    /**
     * Parse answer bean of Sakai to customized answer bean
     * @param answer bean of Sakai
     * @return AnswerToeic
     */
    private AnswerToeic parseAnswer(SelectionBean answer, boolean isPractice) {
        AnswerToeic newAnswer = new AnswerToeic();
        AnswerIfc ans = answer.getAnswer();
        newAnswer.setAnswerId(ans.getId());
        newAnswer.setLabel(ans.getLabel());
        newAnswer.setText(ans.getText());
        if (isPractice) {
            for (AnswerFeedbackIfc a : ans.getAnswerFeedbackSet()) {
                if (a != null) {
                    newAnswer.setFeedback(a.getText());
                }
            }
        }

        return newAnswer;
    }

    @Override
    public String grading(ItemToeicGrading itemToeicGrading, Boolean isSubmit, Boolean isAutoSubmit) {

        // int correctListening = 0;
        // int correctReading = 0;
        // int total = 0;
        String result = null;
        Date submittedDate = new Date();
        ToeicFeedback feedback = new ToeicFeedback();
        // List<Integer> correctParts = Arrays.asList(0, 0, 0, 0, 0, 0, 0);
        Set<ItemGradingData> newItemSet = new HashSet<ItemGradingData>();
        Gson gson = new Gson();

        ItemGradingData itemGradingData;

        AssessmentGradingData assessmentGradingData = gradingService
                .load(itemToeicGrading.getAssessmentGradingId().toString());

        if (assessmentGradingData.getForGrade()) {
            return null;
        }

        List<Long> correctAnswers = gradingService
                .getAssessmentCorrectPublishedAnswerIds(itemToeicGrading.getAssessmentId());
        String title = gradingService.getPublishedAssessmentTitle(itemToeicGrading.getAssessmentGradingId());

        // set AssessmentGradingData's data
        // assessmentGradingData.setPublishedAssessmentTitle(itemToeicGrading.getAssessmentTitle());
        assessmentGradingData.setPublishedAssessmentTitle(title);
        assessmentGradingData.setAssessmentGradingAttachmentSet(null);
        assessmentGradingData.setIsLate(Boolean.FALSE);
        assessmentGradingData.setTimeElapsed(itemToeicGrading.getTimeElapsed());
        assessmentGradingData.setLastVisitedPart(itemToeicGrading.getLastVisitedPart());
        assessmentGradingData.setLastVisitedQuestion(itemToeicGrading.getLastVisitedQuestion());

        int totalSubmiited = assessmentGradingData.getTotalSubmitted() + 1;

        assessmentGradingData.setTotalSubmitted(totalSubmiited);

        if (isSubmit) {
            assessmentGradingData.setForGrade(Boolean.TRUE);
            assessmentGradingData.setStatus(AssessmentGradingData.AUTO_GRADED);
            assessmentGradingData.setGradedDate(submittedDate);
            assessmentGradingData.setSubmittedDate(submittedDate);
        }
        // else {
        // assessmentGradingData.setForGrade(Boolean.FALSE);
        // assessmentGradingData.setStatus(IN_PROGRESS);
        // }

        if (isAutoSubmit) {
            assessmentGradingData.setIsAutoSubmitted(Boolean.TRUE);
        } else {
            assessmentGradingData.setIsAutoSubmitted(Boolean.FALSE);
        }

        // calculate score and set ItemGradingData's data.
        if (itemToeicGrading.getItems() != null && itemToeicGrading.getItems().size() > 0) {

            for (AnswerToeicGrading m : itemToeicGrading.getItems()) {
                boolean changed = false;

                // if (itemGradingSet.size() == 0) then matchingItemGrading = null;
                ItemGradingData matchingItemGrading = assessmentGradingData.getItemGradingSet().stream()
                        .filter(p -> p.getPublishedItemId().equals(m.getItemId())).findAny().orElse(null);

                // if matchingItemGrading != null check if user changed answer or not
                if (matchingItemGrading != null) {
                    itemGradingData = matchingItemGrading;

                    // if user changed answer then set new answer id and set flag changed to true.
                    if (itemGradingData.getPublishedAnswerId() != m.getAnswerId()) {

                        itemGradingData.setPublishedAnswerId(m.getAnswerId());
                        itemGradingData.setGradedDate(new Date());
                        itemGradingData.setSubmittedDate(new Date());
                        itemGradingData.setAnswerText(m.getAnswerText());
                        changed = true;
                    }

                    // if itemGradingData is not in database then make new one to save.
                } else {
                    itemGradingData = new ItemGradingData();

                    itemGradingData.setAssessmentGradingId(itemToeicGrading.getAssessmentGradingId());
                    itemGradingData.setAgentId(assessmentGradingData.getAgentId());
                    itemGradingData.setPublishedItemId(m.getItemId());
                    itemGradingData.setPublishedItemTextId(m.getItemId());
                    itemGradingData.setGradedDate(new Date());
                    itemGradingData.setSubmittedDate(new Date());
                    itemGradingData.setPublishedAnswerId(m.getAnswerId());
                    itemGradingData.setAnswerText(m.getAnswerText());
                }

                // String partNumber = m.getPoolName().substring(m.getPoolName().length() - 1,
                // m.getPoolName().length());
                int correctIndex = correctAnswers.indexOf(m.getAnswerId());

                // if user's selected answer exists in list of correct answers
                if (correctIndex > -1) {
                    itemGradingData.setIsCorrect(Boolean.TRUE);

                    // set correct number answers each part.
                    // if (isSubmit) {
                    // int index = Integer.parseInt(partNumber) - 1;
                    // int value = correctParts.get(index) + 1;
                    //
                    // correctParts.set(index, value);
                    // }

                    // if (partNumber.equals("1") || partNumber.equals("2") || partNumber.equals("3")
                    // || partNumber.equals("4")) {
                    // correctListening++;
                    // } else if (partNumber.equals("5") || partNumber.equals("6") || partNumber.equals("7")) {
                    // correctReading++;
                    // }
                } else {
                    itemGradingData.setIsCorrect(Boolean.FALSE);
                }

                // if itemGradingData is not exists in database or itemGradingData is exists in database and user
                // changed answer.
                if (itemGradingData.getItemGradingId() == null
                        || (itemGradingData.getItemGradingId() != null && changed)) {
                    newItemSet.add(itemGradingData);
                }
            }
        }

        // Integer listeningScore = arrScoreListening.get(correctListening);
        // Integer readingScore = arrScoreReading.get(correctReading);
        //
        // LOG.debug("Calculate final score: correctListeningNumber=" + correctListening + "; correctReadingNumber="
        // + correctReading + "; listeningScore=" + listeningScore + "; readingScore=" + readingScore);
        //
        // total = listeningScore + readingScore;
        //
        // LOG.debug("finalScore=" + total);
        //
        // // set AssessmentGradingData calculated score
        // assessmentGradingData.setFinalScore(new Double(total));
        //
        // gradingService.saveOrUpdateAssessmentGradingOnly(assessmentGradingData);

        if (newItemSet.size() > 0) {
            gradingService.saveOrUpdateAll(newItemSet);
        }

        feedback = calculateScores(assessmentGradingData.getAssessmentGradingId());

        // calculate and set final score and save AssessmentGrading.
        assessmentGradingData.setFinalScore(Double.parseDouble(feedback.getFinalScore().toString()));
        gradingService.saveOrUpdateAssessmentGradingOnly(assessmentGradingData);

        if (isSubmit) {
            PublishedAssessmentService pubS = new PublishedAssessmentService();
            PublishedAssessmentFacade publishedAssessment = pubS
                    .getPublishedAssessment(assessmentGradingData.getPublishedAssessmentId().toString());
            Event event = eventTrackingService.newEvent(SamigoConstants.EVENT_ASSESSMENT_AUTO_GRADED,
                    assessmentGradingData.getPublishedAssessmentTitle(), null, true, NotificationService.NOTI_OPTIONAL,
                    SamigoLRSStatements.getStatementForGradedAssessment(assessmentGradingData, publishedAssessment));
            eventTrackingService.post(event);

            // set user correct answers to ToeicFeedback.
            // feedback.setListeningScore(arrScoreListening.get(correctListening));
            // feedback.setReadingScore(arrScoreReading.get(correctReading));
            // feedback.setFinalScore(total);
            // feedback.setCorrectParts(correctParts);
            feedback.setAssessmentGradingId(assessmentGradingData.getAssessmentGradingId());

            ToeicGeneralFeedback generalFb = gradingService.getToeicGeneralFeedback(feedback.getFinalScore());

            // kind = 0: listening; kind = 1: reading
            ToeicDetailFeedback listeningFb = gradingService.getToeicDetailFeedback(feedback.getListeningScore(), 0);
            ToeicDetailFeedback readingFb = gradingService.getToeicDetailFeedback(feedback.getReadingScore(), 1);

            List<ToeicDetailFeedback> detailFeedbacks = Arrays.asList(listeningFb, readingFb);

            feedback.setToeicGeneralFeedback(generalFb);
            feedback.setToeicDetailFeedback(detailFeedbacks);

            result = gson.toJson(feedback);
        } else {
            result = "{\"status\": \"Success\"}";
        }

        return result;
    }

    @Override
    public ItemToeicGrading getSavedAnswer(String assessmentGradingIdString) {
        ItemToeicGrading result = new ItemToeicGrading();
        List<AnswerToeicGrading> items = new ArrayList<AnswerToeicGrading>();

        AssessmentGradingData assessment = gradingService.load(assessmentGradingIdString);

        if (assessment.getItemGradingSet().size() > 0) {
            for (ItemGradingData i : assessment.getItemGradingSet()) {
                AnswerToeicGrading answer = new AnswerToeicGrading();

                answer.setAnswerId(i.getPublishedAnswerId());
                answer.setItemId(i.getPublishedItemId());
                answer.setItemGradingId(i.getItemGradingId());
                answer.setAnswerText(i.getAnswerText());

                items.add(answer);
            }

            result.setItems(items);
        }

        result.setTimeElapsed(assessment.getTimeElapsed());
        result.setLastVisitedPart(assessment.getLastVisitedPart());
        result.setLastVisitedQuestion(assessment.getLastVisitedQuestion());
        result.setAssessmentGradingId(assessment.getAssessmentGradingId());

        return result;
    }

    /**
     * get all graded (submitted) AssessmentGradingData
     * @param pubAssessmentId
     * @return
     * @see m.k.s.eng.sakai.service.AssessmentToeicService#getAllAssessmentGradingData(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<AssessmentGradingData> getAllAssessmentGradingData(Long pubAssessmentId) {
        List<AssessmentGradingData> ags = gradingService.getAllAssessmentGradingData(pubAssessmentId);
        List<AssessmentGradingData> result = new ArrayList<AssessmentGradingData>();

        for (AssessmentGradingData a : ags) {
            if (a.getForGrade()) {
                AgentFacade agent = new AgentFacade(a.getAgentId());
                a.setAgentId(agent.getEidString());
                result.add(a);
            }
        }

        return result;
    }

    /**
     * get all graded (submitted) AssessmentGradingData of current user
     * @param pubAssessmentId
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<AssessmentGradingData> getAllAssessmentGradingDataByIdAndAgent(Long pubAssessmentId) {

        List<AssessmentGradingData> ags = gradingService.getAllAssessmentGradingByAgentId(pubAssessmentId,
                AgentFacade.getAgentString());

        return ags;
    }

    /**
     * Get list ItemResult (question number, title, answer label of question, user's answer label, isCorrect, answerId,
     * itemId) of AssessmentGradingData.
     * @param idA - assessmentGradingId
     * @return
     * @see m.k.s.eng.sakai.service.AssessmentToeicService#getItemResults(java.lang.String)
     */
    @Override
    public List<ItemResult> getItemResults(Long assessmentGradingId, Long publishedAssessmentId,
            DeliveryBean deliveryBean) {
        List<ItemResult> result = new ArrayList<ItemResult>();

        // get list ItemGradingData of AssessmentGradingData
        List<Object[]> irs = Lists.newArrayList(gradingService.getItemResults(assessmentGradingId));
        AssessmentDataToeic asssessmentData = getToeicData(publishedAssessmentId, assessmentGradingId, deliveryBean);
        String qNumAndAnswerText[];
        String label;

        String questionId;
        String questionCategory;
        String questionPart;
        for (Object[] objectRow : irs) {
            qNumAndAnswerText = objectRow[0].toString().split("-");
            questionId = objectRow[6].toString();
            questionPart = objectRow[1].toString();
            label = AppUtil.findAnswerLabel(asssessmentData, questionPart, Long.valueOf(questionId),
                    Long.valueOf(objectRow[5].toString()));

            if (label == null) {
                label = "";
            }

            ItemResult ir = new ItemResult(qNumAndAnswerText[0].toString(), questionPart, label,
                    qNumAndAnswerText[1].toString(), Boolean.parseBoolean(objectRow[4].toString()), questionId);

            // Using ML to determine category of question
            questionCategory = "none"; // determineCategory(questionId, questionPart);

            ir.setCategory(questionCategory);

            result.add(ir);
        }

        return result;
    }

    /**
     * Version demo of determine category of Parts.
     * @param questionId
     * @param questionPart
     * @return randomized category of the question with given part.
     */
//    private String determineCategory(String questionId, String questionPart) {
//        try {
//            String[][] demoData = {{"Photos of People", "Photos of Things"}, {"What", "When"}, {"Bussines", "School"},
//                    {"Email", "Air sport", "Weather"}, {"Adj", "Adv"},
//                    {"Verbs and Tenses", "Comparisons and Superlatives"}, {"Form", "Letters"}};
//
//            // PartNo start from 0;
//            int partNo = Integer.valueOf(questionPart.charAt(questionPart.length() - 1)) - 1;
//            Random rand = new Random();
//            int randPos = rand.nextInt(demoData[partNo].length) % 7;
//
//            return demoData[partNo][randPos];
//        } catch (Throwable th) {
//            LOG.warn("Wrong in demo code", th);
//            return "Photos of People";
//        }
//    }

    @Override
    public List<Member> getMembersInSite() {
        List<Member> result = new ArrayList<Member>();
        SiteService siteService = sakaiProxy.getMySiteService();

        if (siteService == null) {
            LOG.warn("[getDataTest]Could not get SiteService.");
        } else {
            LOG.info("[getDataTest]Log SiteService...");
            siteService.getUserSites().forEach(site -> {
                LOG.info("SiteId =" + site.getId());

                Set<Member> members = site.getMembers();
                for (Iterator<Member> it = members.iterator(); it.hasNext();) {
                    Member user = it.next();
                    if (!user.equals(null)) {
                        result.add(user);
                    }
                    LOG.info("userId=" + user.getUserId() + ";eid" + user.getUserEid() + ";displayId="
                            + user.getUserDisplayId() + ";Role" + user.getRole());
                }
            });

        }

        return result;
    }

    @Override
    public List<Member> getStudentsInSite(List<Member> members) {
        List<Member> students = new ArrayList<Member>();
        for (Member m : members) {
            if (m.getRole().getId().equals("Student") || m.getRole().getId().equals("Learner")
                    || m.getRole().getId().equals("access")) {
                students.add(m);
            }
        }

        return students;
    }

    @Override
    public List<Object[]> getResultList(Long assessmentGradingId) {
        return gradingService.getItemResults(assessmentGradingId);
    }

    @Override
    public String getDecryptPassword(Long publishedAssessmentId) {
        String description = gradingService.getPublishedAssessmentDescription(publishedAssessmentId);
        String result = "{\"password\": \"\"}";
        final String PASSWORD = "Password: ";
        if (description != null) {

            int index = description.indexOf(PASSWORD);
            if (index > -1) {
                String password = description.substring((index + PASSWORD.length()), description.length());
                result = "{\"password\": \"" + password + "\"}";
            } else {
                return result;
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveItemGradingList(List<AnswerToeicGrading> list) {
        Set<ItemGradingData> itemSet = gradingService
                .getItemGradingSet(list.get(0).getAssessmentGradingId().toString());
        Set<ItemGradingData> newItemSet = new HashSet<ItemGradingData>();

        for (AnswerToeicGrading a : list) {
            saveItemGrading(a, itemSet, newItemSet);
        }

        gradingService.saveOrUpdateAll(newItemSet);
    }

    @Override
    public void saveItemGrading(AnswerToeicGrading data, Set<ItemGradingData> itemSet,
            Set<ItemGradingData> newItemSet) {
        boolean changed = false;

        ItemGradingData item;

        // if (itemGradingSet.size() == 0) then matchingItemGrading = null;
        ItemGradingData matchingItemGrading = itemSet.stream()
                .filter(p -> p.getPublishedItemId().equals(data.getItemId())).findAny().orElse(null);

        // if matchingItemGrading != null check if user changed answer or not
        if (matchingItemGrading != null) {
            item = matchingItemGrading;

            if (!item.getPublishedItemId().equals(data.getItemId())) {
                item.setPublishedAnswerId(data.getAnswerId());
                item.setPublishedItemId(data.getItemId());
                item.setPublishedItemTextId(data.getItemId());

                setIsCorrect(item, data);
                changed = true;
            } else {
                if (!item.getPublishedAnswerId().equals(data.getAnswerId())) {
                    item.setPublishedAnswerId(data.getAnswerId());

                    setIsCorrect(item, data);
                    changed = true;
                }
            }

        } else {
            item = new ItemGradingData();

            item.setAssessmentGradingId(data.getAssessmentGradingId());
            item.setAgentId(AgentFacade.getAgentString());
            item.setPublishedItemId(data.getItemId());
            item.setPublishedItemTextId(data.getItemId());
            item.setGradedDate(new Date());
            item.setSubmittedDate(new Date());
            item.setPublishedAnswerId(data.getAnswerId());
            item.setAnswerText(data.getAnswerText());

            setIsCorrect(item, data);
        }

        if ((changed && item.getItemGradingId() != null) || item.getItemGradingId() == null) {
            newItemSet.add(item);
        }
    }

    private void setIsCorrect(ItemGradingData item, AnswerToeicGrading data) {
        Long correctpubAnswerId = gradingService.getCorrectPublishedAnswerIdByItemId(data.getItemId());

        if (correctpubAnswerId.equals(data.getAnswerId())) {
            item.setIsCorrect(Boolean.TRUE);
        } else {
            item.setIsCorrect(Boolean.FALSE);
        }
    }

    @Override
    public Integer calculateTimeLimit(PublishedAssessmentFacade publishesAssessment) {
        Integer timeLimit = null;
        Integer endToStart;
        Integer nowToStart;

        Date now = new Date();

        if (publishesAssessment.getAssessmentAccessControl().getLateHandling()
                .equals(AssessmentAccessControlIfc.ACCEPT_LATE_SUBMISSION)
                && publishesAssessment.getAssessmentAccessControl().getRetractDate() != null) {

            endToStart = (int) Math.ceil((publishesAssessment.getAssessmentAccessControl().getRetractDate().getTime()
                    - publishesAssessment.getStartDate().getTime()) / 1000);

            nowToStart = (int) Math.ceil((now.getTime() - publishesAssessment.getStartDate().getTime()) / 1000);

            timeLimit = endToStart - nowToStart;

            if (timeLimit > publishesAssessment.getTimeLimit()) {
                timeLimit = publishesAssessment.getTimeLimit();
            } else if (timeLimit == 0) {
                timeLimit = -1;
            }
        } else if (publishesAssessment.getAssessmentAccessControl().getLateHandling().equals(
                AssessmentAccessControlIfc.NOT_ACCEPT_LATE_SUBMISSION) && publishesAssessment.getDueDate() != null) {

            endToStart = (int) Math.ceil(
                    (publishesAssessment.getDueDate().getTime() - publishesAssessment.getStartDate().getTime()) / 1000);

            nowToStart = (int) Math.ceil((now.getTime() - publishesAssessment.getStartDate().getTime()) / 1000);

            timeLimit = endToStart - nowToStart;

            if (timeLimit > publishesAssessment.getTimeLimit()) {
                timeLimit = publishesAssessment.getTimeLimit();
            } else if (timeLimit == 0) {
                timeLimit = -1;
            }
        } else {
            timeLimit = publishesAssessment.getTimeLimit();
        }

        return timeLimit;
    }

    @Override
    public ToeicFeedback calculateScores(Long assessmentGradingId) {
        List<String> correctAnswerParts = gradingService.getListPartTitleOfCorrectAnswer(assessmentGradingId);
        ToeicFeedback score = calculateScores(correctAnswerParts);

        return score;
    }

    @Override
    public ToeicFeedback calculateScores(List<String> correctAnswerParts) {
        ToeicFeedback score = new ToeicFeedback();
        List<Integer> correctParts = Arrays.asList(0, 0, 0, 0, 0, 0, 0);
        String partNumber;
        Integer correctListening = 0;
        Integer correctReading = 0;

        for (String m : correctAnswerParts) {
            partNumber = m.substring(m.length() - 1, m.length());
            int index = Integer.parseInt(partNumber) - 1;
            int value = correctParts.get(index) + 1;
            correctParts.set(index, value);

            if (partNumber.equals("1") || partNumber.equals("2") || partNumber.equals("3") || partNumber.equals("4")) {
                correctListening++;
            } else if (partNumber.equals("5") || partNumber.equals("6") || partNumber.equals("7")) {
                correctReading++;
            }
        }

        Integer listeningScore = arrScoreListening.get(correctListening);
        Integer readingScore = arrScoreReading.get(correctReading);
        Integer total = listeningScore + readingScore;

        LOG.debug("Calculate final score: correctListeningNumber=" + correctListening + "; correctReadingNumber="
                + correctReading + "; listeningScore=" + listeningScore + "; readingScore=" + readingScore
                + "; finalScore=" + total);

        score.setListeningScore(listeningScore);
        score.setReadingScore(readingScore);
        score.setCorrectParts(correctParts);
        score.setFinalScore(total);

        return score;
    }

    private void syncTimeElapsedWithServer(AssessmentGradingData ag) {
        // this is to cover the scenerio when user took an assessment, Save & Exit, Then returned at a
        // later time, we need to account for the time taht he used before
        int timeElapsed = Math.round((new Date().getTime() - ag.getAttemptDate().getTime()) / 1000.0f);
        LOG.debug("***setTimeElapsed=" + timeElapsed);

        ag.setTimeElapsed(Integer.valueOf(timeElapsed));
        gradingService.saveOrUpdateAssessmentGradingOnly(ag);
    }

    @Override
    public AssessmentDataToeic getToeicData(Long publishedAssessmentId, Long assessmentGradingId,
            DeliveryBean delivery) {
        delivery.setAssessmentId(publishedAssessmentId.toString());
        // reference DeliveryBean
        delivery.setActionString("takeAssessment");
        DeliveryActionListener deliveryActionListener = new DeliveryActionListener();
        LinearAccessDeliveryActionListener actionListener = new LinearAccessDeliveryActionListener();
        String publishedItemId = actionListener.getPublishedAssessmentId(delivery);
        // get table of contents
        PublishedAssessmentFacade publishedAssessment = actionListener.getPublishedAssessment(delivery,
                publishedItemId);

        // check if assessment is available to take
        Date currentDate = new Date();
        Date startDate = publishedAssessment.getStartDate();
        Date dueDate = publishedAssessment.getDueDate();
        Date retractDate = publishedAssessment.getRetractDate();
        boolean acceptLateSubmission = AssessmentAccessControlIfc.ACCEPT_LATE_SUBMISSION
                .equals(publishedAssessment.getLateHandling());
        if (startDate != null && startDate.after(currentDate)) {
            return null;
        }

        if (acceptLateSubmission && (dueDate != null && dueDate.before(currentDate))
                && (retractDate == null || retractDate.before(currentDate))) {
            return null;
        }

        // get parts
        List<PublishedSectionData> parts = publishedAssessment.getSectionArraySorted();
        delivery.setPublishedAssessment(publishedAssessment);

        // Clear elapsed time, set not timed out
        //// actionListener.clearElapsedTime(delivery);

        // set show student score
        actionListener.setShowStudentScore(delivery, publishedAssessment);
        actionListener.setShowStudentQuestionScore(delivery, publishedAssessment);
        //// actionListener.setDeliverySettings(delivery, publishedAssessment);

        String agent = actionListener.getAgentString();
        AssessmentGradingData ag = null;

        Set<ItemGradingData> itemGradingSet = gradingService.getItemGradingSet(String.valueOf(assessmentGradingId));

        HashMap<Long, List<ItemGradingData>> itemGradingHash = new HashMap<>();

        for (ItemGradingData data : itemGradingSet) {
            List<ItemGradingData> thisone = itemGradingHash.get(data.getPublishedItemId());
            if (thisone == null) {
                thisone = new ArrayList<>();
            }
            thisone.add(data);
            itemGradingHash.put(data.getPublishedItemId(), thisone);
        }

        ag = gradingService.load(assessmentGradingId.toString());

        delivery.setAssessmentGrading(ag);
        PublishedAssessmentService pubService = new PublishedAssessmentService();
        actionListener.populateSubmissionsRemaining(pubService, publishedAssessment, delivery);

        delivery.setAssessmentGradingId(assessmentGradingId);

        // ag can"t be null beyond this point and must have persisted to DB
        actionListener.setFeedbackMode(delivery);

        // extend session time out
        SessionUtil.setSessionTimeout(FacesContext.getCurrentInstance(), delivery, true);

        Map publishedAnswerHash = pubService.preparePublishedAnswerHash(publishedAssessment);

        // overload itemGradingHash with the sequence in case renumbering is turned off.
        actionListener.overloadItemData(delivery, itemGradingHash, publishedAssessment);
        // get table of contents
        ContentsDeliveryBean tableOfContents = actionListener.getContents(publishedAssessment, itemGradingHash,
                delivery, publishedAnswerHash);

        // get contents of the TOEIC assessment
        AssessmentDataToeic assessment = parseAssessment(tableOfContents, agent, true);
        assessment.setAssessmentGradingId(delivery.getAssessmentGrading().getAssessmentGradingId());
        assessment.setDescription(publishedAssessment.getDescription());
        assessment.setTimeLimit(publishedAssessment.getTimeLimit());
        assessment.setDueDate(publishedAssessment.getDueDate());
        assessment.setAssessmentTitle(publishedAssessment.getTitle());
        assessment.setPractice(true);

        String password = delivery.getSettings().getPassword();
        assessment.setPassword(password);

        return assessment;
    }

    /**
     * get all not past due assessment.
     * @param assessmentList
     * @param h
     * @param updatedAssessmentNeedResubmitList
     * @param updatedAssessmentList
     * @return
     */
    private List<PublishedAssessmentFacade> getAssessmentList(List assessmentList, Map<Long, Integer> h,
            List updatedAssessmentNeedResubmitList, List updatedAssessmentList) {

        List<PublishedAssessmentFacade> takeableList = new ArrayList<PublishedAssessmentFacade>();
        GradingService gradingService = new GradingService();
        Map<Long, StudentGradingSummaryData> numberRetakeHash = gradingService
                .getNumberRetakeHash(AgentFacade.getAgentString());
        Map<Long, Integer> actualNumberRetake = gradingService.getActualNumberRetakeHash(AgentFacade.getAgentString());
        ExtendedTimeDeliveryService extendedTimeDeliveryService;
        for (int i = 0; i < assessmentList.size(); i++) {
            PublishedAssessmentFacade f = (PublishedAssessmentFacade) assessmentList.get(i);
            // Handle extended time info
            extendedTimeDeliveryService = new ExtendedTimeDeliveryService(f);
            if (extendedTimeDeliveryService.hasExtendedTime()) {
                f.setStartDate(extendedTimeDeliveryService.getStartDate());
                f.setDueDate(extendedTimeDeliveryService.getDueDate());
                // Override late handling here, availability check done later
                if (extendedTimeDeliveryService.getRetractDate() != null) {
                    f.setRetractDate(extendedTimeDeliveryService.getRetractDate());
                    f.setLateHandling(AssessmentAccessControlIfc.ACCEPT_LATE_SUBMISSION);
                }
                f.setTimeLimit(extendedTimeDeliveryService.getTimeLimit());
            }
            if (f.getReleaseTo() != null && !("").equals(f.getReleaseTo())
                    && !f.getReleaseTo().contains("Anonymous Users")) {
                if (isAvailable(f, h, numberRetakeHash, actualNumberRetake, updatedAssessmentNeedResubmitList,
                        updatedAssessmentList)) {
                    takeableList.add(f);
                }
            }
        }
        return takeableList;
    }

    /**
     * check if assessment is available (not count assessment start date < current date).
     * @param f
     * @param h
     * @param numberRetakeHash
     * @param actualNumberRetakeHash
     * @param updatedAssessmentNeedResubmitList
     * @param updatedAssessmentList
     * @return
     */
    public boolean isAvailable(PublishedAssessmentFacade f, Map<Long, Integer> h,
            Map<Long, StudentGradingSummaryData> numberRetakeHash, Map<Long, Integer> actualNumberRetakeHash,
            List updatedAssessmentNeedResubmitList, List updatedAssessmentList) {
        boolean returnValue = false;
        // 1. prepare our significant parameters
        Integer status = f.getStatus();
        Date currentDate = new Date();
        Date startDate = f.getStartDate();
        Date dueDate = f.getDueDate();
        Date retractDate = f.getRetractDate();
        boolean acceptLateSubmission = AssessmentAccessControlIfc.ACCEPT_LATE_SUBMISSION.equals(f.getLateHandling());

        if (!Integer.valueOf(1).equals(status)) {
            return false;
        }

        // if (startDate != null && startDate.after(currentDate)) {
        // return false;
        // }

        if (acceptLateSubmission && (dueDate != null && dueDate.before(currentDate))
                && (retractDate == null || retractDate.before(currentDate))) {
            return false;
        }

        if (updatedAssessmentNeedResubmitList.contains(f.getPublishedAssessmentId())
                || updatedAssessmentList.contains(f.getPublishedAssessmentId())) {
            return true;
        }

        int maxSubmissionsAllowed = 9999;
        if ((Boolean.FALSE).equals(f.getUnlimitedSubmissions())) {
            maxSubmissionsAllowed = f.getSubmissionsAllowed();
        }

        int numberRetake = 0;
        if (numberRetakeHash.get(f.getPublishedAssessmentId()) != null) {
            numberRetake = (((StudentGradingSummaryData) numberRetakeHash.get(f.getPublishedAssessmentId()))
                    .getNumberRetake());
        }
        int totalSubmitted = 0;

        // boolean notSubmitted = false;
        if (h.get(f.getPublishedAssessmentId()) != null) {
            totalSubmitted = ((Integer) h.get(f.getPublishedAssessmentId()));
        }

        // 2. time to go through all the criteria
        // Tests if dueDate has passed
        if (dueDate != null && dueDate.before(currentDate)) {
            // DUE DATE HAS PASSED
            if (acceptLateSubmission) {
                // LATE SUBMISSION ARE HANDLED: The assessment is available in these situations:
                // * Is the first submission
                // * A retake has been granted
                // (if late submission are handled, a previous test implies that retract date has not yet passed)
                if (totalSubmitted == 0) {
                    returnValue = true;
                } else {
                    int actualNumberRetake = 0;
                    if (actualNumberRetakeHash.get(f.getPublishedAssessmentId()) != null) {
                        actualNumberRetake = (actualNumberRetakeHash.get(f.getPublishedAssessmentId()));
                    }
                    if (actualNumberRetake < numberRetake) {
                        returnValue = true;
                    } else {
                        returnValue = false;
                    }
                }
            } else {
                // LATE SUBMISSION ARE NOT HANDLED: Test retract date and retakes
                if (retractDate == null || retractDate.after(currentDate)) {
                    int actualNumberRetake = 0;
                    if (actualNumberRetakeHash.get(f.getPublishedAssessmentId()) != null) {
                        actualNumberRetake = (actualNumberRetakeHash.get(f.getPublishedAssessmentId()));
                    }
                    if (actualNumberRetake < numberRetake) {
                        returnValue = true;
                    } else {
                        returnValue = false;
                    }
                } else {
                    // Retract date has passed: Assessment is not available
                    returnValue = false;
                }
            }
        } else {
            if (totalSubmitted < maxSubmissionsAllowed + numberRetake) {
                returnValue = true;
            }
        }

        return returnValue;
    }

    private void setDeliveryBeanie(DeliveryBeanie deliveryBeanie, PublishedAssessmentFacade d,
            SelectActionListener selectActionListener, List updatedAssessmentNeedResubmitList,
            List updatedAssessmentList) {
        deliveryBeanie.setAssessmentId(d.getPublishedAssessmentId().toString());
        deliveryBeanie.setAssessmentTitle(d.getTitle());
        deliveryBeanie.setDueDate(d.getDueDate());
        deliveryBeanie.setTimeRunning(false);// set to true in
        // BeginDeliveryActionListener
        selectActionListener.setTimedAssessment(deliveryBeanie, d);
        // check pastDue
        if (d.getDueDate() != null && (new Date()).after(d.getDueDate()))
            deliveryBeanie.setPastDue(true);
        else
            deliveryBeanie.setPastDue(false);

        if (updatedAssessmentNeedResubmitList.contains(d.getPublishedAssessmentId())) {
            deliveryBeanie.setAssessmentUpdatedNeedResubmit(true);
        } else {
            deliveryBeanie.setAssessmentUpdatedNeedResubmit(false);
        }

        if (updatedAssessmentList.contains(d.getPublishedAssessmentId())) {
            deliveryBeanie.setAssessmentUpdated(true);
        } else {
            deliveryBeanie.setAssessmentUpdated(false);
        }
    }

    private void setAssessmentModel(PublishedAssessmentFacade d, AssessmentModel model) {
        String desc = gradingService.getPublishedAssessmentDescription(d.getPublishedAssessmentId());
        List<Long> itemIds = PersistenceService.getInstance().getPublishedAssessmentFacadeQueries()
                .getQuestionIds(d.getPublishedAssessmentId());
        AssessmentGradingData lastSaved = gradingService.getLastSavedAssessmentGradingByAgentId(
                d.getPublishedAssessmentId().toString(), AgentFacade.getAgentString());

        model.setAssessmentId(d.getPublishedAssessmentId().toString());
        model.setIsPractice(AppUtil.isPractice(desc));
        model.setDescription(desc);
        model.setTotalQuestion(itemIds != null ? itemIds.size() : 0);

        if (lastSaved != null && lastSaved.getItemGradingSet() != null) {
            // int count = 0;
            // for (ItemGradingData i : lastSaved.getItemGradingSet()) {
            // if (i.getIsCorrect()) {
            // count++;
            // }
            // }

            model.setAnswered(lastSaved.getItemGradingSet().size());
            // model.setCorrectAnswered(count);
        }
    }
}
