package m.k.s.eng.sakai.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import m.k.s.eng.sakai.model.vocabulary.AssessmentDataVocabulary;
import m.k.s.eng.sakai.model.vocabulary.QuestionDataVocabulary;

public interface VocabularyService {
	
	/**
	 * get thong tin Assessment voi ID cua Assessment
	 * (Theo chuan Toiec)
	 * 
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	AssessmentDataVocabulary getAssessmentById(String id, HttpServletRequest request,
            HttpServletResponse response);
	
	/**
	 * get thong tin mot cau hoi theo questionId, assessmentId
	 * 
	 * @param assessmentId
	 * @param questionId
	 * @param request
	 * @param response
	 * @return
	 */
	QuestionDataVocabulary getQuestionData(String assessmentId, Long questionId, HttpServletRequest request,
            HttpServletResponse response);
	
	
	Boolean gradingQuestion(String assessmentId, Long answerId, 
			HttpServletRequest request, HttpServletResponse response);
	
	List<QuestionDataVocabulary> getQuestionsPath(String assessmentId, String pathName,
			HttpServletRequest request, HttpServletResponse response);
	
	
	List<QuestionDataVocabulary> getQuestionsPathId(String assessmentId, String pathId,
			HttpServletRequest request, HttpServletResponse response);
	
	
	List<QuestionDataVocabulary> getQuestionByIds(String assessmentId, Long[] questionIds,
			HttpServletRequest request, HttpServletResponse response);
}
