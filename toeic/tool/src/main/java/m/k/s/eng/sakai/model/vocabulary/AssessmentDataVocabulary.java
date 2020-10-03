package m.k.s.eng.sakai.model.vocabulary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.sakaiproject.tool.assessment.ui.listener.util.TimeUtil;
import org.sakaiproject.util.ResourceLoader;

/**
 * Description: Customized Assessment Bean For TOEIC
 * @author MINH MAN
 */
public class AssessmentDataVocabulary {
    final static protected Log LOG = LogFactory.getLog(AssessmentDataVocabulary.class);

    private String display_dateFormat = ContextUtil
            .getLocalizedString("org.sakaiproject.tool.assessment.bundle.GeneralMessages", "output_date_no_sec");
    private SimpleDateFormat displayFormat = new SimpleDateFormat(display_dateFormat, new ResourceLoader().getLocale());

    /** URL of the resources contains parts. */
    private String rootPath;
    private List<PartDataVocabulary> partsContents;
    private double currentScore;
    private double maxScore;
    private String password;
    private Set ip;
    private Integer timeElapsed;
    private long assessmentGradingId;
    private String description;
    private String assessmentTitle;
    private Integer timeLimit;
    private String dueDateString;
    private boolean isPractice;
    private Date dueDate;

    /**
     * Get value of rootPath.
     * @return the rootPath
     */
    public String getRootPath() {
        return rootPath;
    }

    /**
     * Set the value for rootPath.
     * @param rootPath the rootPath to set
     */
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public AssessmentDataVocabulary() {
        // TODO Auto-generated constructor stub
    }

    public AssessmentDataVocabulary(List<PartDataVocabulary> partsContents, double currentScore, double maxScore) {
        this.partsContents = partsContents;
        this.currentScore = currentScore;
        this.maxScore = maxScore;
    }

    public List<PartDataVocabulary> getPartsContents() {
        return partsContents;
    }

    public void setPartsContents(List<PartDataVocabulary> partsContents) {
        this.partsContents = partsContents;
    }

    public double getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(double currentScore) {
        this.currentScore = currentScore;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set getIp() {
        return ip;
    }

    public void setIp(Set ip) {
        this.ip = ip;
    }

    public Integer getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(Integer timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public long getAssessmentGradingId() {
        return assessmentGradingId;
    }

    public void setAssessmentGradingId(long assessmentGradingId) {
        this.assessmentGradingId = assessmentGradingId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPractice() {
        return isPractice;
    }

    public void setPractice(boolean isPractice) {
        this.isPractice = isPractice;
    }

    public String getAssessmentTitle() {
        return assessmentTitle;
    }

    public void setAssessmentTitle(String assessmentTitle) {
        this.assessmentTitle = assessmentTitle;
    }

    public String getDueDateString() {
        String dateString = "";

        try {
            TimeUtil tu = new TimeUtil();
            dateString = tu.getDisplayDateTime(displayFormat, dueDate, true);
        } catch (Exception ex) {
            // we will leave it as an empty string
            LOG.warn("getDueDateString(): " + ex);
        }
        return dateString;
    }

    /**
     * Get value of dueDate.
     * @return the dueDate
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Set the value for dueDate.
     * @param dueDate the dueDate to set
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

}
