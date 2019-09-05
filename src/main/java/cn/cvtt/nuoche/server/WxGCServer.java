package cn.cvtt.nuoche.server;


import cn.cvtt.nuoche.util.HttpClientUtil;
import cn.cvtt.nuoche.util.JedisUtils;
import cn.cvtt.nuoche.util.JsonUtils;
import cn.cvtt.nuoche.util.MessageUtils;
import cn.cvtt.nuoche.util.requestTemplate.ImageMessage2;
import cn.cvtt.nuoche.util.requestTemplate.TextMessage;
import cn.cvtt.nuoche.util.requestTemplate.ImgMessage;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Service
public class WxGCServer {

     @Resource
     private  JedisUtils jedisUtils;
    @Value("${redis.nuonuo:jsapiToken}")
     private  String JSAPI_TOKEN_KEY;
     private static final Logger logger = LoggerFactory.getLogger(WxGCServer.class);
     String urlFindGC1="http://www.atoolbox.net/api/GetRefuseClassification.php";
    // 维护一个本类的静态变量
    private static WxGCServer switchUtil;
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
        WxGCServer.ACCESS_TOKEN_KEY = ACCESS_TOKEN_KEY;
    }



        /**
         * 微信事件、消息处理器
         * @param requestMap
         * @return
         */
    public String wxHandler(Map<String, String> requestMap) {
        String respXml = null;
        String respContent = "未知的消息类型！";

        try {
            // 调用parseXml方法解析请求消息
            // 发送方帐号
            String fromUserName = requestMap.get("FromUserName");
            // 开发者微信号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            String receiveContent = StringUtils.deleteWhitespace(requestMap.get("Content"));
            // 文本消息
            if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_TEXT)) {
                // 回复文本消息
                TextMessage textMessage = new TextMessage();
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(new Date().getTime());
                textMessage.setMsgType(MessageUtils.RESP_MESSAGE_TYPE_TEXT);
                //根据发送的文字调用接口查询垃圾分类的数据
                String returnResult=getTypeGC(receiveContent);
                textMessage.setContent(returnResult);
                // 将文本消息对象转换成xml
                respXml = MessageUtils.messageToXml(textMessage, TextMessage.class);
            }
//            // 图片消息
//            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_IMAGE)) {
//                respContent = "您发送的是图片消息！";
//            }
//            // 语音消息
            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_VOICE)) {
                // 回复文本消息
                TextMessage textMessage = new TextMessage();
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(new Date().getTime());
                textMessage.setMsgType(MessageUtils.RESP_MESSAGE_TYPE_TEXT);
                //根据发送的文字调用接口查询垃圾分类的数据
                String receiveContentVoice = StringUtils.deleteWhitespace(requestMap.get("Recognition"));
                receiveContentVoice=StringUtils.substringBefore(receiveContentVoice,"。");
                String returnResult=getTypeGC(receiveContentVoice);
                textMessage.setContent(returnResult);

//                respContent = "您发送的是语音消息！";
                textMessage.setContent(returnResult);

                // 将文本消息对象转换成xml
                respXml = MessageUtils.messageToXml(textMessage, TextMessage.class);
            }
//            // 视频消息
//            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_VIDEO)) {
//                respContent = "您发送的是视频消息！";
//            }
//            // 视频消息
//            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_SHORTVIDEO)) {
//                respContent = "您发送的是小视频消息！";
//            }
//            // 地理位置消息
//            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_LOCATION)) {
//                respContent = "您发送的是地理位置消息！";
//            }
//            // 链接消息
//            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_LINK)) {
//                respContent = "您发送的是链接消息！";
//            }
            // 事件推送
            else if (msgType.equals(MessageUtils.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");
                // 关注
                if (eventType.equals(MessageUtils.EVENT_TYPE_SUBSCRIBE)) {
                    ImgMessage imgMessage= new ImgMessage();
                    imgMessage.setToUserName(fromUserName);
                    imgMessage.setFromUserName(toUserName);
                    imgMessage.setCreateTime(new Date().getTime());
//                    String content="\uD83D\uDC4D"+"感谢您关注“垃圾分类小助手”官方微信\n";
//                    textMessage.setContent(content);
                    imgMessage.setMsgType(MessageUtils.RESP_MESSAGE_TYPE_IMAGE);
                    ImageMessage2 img=new ImageMessage2();
                    img.setMediaId("q6LH8PX966C0dtodEkqzm6MaIDEmeq1X06KoGSO4DCUNXyierjYsD1uWRLHl9PuY");
                    imgMessage.setImage(img);
                    respXml = MessageUtils.messageToXml(imgMessage);
                }
                // 取消关注
                else if (eventType.equals(MessageUtils.EVENT_TYPE_UNSUBSCRIBE)) {
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

    public String getTypeGC(String searchWords){
        String returnResult="";
        Map<String, String> map=new HashedMap();
        map.put("key",searchWords);
        try {
            String   sResult = HttpClientUtil.doGet(urlFindGC1, map);
            JSONObject obj = JsonUtils.handlerStringToJson(sResult);
            StringBuilder requestData = new StringBuilder();
            //遍历json数据，提取数据
            Set<Map.Entry<String, Object>> entrySet = obj.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                JSONObject item=(JSONObject)entry.getValue();
                String itemName=item.get("name").toString();
                String itemType=item.get("type").toString();
//                requestData.append("\uD83D\uDC49");
//                requestData.append(itemName+"\uD83D\uDC48");
                requestData.append(itemName);
                requestData.append(",厨余垃圾=湿垃圾，其它垃圾=干垃圾");
                requestData.append("\n"+"\uD83D\uDDD1");
                requestData.append(itemType);
                requestData.append("\uD83D\uDDD1"+"\n\n");
            }
            returnResult=requestData.toString();
        }catch (Exception e){
            returnResult="未查找到"+searchWords+"。";
            logger.info("网络连接失败。");
        }
        logger.info("返回的文字为："+returnResult);
        return returnResult;
    }
}
