package cn.cvtt.nuoche.entity.business;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="business_product")
public class BusinessProduct {
    @Id
    @GeneratedValue
    private  Long     id;
    @Column(name="name")
    private  String   name;
    @Column(name="price")
    private  Integer  price;
    @Column(name="type")
    private  Integer  type;
    @Column(name="promotion")
    private  Integer  promotion;
    @Column(name="state")
    private  Integer  state;
    @Column(name="days")
    private  Integer  days;
    @Column(name="leave_message")
    private  Integer  leaveMassgae;
    @Column(name="message")
    private  Integer  message;
    @Column(name="times")
    private  Integer  times;
    @Column(name="oldPrice")
    private  Integer  oldPrice;
    @Column(name="business")
    private  String   business;
    @Column(name="create_time")
    private  Date     createTime;
    @Column(name="update_time")
    private  Date     updateTime;


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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPromotion() {
        return promotion;
    }

    public void setPromotion(Integer promotion) {
        this.promotion = promotion;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getLeaveMassgae() {
        return leaveMassgae;
    }

    public void setLeaveMassgae(Integer leaveMassgae) {
        this.leaveMassgae = leaveMassgae;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Integer getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(Integer oldPrice) {
        this.oldPrice = oldPrice;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
