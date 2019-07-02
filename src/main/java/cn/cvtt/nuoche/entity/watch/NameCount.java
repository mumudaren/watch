package cn.cvtt.nuoche.entity.watch;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
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


    @Column(name="openid")
    private  String  openid;

    //次数
    @Column(name="client")
    private  String  client;


    //order
    @Column(name="createTime")
    private Date createTime;

    @Transient
    private String num;

    public NameCount() {
    }

    public NameCount(Long id, String name, String openid,  Date createTime,String num,String client) {
        this.id = id;
        this.name = name;
        this.openid = openid;
        this.createTime = createTime;
        this.num = num;
        this.client = client;
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

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return String.format("NameCount [id=%d, name=%s, openid=%s, createTime=%s]", id, name, openid, createTime);
    }
}
