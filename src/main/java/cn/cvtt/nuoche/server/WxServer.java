package cn.cvtt.nuoche.server;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.entity.AccessToken;
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
     private JedisUtils jedisUtils;
     @Value("${redis.nuonuo.token}")
     private String ACCESS_TOKEN_KEY;
     private static final Logger logger = LoggerFactory.getLogger(WxServer.class);
     @Autowired
     private IBusinessCusRepository cusRespository;



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
            if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_TEXT)) {
                // 回复文本消息
                TextMessage textMessage = new TextMessage();
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(new Date().getTime());
                textMessage.setMsgType(MessageUtils.RESP_MESSAGE_TYPE_TEXT);
                respContent = "您发送的是文本消息！";
                // 设置文本消息的内容
                textMessage.setContent(respContent);
                // 将文本消息对象转换成xml
                respXml = MessageUtils.messageToXml(textMessage, TextMessage.class);
            }
            // 图片消息
            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_IMAGE)) {
                respContent = "您发送的是图片消息！";
            }
            // 语音消息
            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_VOICE)) {
                respContent = "您发送的是语音消息！";
            }
            // 视频消息
            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_VIDEO)) {
                respContent = "您发送的是视频消息！";
            }
            // 视频消息
            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_SHORTVIDEO)) {
                respContent = "您发送的是小视频消息！";
            }
            // 地理位置消息
            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_LOCATION)) {
                respContent = "您发送的是地理位置消息！";
            }
            // 链接消息
            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_LINK)) {
                respContent = "您发送的是链接消息！";
            }
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
                    textMessage.setContent("感谢您的关注!");
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
    public String getAccessToken() {
        try {
            return jedisUtils.get(ACCESS_TOKEN_KEY, "");
        } catch (Exception e) {
            logger.error("缓存池获取access_tocken失败 exception:{}", e.getMessage());
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
     * 从任意时刻开始每2小时执行一次
     */
    @Scheduled(cron = "0 0 */1 * * *")
    public void AccessTokenTask() {

        AccessToken at = WxUtils.getAccessToken();
        logger.info("定时器中的access_token: {}", at.toString());
        while (true) {
            try {
                if (null != at) {
                    // 调用存储到数据库
                    at = WxUtils.getAccessToken();
                    at.setCreateTime(DateUtils.format(new Date()));
                    // 将access_token更新到redis
                    jedisUtils.set(ACCESS_TOKEN_KEY, at.getAccessToken());
                    logger.info("获取access_token成功，有效时长{}秒 token:{}", at.getExpiresIn(), at.getAccessToken());
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

}
