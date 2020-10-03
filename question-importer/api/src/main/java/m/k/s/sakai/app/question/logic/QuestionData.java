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

import java.util.List;

/**
 * @author Tho
 */
public class QuestionData {
    private String question;
    private String questionType;
    private Double score = 1.0;

    /**
     * For multichoice: Single selection: correct answer is A or B or C or etc.
     */
    private String correctAnswer;
    private List<String> answers;

    private List<String> feedbacks;

    private String correctFeedback;
    private String incorrectFeedback;

    private boolean isRandomize = true;

    private String objective;
    private Boolean hasRationale = Boolean.FALSE;

    /** Reserve . */
    private String keyword;

    /** Reserve . */
    private String rubric;

    /**
     * Get value of question.
     * @return the question
     */
    public String getQuestion() {
        return question;
    }
    /**
     * Set the value for question.
     * @param question the question to set
     */
    public void setQuestion(String question) {
        this.question = question;
    }
    /**
     * Get value of questionType.
     * @return the questionType
     */
    public String getQuestionType() {
        return questionType;
    }
    /**
     * Set the value for questionType.
     * @param questionType the questionType to set
     */
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
    /**
     * Get value of score.
     * @return the score
     */
    public Double getScore() {
        return score;
    }
    /**
     * Set the value for score.
     * @param score the score to set
     */
    public void setScore(Double score) {
        this.score = score;
    }
    /**
     * Get value of correctAnswer.
     * @return the correctAnswer
     */
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    /**
     * Set the value for correctAnswer.
     * @param correctAnswer the correctAnswer to set
     */
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    /**
     * Get value of answers.
     * @return the answers
     */
    public List<String> getAnswers() {
        return answers;
    }
    /**
     * Set the value for answers.
     * @param answers the answers to set
     */
    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
    /**
     * Get value of feedbacks.
     * @return the feedbacks
     */
    public List<String> getFeedbacks() {
        return feedbacks;
    }
    /**
     * Set the value for feedbacks.
     * @param feedbacks the feedbacks to set
     */
    public void setFeedbacks(List<String> feedbacks) {
        this.feedbacks = feedbacks;
    }
    /**
     * Get value of correctFeedback.
     * @return the correctFeedback
     */
    public String getCorrectFeedback() {
        return correctFeedback;
    }
    /**
     * Set the value for correctFeedback.
     * @param correctFeedback the correctFeedback to set
     */
    public void setCorrectFeedback(String correctFeedback) {
        this.correctFeedback = correctFeedback;
    }
    /**
     * Get value of incorrectFeedback.
     * @return the incorrectFeedback
     */
    public String getIncorrectFeedback() {
        return incorrectFeedback;
    }
    /**
     * Set the value for incorrectFeedback.
     * @param incorrectFeedback the incorrectFeedback to set
     */
    public void setIncorrectFeedback(String incorrectFeedback) {
        this.incorrectFeedback = incorrectFeedback;
    }
    /**
     * Get value of isRandomize.
     * @return the isRandomize
     */
    public boolean isRandomize() {
        return isRandomize;
    }
    /**
     * Set the value for isRandomize.
     * @param isRandomize the isRandomize to set
     */
    public void setRandomize(boolean isRandomize) {
        this.isRandomize = isRandomize;
    }
    /**
     * Get value of objective.
     * @return the objective
     */
    public String getObjective() {
        return objective;
    }
    /**
     * Set the value for objective.
     * @param objective the objective to set
     */
    public void setObjective(String objective) {
        this.objective = objective;
    }
    /**
     * Get value of hasRationale.
     * @return the hasRationale
     */
    public Boolean getHasRationale() {
        return hasRationale;
    }
    /**
     * Set the value for hasRationale.
     * @param hasRationale the hasRationale to set
     */
    public void setHasRationale(Boolean hasRationale) {
        this.hasRationale = hasRationale;
    }
    /**
     * Get value of keyword.
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }
    /**
     * Set the value for keyword.
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    /**
     * Get value of rubric.
     * @return the rubric
     */
    public String getRubric() {
        return rubric;
    }
    /**
     * Set the value for rubric.
     * @param rubric the rubric to set
     */
    public void setRubric(String rubric) {
        this.rubric = rubric;
    }

    public String getFeedback(int answerNo) {
        String feedbackAnswerNo = "";

        // Get number of answer options
        int lenAnswer = (answers != null) ? answers.size() : 0;

        if (answerNo < lenAnswer) {
            // Get number of answer feedbacks
            int lenFeedback = (feedbacks != null) ? feedbacks.size() : 0;
            if (answerNo < lenFeedback) {
                feedbackAnswerNo = feedbacks.get(answerNo);
            }
        } else {
            // Do nothing
        }

        return feedbackAnswerNo;
    }
}
