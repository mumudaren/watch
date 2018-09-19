package cn.cvtt.nuoche.entity.gift;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="gift_point")
public class GiftCardNumber {

    @Id
    @GeneratedValue
    private  Long    id;
    @Column(name="openid")
    private  String  openid;
    @Column(name="point_total")
    private  Integer  pointTotal;
    @Column(name="point_used")
    private  Integer  pointUsed;

    public GiftCardNumber() {
    }

    public GiftCardNumber(Long id, String openid, Integer pointTotal, Integer pointUsed) {
        this.id = id;
        this.openid = openid;
        this.pointTotal = pointTotal;
        this.pointUsed = pointUsed;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getPointTotal() {
        return pointTotal;
    }

    public void setPointTotal(Integer pointTotal) {
        this.pointTotal = pointTotal;
    }

    public Integer getPointUsed() {
        return pointUsed;
    }

    public void setPointUsed(Integer pointUsed) {
        this.pointUsed = pointUsed;
    }
}
