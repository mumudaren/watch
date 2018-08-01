package cn.cvtt.nuoche.entity;

/**
 * @decription RegisterVo
 * <p>用户注册vo</p>
 * @author Yampery
 * @date 2018/3/12 15:38
 */
public class RegisterVo extends WxUserInfo {

    /**
     * 验证码
     */
    private String code;

    private String newPhone;

    private String extendDays;

    /**
     * 二维码地址
     */
    private String qrcodePath;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getQrcodePath() {
        return qrcodePath;
    }

    public void setQrcodePath(String qrcodePath) {
        this.qrcodePath = qrcodePath;
    }

    public String getNewPhone() {
        return newPhone;
    }

    public void setNewPhone(String newPhone) {
        this.newPhone = newPhone;
    }

    public String getExtendDays() {
        return extendDays;
    }

    public void setExtendDays(String extendDays) {
        this.extendDays = extendDays;
    }


    @Override
    public String toString() {
        return "RegisterVo{" +
                "code='" + code + '\'' +
                ", newPhone='" + newPhone + '\'' +
                ", extendDays='" + extendDays + '\'' +
                ", qrcodePath='" + qrcodePath + '\'' +"testt ===="+super.getSubscribe()+
                '}';
    }
}
