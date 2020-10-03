package m.k.s.eng.sakai.model;

import java.util.List;

import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.osid.shared.impl.AgentImpl;

public class RankModel {
    private List<AssessmentGradingData> ags;
    private List<AgentModel> agents;
    public List<AssessmentGradingData> getAgs() {
        return ags;
    }
    public void setAgs(List<AssessmentGradingData> ags) {
        this.ags = ags;
    }
    public List<AgentModel> getAgents() {
        return agents;
    }
    public void setAgents(List<AgentModel> agents) {
        this.agents = agents;
    }
}
