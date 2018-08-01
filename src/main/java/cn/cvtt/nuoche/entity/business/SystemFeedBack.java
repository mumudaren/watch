package cn.cvtt.nuoche.entity.business;

import cn.cvtt.nuoche.entity.Entitys;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="system_feedback")
public class SystemFeedBack extends Entitys{
    @Id
    @GeneratedValue
    private  Long     id;
    @Column(name="phone" )
    private  String   phone;
    @Column(name="openid")
    private  String   openid;
    @Column(name="type",nullable = false)
    private  String   type;
    @Column(name="content",nullable = false)
    private  String   content;
    @Column(name="state",nullable = false)
    private  Integer  state;
    @Column(name="handler" )
    private  String   handler;
    @Column(name="update_time",nullable = false)
    private  Date     lastUpdateTime;
    @Column(name="createTime",nullable = false)
    private  Date     createTime;

    public SystemFeedBack() {
    }

    public SystemFeedBack(Long id, String phone, String openid, String type, String content, Integer state, String handler, Date lastUpdateTime, Date createTime) {
        this.id = id;
        this.phone = phone;
        this.openid = openid;
        this.type = type;
        this.content = content;
        this.state = state;
        this.handler = handler;
        this.lastUpdateTime = lastUpdateTime;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
