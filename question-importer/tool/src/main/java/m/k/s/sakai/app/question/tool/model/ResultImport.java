/**
 * 
 */
package m.k.s.sakai.app.question.tool.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ThachLN
 *
 */
public class ResultImport implements Serializable {
    private String status;
    private int invalidQuestionIdx;
    private int invalidColIdx;
    private String errorMessage;

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the invalidQuestionIdx
     */
    public int getInvalidQuestionIdx() {
        return invalidQuestionIdx;
    }

    /**
     * @param invalidQuestionIdx
     *            the invalidQuestionIdx to set
     */
    public void setInvalidQuestionIdx(int invalidQuestionIdx) {
        this.invalidQuestionIdx = invalidQuestionIdx;
    }

    /**
     * @return the invalidColIdx
     */
    public int getInvalidColIdx() {
        return invalidColIdx;
    }

    /**
     * @param invalidColIdx
     *            the invalidColIdx to set
     */
    public void setInvalidColIdx(int invalidColIdx) {
        this.invalidColIdx = invalidColIdx;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage
     *            the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
