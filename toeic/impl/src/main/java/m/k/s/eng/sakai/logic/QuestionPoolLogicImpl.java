package m.k.s.eng.sakai.logic;

import java.util.List;

import org.sakaiproject.tool.assessment.services.QuestionPoolService;

public class QuestionPoolLogicImpl implements QuestionPoolLogic{
	QuestionPoolService questionPoolService = new QuestionPoolService();
	public List getSubPools(Long poolId) {
		return questionPoolService.getSubPools(poolId);
	}

	public boolean hasSubPools(Long poolId) {
		return questionPoolService.hasSubPools(poolId);
	}

	public List getAllItems(Long poolId) {
		return questionPoolService.getAllItems(poolId);
	}

}
