/**
 * Licensed to MKS Group under one or more contributor license
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

package m.k.s.eng.sakai.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBean;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import m.k.s.eng.sakai.model.AgentModel;
import m.k.s.eng.sakai.model.AnswerToeicGrading;
import m.k.s.eng.sakai.model.AssessmentDataToeic;
import m.k.s.eng.sakai.model.BasicAssessmentModel;
import m.k.s.eng.sakai.model.DataResult;
import m.k.s.eng.sakai.model.DataToeic;
import m.k.s.eng.sakai.model.ItemResult;
import m.k.s.eng.sakai.model.ItemToeicGrading;
import m.k.s.eng.sakai.model.RankModel;
import m.k.s.eng.sakai.service.AssessmentToeicService;
import m.k.s.eng.sakai.service.ExportExcelService;
import m.k.s.eng.sakai.util.AppUtil;

/**
 * Handles requests for the application home page.
 */
/**
 * @author MINH MAN
 */

@Controller
public class HomeController {
    final static protected Log LOG = LogFactory.getLog(HomeController.class);

    @Autowired
    @Qualifier("assessmentToeicService")
    private AssessmentToeicService assessmentToeicService;

    @Autowired
    @Qualifier("exportExcelService")
    private ExportExcelService exportExcelService;

    private GradingService gradingService = new GradingService();

    private Gson gson = new Gson();

    /**
     * [Give the description for method].
     * @param model
     * @return
     */
    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String goTest(@RequestParam("idA") Long id, Model model) {
        String siteId = AgentFacade.getCurrentSiteId();
        String role = AgentFacade.getRoleForAgentAndSite(AgentFacade.getAgentString(), siteId);
        List<AssessmentGradingData> ags = (List<AssessmentGradingData>) gradingService
                .getHighestSubmittedOrGradedAssessmentGradingList(id);
        ags.forEach(agd -> {
            agd.setItemGradingSet(null);
            agd.setAgentId(AppUtil.getDisplayName(agd.getAgentId()));
        });

        Collections.sort(ags, (a1, a2) -> a2.getFinalScore().compareTo(a1.getFinalScore()));

        model.addAttribute("role", role);
        model.addAttribute("ags", ags);
        model.addAttribute("userEid", AgentFacade.getEid());

        return "test";
    }

    @RequestMapping(value = "result", method = RequestMethod.GET)
    public String goResult(@RequestParam("idA") Long idA, @RequestParam("idG") Long idG, Model model) {
        // String siteId = AgentFacade.getCurrentSiteId();
        // String role = AgentFacade.getRoleForAgentAndSite(AgentFacade.getAgentString(), siteId);
        //
        // if (role.equals("maintain") || role.equals("Teaching Assistant") || role.equals("Instructor")) {
        // return "test_result";
        // } else {
        // PublishedAssessmentService aService = new PublishedAssessmentService();
        // PublishedAssessmentData assessment = aService.getBasicInfoOfPublishedAssessment(String.valueOf(idA));
        // if (!AppUtil.isPractice(assessment.getDescription())) {
        // return null;
        // } else {
        return "test_result";
        // }
        // }
    }

    @PostMapping("get-result")
    @ResponseBody
    public DataResult getResult(@RequestParam("idA") Long idA, @RequestParam("idG") Long idG,
            HttpServletRequest request, HttpServletResponse response) {
        DeliveryBean deliveryBean = (DeliveryBean) ContextUtil.lookupBeanFromExternalServlet("delivery", request,
                response);
        // String siteId = AgentFacade.getCurrentSiteId();
        // String role = AgentFacade.getRoleForAgentAndSite(AgentFacade.getAgentString(), siteId);
        DataResult result = new DataResult();

        AssessmentDataToeic assessment = assessmentToeicService.getToeicData(idA, idG, deliveryBean);

        // if ((role.equals("maintain") || role.equals("Teaching Assistant") || role.equals("Instructor"))
        // || AppUtil.isPractice(assessment.getDescription())) {
        List<ItemResult> list = assessmentToeicService.getItemResults(idG, idA, deliveryBean);

        result.setAssessment(assessment);
        result.setItems(list);

        return result;
        // } else {
        // return null;
        // }
    }

    /**
     * For Test: Function return View home and object list assessment
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView displayHome(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("home");

        BasicAssessmentModel basic = assessmentToeicService.getAllBasicAssessment(request, response);
        
        LOG.debug("basic=" + basic);

        mav.addObject("basic", basic);

        String siteId = AgentFacade.getCurrentSiteId();
        String role = AgentFacade.getRoleForAgentAndSite(AgentFacade.getAgentString(), siteId);

        mav.addObject("role", role);

        return mav;
    }

    /**
     * For Test: Function return object list assessment
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getBasicAssessment", method = RequestMethod.POST)
    @ResponseBody
    public BasicAssessmentModel getBasicAssessment(HttpServletRequest request, HttpServletResponse response) {
        BasicAssessmentModel basic = assessmentToeicService.getAllBasicAssessment(request, response);

        return basic;
    }

    @GetMapping("/students-result")
    public String goStudentsResult(@RequestParam("idA") String idA, Model model) {
        String siteId = AgentFacade.getCurrentSiteId();
        String role = AgentFacade.getRoleForAgentAndSite(AgentFacade.getAgentString(), siteId);

        if (role.equals("maintain") || role.equals("Instructor") || role.equals("Teaching Assistant")) {
            String title = gradingService.getPublishedAssessmentTitle(Long.valueOf(idA));
            model.addAttribute("idA", idA);
            model.addAttribute("title", title);

            return "students-result";
        }

        return "home";
    }

    /**
     * Use: get data of a published assessment by id
     * @param id - identifier of the published assessment
     * @param request
     * @param response
     * @return json - contain entire contents of the TOEIC test Ex: {"partsContents":[{"itemContents": [{"content": "How
     *         are you","key":"A",...},{}], "partId":"1","maxPoints":60.0,"description":"","title":"Part 1"},{}],
     *         "currentScore":0.0,"maxScore":100.0}
     */
    @RequestMapping(value = "/getDataTest/{id}", method = RequestMethod.POST)
    @ResponseBody
    public DataToeic getDataTest(@PathVariable("id") String id, HttpServletRequest request,
            HttpServletResponse response) {

        AssessmentDataToeic assessment = assessmentToeicService.getAssessmentToeicById(id, request, response);
        ItemToeicGrading itemToeicGrading = assessmentToeicService
                .getSavedAnswer(String.valueOf(assessment.getAssessmentGradingId()));

        DataToeic dataToeic = new DataToeic();
        dataToeic.setAssessment(assessment);

        if (itemToeicGrading != null) {
            dataToeic.setSavedAnswer(itemToeicGrading);
        }

        return dataToeic;
    }

