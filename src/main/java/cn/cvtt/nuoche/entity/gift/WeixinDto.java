package cn.cvtt.nuoche.entity.gift;



import java.io.Serializable;

public class WeixinDto implements Serializable {
    private static final long serialVersionUID = -1935558318674922380L;
    private String actId;
    private String fxOpenid;
    private String fxNickname;
    private String openid;
    private String nickname;
    private String jwid;
    private String appid;
    private String subscribe;

    public WeixinDto() {
    }

    public String getAppid() {
        return this.appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getActId() {
        return this.actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getFxOpenid() {
        return this.fxOpenid;
    }

    public void setFxOpenid(String fxOpenid) {
        this.fxOpenid = fxOpenid;
    }

    public String getFxNickname() {
        return this.fxNickname;
    }

    public void setFxNickname(String fxNickname) {
        this.fxNickname = fxNickname;
    }

    public String getOpenid() {
        return this.openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getJwid() {
        return this.jwid;
    }

    public void setJwid(String jwid) {
        this.jwid = jwid;
    }

    public String getSubscribe() {
        return this.subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public String toString() {
        return "weixinDto [actId=" + this.actId + ", appid=" + this.appid + ", fxOpenid=" + this.fxOpenid + ", fxNickname=" + this.fxNickname + ", openid=" + this.openid + ", nickname=" + this.nickname + ", jwid=" + this.jwid + ", subscribe=" + this.subscribe + "]";
    }
}
