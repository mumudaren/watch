package cn.cvtt.nuoche.entity.gift;


import javax.persistence.*;
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
    //优惠券是否使用，0未使用，1已使用
    @Column(name="is_used")
    private Integer isUsed;
    @Transient
    private GiftCoupon giftCoupon;

    public GiftCouponRecord() {
    }

    public GiftCouponRecord(Long id, String senderOpenid, String receiverOpenid, Long pointUsed,Date getTime,GiftCoupon giftCoupon,Integer isUsed) {
        this.id = id;
        this.senderOpenid = senderOpenid;
        this.receiverOpenid = receiverOpenid;
        this.couponId = pointUsed;
        this.getTime = getTime;
        this.giftCoupon = giftCoupon;
        this.isUsed = isUsed;
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

    public GiftCoupon getGiftCoupon() {
        return giftCoupon;
    }

    public void setGiftCoupon(GiftCoupon giftCoupon) {
        this.giftCoupon = giftCoupon;
    }

    public Integer getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Integer isUsed) {
        this.isUsed = isUsed;
    }
//    @Override
//    public String toString() {
//        return String.format("GiftCouponRecord [id=%d, senderOpenid=%s, receiverOpenid=%s, couponId=%s, getTime=%s]", id, senderOpenid, receiverOpenid, couponId, getTime);
//    }
}
