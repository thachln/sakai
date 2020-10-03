package m.k.s.eng.sakai.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import m.k.s.eng.sakai.entity.Position;
import m.k.s.eng.sakai.model.PositionModel;
import m.k.s.eng.sakai.service.PositionService;

@Controller
public class PositionController {

    @Autowired
    PositionService positionService;

    @PostMapping(value = "position/save")
    @ResponseBody
    public String savePosition(@RequestBody Position p) {
        positionService.save(p);

        return "{\"result\":\"success\"}";
    }

    @PostMapping(value = "position/find-by-assessmentId")
    @ResponseBody
    public List<PositionModel> findByAssessmentId(@RequestParam("idA") Long assessmentId) {

        return positionService.findByAssessmentId(assessmentId);
    }

    @PostMapping(value = "position/find-history-by-assessmentGradingId")
    @ResponseBody
    public List<PositionModel> findByAssessmentIdAndAssessmentGradingId(@RequestParam("idA") Long assessmentId,
            @RequestParam("idG") Long assessmentGradingId) {

        return positionService.findByAssessmentIdAndAssessmentGradingId(assessmentId, assessmentGradingId);
    }
}
