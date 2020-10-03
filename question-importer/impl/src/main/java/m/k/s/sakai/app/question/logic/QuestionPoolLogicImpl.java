package m.k.s.sakai.app.question.logic;

import static mksgroup.java.common.CommonUtil.isNNandNB;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sakaiproject.tool.assessment.data.dao.assessment.Answer;
import org.sakaiproject.tool.assessment.data.dao.assessment.AnswerFeedback;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemMetaData;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemText;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemMetaDataIfc;
import org.sakaiproject.tool.assessment.facade.ItemFacade;
import org.sakaiproject.tool.assessment.facade.TypeFacade;
import org.sakaiproject.tool.assessment.services.ItemService;
import org.sakaiproject.tool.assessment.services.QuestionPoolService;
import org.sakaiproject.tool.cover.SessionManager;

import m.k.s.sakai.app.question.util.AppUtil;
import mksgroup.java.common.Constant;
import mksgroup.java.poi.PoiUtil;

/**
 * Implementation of {@link QuestionPoolLogic}
 * 
 * @author Thach N. Le
 *
 */
public class QuestionPoolLogicImpl implements QuestionPoolLogic {

    private static final Logger log = Logger.getLogger(QuestionPoolLogicImpl.class);
    
	private static final String HTML_BR = "<br/>";
	
