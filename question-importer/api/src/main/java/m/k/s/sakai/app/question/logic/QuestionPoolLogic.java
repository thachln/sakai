package m.k.s.sakai.app.question.logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.sakaiproject.tool.assessment.facade.ItemFacade;

/**
 * This logic provides interfaces to import questions from Excel or Grid Table into Question Pool. 
 *
 * @author Thach N. Le
 *
 */
public interface QuestionPoolLogic {
    // Index of column in the table
    public static final int QUESTION_IDX = 0;
//    public static final int LEVEL_IDX = 1;
//    public static final int MARK_IDX = 2;
//    public static final int ISNOTRANDOM_IDX = 3;
//    public static final int QUESTION_TYPE_IDX = 4;
//    public static final int ANSWER_IDX = 5;
//    public static final int A_IDX = 6;
//    public static final int B_IDX = 7;
//    public static final int C_IDX = 8;
//    public static final int D_IDX = 9;
//    public static final int E_IDX = 10;
//    public static final int F_IDX = 11;
//    public static final int G_IDX = 12;
//    public static final int H_IDX = 13;
//    public static final int I_IDX = 14;
//    public static final int J_IDX = 15;
//    public static final int FEEDBACKCORRECTITEM_IDX = 16;
//    public static final int FEEDBACKINCORRECTITEM_IDX = 17;
//    public static final int FEEDBACKA_IDX = 10;
//    public static final int FEEDBACKB_IDX = 11;
//    public static final int FEEDBACKC_IDX = 12;
//    public static final int FEEDBACKD_IDX = 13;
//    public static final int FEEDBACKCORRECTITEM_IDX = 14;
//    public static final int FEEDBACKINCORRECTITEM_IDX = 15;
//    public static final int OBJECTIVE = 16;
//    public static final int FEEDBACKE_IDX = 22;
//    public static final int FEEDBACKF_IDX = 23;
//    public static final int FEEDBACKG_IDX = 24;
//    public static final int FEEDBACKH_IDX = 25;
//    public static final int FEEDBACKI_IDX = 26;
//    public static final int FEEDBACKJ_IDX = 27;
    
    // Text of question types
    public static final String MULTIPLE_CHOICE = "MULTIPLE_CHOICE";
    public static final String MULTIPLE_CORRECT = "MULTIPLE_CORRECT";
    public static final String MULTIPLE_CORRECT_SINGLE_SELECTION = "MULTIPLE_CORRECT_SINGLE_SELECTION";
    public static final String MULTIPLE_CHOICE_SURVEY = "MULTIPLE_CHOICE_SURVEY";
    public static final String TRUE_FALSE = "TRUE_FALSE";
    public static final String ESSAY_QUESTION = "";
    public static final String FILE_UPLOAD = "ESSAY_QUESTION";
    public static final String AUDIO_RECORDING = "AUDIO_RECORDING";
    public static final String FILL_IN_BLANK = "FILL_IN_BLANK";
    public static final String FILL_IN_NUMERIC = "FILL_IN_NUMERIC";
    public static final String IMAGEMAP_QUESTION = "IMAGEMAP_QUESTION";
    public static final String MATCHING = "MATCHING";

	/**
	 * Save/Import questions into the question pool.
	 * @param listQuestion questions to be imported.
	 * @param poolId The question pool identifier will contain the question list.
     * @param questionColor
     * @param isQuestionBold
	 * @return List of saved status for questions
	 */
	public List<Boolean> save(List<ItemFacade> listQuestion, Long poolId, String questionColor, boolean isQuestionBold, Integer minDuration, Integer maxDuration);
	

    /**
     * Save/Input question from Excel file into the question.
     * Layout of the Excel is:
     * Question         Level       Mark        "IsNotRandom (x)"               Question type               Answer      A   B   C   D   E
     * @param fileName
     * @param inputStream InputStream of the Excel file (format 2007)
     * @param poolId
     * @param questionColor
     * @param isQuestionBold
     * @param invalidRowIdx if >=0, the data at that rowIdx is invalid.
     * @param invalidRowIdx if the question is invalid, this output value is index of the invalid column
     * @param errorMessage if the question is invalid, this output parameter contains the error message
     * @return List of saved status for questions
     * @throws FileNotFoundException, IOException
     */
    public List<Boolean> save(String fileName, InputStream inputStream, Long poolId, String questionColor, boolean isQuestionBold,
            Integer minDuration, Integer maxDuration,
            int invalidRowIdx, int invalidColIdx, String errorMessage) throws FileNotFoundException, IOException;
    
    /**
     * questionItems Columns of the questions: Question    Level   Mark    "IsNotRandom (x)"   Question type   Answer  A B C D E..FA FB..CorrectAnswerFB IncorrectAnswerFB   Objective
     * @param questionData
     * @param questionColor
     * @param isQuestionBold
     * @param invalidColIdx if the question is invalid, this output value is index of the invalid column
     * @param errorMessage if the question is invalid, this output parameter contains the error message
     * @return null if invalid question
     */
    public ItemFacade parseQuestion(QuestionData questionData, String questionColor, boolean isQuestionBold, int invalidColIdx, String errorMessage);
    
    /**
     * Check question is valid or not.
     * For multiple choice question, the values of columns must be not blank:
     * Question, Answer, A, B
     * @param questionItems values of items: Question    Level   Mark    "IsNotRandom (x)"   Question type   Answer  A B C D E
     * @param invalidColIdx if the question is invalid, this output value is index of the invalid column
     * @param errorMessage if the question is invalid, this output parameter contains the error message
     * @return true if the question is valid.
     */
    public boolean checkValid(String[] questionItems, int invalidColIdx, String errorMessage);


    public String checkLicense();
}
