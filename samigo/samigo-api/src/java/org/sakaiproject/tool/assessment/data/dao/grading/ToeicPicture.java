package org.sakaiproject.tool.assessment.data.dao.grading;

import java.util.Date;

public class ToeicPicture {
    private Long id;
    private String agentId;
    private Long assessmentGradingId;
    private Date createdDate;
    private String contentType;
    private byte[] content;
    public Long getId() {
        return id;
    }
    public String getAgentId() {
        return agentId;
    }
    public Long getAssessmentGradingId() {
        return assessmentGradingId;
    }
    public String getContentType() {
        return contentType;
    }
    public byte[] getContent() {
        return content;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
    public void setAssessmentGradingId(Long assessmentGradingId) {
        this.assessmentGradingId = assessmentGradingId;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public void setContent(byte[] content) {
        this.content = content;
    }
    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

}
