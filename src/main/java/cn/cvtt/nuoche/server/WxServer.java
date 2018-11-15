package cn.cvtt.nuoche.server;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.entity.AccessToken;
import cn.cvtt.nuoche.entity.JSAPIToken;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.reponsitory.IBusinessCusRepository;
import cn.cvtt.nuoche.util.*;
import cn.cvtt.nuoche.util.requestTemplate.TextMessage;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @decription WxServer
 * <p></p>
 * @author Yampery
 * @date 2018/3/8 11:55
 */
@Service
public class WxServer {

     @Autowired
     private SubScribeEventServer SubService;
     @Resource
     private  JedisUtils jedisUtils;
    @Value("${redis.nuonuo:jsapiToken}")
     private  String JSAPI_TOKEN_KEY;
     private static final Logger logger = LoggerFactory.getLogger(WxServer.class);
     @Autowired
     private IBusinessCusRepository cusRespository;
    //     @Value("${redis.nuonuo.token}")
    //     private   String ACCESS_TOKEN_KEY;
    // 维护一个本类的静态变量
    private static WxServer switchUtil;
    // 初始化的时候，将本类中的sysConfigManager赋值给静态的本类变量
    @PostConstruct
    public void init() {
        switchUtil = this;
        switchUtil.jedisUtils = this.jedisUtils;
        switchUtil.JSAPI_TOKEN_KEY=this.JSAPI_TOKEN_KEY;
        switchUtil.jedisUtils=this.jedisUtils;
    }
    //accesstoken,key

    private static String ACCESS_TOKEN_KEY;

    public static String getAccessTokenKey() {
        return ACCESS_TOKEN_KEY;
    }
    @Value("${redis.nuonuo.token}")
    public void setAccessTokenKey(String ACCESS_TOKEN_KEY) {
        WxServer.ACCESS_TOKEN_KEY = ACCESS_TOKEN_KEY;
    }



        /**
         * 微信事件、消息处理器
         * @param requestMap
         * @return
         */
    public String wxHandler(Map<String, String> requestMap) {
        // xml格式的消息数据
        String respXml = null;
        // 默认返回的文本消息内容
        String respContent = "未知的消息类型！";
        try {
            // 调用parseXml方法解析请求消息
            // 发送方帐号

            String fromUserName = requestMap.get("FromUserName");
            // 开发者微信号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            logger.info("args:"+fromUserName+","+toUserName+","+msgType);
            // 文本消息
            if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_TEXT)
                    ||msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_IMAGE)
                    ||msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_VOICE)
                    ||msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_VIDEO)
                    ||msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_SHORTVIDEO)
                    ||msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_LOCATION)
                    ||msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_LINK)) {
                // 回复文本消息
                TextMessage textMessage = new TextMessage();
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(new Date().getTime());
                textMessage.setMsgType(MessageUtils.RESP_MESSAGE_TYPE_TEXT);
                respContent = "海牛助手关注每一位用户的咨询，您的咨询问题对我们非常重要，已转至专员进行处理，稍后会与您回复，请您耐心等候。";
                // 设置文本消息的内容
                textMessage.setContent(respContent);
                // 将文本消息对象转换成xml
                respXml = MessageUtils.messageToXml(textMessage, TextMessage.class);
            }
