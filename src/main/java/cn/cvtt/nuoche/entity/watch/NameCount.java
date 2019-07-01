package cn.cvtt.nuoche.entity.watch;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="name_count")
public class NameCount {

    @Id
    @GeneratedValue
    private  Long    id;

    //name
    @Column(name="name")
    private  String  name ;

    //次数
    @Column(name="openid")
    private  String  openid;


    //order
    @Column(name="createTime")
    private Date createTime;

    public NameCount() {
    }

    public NameCount(Long id, String name, String openid,  Date createTime) {
        this.id = id;
        this.name = name;
        this.openid = openid;
        this.createTime = createTime;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
        @Override
    public String toString() {
        return String.format("NameCount [id=%d, name=%s, openid=%s, createTime=%s]", id, name, openid, createTime);
    }
}
