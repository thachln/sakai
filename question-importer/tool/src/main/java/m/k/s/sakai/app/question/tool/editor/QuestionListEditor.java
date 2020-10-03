/**
 * Licensed to MKS under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * MKS Group licenses this file to you under the Apache License,
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
package m.k.s.sakai.app.question.tool.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.facade.ItemFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import m.k.s.sakai.app.question.logic.HeaderMetaData;
import m.k.s.sakai.app.question.logic.QuestionData;
import m.k.s.sakai.app.question.logic.QuestionPoolLogic;
import m.k.s.sakai.app.question.util.AppUtil;

/**
 * @author Thach N. Le
 */
@EnableWebMvc
public class QuestionListEditor extends CustomCollectionEditor {
    private static Logger log = LoggerFactory.getLogger(QuestionListEditor.class);
    
    private QuestionPoolLogic questionPoolLogic;

    private static final boolean NO_BOLD = false;

    private static final String NO_COLOR = null;
    
    public QuestionListEditor(Class<? extends Collection> collectionType, QuestionPoolLogic questionPoolLogic) {
        super(collectionType);
        this.questionPoolLogic = questionPoolLogic;
    }

    /**
     * [Explain the description for this method here].
     * @param element
     * @return
     * @see org.springframework.beans.propertyeditors.CustomCollectionEditor#convertElement(java.lang.Object)
     */
    @Override
    protected Object convertElement(Object element) {
        log.debug("convertElement...");
        if (element == null) {
            return null;
        } else {
            // continue;
        }
        
        log.debug("Class of element=" + element.getClass());
        String strQuestionList = element.toString();
        
        // Check the json of grid of questions
        if (strQuestionList.startsWith("{\"header\"") || (strQuestionList.startsWith("{'header'"))) {

            try {
                List<ItemFacade> listItemDataIfc = new ArrayList<ItemFacade>();
                JsonParser jsonParser = new JsonParser();

                log.debug("strQuestionList=" + strQuestionList);
                JsonElement jsonElement = jsonParser.parse(strQuestionList);
                
                JsonObject jsonObj = jsonElement.getAsJsonObject();
                JsonElement headerElement = jsonObj.get("header");
                JsonElement dataElement = jsonObj.get("data");

                JsonArray jsonHeader = headerElement.getAsJsonArray();
                JsonArray jsonQuestionList = dataElement.getAsJsonArray();

                int invalidRowIdx = -1;
                int invalidColumnIdx = -1;
                String errorMessage = null;
                listItemDataIfc = getQuestionList(jsonQuestionList, jsonHeader, invalidRowIdx, invalidColumnIdx, errorMessage);

                return listItemDataIfc;
            } catch (IllegalStateException isEx) {
                // The element is not a json array
                return element;
            } catch (Exception ex) {
                log.error("Could not parse the question list", ex);
                throw new ConversionFailedException(TypeDescriptor.valueOf(String.class),
                        TypeDescriptor.valueOf(ItemDataIfc.class), strQuestionList, null);
            }
        } 
//        else if (strQuestionList.startsWith("[")) {
//            // Parse the column header "['a', 'b']"
//            // Remove start char '['
//            String[] arrayColHeaders = strQuestionList.split(",");
//            
//            Gson gson = new Gson();
//            List<String> colNameList = gson.fromJson(strQuestionList, List.class);
//            
//            JsonParser jsonParser = new JsonParser();
//            JsonArray jsonHeaderList = jsonParser.parse(strQuestionList).getAsJsonArray();
//            
//            int len = (jsonHeaderList != null) ? jsonHeaderList.size() : 0;
//            String colName;
//            List<String> colNameList = new ArrayList<String>();
//            for (int i = 0; i < len; i++) {
//                colName = jsonHeaderList.get(i).getAsString();
//                colNameList.add(colName);
//            }
//            
//            return colNameList;
//        }
        else {
            return element;
        }
    }

    private List<ItemFacade> getQuestionList(JsonArray jsonQuestionList, JsonArray jsonHeader, int invalidRowIdx, int invalidColumnIdx, String errorMessage) {
        List<ItemFacade> ItemDataIfcs = new ArrayList<ItemFacade>();
        int size = jsonQuestionList.size() - 1;
        
        JsonArray itemDataArr;

        for (int i = 0; i < size; i++) {
            
            
            itemDataArr = (JsonArray) jsonQuestionList.get(i);
            
            log.debug("Parse " + i + ":" + itemDataArr);
            
            ItemFacade itemFacade = new ItemFacade();
            
            itemFacade = parseQuestion(itemDataArr, jsonHeader, invalidColumnIdx, errorMessage);

            if (itemFacade != null) {
                ItemDataIfcs.add(itemFacade);
            } else {
                // Invalid data at row i
                invalidRowIdx = i;
            }
        }
        
        return ItemDataIfcs;
    }

    /**
     * [Give the description for method].
     * @param jsonQuestion
     * Column 0: Content of question
     * Column 1: Corrected answer(s). If multiple choice, corrected answers are separated by comma or space or semi-comma
     * Column 2 ~: Answers.
     * @param invalidColIdx if the question is invalid, this output value is index of the invalid column
     * @param errorMessage if the question is invalid, this output parameter contains the error message
     * @return
     */
    private ItemFacade parseQuestion(JsonArray jsonQuestion, JsonArray jsonHeader, int invalidColIdx, String errorMessage) {
        ItemFacade question;
        // Convert jsonQuestion to Array of String
//        int len = (jsonQuestion != null) ? jsonQuestion.size() : 0;
//        
//        String[] questionItems = new String[len];
//        JsonElement jsonElement;
//        for (int i = 0; i < len; i++) {
//            jsonElement = jsonQuestion.get(i);
//            questionItems[i] = (jsonElement instanceof JsonNull) ? null : jsonElement.getAsString();
//        }
        String[] headerNames = AppUtil.convert(jsonHeader);
        String[] questionItems = AppUtil.convert(jsonQuestion);
        
        log.debug("questionItems: " + questionItems);

        HeaderMetaData hmt = new HeaderMetaData(headerNames);
        QuestionData questionData = AppUtil.parseQuestion(questionItems, hmt);
        // No support Color and Bold for question
        question = questionPoolLogic.parseQuestion(questionData , NO_COLOR, NO_BOLD, invalidColIdx, errorMessage);
        
        return question;
    }



    /**
     * [Explain the description for this method here].
     * @param arg0
     * @see org.springframework.beans.propertyeditors.CustomCollectionEditor#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Object value) {
        log.debug("setValue...value=" + value);
        log.debug("Calss of value=" + ((value != null) ? value.getClass() : "NULL"));
        
        Object data = convertElement(value);

        if (data == null) {
            super.setValue(null);
        } else if (data instanceof List<?>){
            super.setValue((List<ItemDataIfc>) convertElement(value));
        } else {
            super.setValue(value);
        }
    }
}
