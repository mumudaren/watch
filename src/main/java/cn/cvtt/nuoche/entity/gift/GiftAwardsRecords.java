package cn.cvtt.nuoche.entity.gift;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="gift_awards_records")
public class GiftAwardsRecords {

    @Id
    @GeneratedValue
    private  Long    id;
    //奖品id
    @Column(name="awards_id")
    private  Long  awardsId;
    //奖品名称
    @Column(name="awards_name")
    private  String  awardsName;

    //中奖者openid
    @Column(name="openid")
    private  String  openid;

    //中奖者昵称
    @Column(name="nickname")
    private  String  nickname;

    //中奖者手机号
    @Column(name="phone")
    private  String  phone;

    @Column(name="get_time")
    private Date getTime;


    public GiftAwardsRecords() {
    }

    public GiftAwardsRecords(Long id, Long awardsId, String awardsName,
                             String openid,String nickname,
                             String phone,Date getTime) {
        this.id = id;
        this.awardsId = awardsId;
        this.awardsName = awardsName;
        this.openid = openid;
        this.nickname = nickname;
        this.phone = phone;
        this.getTime = getTime;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAwardsId() {
        return awardsId;
    }

    public void setAwardsId(Long awardsId) {
        this.awardsId = awardsId;
    }

    public String getAwardsName() {
        return awardsName;
    }

    public void setAwardsName(String awardsName) {
        this.awardsName = awardsName;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getGetTime() {
        return getTime;
    }

    public void setGetTime(Date getTime) {
        this.getTime = getTime;
    }
}
