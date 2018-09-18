package cn.cvtt.nuoche.entity.gift;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="gift_point_record")
public class GiftPointRecord {

    @Id
    @GeneratedValue
    private  Long    id;
    @Column(name="openid")
    private  String  openid;
    @Column(name="resource")
    private  Integer  resource;
    @Column(name="change_point")
    private   Integer  changePoint;
    @Column(name="update_time")
    private Date updateTime;

    public GiftPointRecord() {
    }

    public GiftPointRecord(Long id, String openid, Integer resource, Integer changePoint, Date updateTime) {
        this.id = id;
        this.openid = openid;
        this.resource = resource;
        this.changePoint = changePoint;
        this.updateTime = updateTime;

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

    public Integer getResource() {
        return resource;
    }

    public void setResource(Integer resource) {
        this.resource = resource;
    }

    public Integer getChangePoint() {
        return changePoint;
    }

    public void setChangePoint(Integer changePoint) {
        this.changePoint = changePoint;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
