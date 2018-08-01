package cn.cvtt.nuoche.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @decription WxUserInfo
 * <p>微信用户</p>
 * @author Yampery
 * @date 2018/3/7 14:16
 */
public class WxUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private  Long   id;
    private String openid;
    // 关注状态（1是关注，0是未关注），未关注时获取不到其余信息
    private int subscribe;
    // 用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
    private String subscribeTime;
    // 昵称
    private String nickname;
    // 用户的性别（1男2女0未知）
    private int sex;
    // 用户所在国家
    private String country;
    // 用户所在省份
    private String province;
    // 用户所在城市
    private String city;
    // 用户的语言，简体中文为zh_CN
    private String language;
    // 用户头像
    private String headimgurl;
    private String remark;
    /**
     * 应用添加的数据
     */
    // 用户真实号码
    private String regphone;
    // 用户95号码
    private String uidnumber;
    // 注册时间
    private Date regtime;
    // 过期时间
    private Date expiretime;


    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public int getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(int subscribe) {
        this.subscribe = subscribe;
    }

    public String getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(String subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getRegphone() {
        return regphone;
    }

    public void setRegphone(String regphone) {
        this.regphone = regphone;
    }

    public String getUidnumber() {
        return uidnumber;
    }

    public void setUidnumber(String uidnumber) {
        this.uidnumber = uidnumber;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getRegtime() {
        return regtime;
    }

    public void setRegtime(Date regtime) {
        this.regtime = regtime;
    }

    public Date getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(Date expiretime) {
        this.expiretime = expiretime;
    }



    @Override
    public String toString() {
        return "WxUserInfo{" +
                "openid='" + openid + '\'' +
                ", subscribe=" + subscribe +
                ", subscribeTime='" + subscribeTime + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", language='" + language + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                ", remark='" + remark + '\'' +
                ", regphone='" + regphone + '\'' +
                ", uidnumber='" + uidnumber + '\'' +
                ", regtime=" + regtime +
                ", expiretime=" + expiretime +
                '}';
    }
}
