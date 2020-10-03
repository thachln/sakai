package m.k.s.eng.sakai.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.samigo.util.SamigoConstants;
import org.sakaiproject.tool.assessment.data.dao.assessment.Answer;
import org.sakaiproject.tool.assessment.data.dao.assessment.EventLogData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemText;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedSectionData;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.dao.grading.ItemGradingData;
import org.sakaiproject.tool.assessment.data.dao.grading.ToeicDetailFeedback;
import org.sakaiproject.tool.assessment.data.dao.grading.ToeicGeneralFeedback;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AnswerFeedbackIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AnswerIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemFeedbackIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemMetaDataIfc;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.facade.EventLogFacade;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.facade.QuestionPoolFacade;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.services.QuestionPoolService;
import org.sakaiproject.tool.assessment.services.assessment.EventLogService;
import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;
import org.sakaiproject.tool.assessment.ui.bean.delivery.ContentsDeliveryBean;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBean;
import org.sakaiproject.tool.assessment.ui.bean.delivery.ItemContentsBean;
import org.sakaiproject.tool.assessment.ui.bean.delivery.SectionContentsBean;
import org.sakaiproject.tool.assessment.ui.bean.delivery.SelectionBean;
import org.sakaiproject.tool.assessment.ui.listener.delivery.DeliveryActionListener;
import org.sakaiproject.tool.assessment.ui.listener.delivery.LinearAccessDeliveryActionListener;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.sakaiproject.tool.assessment.ui.web.session.SessionUtil;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import m.k.s.eng.sakai.model.vocabulary.AnswerVocabulary;
import m.k.s.eng.sakai.model.vocabulary.AssessmentDataVocabulary;
import m.k.s.eng.sakai.model.vocabulary.FeedBackAnswer;
import m.k.s.eng.sakai.model.vocabulary.PartDataVocabulary;
import m.k.s.eng.sakai.model.vocabulary.QuestionContentVocabulary;
import m.k.s.eng.sakai.model.vocabulary.QuestionDataVocabulary;
import m.k.s.eng.sakai.service.VocabularyService;

@Service("vocabularyService")
public class VocabularyServiceImpl implements VocabularyService {

	private static final String TYPE_EXAM = "type: exam";

    private static final String TYPE_PRACTICE = "type: practice";
	
	final static protected Log LOG = LogFactory.getLog(VocabularyServiceImpl.class);
	
	private GradingService gradingService = new GradingService();
	
	private Gson gson = new Gson();
	
	
	
