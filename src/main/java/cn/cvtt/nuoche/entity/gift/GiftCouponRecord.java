package cn.cvtt.nuoche.entity.gift;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="gift_coupon_record")
public class GiftCouponRecord {

    @Id
    @GeneratedValue
    private  Long    id;
    @Column(name="sender_openid")
    private  String  senderOpenid;
    @Column(name="receiver_openid")
    private   String  receiverOpenid;
    @Column(name="coupon_id")
    private  Long  couponId;
    @Column(name="get_time")
    private Date getTime;

    public GiftCouponRecord() {
    }

    public GiftCouponRecord(Long id, String senderOpenid, String receiverOpenid, Long pointUsed,Date getTime) {
        this.id = id;
        this.senderOpenid = senderOpenid;
        this.receiverOpenid = receiverOpenid;
        this.couponId = pointUsed;
        this.getTime = getTime;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderOpenid() {
        return senderOpenid;
    }

    public void setSenderOpenid(String senderOpenid) {
        this.senderOpenid = senderOpenid;
    }

    public String getReceiverOpenid() {
        return receiverOpenid;
    }

    public void setReceiverOpenid(String receiverOpenid) {
        this.receiverOpenid = receiverOpenid;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Date getGetTime() {
        return getTime;
    }

    public void setGetTime(Date getTime) {
        this.getTime = getTime;
    }
}
