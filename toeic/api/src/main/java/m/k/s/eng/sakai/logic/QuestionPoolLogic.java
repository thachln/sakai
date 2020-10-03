package m.k.s.eng.sakai.logic;

import java.util.ArrayList;
import java.util.List;

public interface QuestionPoolLogic {
	public List getSubPools(Long poolId);

	/**
	 * Checks to see if a pool has subpools
	 */
	public boolean hasSubPools(Long poolId);
	
	public List getAllItems(Long poolId);
}
