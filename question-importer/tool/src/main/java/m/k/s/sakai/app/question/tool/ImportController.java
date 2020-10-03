/**
 * Licensed to MKSOL under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Open-Ones Group licenses this file to you under the Apache License,
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
package m.k.s.sakai.app.question.tool;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.tool.assessment.data.ifc.questionpool.QuestionPoolDataIfc;
import org.sakaiproject.tool.assessment.facade.ItemFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;
import m.k.s.sakai.app.question.logic.QuestionPoolLogic;
import m.k.s.sakai.app.question.logic.SakaiProxy;
import m.k.s.sakai.app.question.tool.editor.QuestionListEditor;
import m.k.s.sakai.app.question.tool.model.QuestionListModel;
import m.k.s.sakai.app.question.tool.model.ResultImport;
import m.k.s.sakai.app.question.tool.model.TreeNode;

/**
 * @author Tho
 */
@Controller
public class ImportController {
    private static Logger log = LoggerFactory.getLogger(ImportController.class);
    
    @Setter
    @Getter
    private SakaiProxy sakaiProxy;
    
    @Setter
    @Getter
    private QuestionPoolLogic questionPoolLogic;
    
    @InitBinder
    protected void initBinder(WebDataBinder binder, WebRequest request) {
        //log.info("initBinder");

//        Set<String> mapKeySet = request.getParameterMap().keySet();
//        for (String param : mapKeySet) {
//            if (mapKeySet.contains("questionList")) {
                Class<List<ItemFacade>> collectionType = (Class<List<ItemFacade>>)(Class<?>)List.class;
                PropertyEditor questionListEditor = new QuestionListEditor(collectionType, questionPoolLogic);
                binder.registerCustomEditor((Class<List<ItemFacade>>)(Class<?>)List.class, questionListEditor);
//            }
//        }
    }
    
//    @RequestMapping(value = "/", method = RequestMethod.GET)
//    public String index() {
//        return "home";
//    }
    
    /**
     * Simply selects the home view to render by returning its name.
     * @return 
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("home");

        return mav;
    }
    
    /**
     * Process ajax request from Tree of Question Pools from client.
     * @return json format of Node
     * @see https://www.jstree.com/docs/json/
     */
    @RequestMapping(value = "/getNodeRoot", method = RequestMethod.GET)
    @ResponseBody
    public String getNodeRoot() {
        TreeNode rootNode = new TreeNode();
        
        rootNode.setText("QuestionPools");
        rootNode.setId(0L);
        rootNode.setOpened(true);
        
        List<QuestionPoolDataIfc> questionPools = sakaiProxy.getPools();

        List<TreeNode> listPoolL1 = buildTreeNode(questionPools);

        if (listPoolL1 != null) {
            log.debug("listPoolL1 size:" + listPoolL1.size());
            for (QuestionPoolDataIfc pool : questionPools) {
                log.debug("Pool Title:" + pool.getTitle());
            }
        }
        
        rootNode.setChildren(listPoolL1);

        return new Gson().toJson(rootNode);

    }
    
    /**
     * Process ajax request from Tree of Question Pools to load sub Pools.
     * 
     * @param id Identifier of the Question Pool.
     * @return json data of sub pools.
     */
    @RequestMapping(value = "/getNodeChildren", method = RequestMethod.GET)
    @ResponseBody
    public String retrieveSubPools(@RequestParam("id") Long poolId) {
        TreeNode rootNode = new TreeNode();

        // Get Sub Pools
        List<QuestionPoolDataIfc> questionPools = sakaiProxy.getPools(poolId);
        
        log.debug("Number subpools of '" + poolId + ": " + ((questionPools != null) ? questionPools.size() : -1));

        List<TreeNode> listPoolL1 = buildTreeNode(questionPools);
        
        rootNode.setChildren(listPoolL1);

        return new Gson().toJson(rootNode);
    }
    
    /**
     * Convert from list of entity Question Pools into list of TreeNode to display in the client.
     * @param questionPools
     * @return
     */
    private List<TreeNode> buildTreeNode(List<QuestionPoolDataIfc> questionPools) {
        List<TreeNode> listPools = new ArrayList<TreeNode>();
        TreeNode node;

        List<QuestionPoolDataIfc> listSubPools;
        List<TreeNode> listSubTreeNode;
        for (QuestionPoolDataIfc questionPool : questionPools) {
            log.info("questionPool.getParentPoolId()=" + questionPool.getParentPoolId());

            node = convert2TreeNode(questionPool);
            
            // Build recursively nodes
            listSubPools = sakaiProxy.getPools(questionPool.getQuestionPoolId());
            listSubTreeNode = buildTreeNode(listSubPools);
            node.setChildren(listSubTreeNode);
            
            listPools.add(node);

        }
        
        return listPools;
    }

