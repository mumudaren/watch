package cn.cvtt.nuoche.server;


import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/** 关注事件 服务
 *
 * */
@Service
public class SubScribeEventServer {




    public static void main(String[] args) {
        //sendTemplateInfo();

        String ss="{\"openid\":\"oywijv2KGdAke7O7ObezR3Ibft4U\",\"subscri\n" +
                "be\":0,\"subscribeTime\":null,\"nickname\":null,\"sex\":0,\"country\":null,\"province\":null,\"city\":null,\"language\":null,\"headimgurl\":null,\"remark\":null,\"regphone\":\"13508687083\",\"uidnumber\":\"950135901236825\",\"plate\":\"ag2584\",\"no_x\":null,\"code\":\"2713\"}";

        JSONObject  obj=JSONObject.parseObject(ss);
        System.out.println(obj.get("openid"));

    }





}
