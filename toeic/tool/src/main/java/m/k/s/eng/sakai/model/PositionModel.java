package m.k.s.eng.sakai.model;

public class PositionModel {
    private String longitude;
    private String latitude;
    private String address;
    private String agentEid;
    private String agentId;
    private boolean submited;

    public String getLongitude() {
        return longitude;
    }
    public String getLatitude() {
        return latitude;
    }
    public String getAddress() {
        return address;
    }
    public String getAgentEid() {
        return agentEid;
    }
    public String getAgentId() {
        return agentId;
    }
    public boolean isSubmited() {
        return submited;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setAgentEid(String agentEid) {
        this.agentEid = agentEid;
    }
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
    public void setSubmited(boolean submited) {
        this.submited = submited;
    }

}
