package cn.cvtt.nuoche.util;

import cn.cvtt.nuoche.common.result.Result;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
            CloseableHttpResponse response = httpclient.execute(httpGet);

            try {
                if (response.getStatusLine().getStatusCode() == 200) {
                    // 请求和响应都成功了
                    HttpEntity entity = response.getEntity();// 调用getEntity()方法获取到一个HttpEntity实例
                    // do something useful with the response body
                    // and ensure it is fully consumed
                    String responseXML = EntityUtils.toString(entity, "utf-8");// 用EntityUtils.toString()这个静态方法将HttpEntity转换成字符串,防止服务器返回的数据带有中文,所以在转换的时候将字符集指定成utf-8就可以了
                    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = builderFactory.newDocumentBuilder();
                    Document document = builder.parse(new ByteArrayInputStream(responseXML.getBytes("utf-8")));
                    Element rootElement = document.getDocumentElement();
                    //String retCode = rootElement.getChildNodes().item(1).getChildNodes().item(1).getTextContent();
                    String retMsg = rootElement.getChildNodes().item(1).getChildNodes().item(3).getTextContent();
                    result = retMsg; // "操作成功"是成功标志
                    EntityUtils.consume(entity);
                    return Result.ok(result);
                }else{
                    result = "请求错误";
                    return Result.error(result);
                }
            } finally {
                response.close();
            }
        } finally {
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
