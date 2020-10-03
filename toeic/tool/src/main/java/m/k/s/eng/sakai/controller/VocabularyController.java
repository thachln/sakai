package m.k.s.eng.sakai.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import m.k.s.eng.sakai.model.vocabulary.AssessmentDataVocabulary;
import m.k.s.eng.sakai.model.vocabulary.QuestionDataVocabulary;
import m.k.s.eng.sakai.service.AssessmentToeicService;
import m.k.s.eng.sakai.service.VocabularyService;

@RestController
@RequestMapping(value = "/vocabulary")
public class VocabularyController {
	final static protected Log LOG = LogFactory.getLog(VocabularyController.class);

	@Autowired
	@Qualifier("vocabularyService")
	private VocabularyService vocabularyService;
	
	@Autowired
	@Qualifier("assessmentToeicService")
	private AssessmentToeicService assessmentToeicService;
	
	/**
	 * get Data of Toiec Sakai
	 * get all question data of a Assessment with assessment Id
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getDataToeic/{id}", method = RequestMethod.POST)
    @ResponseBody
	public AssessmentDataVocabulary getAssessmentDataToeic(@PathVariable("id") String id, HttpServletRequest request,
            HttpServletResponse response) {
		return vocabularyService.getAssessmentById(id, request, response);
	}
	
	/**
	 * get data of a question Test and Quizz with assessmentId and questionId
	 * customized according to getDataToiec with a Question
	 * Data of a Question include text.. and Answers with field isCorrect
	 * 
	 * @param assessmentId
	 * @param questionId
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getQuestionData/{assessmentId}/{questionId}", method = RequestMethod.POST)
	@ResponseBody
	public QuestionDataVocabulary getQuestionData(@PathVariable("assessmentId") String assessmentId,
			@PathVariable("questionId") Long questionId, 
			HttpServletRequest request, HttpServletResponse response) {
		
		return vocabularyService.getQuestionData(assessmentId, questionId, request, response);
	}
	
	/**
	 * grading a question with assessmentId and answerId
	 * Because answerId is unique in a assessment, so just only answerId
	 * 
	 * @param assessmentId
	 * @param answerId
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "gradingQuestion/{assessmentId}/{answerId}", method = RequestMethod.POST)
	@ResponseBody
	public Boolean gradingQuestion(@PathVariable("assessmentId") String assessmentId, @PathVariable("answerId") Long answerId,
			HttpServletRequest request, HttpServletResponse response) {

		return vocabularyService.gradingQuestion(assessmentId, answerId, request, response);
	}
	
	/**
	 * Get List Question from a Part in Assessment with Part Name
	 * Thuc hien cho chuc nang tim kiem cac cau hoi trong mot Part theo PartName
	 * 
	 * Ket qua tra ve co the khong chinh xac tuyet doi, do PartName co the bi trung hoac PartName khi setup bi trong
	 * 
	 * @param assessmentId : Id cua mot Assessment trong he thong Sakai
	 * @param partName : PartName 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "getQuestionsPathText/{assessmentId}/{partName}", method = RequestMethod.POST)
	@ResponseBody
	public List<QuestionDataVocabulary> getQuestionsPath(@PathVariable("assessmentId") String assessmentId,
			@PathVariable("partName") String partName, HttpServletRequest request, HttpServletResponse response) {
		
		
		return vocabularyService.getQuestionsPath(assessmentId, partName, request, response);
	}
	
	/**
	 * Get List Question from a Part in Assessment with Part ID
	 * 
	 * Thuc hien cho chuc nang tim kiem cac cau hoi trong mot Part theo Part ID
	 * Ket qua tra ve mang tinh chinh xac cao hon so voi get Questions theo PartName
	 * 
	 * @param assessmentId
	 * @param partId
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "getQuestionsPathId/{assessmentId}/{partId}", method = RequestMethod.POST)
	@ResponseBody
	public List<QuestionDataVocabulary> getQuestionsPathId(@PathVariable("assessmentId") String assessmentId,
			@PathVariable("partId") String partId, HttpServletRequest request, HttpServletResponse response) {
		
		return vocabularyService.getQuestionsPathId(assessmentId, partId, request, response);
	}
	
	/**
	 * Get List Question from a Assessment with List question ID
	 * He thong se tim kiem cac cau hoi trong Assessment, neu questionId ton tai thi tra ve cho nguoi dung
	 * 
	 * @param assessmentId : id cua mot Assessment tren he thong Sakai, 
	 * 		duoc truyen len voi @PathVariable("assessmentId")
	 * 		
	 * @param ids : List<Long> question ID. Duoc truyen trong Body, 
	 * 		with Headers: {"Content-Type":"application/json"},
	 * 		Example-Data: [123,123] 
	 * @param request : Example : link: ./getQuestionByIds/5.json, @Header: {"Content-Type":"application/json"}, @Body : [123,123]
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getQuestionByIds/{assessmentId}", method = RequestMethod.POST)
	@ResponseBody
	public List<QuestionDataVocabulary> getQuestionByIds(@PathVariable("assessmentId") String assessmentId, 
			@RequestBody Long[] ids, HttpServletRequest request, HttpServletResponse response) {
		
		return vocabularyService.getQuestionByIds(assessmentId, ids, request, response);
	}
	
}
