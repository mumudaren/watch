package cn.cvtt.nuoche.entity;

/**
 * @decription JSAPIToken
 * <p>微信访问令牌</p>
 * @author Yampery
 * @date 2018/3/6 14:44
 */
public class JSAPIToken {
    /**
     * 随机许可令牌
     */
    private String JSAPIToken;
    /**
     * 令牌有效时间，单位：s
     */
    private int expiresIn;


    public String getJSAPIToken() {
        return JSAPIToken;
    }

    public void setJSAPIToken(String JSAPIToken) {
        this.JSAPIToken = JSAPIToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "JSAPIToken='" + JSAPIToken + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
