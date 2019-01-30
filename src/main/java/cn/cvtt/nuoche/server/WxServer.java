package cn.cvtt.nuoche.server;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.entity.AccessToken;
import cn.cvtt.nuoche.entity.JSAPIToken;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.entity.watch.AddrUrl;
import cn.cvtt.nuoche.reponsitory.IAddrUrlRepository;
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
import java.util.List;
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
    @Autowired
    private IAddrUrlRepository iAddrUrlRepository;
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
            logger.info("requestMap:"+requestMap);
            String fromUserName = requestMap.get("FromUserName");
            // 开发者微信号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");

            String receiveContent = requestMap.get("Content");
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
                List<AddrUrl> dataSourceData=iAddrUrlRepository.findAllByNameLike("%"+receiveContent+"%");
                String respContentBefore="";
                if(dataSourceData.size()>0){
                    for (AddrUrl aDataSourceData : dataSourceData) {
                        respContentBefore = respContentBefore  +"\uD83D\uDC49"+ "<a href=\"" + aDataSourceData.getUrl() + "\">"
                                + receiveContent + "网盘链接"+aDataSourceData.getId()+"点这里</a>"+"\n";
                    }
                }
                //首发网站查询结果
                respContentBefore=respContentBefore+"\uD83D\uDC49"+"<a href=\"http://sfys5555.com/?s="+receiveContent+"\">"+receiveContent+"网盘结果点这里</a>";
                respContentBefore=respContentBefore+"\n"+"\uD83D\uDC49"+"<a href=\"https://m.kankanwu.com/vod-search-wd-"+receiveContent+".html\">"+"非网盘手机在线结果1</a>";
                respContentBefore=respContentBefore+"\n"+"\uD83D\uDC49"+"<a href=\"http://www.highmm.fun/index.php/vod/search.html?wd="+receiveContent+"\">"+"非网盘手机在线结果2</a>";
                respContent =
                         "\n"+ "\u26a0\u26a0"+"有事就留言"+"\u26a0\u26a0"
                        +"\n格式：留言XXX没找到";
                String respContentAfter= "\n"+"\uD83C\uDFC5"+"<a href=\"http://mp.weixin.qq.com/s/3ZexkkCriBgPiQwnSZ3Wdg\">"+"不会保存?教程点这里</a>"
                        +"\n最后，请仔细核对剧名，看到好多小伙伴剧名都搜错了"
                        ;
                String responseFinal=respContentBefore+respContent+respContentAfter;
                //找不到
                if(receiveContent.contains("找不到")||receiveContent.contains("没找到")){
                    responseFinal="留言已记录，即将补上资源~\n"+"如着急看，可先在线追\n"+
                            "<a href=\"http://www.highmm.fun\">"+"在线追点这里</a>";
                }
                //留言
                if(receiveContent.contains("留言")){
                    responseFinal="留言已记录\n"+
                            "<a href=\"http://www.highmm.fun\">"+"在线追点这里</a>";
                }
                //上传
                if(receiveContent.contains("木有会")){
                    AddrUrl upload=new AddrUrl();
                    upload.setName(StringUtils.substringBeforeLast(receiveContent,","));
                    upload.setUrl(StringUtils.substringAfterLast(receiveContent,","));
                    iAddrUrlRepository.saveAndFlush(upload);
                    responseFinal="已成功上传！";
                }
                // 设置文本消息的内容
                textMessage.setContent(responseFinal);
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
                    String content="\uD83D\uDC4D"+"感谢您关注“叫我追剧”官方微信\n" +
                            "欢迎爱追剧的大家一起留言讨论\n" +
                            "公众号里回复剧名会自动搜索\n"+
                            "一定仔细核对剧名，不加特殊符号，例如搜知否可以出现该剧集，搜123知否或《知否》一定搜索不到\n"+
                            "公众号里不定期推送更多的追剧指南\n"+
                            "淘宝、京东等购物返钱福利后续会进行开发敬请期待\n";
                    textMessage.setContent(content);
                    textMessage.setMsgType(MessageUtils.RESP_MESSAGE_TYPE_TEXT);
                    // 将文本消息对象转换成xml
                    /**  先删除 已经存在的用户 再保存**/
//                    if(cusRespository.findByOpenidEquals(fromUserName)!=null) {
//                        logger.info("user is exits,{}",cusRespository.findByOpenidEquals(fromUserName));
//                        BusinessCustomer businessCustomer = cusRespository.findByOpenidEquals(fromUserName);
//                        cusRespository.delete(businessCustomer);
//                    }
//                    BusinessCustomer  cus=new BusinessCustomer();
//                    cus.setOpenid(fromUserName);
//                    cus.setCreateTime(new Date());
//                    cus.setIsAble(1);
//                    cus.setPassword(ApiSignUtils.getMessageMD5("123456"));
//                    loadUserInfo(cus);
//                    cusRespository.save(cus);
                    respXml = MessageUtils.messageToXml(textMessage);
                }
                // 取消关注
                else if (eventType.equals(MessageUtils.EVENT_TYPE_UNSUBSCRIBE)) {
//                    BusinessCustomer  user=cusRespository.findByOpenidEquals(fromUserName);
//                    user.setSubscribe(0);
//                    cusRespository.saveAndFlush(user);
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
//    @Scheduled(cron = "0/60 0/30 * * *  *")
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
//    @Scheduled(cron = "1/60 0/30 * * *  *")
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
