package m.k.s.eng.sakai.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedAnswer;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import m.k.s.eng.sakai.model.AnswerToeic;
import m.k.s.eng.sakai.model.AssessmentDataToeic;
import m.k.s.eng.sakai.model.FaceRecognitionModel;
import m.k.s.eng.sakai.model.PartDataToeic;
import m.k.s.eng.sakai.model.QuestionDataToeic;

public class AppUtil {

    /** For logging. */
    private final static Logger LOG = LoggerFactory.getLogger(AppUtil.class);
    private static final String TYPE_PRACTICE = "type: practice";
    private static final String TYPE_EXAM = "type: exam";
    private static final String START_RECOGNITION = "start_face_recognition: true";
    private static final String END_RECOGNITION = "end_face_recognition: true";
    private static final String PART_RECOGNITION_REGEX = "part_face_recognition:\\s\\[\\s*[0-9\\,\\s*].*\\]";
    private static final String LIST_REGEX = "\\[\\s*[0-9\\,\\s*].*\\]";

    /**
     * find PublishedAnswer by publishedAnswerId
     * @param list
     * @param id
     * @return
     */
    public static PublishedAnswer findPublishedAnswerById(List<PublishedAnswer> list, final Long id) {

        return list.stream().filter(a -> a.getId().equals(id)).findAny().orElse(null);
    }

    /**
     * find PublishedAnswer by itemId
     * @param list
     * @param itemId
     * @return
     */
    public static PublishedAnswer findPublishedAnswerByItemId(List<PublishedAnswer> list, final Long itemId) {

        return list.stream().filter(a -> a.getItem().getItemId().equals(itemId)).findAny().orElse(null);
    }

    /**
     * [Give the description for method].
     * @param asssessmentData
     * @param partTitle
     * @return
     */
    public static PartDataToeic findPartByTitle(AssessmentDataToeic asssessmentData, String partTitle) {
        for (PartDataToeic part : asssessmentData.getPartsContents()) {
            if (part.getTitle().trim().equals(partTitle.trim())) {
                return part;
            }
        }

        return null;
    }

    /**
     * [Give the description for method].
     * @param part
     * @param itemId
     * @return
     */
    public static QuestionDataToeic findQuestionByItemId(PartDataToeic part, Long itemId) {
        for (QuestionDataToeic question : part.getItemContents()) {
            if (question.getQuestionId().equals(itemId)) {
                return question;
            }
        }

        return null;
    }

    /**
     * [Give the description for method].
     * @param question
     * @param answerId
     * @return
     */
    public static String findAnswerLabelByAnswerId(QuestionDataToeic question, Long answerId) {
        for (AnswerToeic answer : question.getAnswers()) {
            if (answer.getAnswerId().equals(answerId)) {
                return answer.getLabel();
            }
        }

        return null;
    }

    /**
     * [Give the description for method].
     * @param asssessmentData
     * @param partTitle
     * @param itemId
     * @param answerId
     * @return
     */
    public static String findAnswerLabel(AssessmentDataToeic asssessmentData, String partTitle, Long itemId,
            Long answerId) {
        PartDataToeic p = findPartByTitle(asssessmentData, partTitle);
        QuestionDataToeic q = findQuestionByItemId(p, itemId);

        return findAnswerLabelByAnswerId(q, answerId);
    }

    public static boolean isPractice(String desc) {
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

        return isPractice;
    }

    public static String getDisplayName(String agentId) {
        AgentFacade agent = new AgentFacade(agentId);

        return agent.getDisplayName();
    }

    public static FaceRecognitionModel getFaceRecognition(String desc) {
        FaceRecognitionModel model = new FaceRecognitionModel();
        ObjectMapper objectMapper = new ObjectMapper();

        if (desc != null) {
            desc = desc.toLowerCase();

            if (desc.contains(START_RECOGNITION)) {
                model.setStartRecognition(true);
            }

            if (desc.contains(END_RECOGNITION)) {
                model.setEndRecognition(true);
            }

            Pattern p = Pattern.compile(PART_RECOGNITION_REGEX);
            Matcher m = p.matcher(desc);
            if (m.find()) {
                Pattern p2 = Pattern.compile(LIST_REGEX);
                Matcher m2 = p2.matcher(m.group().toString());
                if (m2.find()) {
                    try {
                        List<Integer> parts = Arrays.asList(objectMapper.readValue(m2.group(), Integer[].class));
                        model.setPartRecognition(parts);
                    } catch (JsonParseException ex) {
                        ex.printStackTrace();
                    } catch (JsonMappingException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        }

        return model;
    }
}
