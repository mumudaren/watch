package cn.cvtt.nuoche.entity;

/**
 * @decription AccessToken
 * <p>微信访问令牌</p>
 * @author Yampery
 * @date 2018/3/6 14:44
 */
public class AccessToken {
    /**
     * 随机许可令牌
     */
    private String accessToken;
    /**
     * 令牌有效时间，单位：s
     */
    private int expiresIn;

    private String createTime;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
