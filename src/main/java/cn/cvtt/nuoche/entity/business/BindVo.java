package cn.cvtt.nuoche.entity.business;

import java.io.Serializable;

/**
 * @decription BindVo
 * <p>单绑业务对象（参数）</p>
 * @author Yampery
 * @date 2017/9/18 16:14
 */
public class BindVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String regphone;
    private String uidnumber;
    private String uuidinpartner;
    private String expiretime;
    private String uidtype;
    private String callrestrict;

    public String getRegphone() {
        return regphone;
    }

    public void setRegphone(String regphone) {
        this.regphone = regphone;
    }

    public String getUuidinpartner() {
        return uuidinpartner;
    }

    public void setUuidinpartner(String uuidinpartner) {
        this.uuidinpartner = uuidinpartner;
    }

    public String getUidtype() {
        return uidtype;
    }

    public void setUidtype(String uidtype) {
        this.uidtype = uidtype;
    }

    public String getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(String expiretime) {
        this.expiretime = expiretime;
    }

    public String getUidnumber() {
        return uidnumber;
    }

    public void setUidnumber(String uidnumber) {
        this.uidnumber = uidnumber;
    }

    public String getCallrestrict() {
        return callrestrict;
    }

    public void setCallrestrict(String callrestrict) {
        this.callrestrict = callrestrict;
    }

    @Override
    public String toString() {
        return "BindVo{" +
                "regphone='" + regphone + '\'' +
                ", uuidinpartner='" + uuidinpartner + '\'' +
                ", uidtype='" + uidtype + '\'' +
                '}';
    }
}
