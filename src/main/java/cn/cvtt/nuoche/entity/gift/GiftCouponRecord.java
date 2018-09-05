package cn.cvtt.nuoche.entity.gift;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="gift_point")
public class GiftCouponRecord {

    @Id
    @GeneratedValue
    private  Long    id;
    @Column(name="sender_openid")
    private  String  senderOpenid;
    @Column(name="receiver_openid")
    private   String  receiverOpenid;
    @Column(name="point_used")
    private  Integer  pointUsed;

    public GiftCouponRecord() {
    }

    public GiftCouponRecord(Long id, String senderOpenid, String receiverOpenid, Integer pointUsed) {
        this.id = id;
        this.senderOpenid = senderOpenid;
        this.receiverOpenid = receiverOpenid;
        this.pointUsed = pointUsed;

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

    public Integer getPointUsed() {
        return pointUsed;
    }

    public void setPointUsed(Integer pointUsed) {
        this.pointUsed = pointUsed;
    }
}
