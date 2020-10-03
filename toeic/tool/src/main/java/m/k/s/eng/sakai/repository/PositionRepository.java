package m.k.s.eng.sakai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import m.k.s.eng.sakai.entity.Position;

@Repository
public interface PositionRepository extends CrudRepository<Position, Long> {

    @Query("select distinct a from Position a where a.assessmentId = :assessmentId order by a.created desc")
    public List<Position> findByAssessmentId(@Param("assessmentId") Long assessmentId);

    @Query("select a from Position a where a.assessmentId = :assessmentId and a.assessmentGradingId = :assessmentGradingId")
    public List<Position> findByAssessmentIdAndAssessmentGradingId(@Param("assessmentId") Long assessmentId,
            @Param("assessmentGradingId") Long assessmentGradingId);
}
