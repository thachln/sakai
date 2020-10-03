package m.k.s.eng.sakai.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TOEIC_POSITION")
public class Position {
    /** Auto increment key. */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "address")
    private String address;
    @Column(name = "assessmentGradingId")
    private Long assessmentGradingId;
    @Column(name = "assessmentId")
    private Long assessmentId;
    @Column(name = "created")
    private Date created;

    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    public Long getAssessmentId() {
        return assessmentId;
    }
    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }
    public Integer getId() {
        return id;
    }
    public String getLatitude() {
        return latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public String getAddress() {
        return address;
    }
    public Long getAssessmentGradingId() {
        return assessmentGradingId;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setAssessmentGradingId(Long assessmentGradingId) {
        this.assessmentGradingId = assessmentGradingId;
    }

}