//            // 图片消息
//            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_IMAGE)) {
//                //respContent = "您发送的是图片消息！";
//            }
//            // 语音消息
//            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_VOICE)) {
//                //respContent = "您发送的是语音消息！";
//            }
//            // 视频消息
//            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_VIDEO)) {
//                //respContent = "您发送的是视频消息！";
//            }
//            // 视频消息
//            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_SHORTVIDEO)) {
//                //respContent = "您发送的是小视频消息！";
//            }
//            // 地理位置消息
//            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_LOCATION)) {
//                //respContent = "您发送的是地理位置消息！";
//            }
//            // 链接消息
//            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_LINK)) {
//                //respContent = "您发送的是链接消息！";
//            }
            // 事件推送
            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");
                // 关注
                if (eventType.equals(MessageUtils.EVENT_TYPE_SUBSCRIBE)) {
                    logger.info("my subScript>>>>:{} "+MessageUtils.EVENT_TYPE_SUBSCRIBE);
                    TextMessage  textMessage= new TextMessage();
                    textMessage.setToUserName(fromUserName);
                    textMessage.setFromUserName(toUserName);
                    textMessage.setCreateTime(new Date().getTime());
                    String content="感谢您关注“海牛助手”官方微信\n" +
                            "海牛助手是您真实联系方式的忠诚保护伞\n" +
                            "代替真实号码实现通信功能\n" +
                            "避免号码泄露防止各种骚扰\n" +
                           "\uD83D\uDC4D"+ "<a href=\"http://i.95013.com/wechatsite/helpcenter/help\">如何使用海牛助手？</a>\n" +
                            "\uD83C\uDFDD"+"<a href=\"http://i.95013.com/hainiu.html\">生活中哪些情况需要海牛助手？</a>\n" +
                           "\uD83C\uDFC5"+ "<a href=\"http://i.95013.com/oauth/regex/buy_zhizun.html\">申请海牛助手号码</a>";
                    textMessage.setContent(content);
                    textMessage.setMsgType(MessageUtils.RESP_MESSAGE_TYPE_TEXT);
                    // 将文本消息对象转换成xml
                    /**  先删除 已经存在的用户 再保存**/
                    if(cusRespository.findByOpenidEquals(fromUserName)!=null) {
                        logger.info("user is exits,{}",cusRespository.findByOpenidEquals(fromUserName));
                        BusinessCustomer businessCustomer = cusRespository.findByOpenidEquals(fromUserName);
                        cusRespository.delete(businessCustomer);
                    }
                    BusinessCustomer  cus=new BusinessCustomer();
                    cus.setOpenid(fromUserName);
                    cus.setCreateTime(new Date());
                    cus.setIsAble(1);
                    cus.setPassword(ApiSignUtils.getMessageMD5("123456"));
                    loadUserInfo(cus);
                    cusRespository.save(cus);
                    respXml = MessageUtils.messageToXml(textMessage);
                }
                // 取消关注
                else if (eventType.equals(MessageUtils.EVENT_TYPE_UNSUBSCRIBE)) {
                    BusinessCustomer  user=cusRespository.findByOpenidEquals(fromUserName);
                    user.setSubscribe(0);
                    cusRespository.saveAndFlush(user);
                    logger.info("my subScript>>>>:{} "+MessageUtils.EVENT_TYPE_UNSUBSCRIBE);
                }
                // 扫描带参数二维码
                else if (eventType.equals(MessageUtils.EVENT_TYPE_SCAN)) {
                    // TODO 处理扫描带参数二维码事件
                }
                // 上报地理位置
                else if (eventType.equals(MessageUtils.EVENT_TYPE_LOCATION)) {
                    // TODO 处理上报地理位置事件
                }
                // 自定义菜单
                else if (eventType.equals(MessageUtils.EVENT_TYPE_CLICK)) {
                    // TODO 处理菜单点击事件
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respXml;
    }


    /**
     * 从redis中获取access_token
     * @return
     */
    public static String getAccessToken() {

        try {
            logger.error("ACCESS_TOKEN_KEY:{}", ACCESS_TOKEN_KEY);
            return switchUtil.jedisUtils.get(ACCESS_TOKEN_KEY, "");
        } catch (Exception e) {
            logger.error("缓存池获取access_tocken失败 exception:{}", e.getMessage());
            throw e;
        }
    }
    /**
     * 从redis中获取getJSAPIToken
     * @return
     */
    public String getJSAPIToken() {
        try {
            return jedisUtils.get(JSAPI_TOKEN_KEY, "");
        } catch (Exception e) {
            logger.error("[getJSAPIToken]getJSAPIToken fail, exception:{}", e.getMessage());
            throw e;
        }
    }

    public  void  loadUserInfo(BusinessCustomer  user){
        String  token=getAccessToken();
        if(StringUtils.isEmpty(token)){
            AccessToken at = WxUtils.getAccessToken();
            token=at.getAccessToken();
        }
        String  url= Constant.userInfoUrl;
        Map<String,String>  map=new HashMap<>();
        map.put("access_token",token);
        map.put("openid",user.getOpenid());
        map.put("lang","zh_CN");
        String  result=HttpClientUtil.doGet(url,map);
        logger.info("user Info "+result);
        JSONObject obj=JSONObject.parseObject(result);
        user.setNickname(obj.getString("nickname"));
        user.setHeadimgurl(obj.getString("headimgurl"));
        user.setSex(obj.getIntValue("sex"));
        user.setSubscribe(obj.getIntValue("subscribe"));
        user.setSubscribeTime(new Date());
    }
    /**
     * 定时器维护access_token
     * 从0秒0分钟开始每半时执行一次
     */
    @Scheduled(cron = "0/60 0/30 * * *  *")
    public static void AccessTokenTask() {
        //获取AccessToken
        AccessToken at = WxUtils.getAccessToken();
        //进入死循环
        while (true) {
            try {
                if (null != at) {
                    // 调用存储到数据库
                    logger.info("[AccessTokenTask]access_token is: {}", at.toString());
                    at = WxUtils.getAccessToken();
                    at.setCreateTime(DateUtils.format(new Date()));
                    // 将access_token更新到redis
                    switchUtil.jedisUtils.set(ACCESS_TOKEN_KEY, at.getAccessToken());
                    logger.info("[AccessTokenTask]get access_token success，有效时长{}秒 token:{}", at.getExpiresIn(), at.getAccessToken());
                    //引起函数的强制结束
                    return;

                } else {
                    // 如果access_token为null，60秒后再获取
                    Thread.sleep(60 * 1000);
                }
            } catch(Exception e){
                try {
                    Thread.sleep(60 * 1000);
                } catch (Exception e1) {
                    logger.error("{}", e1);
                }
                logger.error("{}", e.getMessage().replaceAll("\n", "|"));
            }
        }
    }
    /**
     * 定时器维护JSAPI_token
     * 从1秒0分钟开始每半小时执行一次
     */
    @Scheduled(cron = "1/60 0/30 * * *  *")
    public static void JSAPITokenTask()  {
        //函数调用次数
        String  accessToken=getAccessToken();
        for(int totalTimes=0;totalTimes<300;totalTimes++){
            //如果获取的accessToken为空，则执行上面的accesstoken定时器。
            if(StringUtils.isEmpty(accessToken)){
                logger.info("[JSAPITokenTask]get access_token fail，so will do AccessTokenTask.");
                //调用获取AccessTokenTask后for循环函数
                AccessTokenTask();
            }else{
                //成功获取到accessToken后，获取jsapi的token
                JSAPIToken atJSAPI = WxUtils.getJSAPIToken();
                if(atJSAPI.getJSAPIToken()!=null){
                    try{
                        // 存储到redis数据库
                        String jsapiKey= atJSAPI.getJSAPIToken();
                        logger.info("atJSAPI is:"+atJSAPI.toString());
                        switchUtil.jedisUtils.set(switchUtil.JSAPI_TOKEN_KEY,jsapiKey);
                        logger.info("[JSAPITokenTask]set JSAPIToken success，validTime{}'s , token is:{}", atJSAPI.getExpiresIn(), atJSAPI.getJSAPIToken());
                        break;
                    }catch (Exception e){
                        //用过期的accessToken获取JSAPI的token会获取不到。或因为未知原因获取不到JSAPI的token需要重新获取accessToken。
                        AccessTokenTask();
                    }
                }else{
                    AccessTokenTask();
                }
            }
            logger.error("[JSAPITokenTask]access JSAPIToken："+totalTimes+" times");
        }
    }
}
