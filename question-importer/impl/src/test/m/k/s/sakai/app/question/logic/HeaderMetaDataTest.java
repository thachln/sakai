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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author ThachLN
 *
 */
public class HeaderMetaDataTest {

    /**
     * [Give the description for method].
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * [Give the description for method].
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * [Give the description for method].
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * [Give the description for method].
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link m.k.s.sakai.app.question.logic.HeaderMetaData#HeaderMetaData(java.lang.String[])}.
     */
    @Test
    public void testHeaderMetaData() {
        String[] headerNames = {"Question", "Level", "Mark", "IsNotRandom (x)", "Question type", "Answer", "A", "B", "C", "D", "E", "FA", "FB", "FC", "FD", "CorrectAnswerFB", "IncorrectAnswerFB", "Objective"};
        HeaderMetaData hmt  = new HeaderMetaData(headerNames);
        
        assertEquals(0, hmt.getIndexQuestion());
        assertEquals(1, hmt.getIndexLevel());
        assertEquals(2, hmt.getIndexScore());
        assertEquals(3, hmt.getIndexIsNotRandom());
        assertEquals(4, hmt.getIndexQuestionType());
        assertEquals(5, hmt.getIndexCorrectAnswer());
        assertEquals(6, hmt.getIndexAnswerStart());
        assertEquals(10, hmt.getIndexAnswerEnd());
        assertEquals(11, hmt.getIndexFeedbackStart());
        assertEquals(14, hmt.getIndexFeedbackEnd());
        
        assertEquals(15, hmt.getIndexCorrectAnswerFeedback());
        assertEquals(16, hmt.getIndexInCorrectAnswerFeedback());
        assertEquals(17, hmt.getIndexObjective());
    }

}
