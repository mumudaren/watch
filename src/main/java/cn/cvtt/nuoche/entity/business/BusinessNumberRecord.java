package cn.cvtt.nuoche.entity.business;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="business_number_record")
public class BusinessNumberRecord {

    @Id
    @GeneratedValue
    private  Long    id;
    @Column(name="user_phone")
    private  String  userPhone;
    @Column(name="regex_id")
    private  Integer  regexId;
    @Column(name="prtms")
    private  String  prtms;
    @Column(name="callrestrict")
    private  String  callrestrict;
    @Column(name="smbms")
    private  String  smbms;
    @Column(name="result")
    private  Integer  result;
    @Column(name="valid_time")
    private  Date    validTime;
    @Transient
    private  String  time;
    @Column(name="subts")
    private  Date    subts;
    @Transient
    private  String  subtsString;
    @Transient
    private  Integer index;
    @Column(name="business_id")
    private  String  businessId;
    @Transient
    private  Integer  isValid;



    public BusinessNumberRecord() {
    }

    public BusinessNumberRecord(Long id, String userPhone, Integer regexId, String prtms, String smbms, Integer result, Date validTime, Date subts, String businessId) {
        this.id = id;
        this.userPhone = userPhone;
        this.regexId = regexId;
        this.prtms = prtms;
        this.smbms = smbms;
        this.result = result;
        this.validTime = validTime;
        this.subts = subts;
        this.businessId = businessId;
    }

    public String getCallrestrict() {
        return callrestrict;
    }

    public void setCallrestrict(String callrestrict) {
        this.callrestrict = callrestrict;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSubtsString() {
        return subtsString;
    }

    public void setSubtsString(String subtsString) {
        this.subtsString = subtsString;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Integer getRegexId() {
        return regexId;
    }

    public void setRegexId(Integer regexId) {
        this.regexId = regexId;
    }

    public String getPrtms() {
        return prtms;
    }

    public void setPrtms(String prtms) {
        this.prtms = prtms;
    }

    public String getSmbms() {
        return smbms;
    }

    public void setSmbms(String smbms) {
        this.smbms = smbms;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public Date getSubts() {
        return subts;
    }

    public void setSubts(Date subts) {
        this.subts = subts;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
}