    /** Default score of question. */
//    private static final Double DEFAULT_SCORE = 1.0;

//    private static final boolean NO_BOLD = false;
//
//    private static final String NO_COLOR = null;

	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
	}

     /**
     * Save/Import questions into the question pool.
     * @param listQuestion questions to be imported.
     * @param poolId The question pool identifier will contain the question list.
     * @param questionColor
     * @param isQuestionBold
     * @return List of result messages for questions
     */
    public List<Boolean> save(List<ItemFacade> listQuestion, Long poolId, String questionColor, boolean isQuestionBold, Integer minDuration, Integer maxDuration) {
        List<Boolean> listResult = new ArrayList<Boolean>();

        ItemService itemService = new ItemService();
        QuestionPoolService qpdelegate = new QuestionPoolService();
        
        if ((null != listQuestion) && (!listQuestion.isEmpty())) {
            for (ItemFacade question : listQuestion) {
                // Format question
                question = format(question, questionColor, isQuestionBold);
                
                if (minDuration != null) {
                    question.setMinDuration(minDuration);
                }
                
                if (maxDuration != null) {
                    question.setMaxDuration(maxDuration);
                }

                question = itemService.saveItem(question);
                
                if (question != null) {
                    // add question to question pool
                    log.debug("ITEM ID: " + question.getData().getItemId());
                    qpdelegate.addItemToPool(question.getData().getItemId(), poolId);
                    
                    listResult.add(true);
                } else {
                    log.debug("Could not save the question");
                    listResult.add(false);
                }
            }
        } else {
            log.debug("Question list is null or empty");
        }
        
        return listResult;
    }


    /**
     * Parse uploaded excel file to save question into Question Pool.
     * @param fileName Name of file which is uploaded
     * @param inputStream Stream of Excel file format 2007
     * @param poolId
     * @param questionColor
     * @param isQuestionBold
     * @param invalidRowIdx
     * @param invalidColIdx if the question is invalid, this output value is index of the invalid column
     * @param errorMessage if the question is invalid, this output parameter contains the error message
     * @see m.k.s.sakai.app.question.logic.QuestionPoolLogic#save(java.io.InputStream, java.lang.Long)
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public List<Boolean> save(String fileName, InputStream inputStream, Long poolId, String questionColor, boolean isQuestionBold,
            Integer minDuration, Integer maxDuration,
            int invalidRowIdx, int invalidColIdx, String errorMessage) throws FileNotFoundException, IOException {
        List<Boolean> listResult;
        Workbook workbook;
        
        log.debug("fileName=" + fileName);
        
        if (fileName.endsWith(Constant.DOT_XLS)) {
            // Excel 2003
            workbook = new HSSFWorkbook(new POIFSFileSystem(inputStream));
        } else if (fileName.endsWith(Constant.DOT_XLSX)) {
            // Excel 2007
            workbook = new XSSFWorkbook(inputStream);
        } else {
            errorMessage = "Unsupported files";
            return null;
        }

        List<ItemFacade> listQuestion = parseQuestion(workbook, questionColor, isQuestionBold, invalidRowIdx, invalidColIdx, errorMessage);
        
        listResult = save(listQuestion, poolId, questionColor, isQuestionBold, minDuration, maxDuration);
        
        return listResult;
    }
    
    /**
     * Parse workbook into list of questions.
     * Header of the Excel file:
     * No    Question    Level   Mark    "IsNotRandom (x)"   Question type   Answer  A B C D E...FA FB FC FD FE...CorrectAnswerFB   IncorrectAnswerFB   Objective
     * 
     * <p>
     * Number of columns of Answers (A, B, C, ...) and columns of Feedbacks (FA, FB, FC,..) are dynamic.
     * </p>
     * @param workbook
     * @param questionColor
     * @param isQuestionBold
     * @param invalidRowIdx output < 0 if no error
     * @param errorMessage output
     * @return
     */
    protected List<ItemFacade> parseQuestion(Workbook workbook, String questionColor, boolean isQuestionBold, int invalidRowIdx, int invalidColIdx, String errorMessage) {
        log.info("Parsing the workbook...");
        List<ItemFacade> listItemFacades = new ArrayList<ItemFacade>();
        
        // Get the first sheet
        Sheet sheet = workbook.getSheetAt(0);
        Row row;
        
        // Screen the Excel skip the header
        Iterator<Row> itRow = sheet.rowIterator();
        
        log.debug("itRow hasNext():" + itRow.hasNext());
        log.debug("sheet.getLastRowNum():" + sheet.getLastRowNum());
        String[] questionItems;
        ItemFacade itemFacade;
        
        Row header = itRow.next();
        String[] headerNames = parseHeader(header);
        HeaderMetaData hmt = new HeaderMetaData(headerNames);
        boolean isValid;
        
        QuestionData questionData;
        // Skip header
        for (int i = 1; itRow.hasNext(); i++) {
            row = itRow.next();
            questionItems = parseRow(row).toArray(new String[]{});

            isValid = checkValid(questionItems, invalidRowIdx, errorMessage);
            
            log.debug("Row data:" + questionItems);
            
            if (isValid) {
                questionData = AppUtil.parseQuestion(questionItems, hmt);
                itemFacade = parseQuestion(questionData, questionColor, isQuestionBold, invalidRowIdx, errorMessage);

                if (itemFacade != null) {
                    listItemFacades.add(itemFacade);
                } else {
                    // reach at the end of valid data
                    break;
                }
            } else {
                // Report invalid row
                invalidRowIdx = i;
                break;
            }
        }

        return listItemFacades;
    }
    

    /**
     * Get columns name of header.
     * @param header
     * @return
     */
    private String[] parseHeader(Row header) {
        List<String> headerNames = new ArrayList<String>();
        
        short firstCol = header.getFirstCellNum();
        short lastCol = header.getLastCellNum();
        
        Cell headerCell;
        String cellValue;
        for (int i = firstCol; i <= lastCol; i++) {
            headerCell = header.getCell(i);
            
            if (headerCell != null) {
                cellValue = headerCell.getStringCellValue();
                
                if ((cellValue != null) && (!cellValue.isEmpty())) {
                    headerNames.add(cellValue);
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        
        return headerNames.toArray(new String[] {});
    }

    /**
     * Check question is valid or not.
     * For multiple choice question, the values of columns must be not blank:
     * Question, Answer, A, B
     * @param questionItems values of items: No    Question    Level   Mark    "IsNotRandom (x)"   Question type   Answer  A B C D E
     * @param invalidRowIdx if the question is invalid, this output value is index of the invalid column
     * @param errorMessage if the question is invalid, this output parameter contains the error message
     * @return true if the question is valid.
     */
    public boolean checkValid(String[] questionItems, int invalidRowIdx, String errorMessage) {
        boolean isValid = (questionItems != null) &&
                          (questionItems.length > 8) &&
                          isNNandNB(questionItems[QUESTION_IDX])
//                          && 
                          // isNNandNB(questionItems[ANSWER_IDX]) &&  // Default Answer is A
//                          isNNandNB(questionItems[A_IDX]) && 
//                          isNNandNB(questionItems[B_IDX]);
                          ;
                          
        return isValid;
    }

    /**
     * Parse a row of question in the Excel into List of Strings.
     * @param questionRow Row object in POI library.
     * @return List of values of cells in the rows without the first column "No"
     * Question    Level   Mark    "IsNotRandom (x)"   Question type   Answer  A B C D E
     */
    private List<String> parseRow(Row questionRow) {
        log.debug("Parsing row...");
        List<String> colItems = new ArrayList<String>();
        
        Object cellValue;
        String strCellValue;
        
        int lastCellNum = questionRow.getLastCellNum();
        
        log.debug("lastCellNum:" + lastCellNum);
        // Scan columns from "Question"
        for (int i = QUESTION_IDX; i < lastCellNum; i++) {
            cellValue = PoiUtil.getValue(questionRow, i);
            strCellValue = (cellValue != null) ? cellValue.toString() : null;
            log.debug("cellValue=" + cellValue);
            
            colItems.add(strCellValue);
            
            // Stop if reach at the end of answer options
            // Before column "Answer" can be null.
            // From column Answer, null means at the end of data of the question.
//            if ((i > ANSWER_IDX) && (!CommonUtil.isNNandNB(strCellValue))) {
//                break;
//            }
        }
        
        return colItems;
    }
    

    /**
     * @see m.k.s.sakai.app.question.logic.QuestionPoolLogic#parseQuestion(java.lang.String[], int, java.lang.String)
     */
    public ItemFacade parseQuestion(QuestionData questionData, String questionColor, boolean isQuestionBold, int invalidRowIdx, String errorMessage) {
        // Check valid parameter
        if (questionData == null) {
            return null;
        }

        ItemFacade itemFacade = null;
//        AssessmentAnswer importableAnswer;
//        AssessmentAnswer importableChoice;
        String questionTextString = null;
        ItemText text = null;
        HashSet textSet = null;
        Answer answer = null;
        HashSet answerSet = null;
        AnswerFeedback answerFeedback = null;
        HashSet answerFeedbackSet = null;
//        int questionCount = 0;
        
        itemFacade = new ItemFacade();

        textSet = new HashSet();

        
        questionTextString = format(questionData.getQuestion(), questionColor, isQuestionBold);

        String strQuestionType = questionData.getQuestionType();
        if (MATCHING.equalsIgnoreCase(strQuestionType)) {
            itemFacade.setInstruction(questionTextString);
            itemFacade.setTypeId(TypeFacade.MATCHING);
//            Collection choices = getChoices(questionItems);
            int answerIndex = 1;
            
            // Scan answers
            // for (int i = ANSWER_IDX + 1; i < questionItems.length; i++) {
            for (String answerOption : questionData.getAnswers()) {
                text = new ItemText();
                text.setSequence(Long.valueOf(answerIndex));
                answerIndex++;
                text.setText(answerOption);
                answerSet = new HashSet();
//                int choiceIndex = 1;
//                for (Iterator k = choices.iterator();k.hasNext();) {
//                    importableChoice = (AssessmentAnswer)k.next();
//                    answer = new Answer();
//                    answer.setItem(itemFacade.getData());
//                    answer.setItemText(text);
//                    answer.setSequence(new Long(choiceIndex));
//                    choiceIndex++;
//                    // set label A, B, C, D, etc. on answer based on its sequence number
//                    answer.setLabel(new Character((char)(64 + choiceIndex)).toString());
//                    answer.setText("Test");
//                    answer.setIsCorrect(Boolean.valueOf(importableAnswer.getChoiceId().equals(importableChoice.getAnswerId())));
//                    answerSet.add(answer);
//                }
                text.setAnswerSet(answerSet);
                text.setItem(itemFacade.getData());
                textSet.add(text);
            }
        } else {
            text = new ItemText();
            text.setSequence(new Long(1));
            text.setText(questionTextString);
            
            answerSet = new HashSet();
//            answerFeedbackSet = new HashSet();
//            Collection answers = null;
            StringBuilder answerBuffer = new StringBuilder();
            char label = 'A';
            
            String correctAnwser = questionData.getCorrectAnswer();
            if (correctAnwser == null) {
                correctAnwser = "A";
            }

            String upperCorrectedAnswers = correctAnwser.toUpperCase();

            Long questionType = determineQuestionType(strQuestionType, upperCorrectedAnswers);
            
            log.debug("Question:" + correctAnwser + "; type: " + questionType);
            itemFacade.setTypeId(questionType);
            
            int position;
            // Scan answers, Skip empty items. && (questionItems[i] != null) && !questionItems[i].trim().isEmpty()
            // Scan until reach at header column with a character of alphabet A, B ... or Z
//            for (int i = ANSWER_IDX + 1; (i < ANSWER_IDX + 5); i++) {

            int i = 0;
            for (String answerOption : questionData.getAnswers()) {
                answerBuffer.append(answerOption);
//                if (j.hasNext()) answerBuffer.append("|");
//                String answerId = importableAnswer.getAnswerId();
                answer = new Answer();
                answer.setItem(itemFacade.getData());
                answer.setItemText(text);
                
                position = i + 1;
                answer.setSequence(new Long(position));
                // set label A, B, C, D, etc. on answer based on its sequence number
                answer.setLabel(new Character((char)(64 + position)).toString());
                
                if (questionType == TypeFacade.TRUE_FALSE) {
                    // Samigo only understands True/False answers in lower case
                    answer.setText(upperCorrectedAnswers.toLowerCase());
                } else if (questionType == TypeFacade.FILL_IN_NUMERIC) {
                    answer.setText(upperCorrectedAnswers);
                    Pattern pattern = Pattern.compile("_+|<<.*>>");
                    Matcher matcher = pattern.matcher(questionTextString);
                    if (matcher.find()) questionTextString = questionTextString.replaceFirst(matcher.group(),"{}");
                    text.setText(questionTextString);
                    itemFacade.setTypeId(Long.valueOf(TypeFacade.FILL_IN_BLANK));
                } else if (questionType == TypeFacade.FILL_IN_BLANK) {
//                    if (j.hasNext()) continue;
                    answer.setText(answerBuffer.toString());
                    Pattern pattern = Pattern.compile("_+|<<.*>>");
                    Matcher matcher = pattern.matcher(questionTextString);
                    if (matcher.find()) questionTextString = questionTextString.replaceFirst(matcher.group(),"{}");
                    text.setText(questionTextString);
                    answer.setSequence(new Long(1));
                } else {
//                    answer.setText(format(answerOption, NO_COLOR, NO_BOLD));
                    answer.setText(answerOption);
                }
                
                answer.setIsCorrect(upperCorrectedAnswers.contains(String.valueOf(label)));
                
                // DuyDPD+Tho: Set answerFeedbackSet - START
                answerFeedback = new AnswerFeedback();
                answerFeedback.setAnswer(answer);
                // answerFeedback.setText(questionItems[i + 4]);
                
                // Get Feedback of the answer option
                String feedback = questionData.getFeedback(i);
                if ((feedback != null) && (!feedback.isEmpty())) {
                    answerFeedback.setText(feedback);
                }
                
                answerFeedbackSet = new HashSet();
                answerFeedbackSet.add(answerFeedback);
                answer.setAnswerFeedbackSet(answerFeedbackSet);
                // DuyDPD+Tho: Set answerFeebackSet - END
                
                answerSet.add(answer);
                
                label++;
                
                // Next column
                i++;
            }
            text.setAnswerSet(answerSet);
            text.setItem(itemFacade.getData());
            textSet.add(text);
            
        }
        itemFacade.setItemTextSet(textSet);
        
        // Set question feedbacks
        String correctAnswerFB = questionData.getCorrectFeedback();
        if (correctAnswerFB != null) {
            itemFacade.setCorrectItemFeedback(correctAnswerFB);
        }
        
        
        String incorrectAnswerFB = questionData.getIncorrectFeedback();
        
        if (incorrectAnswerFB != null) {
            itemFacade.setInCorrectItemFeedback(incorrectAnswerFB);
        }

        // Get Score
//        Double score = isNNandNB(questionItems[MARK_IDX]) ? Double.valueOf(questionItems[MARK_IDX]) : DEFAULT_SCORE; 
        itemFacade.setScore(questionData.getScore());
        
        boolean isRandomize = questionData.isRandomize();
         // Set Randomize
         Set<ItemMetaDataIfc> setMetaData = new HashSet<ItemMetaDataIfc>();
         
      // Set Randomize
         
//         ItemMetaData itemMetaData = new ItemMetaData();
//         itemMetaData.setItem(itemFacade.getData());
//         itemMetaData.setLabel("RANDOMIZE");
//         itemMetaData.setEntry(String.valueOf(isRandomize));
         
//         setMetaData.add(itemMetaData);
         ItemMetaData imdRandomize = new ItemMetaData(itemFacade.getData(), ItemMetaDataIfc.RANDOMIZE, String.valueOf(isRandomize));
         setMetaData.add(imdRandomize);

         // Set Objective
        String strObjective = questionData.getObjective();
        if (strObjective != null) {
            ItemMetaData imdObjective = new ItemMetaData(itemFacade.getData(), ItemMetaDataIfc.OBJECTIVE, strObjective);
        
            setMetaData.add(imdObjective);
        }
        // Set KEYWORD
        String strKeyword = questionData.getKeyword();
        if (strKeyword != null) {
            ItemMetaData imdObjective = new ItemMetaData(itemFacade.getData(), ItemMetaDataIfc.KEYWORD, strKeyword);
        
            setMetaData.add(imdObjective);
        }
        // Set RUBRIC
        String strRubric = questionData.getRubric();
        if (strRubric != null) {
            ItemMetaData imdObjective = new ItemMetaData(itemFacade.getData(), ItemMetaDataIfc.RUBRIC, strRubric);
        
            setMetaData.add(imdObjective);
        }

         if (!setMetaData.isEmpty()) {
             itemFacade.getData().setItemMetaDataSet(setMetaData);
         }
        
        //itemFacade.setSequence(importableQuestion.getPosition());
        // status is 0=inactive or 1=active
        itemFacade.setStatus(ItemDataIfc.ACTIVE_STATUS);
        itemFacade.setHasRationale(Boolean.FALSE);
        itemFacade.setCreatedBy(SessionManager.getCurrentSessionUserId());
        itemFacade.setCreatedDate(new java.util.Date());
        itemFacade.setLastModifiedBy(SessionManager.getCurrentSessionUserId());
        itemFacade.setLastModifiedDate(new java.util.Date());
        // itemService.saveItem(itemFacade);
        
        
        return itemFacade;
    }

    /**
     * Format the Instruction of the question.
     * @param question
     * @param questionColor
     * @param isQuestionBold
     * @return
     */
    private ItemFacade format(ItemFacade question, String questionColor, boolean isQuestionBold) {
        String formattedText = format(question.getText(), questionColor, isQuestionBold);
        
        ItemText itemText;
        for (Object obj : question.getItemTextSet()) {
            itemText = (ItemText) obj;
            
            itemText.setText(formattedText);
        }

        return question;
    }
    
    /**
     * Processing:<br/>
     * 1) Multiple lines: replace enter character by html tag <br/>
     * 2) Replace special character: 
     *    < -> &lt;
     *    > -> &gt;
     * 
     * @param text content will be formatted.
     * @param questionColor if this is not empty, the html style of color will be added to format color for the text.
     * @param isQuestionBold if this is true, the html format bold will be set for the text.
     * @return
     */
    private String format(String text, String questionColor, boolean isQuestionBold) {
        String newText;
        // boolean useHTML = false;
        
        // Test
        newText = escapeHtml(text);
        
        if (isQuestionBold) {
            StringBuffer sb = new StringBuffer();
            sb.append("<strong>").append(newText).append("<strong>");
            newText = sb.toString();
        }
        
        if (isNNandNB(questionColor)) {
//            String pattern = "<p><span style='color:${color};'>${text}</span></p>";
//            Map<String, Object> mapValue = new HashMap<String, Object>();
//            mapValue.put("color", questionColor);
//            mapValue.put("text", newText);
//            
//            newText = CommonUtil.formatPattern(pattern, mapValue);
            
            newText= "<p><span style='color:" + questionColor + ";'>" + newText + "</span></p>";
        }
        
        if (isQuestionBold || (isNNandNB(questionColor))) {
            newText = newText.replaceAll("\n\r", HTML_BR);
            newText = newText.replaceAll("\r\n", HTML_BR);
            newText = newText.replaceAll("\n", HTML_BR);
        }

        return newText;
    }

    private String escapeHtml(String text) {
//        text = text.replace("<", "&lt;");
//        text = text.replace(">", "&gt;");
        
        return text;
    }
    /**
     * Determine value of Question Type basing data from Excel File or Grid Table.
     * @param questionType available values:<br/>
     * MULTIPLE_CHOICE
     * MULTIPLE_CORRECT
     * MULTIPLE_CORRECT_SINGLE_SELECTION
     * MULTIPLE_CHOICE_SURVEY
     * TRUE_FALSE
     * ESSAY_QUESTION
     * FILE_UPLOAD
     * AUDIO_RECORDING
     * FILL_IN_BLANK
     * FILL_IN_NUMERIC
     * IMAGEMAP_QUESTION
     * @param answer
     * @return
     */
    private Long determineQuestionType(String questionType, String answer) {
        Long finalQuestionType;
        
        if (isNNandNB(questionType)) {
            // Question is filled from Excel or Grid Table
            
            if (MULTIPLE_CHOICE.equalsIgnoreCase(questionType)) {
                finalQuestionType = TypeFacade.MULTIPLE_CHOICE;
            } else if (MULTIPLE_CORRECT.equalsIgnoreCase(questionType)) {
                finalQuestionType = TypeFacade.MULTIPLE_CORRECT;
            } else if (MULTIPLE_CORRECT_SINGLE_SELECTION.equalsIgnoreCase(questionType)) {
                finalQuestionType = TypeFacade.MULTIPLE_CORRECT_SINGLE_SELECTION;
            } else if (MULTIPLE_CHOICE_SURVEY.equalsIgnoreCase(questionType)) {
                finalQuestionType = TypeFacade.MULTIPLE_CHOICE_SURVEY;
            } else if (TRUE_FALSE.equalsIgnoreCase(questionType)) {
                finalQuestionType = TypeFacade.TRUE_FALSE;
            } else if (ESSAY_QUESTION.equalsIgnoreCase(questionType)) {
                finalQuestionType = TypeFacade.ESSAY_QUESTION;
            } else if (FILE_UPLOAD.equalsIgnoreCase(questionType)) {
                finalQuestionType = TypeFacade.FILE_UPLOAD;
            } else if (AUDIO_RECORDING.equalsIgnoreCase(questionType)) {
                finalQuestionType = TypeFacade.AUDIO_RECORDING;
            } else if (FILL_IN_BLANK.equalsIgnoreCase(questionType) || FILL_IN_NUMERIC.equalsIgnoreCase(questionType)) {
                finalQuestionType = TypeFacade.FILL_IN_BLANK;
            } else if (IMAGEMAP_QUESTION.equalsIgnoreCase(questionType)) {
                finalQuestionType = TypeFacade.IMAGEMAP_QUESTION;
            } else {
                log.error("Don't support question type: " + questionType);
                finalQuestionType = TypeFacade.MULTIPLE_CHOICE;
                // [TODO] Consider to set null later for display error.
                // return null;
            }
            
        } else {
            // Determine question type basing on value of Answer
            if ((answer == null) || answer.isEmpty()) {
                // Default is MULTIPLE_CHOICE
                finalQuestionType = TypeFacade.MULTIPLE_CHOICE;
            } else {
                // Support values of Answer:
                // A =>  MULTIPLE_CHOICE
                // A,B => MULTIPLE_CORRECT
                // A B => MULTIPLE_CORRECT
                finalQuestionType = (answer.length() == 1) ? TypeFacade.MULTIPLE_CHOICE : TypeFacade.MULTIPLE_CORRECT;
                // [TODO] Will support following logic later:
//                Multiple Correct, Multiple Selection:
//                    A multiple correct, multiple selection answer requires several selections and allows different policies for granting the points
//
//                    The option Right Less Wrong means that the points possible will be reduced by each box checked wrongly, either affirmed for a selection that should not be included, or left empty for a selection that should be included in the correct answers.
//                    The option All or Nothing means that all points are granted for a fully correct answer only; any other combination of affirmed and empty check boxes earns no points.
            }
        }

        return finalQuestionType;
    }

    @Override
    public String checkLicense() {
        BufferedReader in = null;
        HttpURLConnection conn = null;
        try {
            URL obj = new URL("http://myworkspace.vn/myworld/vthachln?q=SVTECH-L-QI");
            conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();

            log.debug("Response Code : " + responseCode);

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            
            return response.toString();
        } catch (Exception ex) {
            log.warn("Could connect to server license of MKSOL: " + ex.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    // Skip this exception
                    log.warn("Could not clode the BufferedReader: " + ex.getMessage());
                }
            }
        }
        
        return null;
    }
}
