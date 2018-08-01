package cn.cvtt.nuoche.util;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.entity.*;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @decription WxUtils
 * <p>微信工具</p>
 * @author Yampery
 * @date 2018/3/7 15:16
 */
@SuppressWarnings("all")
public class WxUtils {

    private static final Logger log = LoggerFactory.getLogger(WxUtils.class);
    private static final String URL_OPENID = "https://api.weixin.qq.com/sns/oauth2/access_token";
    private static final String URL_OPENIDLIST = "https://api.weixin.qq.com/cgi-bin/user/get";
    private static final String URL_ACCESSTOKEN = "https://api.weixin.qq.com/cgi-bin/token";
    private static final String URL_USERINFO = "https://api.weixin.qq.com/cgi-bin/user/info";
    /**
     * 通过网页授权获取用户信息url
     */
    private static final String URL_SNS = "https://api.weixin.qq.com/sns/userinfo";
    /**
     * 网页授权凭证获取url
     */
    private static final String URL_OUATH2 = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /**
     * 获取openId
     * @param code
     * @return
     */
    public static Result getOpenid(String code) {

        //获取用户openId
        String openId = "";

        //如果code为空则（请自己实现判断，这里是简写）
        if(code == null){
            return Result.error();
        }

        Map<String, String> map = new HashMap<>();
        map.put("appid", Constant.APP_ID);
        map.put("secret", Constant.APP_SECRET);
        map.put("code", code);
        map.put("grant_type", "authorization_code");
        String sResult = HttpClientUtil.doGet(URL_OPENID, map);
        if (StringUtils.equals("504", sResult)) {
            return Result.error("连接失败");
        }

        JSONObject jResult = JSONObject.parseObject(sResult);
        if(jResult.containsKey("openid")){
            openId = jResult.getString("openid");
        }
        if (StringUtils.isBlank(openId)) {
            return Result.error("获取失败");
        }
        System.out.println(openId);
        return Result.ok(openId);
    }