    /**
     * Convert data from QuestionPoolDataIfc to format of JSTree.
     * @param poolData maybe contains all Question Pools which includes sub pools
     * @return
     */
    private TreeNode convert2TreeNode(QuestionPoolDataIfc poolData) {
        TreeNode treeNode;
        
        if (poolData == null) {
            treeNode = null;
        } else {
            treeNode = new TreeNode();
            treeNode.setId(poolData.getQuestionPoolId());
            treeNode.setText(poolData.getTitle());
            treeNode.setParent(poolData.getParentPoolId());
            
            Map<String, String> a_attr = new HashMap<String, String>();
            a_attr.put("title", poolData.getTitle());
            a_attr.put("description", poolData.getDescription());
            
            treeNode.setA_attr(a_attr);
            
//            // Check having children or not.
//            for (QuestionPoolDataIfc poolItem : questionPools) {
//                if (poolItem.getParentPoolId() == treeNode.getId()) {
//                    treeNode.setOpened(true);
//                    treeNode.setHasChildren();
//                }
//            }
        }
        
        return treeNode;
    }

    
    /**
     * Process event "save" questions.
     * If there is the uploaded file, the content of the file will be parsed to get questions.
     * If there are input data in the table, the question will be prepared continuously.
     * @param model reflect the data in the screen.
     * @param bindingResult result of capture data from the client into the model.
     * @return json of result of each importing question.
     * <br/>
     * Ex:
     * <br/>
     * {'status': [true, true,...false]}
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public String processSave(@ModelAttribute("model") QuestionListModel model, BindingResult bindingResult, MultipartHttpServletRequest request) {
        ResultImport result = new ResultImport();
        
        // Initial: no error
        int invalidQuestionIdx = -1;
        int invalidColIdx = -1;
        String errorMessage = null;
        
        log.info("process event: Run.");

        if (bindingResult.hasErrors()) {
            log.error("Binding result; hasError=" + bindingResult.hasErrors());

            // Log errors
            for (ObjectError objErr : bindingResult.getAllErrors()) {
                log.error("Error=" + objErr.getCode() + ";" + objErr.getDefaultMessage());
            }

        } else {
            // Check license
            String licenseMessage = questionPoolLogic.checkLicense();
            log.debug("licenseMessage=" + licenseMessage);

            List<Boolean> listResult = new ArrayList<Boolean>();

            log.debug("Selected Question Pool Id:" + model.getPoolId());
            
            log.debug("model.getAttachment()=" + model.getAttachment());

            // Step 1: Import question(s) from the uploaded file
            MultipartFile attachedFile = model.getAttachment();
            
            try {
                log.debug("Attached file size:" + attachedFile.getSize());
                if ((attachedFile != null) && (attachedFile.getSize() > 0)) {  // Has attached file
                    log.debug("Has a attached file.");
                    
                    String questionColor = model.isSetQuestionColor() ? model.getQuestionColor() : null;
                    
                    List<Boolean> listResultFile = questionPoolLogic.save(attachedFile.getOriginalFilename(), attachedFile.getInputStream(),
                            model.getPoolId(), questionColor, model.isSetQuestionBold(), model.getMinDuration(), model.getMaxDuration(), invalidQuestionIdx, invalidColIdx, errorMessage);
                    
                    if ((listResultFile != null) && (listResultFile.size() > 0)) {
                        listResult.addAll(listResultFile);
                    }
                } else {
                    log.debug("No attached file.");
                }
            } catch (IOException ioEx) {
                log.error("Could not get the uploaded file.", ioEx);
                result.setStatus("ERROR");
                
                errorMessage = ioEx.getMessage();
                result.setErrorMessage(errorMessage);
            }

            // Step 2: Import the question(s) from the Grid Table.
            List<ItemFacade> listQuestion = model.getQuestionList();
            
            String questionColor = model.isSetQuestionColor() ? model.getQuestionColor() : null;
            
            if ((listQuestion != null) && (listQuestion.size() > 0)) {
                List<Boolean> listResultTable = questionPoolLogic.save(listQuestion, model.getPoolId(), questionColor, model.isSetQuestionBold(), model.getMinDuration(), model.getMaxDuration());
                
                listResult.addAll(listResultTable);
            }
            
            log.debug("errorMessage=" + errorMessage);

            if ((listResult != null) && (errorMessage == null)) {
                result.setStatus("Inserted " + listResult.size() + " question(s).");
            } if ("ERROR".equals(result.getStatus())) {
                result.setStatus("FAILED");
                result.setInvalidQuestionIdx(invalidQuestionIdx);
                result.setInvalidColIdx(invalidColIdx);
                result.setErrorMessage(errorMessage);
            }
        }

        return new Gson().toJson(result);
    }
}
