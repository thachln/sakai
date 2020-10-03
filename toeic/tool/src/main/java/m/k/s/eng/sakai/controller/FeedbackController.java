package m.k.s.eng.sakai.controller;

import java.util.List;

import org.sakaiproject.tool.assessment.data.dao.grading.ToeicDetailFeedback;
import org.sakaiproject.tool.assessment.data.dao.grading.ToeicGeneralFeedback;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import m.k.s.eng.sakai.model.FeedbackModel;
import m.k.s.eng.sakai.service.FeedbackService;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackService feedBackService;

    @GetMapping("feedback")
    public String goFeedbackPage() {
        String siteId = AgentFacade.getCurrentSiteId();
        String role = AgentFacade.getRoleForAgentAndSite(AgentFacade.getAgentString(), siteId);
        if (role.equals("maintain") || role.equals("Teaching Assistant") || role.equals("Instructor")) {

            return "feedback";
        }

        return "redirect:/home";
    }

    @PostMapping("get-general-feedback-list")
    @ResponseBody
    public List<ToeicGeneralFeedback> getGeneralFeedbackList() {
        String siteId = AgentFacade.getCurrentSiteId();
        String role = AgentFacade.getRoleForAgentAndSite(AgentFacade.getAgentString(), siteId);
        if (role.equals("maintain") || role.equals("Teaching Assistant") || role.equals("Instructor")) {

            return feedBackService.getListGeneralFeedback();
        }

        return null;
    }

    @PostMapping("save-general-feedback")
    @ResponseBody
    public String saveGeneral(@RequestBody FeedbackModel feedback) {
        String siteId = AgentFacade.getCurrentSiteId();
        String role = AgentFacade.getRoleForAgentAndSite(AgentFacade.getAgentString(), siteId);
        if (role.equals("maintain") || role.equals("Teaching Assistant") || role.equals("Instructor")) {
            feedBackService.saveOrUpdateGeneralFeedback(feedback);
        }

        return "{\"result\":\"success\"}";
    }

    @PostMapping("get-detail-feedback-list")
    @ResponseBody
    public List<ToeicDetailFeedback> getDetailFeedbackList() {
        String siteId = AgentFacade.getCurrentSiteId();
        String role = AgentFacade.getRoleForAgentAndSite(AgentFacade.getAgentString(), siteId);
        if (role.equals("maintain") || role.equals("Teaching Assistant") || role.equals("Instructor")) {

            return feedBackService.getListToeicDetailFeedback();
        }

        return null;
    }

    @PostMapping("save-detail-feedback")
    @ResponseBody
    public String saveDetail(@RequestBody FeedbackModel feedback) {
        String siteId = AgentFacade.getCurrentSiteId();
        String role = AgentFacade.getRoleForAgentAndSite(AgentFacade.getAgentString(), siteId);
        if (role.equals("maintain") || role.equals("Teaching Assistant") || role.equals("Instructor")) {
            feedBackService.saveOrUpdateDetailFeedback(feedback);
        }

        return "{\"result\":\"success\"}";
    }
}
