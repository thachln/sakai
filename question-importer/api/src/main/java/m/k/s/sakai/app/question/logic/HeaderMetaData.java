/**
 * Licensed to FA Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * FA licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package m.k.s.sakai.app.question.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tho
 * This class contain the indexes of header in template Excel.
 * <br/>
 * The standard columns:
 * <br/>
 * Question Level   Score    "IsNotRandom (x)" Question type   Answer  A   B   C   D   E   FA  FB  FC  FD  FF  CorrectAnswerFB IncorrectAnswerFB   Objective
 * <br/>
 * Compatible old header:
 * Question Level   Score    "IsNotRandom (x)" Question type   Answer  "Option A"   "Option B"  "Option C" "Option D" "Option E"
 */
public class HeaderMetaData {
    /** For logging. */
    private final static Logger LOG = LoggerFactory.getLogger(HeaderMetaData.class);

    private String[] headerNames;

    /** Default column of the Question. */
    private int indexQuestion = 0;

    /** Default column of the Level. */
    private int indexLevel = 1;

    /** Default column of the Score . */
    private int indexScore = 2;

    /** Default column of Not Random checking . */
    private int indexIsNotRandom = 3;

    /** Default column of Question Type . */
    private int indexQuestionType = 4;

    /** Default column of Corrective Answer. */
    private int indexCorrectAnswer = 5;

    /** Default start column of options. */
    private int indexAnswerStart = 6;
    private int indexAnswerEnd = -1;
    private int indexFeedbackStart = -1;
    private int indexFeedbackEnd = -1;
    private int indexCorrectAnswerFeedback = -1;
    private int indexInCorrectAnswerFeedback = -1;
    private int indexObjective = -1;
    
    
    public HeaderMetaData(String[] headerNames) {
        this.headerNames = headerNames;
        
        analysisHeader();
    }

    private void analysisHeader() {
        int len = (headerNames != null) ? headerNames.length : 0;
        
        String headerName;
        String nextHeaderName;
        for (int i = 0; i < len; i++) {
            headerName = headerNames[i];
            if ("Question".equalsIgnoreCase(headerName)) {
                indexQuestion = i;
            } else if ("Level".equalsIgnoreCase(headerName)) {
                indexLevel = i;
            } else if (("Mark".equalsIgnoreCase(headerName)) || ("Score".equalsIgnoreCase(headerName))) {
                indexScore = i;
            } else if ("IsNotRandom (x)".equalsIgnoreCase(headerName)) {
                indexIsNotRandom = i;
            } else if ("Question type".equalsIgnoreCase(headerName)) {
                indexQuestionType = i;
            } else if ("Answer".equalsIgnoreCase(headerName)) {
                indexCorrectAnswer = i;
                
                // Pre-processing Answers column
                if (i + 1 < len) {
                    nextHeaderName = headerNames[i + 1];
                    if (!"A".equalsIgnoreCase(nextHeaderName)) {
                        LOG.info("The next column of Answer is not A. So the old template is checking by ignoring the header names.");
                        // Support the old template by ignore the name of columns Answers
                        indexAnswerStart = i;
                        // Scan next header of Answers
                        i++;
                        while ((i < len) && (headerNames[i].length() > 0)) { // Has still header
                            indexAnswerEnd = i;
                            i++;
                        }
                        LOG.info(String.format("Answers columns from %d to %d", indexAnswerStart, indexAnswerEnd));
                    }
                }
            } else if ("A".equalsIgnoreCase(headerName)) {
                indexAnswerStart = i;
                
                // Scan next header of Answers
                i++;
                while ((i < len) && (headerNames[i].length() == 1)) { // Length of A, B...: 1
                    indexAnswerEnd = i;
                    i++;
                }
            }
            
            if (i >= len) {
                break;
            }
            
            // Check the Feedback
            if ("FA".equalsIgnoreCase(headerNames[i])) {
                indexFeedbackStart = i;
                
                // Scan next header of Feedback
                i++;
                while ((i < len) && (headerNames[i].length() == 2)) { // Length of FA, FB,... 2
                    indexFeedbackEnd = i;
                    i++;
                }
            }
            
            if (i >= len) {
                break;
            }
            headerName = headerNames[i];
            // Check CorrectFeedback, InCorrectFeedback, Objective
            if ("CorrectAnswerFB".equalsIgnoreCase(headerName)) {
                indexCorrectAnswerFeedback = i;
            } else if ("IncorrectAnswerFB".equalsIgnoreCase(headerName)) {
                indexInCorrectAnswerFeedback = i;
            } else if ("Objective".equalsIgnoreCase(headerName)) {
                indexObjective = i;
            }
            
         }
    }

    /**
    * Get value of headerNames.
    * @return the headerNames
    */
    public String[] getHeaderNames() {
        return headerNames;
    }

    /**
    * Get value of indexQuestion.
    * @return the indexQuestion
    */
    public int getIndexQuestion() {
        return indexQuestion;
    }

    /**
    * Get value of indexLevel.
    * @return the indexLevel
    */
    public int getIndexLevel() {
        return indexLevel;
    }

    /**
    * Get value of indexScore.
    * @return the indexScore
    */
    public int getIndexScore() {
        return indexScore;
    }

    /**
    * Get value of indexIsNotRandom.
    * @return the indexIsNotRandom
    */
    public int getIndexIsNotRandom() {
        return indexIsNotRandom;
    }

    /**
    * Get value of indexQuestionType.
    * @return the indexQuestionType
    */
    public int getIndexQuestionType() {
        return indexQuestionType;
    }

    /**
    * Get value of indexCorrectAnswer.
    * @return the indexCorrectAnswer
    */
    public int getIndexCorrectAnswer() {
        return indexCorrectAnswer;
    }

    /**
    * Get value of indexAnswerStart.
    * @return the indexAnswerStart
    */
    public int getIndexAnswerStart() {
        return indexAnswerStart;
    }

    /**
    * Get value of indexAnswerEnd.
    * @return the indexAnswerEnd
    */
    public int getIndexAnswerEnd() {
        return indexAnswerEnd;
    }

    /**
    * Get value of indexFeedbackStart.
    * @return the indexFeedbackStart
    */
    public int getIndexFeedbackStart() {
        return indexFeedbackStart;
    }

    /**
    * Get value of indexFeedbackEnd.
    * @return the indexFeedbackEnd
    */
    public int getIndexFeedbackEnd() {
        return indexFeedbackEnd;
    }

    /**
    * Get value of indexCorrectAnswerFeedback.
    * @return the indexCorrectAnswerFeedback
    */
    public int getIndexCorrectAnswerFeedback() {
        return indexCorrectAnswerFeedback;
    }

    /**
    * Get value of indexInCorrectAnswerFeedback.
    * @return the indexInCorrectAnswerFeedback
    */
    public int getIndexInCorrectAnswerFeedback() {
        return indexInCorrectAnswerFeedback;
    }

    /**
    * Get value of indexObjective.
    * @return the indexObjective
    */
    public int getIndexObjective() {
        return indexObjective;
    }
}
