package m.k.s.eng.sakai.service;

import java.util.List;

import m.k.s.eng.sakai.entity.Position;
import m.k.s.eng.sakai.model.PositionModel;

public interface PositionService {

    void save(Position p);

    List<PositionModel> findByAssessmentId(Long assessmentId);

    List<PositionModel> findByAssessmentIdAndAssessmentGradingId(Long assessmentId, Long assessmentGradingId);

}
