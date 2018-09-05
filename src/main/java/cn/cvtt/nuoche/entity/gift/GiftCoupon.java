package cn.cvtt.nuoche.entity.gift;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Entity
@Table(name="gift_coupon")
public class GiftCoupon {

    @Id
    @GeneratedValue
    private  Long    id;
    @Column(name="name")
    private  String  name;
    @Column(name="amount")
    private  Integer  amount;
    @Column(name="point")
    private  Integer  point;
    @Column(name="creator")
    private  String  creator;
    @Column(name="create_at")
    private  Date  createAt;
    @Column(name="effective_time")
    private  Date  effectiveTime;
    @Column(name="end_time")
    private  Date  endTime;
    @Column(name="is_able")
    private  Integer  isAble;
    public GiftCoupon() {
    }

    public GiftCoupon(Long id, String name, Integer amount, Integer point, String creator, Date createAt, Date effectiveTime, Date endTime, Integer isAble) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.point = point;
        this.creator = creator;
        this.createAt = createAt;
        this.effectiveTime = effectiveTime;
        this.endTime = endTime;
        this.isAble = isAble;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getIsAble() {
        return isAble;
    }

    public void setIsAble(Integer isAble) {
        this.isAble = isAble;
    }
}