	/**
	 * get thong tin Assessment boi Assessment ID
	 * Theo chuan TOIEC
	 * COPY qua
	 */
	@Override
	public AssessmentDataVocabulary getAssessmentById(String id, HttpServletRequest request, HttpServletResponse response) {
		DeliveryBean delivery = lookupDeliveryBean(request, response);
        if (delivery == null) {
            return null;
        }
        delivery.setAssessmentId(id);
        // reference DeliveryBean
        delivery.setActionString("takeAssessment");
        
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
        AssessmentDataVocabulary assessment = parseAssessment(tableOfContents, agent, isPractice);
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
	
	private void syncTimeElapsedWithServer(AssessmentGradingData ag) {
        // this is to cover the scenerio when user took an assessment, Save & Exit, Then returned at a
        // later time, we need to account for the time taht he used before
        int timeElapsed = Math.round((new Date().getTime() - ag.getAttemptDate().getTime()) / 1000.0f);
        LOG.debug("***setTimeElapsed=" + timeElapsed);

        ag.setTimeElapsed(Integer.valueOf(timeElapsed));
        gradingService.saveOrUpdateAssessmentGradingOnly(ag);
    }
	
	/**
     * Parse assessment bean of Sakai to customized assessment bean for TOEIC
     * @param tableOfContents
     * @param agentId - identifier of the agent
     * @return customized assessment bean
     */
    private AssessmentDataVocabulary parseAssessment(ContentsDeliveryBean tableOfContents, String agentId,
            boolean isPractice) {
    	
    	List<SectionContentsBean> parts = tableOfContents.getPartsContents();
        List<PartDataVocabulary> newParts = parseParts(parts, agentId, isPractice);

        AssessmentDataVocabulary aDataToeic = new AssessmentDataVocabulary(newParts, tableOfContents.getCurrentScore(),
                tableOfContents.getMaxScore());

        return aDataToeic;
    }

    /**
     * Get list of customized parts for TOEIC test
     * @param oldParts - list of parts based on part bean of Sakai
     * @param agentId - identifier of the agent
     * @return customized parts
     */
    private List<PartDataVocabulary> parseParts(List<SectionContentsBean> oldParts, String agentId, boolean isPractice) {
        List<PartDataVocabulary> newParts = new ArrayList<>();

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
    private PartDataVocabulary parsePart(SectionContentsBean part, String agentId, boolean isPractice) {
    	PartDataVocabulary partDataToeic = new PartDataVocabulary();
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

        List<QuestionDataVocabulary> questions = parseQuestions(itemContents, isPractice);
        partDataToeic.setItemContents(questions);

        return partDataToeic;
    }

    /**
     * Get list of customized questions for TOEIC test
     * @param itemContents - list of questions based on question bean of Sakai
     * @return customized questions
     */
    private List<QuestionDataVocabulary> parseQuestions(List<ItemContentsBean> itemContents, boolean isPractice) {
    	List<QuestionDataVocabulary> questions = new ArrayList<>();

        for (ItemContentsBean question : itemContents) {
            questions.add(parseQuestion(question, isPractice));
        }

        return questions;
    }

    /**
     * Parse bean represented for each question of Sakai to customized question bean
     * @param itemContent - question bean of Sakai
     * @return QuestionDataToeic - customized bean for each question
     */
    private QuestionDataVocabulary parseQuestion(ItemContentsBean itemContent, boolean isPractice) {
    	QuestionDataVocabulary question = new QuestionDataVocabulary();
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
        
        // get questionId : itemContent.getItemData().getItemId()

        question.setQuestionId(itemContent.getItemData().getItemId());
        question.setObjective(objective);
        question.setContent(getContentQuestion((PublishedItemData) itemContent.getItemData()));
        ArrayList<SelectionBean> answers = (ArrayList<SelectionBean>) itemContent.getAnswers();
        question.setAnswers(parseAnswers(answers, isPractice));

        return question;
    }
    
    /**
     * Get detailed content of the question
     * @param itemData - contain data of the question
     * @return
     */
    private QuestionContentVocabulary getContentQuestion(PublishedItemData itemData) {
    	Iterator<PublishedItemText> iterator = itemData.getItemTextSet().iterator();
        // PublishedItemText contentQuestion = iterator.next();
        String questionString = iterator.next().getText();

        Gson gson = new Gson();
        QuestionContentVocabulary questionContent = new QuestionContentVocabulary();

        if (questionString.startsWith("\"{") || questionString.startsWith("{")) {
            questionContent = gson.fromJson(questionString, QuestionContentVocabulary.class);
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
    private ArrayList<AnswerVocabulary> parseAnswers(ArrayList<SelectionBean> answers, boolean isPractice) {
    	ArrayList<AnswerVocabulary> newanswers = new ArrayList<>();
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
    private AnswerVocabulary parseAnswer(SelectionBean answer, boolean isPractice) {
    	AnswerVocabulary newAnswer = new AnswerVocabulary();
        AnswerIfc ans = answer.getAnswer();
        //Answer an = (Answer) answer.getAnswer();
       
        newAnswer.setIsCorrent(ans.getIsCorrect());
        newAnswer.setAnswerId(ans.getId());
        newAnswer.setLabel(ans.getLabel());
        newAnswer.setText(ans.getText());
        
        if (true) {
        	List<FeedBackAnswer> feedbackAnswer = new ArrayList<FeedBackAnswer>();
        	Set<ItemFeedbackIfc> f = ans.getItem().getItemFeedbackSet();
        	Iterator<ItemFeedbackIfc> iterater = f.iterator();
        	
            while (iterater.hasNext()) {
            	ItemFeedbackIfc feedBack = iterater.next();
     
            	if (feedBack != null) {
            		feedbackAnswer.add(new FeedBackAnswer(feedBack.getId(), feedBack.getText(), feedBack.getTypeId()));
            	}
            }
            newAnswer.setFeedback(feedbackAnswer);
        }

        return newAnswer;
    }
    
    private DeliveryBean lookupDeliveryBean(HttpServletRequest request, HttpServletResponse response) {
    	DeliveryBean delivery = null;
        try {
            delivery = (DeliveryBean) ContextUtil.lookupBeanFromExternalServlet("delivery", request, response);
        } catch (Throwable th) {
            LOG.error("Could not lookup bean " + delivery + ".", th);
        }
        return delivery;
    }

    
    /**
     * get thong tin mot cau hoi theo questionId, assessmentId
     */
	@Override
	public QuestionDataVocabulary getQuestionData(String assessmentId, Long questionId, HttpServletRequest request,
			HttpServletResponse response) {
		
		DeliveryBean delivery = lookupDeliveryBean(request, response);
        if (delivery == null) {
            return null;
        }
        delivery.setAssessmentId(assessmentId);
        // reference DeliveryBean
        delivery.setActionString("takeAssessment");
      
        LinearAccessDeliveryActionListener actionListener = new LinearAccessDeliveryActionListener();
        String publishedItemId = actionListener.getPublishedAssessmentId(delivery);
        // get table of contents
        PublishedAssessmentFacade publishedAssessment = actionListener.getPublishedAssessment(delivery,
                publishedItemId);

        // get parts
        List<PublishedSectionData> parts = publishedAssessment.getSectionArraySorted();
        delivery.setPublishedAssessment(publishedAssessment);

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
        actionListener.setStatus(delivery, pubService, Long.valueOf(assessmentId));

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

        // get contents of the TOEIC assessment
        // AssessmentDataToeic assessment = parseAssessment(tableOfContents, agent, isPractice);
        
        QuestionDataVocabulary question = parseDataQuestion(tableOfContents, agent, questionId, isPractice);
       
		return question;
	}


	private QuestionDataVocabulary parseDataQuestion(ContentsDeliveryBean tableOfContents, String agent, Long questionId, Boolean isPractic) {
		List<SectionContentsBean> parts = tableOfContents.getPartsContents();// get list Part of Table
		QuestionDataVocabulary question = new QuestionDataVocabulary();
		for(SectionContentsBean part : parts) {
			List<ItemContentsBean> itemContents = part.getItemContents(); // get list question
			// part questions
			for (ItemContentsBean questionBean : itemContents) {
				//Set<ItemMetaDataIfc> itemMetaDataSet = question.getItemData().getItemMetaDataSet();
				if (questionId.equals(questionBean.getItemData().getItemId())) {
					question = parseQuestion(questionBean,isPractic);
				}
			}
		}
		return question;
	}

	@Override
	public Boolean gradingQuestion(String assessmentId, Long answerId, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			Long assessment = Long.parseLong(assessmentId);
			List<Long> correctAnswers = gradingService
	                .getAssessmentCorrectPublishedAnswerIds(assessment);
	        
	        int correctIndex = correctAnswers.indexOf(answerId);
	        if (correctIndex > -1) {
	            return Boolean.TRUE;
	        } else {
	            return Boolean.FALSE;
	        }
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	private List<QuestionDataVocabulary> parseListQuestion(ContentsDeliveryBean tableOfContents, 
			String agent, String pathName, Boolean isPractic) {
		PartDataVocabulary partNew = new PartDataVocabulary();
		List<SectionContentsBean> parts = tableOfContents.getPartsContents();// get list Part of Table
		for(SectionContentsBean part : parts) {
			if (pathName.equals(part.getTitle())) {
				partNew = parsePart(part, agent, isPractic);
			}
		}
		return partNew.getItemContents();
	}
	
	private List<QuestionDataVocabulary> parseListQuestionPartId(ContentsDeliveryBean tableOfContents, 
			String agent, String pathId, Boolean isPractic) {
		PartDataVocabulary partNew = new PartDataVocabulary();
		List<SectionContentsBean> parts = tableOfContents.getPartsContents();// get list Part of Table
		for(SectionContentsBean part : parts) {
			
			String Id = part.getSectionId();
			if (part.getPoolIdToBeDrawn() != null) {
				Id = part.getPoolIdToBeDrawn().toString();
			}
			
			LOG.info(Id);
			
			if (pathId.equals(Id)) {
				partNew = parsePart(part, agent, isPractic);
			}
		}
		return partNew.getItemContents();
	}
	
	@Override
	public List<QuestionDataVocabulary> getQuestionsPath(String assessmentId, String pathName,
			HttpServletRequest request, HttpServletResponse response) {
		
		DeliveryBean delivery = lookupDeliveryBean(request, response);
        if (delivery == null) {
            return null;
        }
        delivery.setAssessmentId(assessmentId);
        // reference DeliveryBean
        delivery.setActionString("takeAssessment");
      
        LinearAccessDeliveryActionListener actionListener = new LinearAccessDeliveryActionListener();
        String publishedItemId = actionListener.getPublishedAssessmentId(delivery);
        // get table of contents
        PublishedAssessmentFacade publishedAssessment = actionListener.getPublishedAssessment(delivery,
                publishedItemId);

        // get parts
        List<PublishedSectionData> parts = publishedAssessment.getSectionArraySorted();
        delivery.setPublishedAssessment(publishedAssessment);

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
        actionListener.setStatus(delivery, pubService, Long.valueOf(assessmentId));

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
        
		return parseListQuestion(tableOfContents, agent, pathName, isPractice);
	}

	@Override
	public List<QuestionDataVocabulary> getQuestionsPathId(String assessmentId, String pathId,
			HttpServletRequest request, HttpServletResponse response) {
		DeliveryBean delivery = lookupDeliveryBean(request, response);
        if (delivery == null) {
            return null;
        }
        delivery.setAssessmentId(assessmentId);
        // reference DeliveryBean
        delivery.setActionString("takeAssessment");
      
        LinearAccessDeliveryActionListener actionListener = new LinearAccessDeliveryActionListener();
        String publishedItemId = actionListener.getPublishedAssessmentId(delivery);
        // get table of contents
        PublishedAssessmentFacade publishedAssessment = actionListener.getPublishedAssessment(delivery,
                publishedItemId);

        // get parts
        List<PublishedSectionData> parts = publishedAssessment.getSectionArraySorted();
        delivery.setPublishedAssessment(publishedAssessment);

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
        actionListener.setStatus(delivery, pubService, Long.valueOf(assessmentId));

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
		return parseListQuestionPartId(tableOfContents, agent, pathId, isPractice);
	}

	/**
	 * Method implements from {@link : VocabularyService}
	 * 
	 */
	@Override
	public List<QuestionDataVocabulary> getQuestionByIds(String assessmentId, Long[] questionIds,
			HttpServletRequest request, HttpServletResponse response) {
		DeliveryBean delivery = lookupDeliveryBean(request, response);
        if (delivery == null) {
            return null;
        }
        delivery.setAssessmentId(assessmentId);
        // reference DeliveryBean
        delivery.setActionString("takeAssessment");
      
        LinearAccessDeliveryActionListener actionListener = new LinearAccessDeliveryActionListener();
        String publishedItemId = actionListener.getPublishedAssessmentId(delivery);
        // get table of contents
        PublishedAssessmentFacade publishedAssessment = actionListener.getPublishedAssessment(delivery,
                publishedItemId);

        // get parts
        List<PublishedSectionData> parts = publishedAssessment.getSectionArraySorted();
        delivery.setPublishedAssessment(publishedAssessment);

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
        actionListener.setStatus(delivery, pubService, Long.valueOf(assessmentId));

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
		//ContentsDeliveryBean tableOfContents, String agent, Long questionId, Boolean isPractic
		return getListQuestionByIds(tableOfContents, agent, questionIds, isPractice);
	}

	/**
	 * Method parse List Question from {@link : ContentsDeliveryBean} with List<Long> questionIds
	 * Neu ContentsDeliveryBean co Question-id ton tai voi {@link : questionIds} input thi luu lai va tra ve 
	 * 
	 * @param tableOfContents {@link : ContentsDeliveryBean} ContentData theo chuan cua Sakai samigo
	 * @param agent User hien tai
	 * @param questionIds {@link : List<Long> questionId}}
	 */
	private List<QuestionDataVocabulary> getListQuestionByIds(ContentsDeliveryBean tableOfContents, String agent,
			Long[] questionIds, boolean isPractice) {
		
		try {
			// parse Long[] to List<Long> de dung duoc contains
			List<Long> question = Arrays.asList(questionIds);
			// List luu tru cau hoi tra ve
			List<QuestionDataVocabulary> questionNew = new ArrayList<QuestionDataVocabulary>();
			// parse table Contents
			List<SectionContentsBean> parts = tableOfContents.getPartsContents();// get list Part of Table
			for(SectionContentsBean part : parts) {
				List<ItemContentsBean> itemContents = part.getItemContents(); // get list question
				// part questions
				for (ItemContentsBean questionBean : itemContents) {
					// if List question truyen vao co ton tai question tu sakai
					if (question.contains(questionBean.getItemData().getItemId())) {
						questionNew.add(parseQuestion(questionBean,isPractice));
					}
				}
			}
			return questionNew;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	
	
	
}
