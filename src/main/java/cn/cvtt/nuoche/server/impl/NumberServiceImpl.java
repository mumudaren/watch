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
        //主叫外呼
        map.put("productid", "1");
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
        logger.info("binding_response:Result.ok");
        return Result.ok(result);
    }

    @Override
    public Result bindZhiZun(BindVo param) throws IOException {
        args=JsonUtils.handlerJson(systemParamInterface.getSystemConfigByArgs(1,util.getBusinessKey()));
        // 请求服务接口
        String url = args.get("SAFENUMBER_APP_DOMAIN") + "/safenumberservicessm/api/manage/dataManage";

        // 服务接口请求参数
        Map<String, String> map = new HashMap<>();
        map.put("msgtype", "binding_Relation");
        map.put("unitID", args.get("SAFENUMBER_APP_UNITID_ZZ") );
        //主叫外呼
        map.put("productid", "1");
        map.put("prtms", param.getRegphone());
        map.put("smbms", param.getUidnumber() == null ? "" : param.getUidnumber());
        map.put("uuidinpartner", "");
        map.put("validitytime", param.getExpiretime() == null ? "" : param.getExpiretime());
        map.put("uidType", "0");
        map.put("callrestrict", param.getCallrestrict() == null ? "" : param.getCallrestrict());
        map.put("appkey", args.get("SAFENUMBER_APP_KEY_ZZ"));
        map.put("msgid", "1");
        map.put("service", "SafeNumber");
        map.put("ver", "2.0");
        map.put("ts", DateUtils.format(new Date()));
        map.put("opuser", args.get("SAFENUMBER_APP_OPUSER"));
        map.put("opmodule", args.get("SAFENUMBER_APP_OPMODULER"));
        map.put("sid", ApiSignUtils.signTopRequest(map, args.get("SAFENUMBER_APP_SECRET_ZZ"), "MD5"));
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
        logger.info("binding_response:bindZhiZun.ok");
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
    //延期接口
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
        map.put("subts", DateUtils.format(new Date()));
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
    //靓号延期接口
    @Override
    public Result extendZhiZun(BindVo param) throws IOException {
        args=JsonUtils.handlerJson(systemParamInterface.getSystemConfigByArgs(1,util.getBusinessKey()));
        // 请求服务接口
        String url = args.get("SAFENUMBER_APP_DOMAIN") + "/safenumberservicessm/api/manage/extendRelation";
        // 服务接口请求参数
        Map<String, String> map = new HashMap<>();
        map.put("msgtype", "extend_Relation");
        map.put("unitID",args.get("SAFENUMBER_APP_UNITID_ZZ"));
        map.put("smbms", param.getUidnumber() == null ? "" : param.getUidnumber());
        map.put("uuidinpartner", "");
        map.put("validitytime", param.getExpiretime());
        map.put("subts", DateUtils.format(new Date()));
        map.put("appkey", args.get("SAFENUMBER_APP_KEY_ZZ"));
        map.put("msgid", "1");
        map.put("service", "SafeNumber");
        map.put("ver", "2.0");
        map.put("ts", DateUtils.format(new Date()));
        map.put("opuser",  args.get("SAFENUMBER_APP_OPUSER"));
        map.put("opmodule", args.get("SAFENUMBER_APP_OPMODULER"));
        map.put("sid", ApiSignUtils.signTopRequest(map, args.get("SAFENUMBER_APP_SECRET_ZZ"), "MD5"));
        StringBuilder sb = new StringBuilder(url + "?");
        for (Map.Entry<String, String> e : map.entrySet()) {
            sb.append(e.getKey() + "=" + e.getValue()).append("&");
        }
        logger.info("ZhiZun extend_request:{}", sb);
        String result = HttpClientUtil.doGet(url, map);
        logger.info("ZhiZun  extend_response:{}", result);
        if(result.equals("504")) {
            return Result.error("ZhiZun  http服务连接失败");
        }
        if (result.contains("error")) {
            return Result.error("靓号延期失败");
        }
        return Result.ok(param.getExpiretime(),result);
    }


    //普通号解冻接口
    @Override
    public Result recoverRelation(BindVo param) throws IOException {
        args=JsonUtils.handlerJson(systemParamInterface.getSystemConfigByArgs(1,util.getBusinessKey()));
        // 请求服务接口
        String url = args.get("SAFENUMBER_APP_DOMAIN") + "/safenumberservicessm/api/manage/recoverRelation";
        // 服务接口请求参数
        Map<String, String> map = new HashMap<>();
        map.put("msgtype", "recover_Relation");
        map.put("unitID",args.get("SAFENUMBER_APP_UNITID"));
        //map.put("uuidinpartner", "");
        map.put("validitytime", param.getExpiretime());
        //map.put("uidType", "0");
        map.put("appkey", args.get("SAFENUMBER_APP_KEY"));
        map.put("msgid", "1");
        map.put("service", "SafeNumber");
        map.put("ver", "2.0");
        map.put("ts", DateUtils.format(new Date()));
        map.put("opuser",  args.get("SAFENUMBER_APP_OPUSER"));
         map.put("opmodule", args.get("SAFENUMBER_APP_OPMODULER"));
        map.put("prtms", param.getRegphone());
        map.put("smbms", param.getUidnumber() == null ? "" : param.getUidnumber());
        map.put("sid", ApiSignUtils.signTopRequest(map, args.get("SAFENUMBER_APP_SECRET"), "MD5"));
        StringBuilder sb = new StringBuilder(url + "?");
        for (Map.Entry<String, String> e : map.entrySet()) {
            sb.append(e.getKey() + "=" + e.getValue()).append("&");
        }
        logger.info(" recoverRelation will send:{}", sb);
        String result = HttpClientUtil.doGet(url, map);
        logger.info("  recoverRelationZZ result:{}", result);
        if(result.equals("504")) {
            return Result.error(" ice_out  http  fail:504");
        }
        if (result.contains("error")) {
            return Result.error(" ice_out fail");
        }
        return Result.ok(param.getExpiretime(),result);
    }
    //靓号解冻接口
    @Override
    public Result recoverRelationZZ(BindVo param) throws IOException {
        args=JsonUtils.handlerJson(systemParamInterface.getSystemConfigByArgs(1,util.getBusinessKey()));
        // 请求服务接口
        String url = args.get("SAFENUMBER_APP_DOMAIN") + "/safenumberservicessm/api/manage/recoverRelation";
        // 服务接口请求参数
        Map<String, String> map = new HashMap<>();
        map.put("msgtype", "recover_Relation");
        map.put("unitID",args.get("SAFENUMBER_APP_UNITID_ZZ"));
        //map.put("uuidinpartner", "");
        map.put("validitytime", param.getExpiretime());
        //map.put("uidType", "0");
        map.put("appkey", args.get("SAFENUMBER_APP_KEY_ZZ"));
        map.put("msgid", "1");
        map.put("service", "SafeNumber");
        map.put("ver", "2.0");
        map.put("ts", DateUtils.format(new Date()));
        map.put("opuser",  args.get("SAFENUMBER_APP_OPUSER"));
        map.put("opmodule", args.get("SAFENUMBER_APP_OPMODULER"));
        map.put("prtms", param.getRegphone());
        map.put("smbms", param.getUidnumber() == null ? "" : param.getUidnumber());
        map.put("sid", ApiSignUtils.signTopRequest(map, args.get("SAFENUMBER_APP_SECRET_ZZ"), "MD5"));
        StringBuilder sb = new StringBuilder(url + "?");
        for (Map.Entry<String, String> e : map.entrySet()) {
            sb.append(e.getKey() + "=" + e.getValue()).append("&");
        }
        logger.info("ZhiZun recoverRelationZZ  will send:{}", sb);
        String result = HttpClientUtil.doGet(url, map);
        logger.info("ZhiZun  recoverRelationZZ result:{}", result);
        if(result.equals("504")) {
            return Result.error("ZhiZun ice_out  http fail:504");
        }
        if (result.contains("error")) {
            return Result.error("ZhiZun ice_out fail");
        }
        return Result.ok(param.getExpiretime(),result);
    }
    //靓号查询接口
    @Override
    public Result queryRelation(BindVo param) throws IOException {
        args=JsonUtils.handlerJson(systemParamInterface.getSystemConfigByArgs(1,util.getBusinessKey()));
        // 请求服务接口
        String url = args.get("SAFENUMBER_APP_DOMAIN") + "/safenumberservicessm/api/manage/queryRelation";
        // 服务接口请求参数
        Map<String, String> map = new HashMap<>();
        map.put("msgtype", "query_Relation");
        map.put("unitID",args.get("SAFENUMBER_APP_UNITID_ZZ"));
        map.put("appkey", args.get("SAFENUMBER_APP_KEY_ZZ"));
        map.put("ver", "2.0");
        map.put("ts", DateUtils.format(new Date()));
        map.put("msgid", "1");
        map.put("service", "SafeNumber");
        map.put("opuser",  args.get("SAFENUMBER_APP_OPUSER"));
        map.put("opmodule", args.get("SAFENUMBER_APP_OPMODULER"));
        map.put("number", param.getUidnumber());
        map.put("smbms", param.getUidnumber());
        map.put("sid", ApiSignUtils.signTopRequest(map, args.get("SAFENUMBER_APP_SECRET_ZZ"), "MD5"));
        StringBuilder sb = new StringBuilder(url + "?");
        for (Map.Entry<String, String> e : map.entrySet()) {
            sb.append(e.getKey() + "=" + e.getValue()).append("&");
        }
        logger.info("[queryRelation]queryRelation  will send:{}", sb);
        String result = HttpClientUtil.doGet(url, map);
        logger.info("[queryRelation]queryRelation   result:{}", result);
        if(result.equals("504")) {
            return Result.error("[queryRelation]queryRelation http fail:504");
        }
        if (result.contains("error")) {
            return Result.error("[queryRelation]queryRelation fail");
        }
        return Result.ok(DateUtils.format(new Date()),result);
    }
    //非靓号查询接口
    @Override
    public Result queryNormalRelation(BindVo param) throws IOException {
        args=JsonUtils.handlerJson(systemParamInterface.getSystemConfigByArgs(1,util.getBusinessKey()));
        // 请求服务接口
        String url = args.get("SAFENUMBER_APP_DOMAIN") + "/safenumberservicessm/api/manage/queryRelation";
        // 服务接口请求参数
        Map<String, String> map = new HashMap<>();
        map.put("msgtype", "query_Relation");
        map.put("unitID",args.get("SAFENUMBER_APP_UNITID"));
        map.put("appkey", args.get("SAFENUMBER_APP_KEY"));
        map.put("ver", "2.0");
        map.put("ts", DateUtils.format(new Date()));
        map.put("msgid", "1");
        map.put("service", "SafeNumber");
        map.put("opuser",  args.get("SAFENUMBER_APP_OPUSER"));
        map.put("opmodule", args.get("SAFENUMBER_APP_OPMODULER"));
        map.put("number", param.getUidnumber());
        map.put("smbms", param.getUidnumber());
        map.put("sid", ApiSignUtils.signTopRequest(map, args.get("SAFENUMBER_APP_SECRET"), "MD5"));
        StringBuilder sb = new StringBuilder(url + "?");
        for (Map.Entry<String, String> e : map.entrySet()) {
            sb.append(e.getKey() + "=" + e.getValue()).append("&");
        }
        logger.info("[queryRelation]queryRelation  will send:{}", sb);
        String result = HttpClientUtil.doGet(url, map);
        logger.info("[queryRelation]queryRelation   result:{}", result);
        if(result.equals("504")) {
            return Result.error("[queryRelation]queryRelation http fail:504");
        }
        if (result.contains("error")) {
            return Result.error("[queryRelation]queryRelation fail");
        }
        return Result.ok(DateUtils.format(new Date()),result);
    }
//    @Override
//    public Result changeBind(BindVo bindVo) throws IOException {
//        // 1. 调用解绑
//        if (unbind(bindVo.getUidnumber()).getCode() != 200) {
//            return Result.error("原绑定关系解绑失败");
//        }
//        // 2. 调用绑定接口
//        logger.info(" calltype>>>>>>>>>>>>>>>>>>>>>>>>:"+bindVo.getCallrestrict());
//        return bind(bindVo);
//    }
    //更改接口

    @Override
    public Result changeBindNew(BindVo param) throws IOException {
        System.out.println("changeBindNew changeBind method bindVo item is:"+param.toString());
        // 请求服务接口
        String url = args.get("SAFENUMBER_APP_DOMAIN") + "/safenumberservicessm/api/manage/changeRelation";

        // 服务接口请求参数
        Map<String, String> map = new HashMap<>();
        map.put("ver", "2.0");
        map.put("opmodule", "nuonuo");
        map.put("opuser", "nuonuo");
        map.put("msgid", "1");
        map.put("service", "SafeNumber");
        map.put("smbms", param.getUidnumber() == null ? "" : param.getUidnumber());
        map.put("field", param.getField() == null ? "" : param.getField());
        map.put("value", param.getValue() == null ? "" : param.getValue());
        map.put("unitID", args.get("SAFENUMBER_APP_UNITID_ZZ") );
        map.put("appkey", args.get("SAFENUMBER_APP_KEY_ZZ"));
        map.put("msgtype", "change_Relation");
        map.put("ts", DateUtils.format(new Date()));
        map.put("sid", ApiSignUtils.signTopRequest(map, args.get("SAFENUMBER_APP_SECRET_ZZ"), "MD5"));
        StringBuilder sb = new StringBuilder(url + "?");
        for (Map.Entry<String, String> e : map.entrySet()) {
            sb.append(e.getKey() + "=" + e.getValue()).append("&");
        }
        logger.info("请求绑定接口:{}", sb);
        String result = HttpClientUtil.doGet(url, map);
        logger.info("changeRelation:{}", result);
        System.out.println("changeRelation:"+result.toString());
        if(result.equals("504")) {
            return Result.error("http服务连接失败");
        }
        if (result.contains("error")) {
            return Result.error("绑定失败");
        }
        return Result.ok(result);
    }

}
