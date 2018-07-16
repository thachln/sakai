package org.sakaiproject.scorm.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SummaryRecord implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userName;
	private List<BasicResultPerContentP> lstResult = new ArrayList<>();
	
	public SummaryRecord(String userName, List<BasicResultPerContentP> lstResult){
		this.setUserName(userName);
		this.lstResult.clear();
		this.lstResult.addAll(lstResult);
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<BasicResultPerContentP> getLstResult() {
		return lstResult;
	}

	public void setLstResult(List<BasicResultPerContentP> lstResult) {
		this.lstResult = lstResult;
	}
}
