package cn.cvtt.nuoche.entity.gift;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="gift_coupon_qrcode")
public class GiftCouponQrcode {

    @Id
    @GeneratedValue
    private  Long    id;
    @Column(name="creator_openid")
    private  String  creatorOpenid;
    @Column(name="coupon_id")
    private  Long  couponId;
    @Column(name="create_time")
    private Date createTime;
    @Column(name="qrcode")
    private   String  qrcode;
    @Column(name="qrcode_url")
    private   String  qrcodeUrl;


    public GiftCouponQrcode() {
    }

    public GiftCouponQrcode(Long id, String creatorOpenid, Long couponId, Date createTime,
                            String qrcode, String qrcodeUrl) {
        this.id = id;
        this.creatorOpenid = creatorOpenid;
        this.couponId = couponId;
        this.createTime = createTime;
        this.qrcode = qrcode;
        this.qrcodeUrl = qrcodeUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatorOpenid() {
        return creatorOpenid;
    }

    public void setCreatorOpenid(String creatorOpenid) {
        this.creatorOpenid = creatorOpenid;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }
}
