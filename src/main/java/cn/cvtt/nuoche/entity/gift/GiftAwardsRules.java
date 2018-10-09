package cn.cvtt.nuoche.entity.gift;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="gift_awards_rules")
public class GiftAwardsRules {

    @Id
    @GeneratedValue
    private  Long    id;
    //活动名称
    @Column(name="name")
    private  String  name;
    //创建者
    @Column(name="createBy")
    private  String  createBy;
    //描述
    @Column(name="description")
    private  String  description;

    @Column(name="create_time")
    private Date createTime;
    //是否是当前活动
    @Column(name="is_able")
    private  Integer  isAble;

    public GiftAwardsRules() {
    }

    public GiftAwardsRules(Long id, String name, String createBy, String description, Date createTime, Integer isAble) {
        this.id = id;
        this.name = name;
        this.createBy = createBy;
        this.description = description;
        this.createTime = createTime;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getIsAble() {
        return isAble;
    }

    public void setIsAble(Integer isAble) {
        this.isAble = isAble;
    }
}
