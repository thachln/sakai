package org.sakaiproject.lessonbuildertool.service;

/**
 * @author ThachLN
 *
 */
public class JSonResult {
    private String status = "true";
    
    private Object data;

    /**
    * Get value of status.
    * @return the status
    */
    public String getStatus() {
        return status;
    }

    /**
     * Set the value for status.
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
    * Get value of data.
    * @return the data
    */
    public Object getData() {
        return data;
    }

    /**
     * Set the value for data.
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }
}
