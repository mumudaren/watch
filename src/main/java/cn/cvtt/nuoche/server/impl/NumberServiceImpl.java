package cn.cvtt.nuoche.server.impl;

import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.entity.business.BindVo;
import cn.cvtt.nuoche.facade.ISystemParamInterface;
import cn.cvtt.nuoche.server.NumberService;
import cn.cvtt.nuoche.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @decription NumberServiceImpl
 * <p></p>
 * @author Yampery
 * @date 2018/3/12 9:17
 */
@Service
public class NumberServiceImpl implements NumberService {

    @Autowired
    ConfigUtil  util;
    @Autowired
    private ISystemParamInterface systemParamInterface;
    private   Map<String,String>  args=null;

    private static final Logger logger = LoggerFactory.getLogger(NumberService.class);

    @Override
    public Result bind(BindVo param) throws IOException {
        args=JsonUtils.handlerJson(systemParamInterface.getSystemConfigByArgs(1,util.getBusinessKey()));
        // 请求服务接口
        String url = args.get("SAFENUMBER_APP_DOMAIN") + "/safenumberservicessm/api/manage/dataManage";

        // 服务接口请求参数
        Map<String, String> map = new HashMap<>();
        map.put("msgtype", "binding_Relation");
        map.put("unitID", args.get("SAFENUMBER_APP_UNITID") );
        map.put("prtms", param.getRegphone());
        map.put("smbms", param.getUidnumber() == null ? "" : param.getUidnumber());
        map.put("uuidinpartner", "");
        map.put("validitytime", param.getExpiretime() == null ? "" : param.getExpiretime());
        map.put("uidType", "0");
        map.put("callrestrict", param.getCallrestrict() == null ? "" : param.getCallrestrict());
        map.put("appkey", args.get("SAFENUMBER_APP_KEY"));
        map.put("msgid", "1");
        map.put("service", "SafeNumber");
        map.put("ver", "2.0");
        map.put("ts", DateUtils.format(new Date()));
        map.put("opuser", args.get("SAFENUMBER_APP_OPUSER"));
        map.put("opmodule", args.get("SAFENUMBER_APP_OPMODULER"));
        map.put("sid", ApiSignUtils.signTopRequest(map, args.get("SAFENUMBER_APP_SECRET"), "MD5"));
        StringBuilder sb = new StringBuilder(url + "?");
        for (Map.Entry<String, String> e : map.entrySet()) {
            sb.append(e.getKey() + "=" + e.getValue()).append("&");
        }
        logger.info("请求绑定接口:{}", sb);
        String result = HttpClientUtil.doGet(url, map);
        logger.info("binding_response:{}", result);
        if(result.equals("504")) {
            return Result.error("http服务连接失败");
        }
        if (result.contains("error")) {
            return Result.error("绑定失败");
        }
        return Result.ok(result);
    }

    @Override
    public Result unbind(String uidnumber) throws IOException {
        args=JsonUtils.handlerJson(systemParamInterface.getSystemConfigByArgs(1,util.getBusinessKey()));
        // 请求服务接口
        String url = args.get("SAFENUMBER_APP_DOMAIN") + "/safenumberservicessm/api/manage/dataManage";

        // 服务接口请求参数
        Map<String, String> map = new HashMap<>();
        map.put("msgtype", "remove_Relation");
        map.put("unitID",args.get("SAFENUMBER_APP_UNITID"));
        map.put("smbms", uidnumber);
        map.put("uuidinpartner", "");
        map.put("appkey", args.get("SAFENUMBER_APP_KEY"));
        map.put("uidType", "0");
        map.put("msgid", "1");
        map.put("service", "SafeNumber");
        map.put("ver", "2.0");
        map.put("ts", DateUtils.format(new Date()));
        map.put("opuser", args.get("SAFENUMBER_APP_OPUSER"));
        map.put("opmodule", args.get("SAFENUMBER_APP_OPMODULER"));
        map.put("sid", ApiSignUtils.signTopRequest(map, args.get("SAFENUMBER_APP_SECRET"), "MD5"));
        StringBuilder sb = new StringBuilder(url + "?");
        for (Map.Entry<String, String> e : map.entrySet()) {
            sb.append(e.getKey() + "=" + e.getValue()).append("&");
        }
        logger.info("请求解绑接口:{}", sb);
        String result = HttpClientUtil.doGet(url, map);
        logger.info("binding_response:{}", result);
        if(result.equals("504"))
            return Result.error("http服务连接失败");
        if (result.contains("error"))
            return Result.error("解绑失败");
        return Result.ok();
    }

    @Override
    public Result extend(BindVo param) throws IOException {
        args=JsonUtils.handlerJson(systemParamInterface.getSystemConfigByArgs(1,util.getBusinessKey()));
        // 请求服务接口
        String url = args.get("SAFENUMBER_APP_DOMAIN") + "/safenumberservicessm/api/manage/extendRelation";
        // 服务接口请求参数
        Map<String, String> map = new HashMap<>();
        map.put("msgtype", "extend_Relation");
        map.put("unitID",args.get("SAFENUMBER_APP_UNITID"));
        map.put("smbms", param.getUidnumber() == null ? "" : param.getUidnumber());
        map.put("uuidinpartner", "");
        map.put("validitytime", param.getExpiretime());
        map.put("uidType", "0");
        map.put("appkey", args.get("SAFENUMBER_APP_KEY"));
        map.put("msgid", "1");
        map.put("service", "SafeNumber");
        map.put("ver", "2.0");
        map.put("ts", DateUtils.format(new Date()));
        map.put("opuser",  args.get("SAFENUMBER_APP_OPUSER"));
        map.put("opmodule", args.get("SAFENUMBER_APP_OPMODULER"));
        map.put("sid", ApiSignUtils.signTopRequest(map, args.get("SAFENUMBER_APP_SECRET"), "MD5"));
        StringBuilder sb = new StringBuilder(url + "?");
        for (Map.Entry<String, String> e : map.entrySet()) {
            sb.append(e.getKey() + "=" + e.getValue()).append("&");
        }
        logger.info("extend_request:{}", sb);
        String result = HttpClientUtil.doGet(url, map);
        logger.info("extend_response:{}", result);
        if(result.equals("504")) {
            return Result.error("http服务连接失败");
        }
        if (result.contains("error")) {
            return Result.error("绑定失败");
        }
        return Result.ok(param.getExpiretime(),result);
    }


    @Override
    public Result changeBind(BindVo bindVo) throws IOException {
        // 1. 调用解绑
        if (unbind(bindVo.getUidnumber()).getCode() != 200) {
            return Result.error("原绑定关系解绑失败");
        }
        // 2. 调用绑定接口
        logger.info(" calltype>>>>>>>>>>>>>>>>>>>>>>>>:"+bindVo.getCallrestrict());
        return bind(bindVo);
    }

}
