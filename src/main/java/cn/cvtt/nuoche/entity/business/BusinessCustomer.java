package cn.cvtt.nuoche.entity.business;


import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name="business_customer")
public class BusinessCustomer {
    @Id
    @GeneratedValue
    private  Long  id;
    @Column(name = "openid")
    private  String  openid;
    @Column(name="phone")
    private  String  phone;
    @Column(nullable = false)
    private  String  password;
    @Column(name = "is_able")
    private  Integer  isAble;
    @Column(name="last_login")
    private  Date     lastLogin;
    @Column(name="create_time")
    private  Date     createTime;
    @Column(name="subscribe")
    private  Integer   subscribe;
    @Column(name="subscribe_time")
    private  Date      subscribeTime;
    @Column(name="sex")
    private  Integer  sex;
    @Column(name="nickname")
    private  String    nickname;
    @Column(name="city")
    private  String    city;
    @Column(name="country")
    private  String  country;
    @Column(name="province")
    private  String  province;
    @Column(name="headimgurl")
    private  String  headimgurl;
    @Transient
    private  String   time;

    public BusinessCustomer() {
    }

    public BusinessCustomer(String openid, String phone, String password, Integer isAble, Date lastLogin, Date createTime) {
        this.openid = openid;
        this.phone = phone;
        this.password = password;
        this.isAble = isAble;
        this.lastLogin = lastLogin;
        this.createTime = createTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

    public Date getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(Date subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public Long getId() {
        return id;
    }


    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIsAble() {
        return isAble;
    }

    public void setIsAble(Integer isAble) {
        this.isAble = isAble;
    }



    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
