package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.entity.WeChat;
import cn.cvtt.nuoche.server.WxGCServer;
import cn.cvtt.nuoche.util.JsonUtils;
import cn.cvtt.nuoche.util.MessageUtils;
import cn.cvtt.nuoche.util.SignUtil;
import cn.cvtt.nuoche.util.requestTemplate.ImageMessage;
import cn.cvtt.nuoche.util.requestTemplate.ImageMessage2;
import cn.cvtt.nuoche.util.requestTemplate.ImgMessage;
import cn.cvtt.nuoche.util.requestTemplate.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

/**
 * @decription垃圾分类公众号接收地址
 */
@RestController
public class WxGCConnect {

    private static final Logger LOGGER = LoggerFactory.getLogger(WxGCServer.class);
    @Resource private WxGCServer wxGCServer;

    /**
     * 微信接入
     * @param weChat {@link WeChat}
     * @return
     */
    @RequestMapping(value = "/gc", method = RequestMethod.GET)
    public String connect(WeChat weChat) {

        // 接收微信服务器发送get请求到服务器地址URL上的参数
        String signature = weChat.getSignature();
        String timesStamp = weChat.getTimestamp();
        String nonce = weChat.getNonce();
        String enchostr = weChat.getEchostr();
        // 验证签名
        if (SignUtil.validateSigniture(signature, timesStamp, nonce)) {
            LOGGER.info("[wechatGC connect]validateSigniture signature , timesStamp ,nonce is right!"+"\n");
            return enchostr;
        } else {
            LOGGER.warn("[wechatGC connect]Pay Attention!!!!the method is not come from wechat sever!!!!"+"\n");
            return null;
        }
    }

    /**
     * 处理被动事件、消息，接收微信服务器POST数据
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/gc", method = RequestMethod.POST)
    public void eventHandler(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 设置编码
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> requestMap = MessageUtils.parseXml(request);

        LOGGER.info("[eventHandler wechatGC] parseXml from WeChat. remote_ip:{} request_param:{}",
                request.getRemoteHost(), JsonUtils.objectToJson(requestMap));
        String xmlResp = wxGCServer.wxHandler(requestMap);
        LOGGER.info("[eventHandler wechatGC]response:{}", xmlResp+"\n");
        PrintWriter out = response.getWriter();
        out.print(xmlResp);
        out.close();
    }

    @RequestMapping(value = "/gc", method = RequestMethod.PUT)
    public String eventHandler2(@RequestParam Map<String,String> map){
        // 设置编码
        Map<String, String> requestMap = map;
        String xmlResp = wxGCServer.wxHandler(requestMap);
        ImgMessage imgMessage= new ImgMessage();
        imgMessage.setToUserName("123");
        imgMessage.setFromUserName("123");
        imgMessage.setCreateTime(new Date().getTime());
//                    String content="\uD83D\uDC4D"+"感谢您关注“垃圾分类小助手”官方微信\n";
//                    textMessage.setContent(content);
        imgMessage.setMsgType(MessageUtils.RESP_MESSAGE_TYPE_IMAGE);
        ImageMessage2 img=new ImageMessage2();
        img.setMediaId("q6LH8PX966C0dtodEkqzm6MaIDEmeq1X06KoGSO4DCUNXyierjYsD1uWRLHl9PuY");
        imgMessage.setImage(img);
       String respXml = MessageUtils.messageToXml(imgMessage);
        LOGGER.info("[eventHandler wechatGC]response:{}", xmlResp+"\n");
        return respXml;
    }


}