    /**
     * 获取列表
     * @param nextOpenid
     * @param access_token
     * @return
     */
    public static Result getOpenIdList(String nextOpenid, String access_token) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", Constant.APP_ID);
        map.put("secret", Constant.APP_SECRET);
        String sResult = HttpClientUtil.doGet(URL_OPENIDLIST);
        return Result.ok(sResult);
    }

    /**
     * 获取access_token
     * @return
     */
    public static AccessToken getAccessToken() {
        Map<String, String> map = new HashMap<>();
        map.put("appid", Constant.APP_ID);
        map.put("secret", Constant.APP_SECRET);
        map.put("grant_type", "client_credential");
        String sResult = HttpClientUtil.doGet(URL_ACCESSTOKEN, map);
        
        if (StringUtils.equals("504", sResult)) {
            return null;
        }
        JSONObject jObj = JSONObject.parseObject(sResult);
        AccessToken at = null;
        if (null != jObj) {
            try {
                at = new AccessToken();
                at.setAccessToken(jObj.getString("access_token"));
                at.setExpiresIn(jObj.getIntValue("expires_in"));
            } catch (Exception e) {
                log.error("获取access_token失败");
                at = null;
            }
        }

        return at;
    }

    /**
     * 获取微信用户
     * @param accessToken
     * @param openid
     * @return
     */
    public static WxUserInfo getUserInfo(String accessToken, String openid) {
        WxUserInfo user = null;
        Map<String, String> map = new HashMap<>();
        map.put("access_token", accessToken);
        map.put("openid", openid);
        // 获取用户信息
        String sResult = HttpClientUtil.doGet(URL_USERINFO);
        JSONObject jsonObject = JSONObject.parseObject(sResult);

        if (null != jsonObject) {
            try {
                user = new WxUserInfo();
                user.setOpenid(jsonObject.getString("openid"));
                user.setSubscribe(jsonObject.getIntValue("subscribe"));
                user.setSubscribeTime(jsonObject.getString("subscribe_time"));
                user.setNickname(jsonObject.getString("nickname"));
                user.setSex(jsonObject.getIntValue("sex"));
                user.setCountry(jsonObject.getString("country"));
                user.setProvince(jsonObject.getString("province"));
                user.setCity(jsonObject.getString("city"));
                user.setLanguage(jsonObject.getString("language"));
                user.setHeadimgurl(jsonObject.getString("headimgurl"));
            } catch (Exception e) {
                if (0 == user.getSubscribe()) {
                    log.error("用户{}已取消关注", user.getOpenid());
                } else {
                    int errorCode = jsonObject.getIntValue("errcode");
                    String errorMsg = jsonObject.getString("errmsg");
                    log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
                }
            }
        }
        return user;
    }

    /**
     * 获取网页收钱凭证
     * @param appId
     * @param appSecret
     * @param code
     * @return
     */
    public static WeixinOauth2Token getOauth2AccessToken(String code) {
        WeixinOauth2Token wat = null;
        Map<String, String> map = new HashMap<>();
        map.put("appid", Constant.APP_ID);
        map.put("secret", Constant.APP_SECRET);
        map.put("code", code);
        map.put("grant_type", "authorization_code");
        // 获取网页授权凭证
        String result = HttpClientUtil.doGet(URL_OUATH2, map);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (null != jsonObject) {
            try {
                wat = new WeixinOauth2Token();
                wat.setAccessToken(jsonObject.getString("access_token"));
                wat.setExpiresIn(jsonObject.getIntValue("expires_in"));
                wat.setRefreshToken(jsonObject.getString("refresh_token"));
                wat.setOpenId(jsonObject.getString("openid"));
                wat.setScope(jsonObject.getString("scope"));
            } catch (Exception e) {
                wat = null;
                int errorCode = jsonObject.getIntValue("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取网页授权凭证失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return wat;
    }

    /**
     * 通过网页授权获取用户信息
     * @param accessToken
     * @param openId
     * @return
     */
    public static SNSUserInfo getSNSUserInfo(String accessToken, String openId) {
        SNSUserInfo snsUserInfo = null;
        // 拼接请求地址
        Map<String, String> map = new HashMap<>();
        map.put("access_token", accessToken);
        map.put("openid", openId);
        // 通过网页授权获取用户信息
        String result = HttpClientUtil.doGet(URL_SNS, map);
        JSONObject jsonObject = JSONObject.parseObject(result);

        if (null != jsonObject) {
            try {
                snsUserInfo = new SNSUserInfo();
                // 用户的标识
                snsUserInfo.setOpenid(jsonObject.getString("openid"));
                // 昵称
                snsUserInfo.setNickname(jsonObject.getString("nickname"));
                // 性别（1是男性，2是女性，0是未知）
                snsUserInfo.setSex(jsonObject.getIntValue("sex"));
                // 用户所在国家
                snsUserInfo.setCountry(jsonObject.getString("country"));
                // 用户所在省份
                snsUserInfo.setProvince(jsonObject.getString("province"));
                // 用户所在城市
                snsUserInfo.setCity(jsonObject.getString("city"));
                // 用户头像
                snsUserInfo.setHeadimgurl(jsonObject.getString("headimgurl"));
                // 用户特权信息
                snsUserInfo.setPrivilegeList(
                        JsonUtils.jsonToList(jsonObject.getJSONArray("privilege").toJSONString(), String.class));
            } catch (Exception e) {
                snsUserInfo = null;
                int errorCode = jsonObject.getIntValue("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return snsUserInfo;
    }

    /**
     *   将微信用户信息和nuonuo用户平台的信息整合返回
     *   @param   SNSUserInfo  微信用户
     *   @param   WxUserInfo   nuonuo 用户
     * */
    public  static RegisterVo convertEntityVo(SNSUserInfo sn, BusinessCustomer wx){
        RegisterVo  vo=new RegisterVo();
        vo.setOpenid(sn.getOpenid());
        vo.setSubscribe(wx.getSubscribe());
        vo.setSubscribeTime(DateUtils.format(wx.getSubscribeTime()));
        vo.setCity(sn.getCity());
        vo.setCountry(sn.getCountry());
        vo.setHeadimgurl(sn.getHeadimgurl());
        vo.setNickname(sn.getNickname());
        vo.setProvince(sn.getProvince());
        vo.setSex(sn.getSex());
        vo.setRegphone(wx.getPhone());
        return  vo;
    }




}