    /**
     * Export excel to user
     * @param jsonData - list answer user selected and id question
     * @return excel file
     */
    @RequestMapping(value = "export", method = RequestMethod.GET)
    @ResponseBody
    public void exportReport(@RequestParam("assessmentGradingId") String assessmentGradingId,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            exportExcelService.exportAssessmentResult(assessmentGradingId, request, response);
        } catch (IOException ex) {
            LOG.error("Could not download.", ex);
        }
    }

    @GetMapping("export-students-result")
    @ResponseBody
    public void exportStudentsResult(@RequestParam("idA") String publishedAssessmentId, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            exportExcelService.exportAllAssessmentResult(Long.valueOf(publishedAssessmentId), request, response);
        } catch (IOException ex) {
            LOG.error("Could not download.", ex);
        }
    }

    @PostMapping("submit-test")
    @ResponseBody
    public String submitTest(@RequestParam("isSubmit") String isSubmit,
            @RequestParam("isAutoSubmit") String isAutoSubmit, @RequestBody ItemToeicGrading data,
            HttpServletRequest request, HttpServletResponse response) {

        String result = assessmentToeicService.grading(data, Boolean.parseBoolean(isSubmit),
                Boolean.parseBoolean(isAutoSubmit));

        return result;
    }

    @PostMapping("get-all-assessmentGrading")
    @ResponseBody
    public String getAllAssessmentGrading(@RequestParam("idA") String idA) {

        return gson.toJson(assessmentToeicService.getAllAssessmentGradingData(Long.valueOf(idA)));
    }

    @PostMapping("get-all-student-assessmentGrading")
    @ResponseBody
    public String getAllStudentAssessmentGrading(@RequestParam("idA") String idA) {

        return gson.toJson(assessmentToeicService.getAllAssessmentGradingDataByIdAndAgent(Long.valueOf(idA)));
    }

    @PostMapping("get-list-publishedItem")
    @ResponseBody
    public List<ItemResult> getPublishedItems(@RequestParam("idA") Long assessmentGradingId,
            @RequestParam("publishedId") Long publishedId, HttpServletRequest request, HttpServletResponse response) {
        DeliveryBean deliveryBean = (DeliveryBean) ContextUtil.lookupBeanFromExternalServlet("delivery", request,
                response);
        return assessmentToeicService.getItemResults(assessmentGradingId, publishedId, deliveryBean);
    }

    @PostMapping("get-password")
    @ResponseBody
    public String getDecryptPassword(@RequestParam("idA") String publishedAssessmentId) {

        return assessmentToeicService.getDecryptPassword(Long.parseLong(publishedAssessmentId));
    }

    @PostMapping("save-itemGradingList")
    @ResponseBody
    public String saveItemGrading(@RequestBody List<AnswerToeicGrading> data) {

        assessmentToeicService.saveItemGradingList(data);

        return gson.toJson("{\"result\": \"Success\"}");
    }

    @PostMapping("get-timeLimit")
    @ResponseBody
    public Integer getTimeLimit(@RequestParam("idA") String id) {

        PublishedAssessmentFacade publishedAssessment = (new PublishedAssessmentService()).getPublishedAssessment(id);

        return assessmentToeicService.calculateTimeLimit(publishedAssessment);
    }

    @PostMapping("get-highest-score")
    @ResponseBody
    public RankModel getHighestScores(@RequestParam("idA") Long id) {
        RankModel rank = new RankModel();
        AgentModel aM;
        List<AssessmentGradingData> ags = (List<AssessmentGradingData>) gradingService
                .getHighestSubmittedOrGradedAssessmentGradingList(id);
        List<AgentModel> agents = new ArrayList<AgentModel>();

        ags.forEach(agd -> {
            agd.setItemGradingSet(null);
            // agd.setAgentId(AppUtil.getDisplayName(agd.getAgentId()));
        });

        Collections.sort(ags, (a1, a2) -> a2.getFinalScore().compareTo(a1.getFinalScore()));

        for (AssessmentGradingData a : ags) {
            AgentFacade agent = new AgentFacade(a.getAgentId());
            aM = new AgentModel();
            aM.setAgentId(a.getAgentId());
            aM.setAgentName(agent.getDisplayName());
            agents.add(aM);
        }

        rank.setAgs(ags);
        rank.setAgents(agents);

        return rank;
    }

    @PostMapping("get-total-answered")
    @ResponseBody
    public String countAnswered(@RequestParam("idG") Long assessmentGradingId) {

        List<Long> items = gradingService.getItemGradingIds(assessmentGradingId);

        return "{\"result\": \"" + items.size() + "\"}";
    }
}
