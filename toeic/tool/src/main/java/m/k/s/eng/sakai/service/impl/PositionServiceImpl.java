package m.k.s.eng.sakai.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import m.k.s.eng.sakai.entity.Position;
import m.k.s.eng.sakai.model.PositionModel;
import m.k.s.eng.sakai.repository.PositionRepository;
import m.k.s.eng.sakai.service.PositionService;

@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    PositionRepository positionRepository;

    GradingService gradingService = new GradingService();

    @Override
    public void save(Position p) {
        p.setCreated(new Date());
        positionRepository.save(p);
    }

    @Override
    public List<PositionModel> findByAssessmentId(Long assessmentId) {
        List<Position> list = positionRepository.findByAssessmentId(assessmentId);
        List<PositionModel> models = new ArrayList<PositionModel>();
        PositionModel m;
        AssessmentGradingData agd;

        for (Position p : list) {
            agd = new AssessmentGradingData();
            m = new PositionModel();
            AgentFacade agent = new AgentFacade(agd.getAgentId());
            agd = gradingService.loadAssessmentGradingDataOnly(p.getAssessmentGradingId());
            m.setAddress(p.getAddress());
            m.setLatitude(p.getLatitude());
            m.setLongitude(p.getLongitude());
            m.setAgentId(agd.getAgentId());
            m.setSubmited(agd.getForGrade());
            m.setAgentEid(agent.getEid());
            models.add(m);
        }

        return models;
    }

    @Override
    public List<PositionModel> findByAssessmentIdAndAssessmentGradingId(Long assessmentId, Long assessmentGradingId) {
        List<PositionModel> models = new ArrayList<PositionModel>();
        PositionModel m;
        AssessmentGradingData agd;
        List<Position> list = positionRepository.findByAssessmentIdAndAssessmentGradingId(assessmentId,
                assessmentGradingId);

        for (Position p : list) {
            agd = new AssessmentGradingData();
            m = new PositionModel();
            AgentFacade agent = new AgentFacade(agd.getAgentId());
            agd = gradingService.loadAssessmentGradingDataOnly(p.getAssessmentGradingId());
            m.setAddress(p.getAddress());
            m.setLatitude(p.getLatitude());
            m.setLongitude(p.getLongitude());
            m.setAgentId(agd.getAgentId());
            m.setSubmited(agd.getForGrade());
            m.setAgentEid(agent.getEid());
            models.add(m);
        }

        return models;
    }
}
