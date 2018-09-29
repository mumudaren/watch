package cn.cvtt.nuoche.util;

import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.web.BaseController;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * @decription SmsValidateUtils
 * <p>短信校验工具</p>
 * @author Yampery
 * @date 2018/3/12 9:58
 */
@Component
public class SmsValidateUtils {
    protected  static final Logger logger = LoggerFactory.getLogger(SmsValidateUtils.class);

    public static  Result sendMsg(String receivers, String code, Map<String,String> map) throws Exception {
        String result = "未知错误";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            String sms_text = map.get("SMSCONTENT").replaceAll("%s", code);
            StringBuilder requestData = new StringBuilder();

            requestData.append("method=").append("cvttSmsSend")
                    .append("&").append("format=").append("1")
                    .append("&").append("charset=").append("utf-8")
                    .append("&").append("partner_id=").append(map.get("SMSPARTNERDID"))
                    .append("&").append("app_id=").append("0")
                    .append("&").append("user_id=").append(map.get("SMSUSERID"))
                    .append("&").append("sms_id=").append(map.get("SMSID"))
                    .append("&").append("receivers=").append(receivers)
                    .append("&").append("sms_text=").append(sms_text)
                    .append("&").append("sign=").append("+");
            HttpGet httpGet = new HttpGet(map.get("SMS_URL") + "?" + requestData);
            logger.info("request  is :"+map.get("SMS_URL") + "?" + requestData+"\n");
            CloseableHttpResponse response = httpclient.execute(httpGet);
            try {
                logger.info("[SmsValidateUtils]CloseableHttpResponse  response.getStatusLine() is :"+response.getStatusLine());
                logger.info("[SmsValidateUtils]CloseableHttpResponse  response.getStatusLine().getStatusCode() is :"+response.getStatusLine().getStatusCode());
                if (response.getStatusLine().getStatusCode() == 200) {
                    response.close();
                    logger.info("[SmsValidateUtils]sendMsg request success");
                    return Result.ok(result);
                }else{
                    logger.info("[SmsValidateUtils]request fail and fail code is :"+response.getStatusLine().getStatusCode()+"\n");
                    result = "请求错误";
                    response.close();
                    return Result.error(result);
                }
            } catch (Exception e){
                result = "请求错误";
                response.close();
                logger.info("request fail and fail message is :"+e.getClass()+","+e.getCause()+"\n");
                return Result.error(result);
            }
        }finally {
            httpclient.close();
        }
    }

    /**
     * 生成随机数验证码
     * @param min
     * @param max
     * @return
     */
    public static String randomCode(int min, int max) {
        int randNum = min + (int)(Math.random() * ((max - min) + 1));
        return String.valueOf(randNum);
    }
}
