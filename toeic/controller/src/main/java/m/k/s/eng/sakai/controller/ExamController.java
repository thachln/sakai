/**
 * The licensed to the CAMPUSLINK program.
 */
package m.k.s.eng.sakai.controller;

import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedAssessmentData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemAttachment;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemText;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedSectionData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemAttachmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemTextIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * This class processes events of examination.
 * @author MINH MAN
 *
 */
@RestController
public class ExamController {

	/**
	 * Process event next question.
	 * @param id identifier of the current question.
	 * @return json of the next question.
	 * Ex: "{next: 123}"
	 */
	@RequestMapping(value="nextQuestion", method=RequestMethod.GET,headers = "Accept=application/json")
	public String loadQuestion(int id) {
		JsonObject question = new JsonObject();

		question.addProperty("imgUrl", "https://elearn.edu.vn/Asset/Image/Course/P1_78951.jpg");
		question.addProperty("audioUrl", "https://elearn.edu.vn/Asset/Media/Course/P1_78951.mp3");
		question.addProperty("duration", 10); // second
		
		Gson gson = new Gson();
		return gson.toJson(question);
	}

	/**
	 * Load all content of the exam.
	 * @param id identifier of the exam
	 * @return json of exam.
	 * Ex: "{title : 'ABC'}"
	 */
	@RequestMapping(value="loadExam", method=RequestMethod.GET,headers = "Accept=application/json")
	public String loadExam(int id){
		
		JsonObject question = new JsonObject();

		PublishedAssessmentData publishedAssessment = new PublishedAssessmentData();
		publishedAssessment.setTitle("Đề thi TOEIC 500");
		
		// Các phần của đề thi
		Set sectionSet = new HashSet<SectionDataIfc>();
		SectionDataIfc part1 = new PublishedSectionData();
		part1.setTitle("Part 1 Section");
		
		// Câu hỏi
		ItemDataIfc questionItem = new PublishedItemData();
		Set<ItemTextIfc> itemTextSet = new HashSet<ItemTextIfc>();
		ItemTextIfc item = new PublishedItemText();
        itemTextSet.add(item);
		
        // Set nội dung câu hỏi?
        questionItem.setItemTextSet(itemTextSet);
        
        Set<ItemAttachmentIfc> itemAttachmentSet = new HashSet<ItemAttachmentIfc>();
        ItemAttachmentIfc option1 = new PublishedItemAttachment();
        ItemDataIfc optionItem = new PublishedItemData();
        option1.setItem(optionItem);
        
        itemAttachmentSet.add(option1 );
        // Set các option của câu hỏi
        questionItem.setItemAttachmentSet(itemAttachmentSet);
        
        part1.addItem(questionItem);
		

        publishedAssessment.setSectionSet(sectionSet);
		
		
		Gson gson = new Gson();

		return gson.toJson(publishedAssessment);
	}
}
