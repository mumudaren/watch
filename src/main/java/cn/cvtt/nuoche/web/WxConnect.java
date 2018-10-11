package cn.cvtt.nuoche.web;

import ch.qos.logback.core.net.SyslogOutputStream;
import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.entity.WeChat;
import cn.cvtt.nuoche.entity.business.BindVo;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.entity.business.BusinessNumberRecord;
import cn.cvtt.nuoche.entity.business.businessPayNotify;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.ISystemParamInterface;
import cn.cvtt.nuoche.reponsitory.IBusinessCusRepository;
import cn.cvtt.nuoche.reponsitory.IBusinessNumberRecordRepository;
import cn.cvtt.nuoche.reponsitory.IBusinessPayRepository;
import cn.cvtt.nuoche.server.WxServer;
import cn.cvtt.nuoche.server.impl.NumberServiceImpl;
import cn.cvtt.nuoche.util.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @decription WxConnect
 * <p>测试</p>
 * @author Yampery
 * @date 2018/3/6 13:48
 */
@RestController
public class WxConnect {

    private static final Logger LOGGER = LoggerFactory.getLogger(WxConnect.class);
    @Resource private WxServer wxServer;
    @Autowired  private IBusinessPayRepository payRepository;
    @Autowired  private NumberServiceImpl  numberService;
    @Autowired  private  ConfigUtil util;
    @Autowired  private IBusinessNumberRecordRepository  businessNumberRecordRepository;
    @Autowired  private IBusinessCusRepository  businessCusRepository;
    @Autowired  private IProductInterface  productInterface;
    @Autowired  private JedisUtils  jedisUtils;
                private static  Object  obj=new Object();

    /**
     * 微信接入
     * @param weChat {@link WeChat}
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String connect(WeChat weChat) {

        // 接收微信服务器发送get请求到服务器地址URL上的参数
        String signature = weChat.getSignature();
        String timesStamp = weChat.getTimestamp();
        String nonce = weChat.getNonce();
        String enchostr = weChat.getEchostr();
        // 验证签名
        if (SignUtil.validateSigniture(signature, timesStamp, nonce)) {
            LOGGER.info("[wechat connect]validateSigniture signature , timesStamp ,nonce is right!"+"\n");
            return enchostr;
        } else {
            LOGGER.warn("[wechat connect]Pay Attention!!!!the method is not come from wechat sever!!!!"+"\n");
            return null;
        }
    }

    /**
     * 处理被动事件、消息，接收微信服务器POST数据
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void eventHandler(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 设置编码
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> requestMap = MessageUtils.parseXml(request);

        LOGGER.info("[eventHandler] parseXml from WeChat. remote_ip:{} request_param:{}",
                request.getRemoteHost(), JsonUtils.objectToJson(requestMap));
        String xmlResp = wxServer.wxHandler(requestMap);
        LOGGER.info("[eventHandler]response:{}", xmlResp+"\n");
        PrintWriter out = response.getWriter();
        out.print(xmlResp);
        out.close();
    }

    /**
     *  接收支付成功xml信息
     * @param  request
     * @param  response
     *
     * */
    @RequestMapping(value="/paySuccess",method = RequestMethod.POST)
    public void paySuccess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置编码
        /** 绑定 安全号
         *  TODO
         *   这个地方根据查库中订单信息找到 绑定的数据是否存在 号码的，有就复制到business_cusmer中
         * **/
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> requestMap = MessageUtils.parseXml(request);
        LOGGER.info("[paySuccess]parseXml from WeChat. requestMap is:{}" +requestMap+"\n");
       // payService.savePayOrderInfo(requestMap);
        /** 因为微信推送的的重入性,可能导致重入多次延期,故加上在redis上加入版本号 解决*/
        String trade=jedisUtils.get("nuoche:expire:"+requestMap.get("transaction_id"),"");
        if(StringUtils.isEmpty(trade)) {
            jedisUtils.setex("nuonuo:expire:" + requestMap.get("transaction_id"), requestMap.get("out_trade_no"), 4500);
            Map<String,String> flag=savePayOrderInfo(requestMap);
            String  openId=requestMap.get("openid");
            /** tips: 这里取的是 订单参数attach中的业务business  这个business是在配置文件中
             *        根据业务ID{nuoche} 和价格 取wx_priduct 中的天数进行延期
             *   attach:天舟通信安全号支付-nuoche   */
            BusinessCustomer  customer=businessCusRepository.findByOpenidEquals(openId);
            String  attach=requestMap.get("attach");
            String  business=null;
            BindVo  bind=new BindVo();
            String phone=flag.get("phone");
            String operateType=flag.get("operateType");
            bind.setExpiretime(flag.get("days"));
            /** 区分绑定、延期、解冻、套餐卡购买（不选号）operateType,0,1,2,3**/
            Result  result=null;
            if(StringUtils.equals(operateType,"0")){
                bind.setRegphone(phone);
                String regexId="";
                if(StringUtils.isNotEmpty(flag.get("uidnumber")))bind.setUidnumber(flag.get("uidnumber"));
                /** 此处使用锁 保证删除至尊靓号之前同时不能被别的再次绑定*/
                    if(StringUtils.isNotEmpty(flag.get("phone"))&&StringUtils.isNotEmpty(flag.get("uidnumber"))){
                        synchronized (obj){
                            result=numberService.bindZhiZun(bind);
                            if(result.getCode()==200){
                                //删除号码池中的号码。
                               String  json=productInterface.deleteSpeNumber(util.getBusinessKey(),flag.get("uidnumber"));
                               regexId=JsonUtils.handlerNumberReturnRegexJson(json);
                                LOGGER.info("[paySuccess]bind ZhiZun number is success and result:"+json+"\n");
                                //获取返回结果。
                                JSONObject jobjZhiZun = JSONObject.parseObject(result.getData().toString());
                                JSONObject resZhiZun = jobjZhiZun.getJSONObject("binding_Relation_response");
                                if (null == resZhiZun) {
                                    LOGGER.warn("[paySuccess]"+phone+" bind wrong."+"\n");
                                }
                                //保存记录
                                BusinessNumberRecord  record2=new BusinessNumberRecord();
                                record2.setBusinessId(util.getBusinessKey());
                                record2.setPrtms(resZhiZun.getString("prtms"));
                                record2.setSmbms(resZhiZun.getString("smbms"));
                                record2.setResult(1);
                                record2.setCallrestrict(0+"");
                                record2.setSubts(new Date());
                                record2.setUserPhone(customer.getPhone());
                                record2.setValidTime(DateUtils.parse(resZhiZun.getString("validitytime")));
                                record2.setRegexId(Integer.parseInt(StringUtils.equals(regexId,"")?"0":regexId));
                                businessNumberRecordRepository.save(record2);
                            }
                         }
                    }else {
                        synchronized (obj) {
                            result = numberService.bind(bind);
                            LOGGER.info("[paySuccess]bind usual number is success and result:" + result + "\n");
                            if (result.getCode() == 200) {
                                //删除号码池中的号码。
//                                String  json=productInterface.deleteSpeNumber(util.getBusinessKey(),flag.get("uidnumber"));
//                                regexId=JsonUtils.handlerNumberReturnRegexJson(json);
                                JSONObject jobj = JSONObject.parseObject(result.getData().toString());
                                JSONObject res = jobj.getJSONObject("binding_Relation_response");
                                if (null == res) {
                                    LOGGER.warn("[paySuccess]" + phone + " bind wrong." + "\n");
                                }
                                //保存记录
                                BusinessNumberRecord record = new BusinessNumberRecord();
                                record.setBusinessId(util.getBusinessKey());
                                record.setPrtms(res.getString("prtms"));
                                record.setSmbms(res.getString("smbms"));
                                record.setResult(1);
                                record.setCallrestrict(0 + "");
                                record.setSubts(new Date());
                                record.setUserPhone(customer.getPhone());
                                record.setValidTime(DateUtils.parse(res.getString("validitytime")));
                                record.setRegexId(Integer.parseInt(StringUtils.equals(regexId, "") ? "0" : regexId));
                                businessNumberRecordRepository.save(record);
                            }
                        }
                    }
                //绑定
            } else if(StringUtils.equals(operateType,"1")){
                synchronized (obj) {
                        LOGGER.info("[paySuccess]operateType is extend number......");
                        BusinessNumberRecord recordSearch = businessNumberRecordRepository.findBySmbmsEqualsAndBusinessIdEquals(flag.get("uidnumber"), util.getBusinessKey());
                        int  sumbms=recordSearch.getRegexId();
                        //靓号延期
                        if(sumbms>0){
                            LOGGER.info("[paySuccess]number type is ZhiZun."+"\n");
                            bind.setUidnumber(flag.get("uidnumber"));
                            result = numberService.extendZhiZun(bind);
                            if (result.getCode() == 200) {
                                JSONObject jobj = JSONObject.parseObject(result.getData().toString());
                                JSONObject res = jobj.getJSONObject("extend_Relation_response");
                                if (null == res) {
                                    LOGGER.warn("[paySuccess]"+phone + "ZhiZun bind wrong"+"\n");
                                }
                                BusinessNumberRecord record = businessNumberRecordRepository.findBySmbmsEqualsAndBusinessIdEquals(flag.get("uidnumber"), util.getBusinessKey());
                                String days = result.getMsg().toString();
                                Date date = record.getValidTime();
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(days));
                                record.setValidTime(calendar.getTime());
                                businessNumberRecordRepository.saveAndFlush(record);
                            }
                        }
                        //非靓号延期
                        if(sumbms==0){
                            LOGGER.info("[paySuccess]number type is usual."+"\n");
                                bind.setUidnumber(flag.get("uidnumber"));
                                result = numberService.extend(bind);
                                if (result.getCode() == 200) {
                                    JSONObject jobj = JSONObject.parseObject(result.getData().toString());
                                    JSONObject res = jobj.getJSONObject("extend_Relation_response");
                                    if (null == res) {
                                        LOGGER.warn("[paySuccess]"+phone + "usual bind wrong"+"\n");
                                    }
                                    BusinessNumberRecord record = businessNumberRecordRepository.findBySmbmsEqualsAndBusinessIdEquals(flag.get("uidnumber"), util.getBusinessKey());
                                    String days = result.getMsg().toString();
                                    Date date = record.getValidTime();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(days));
                                    record.setValidTime(calendar.getTime());
                                    businessNumberRecordRepository.saveAndFlush(record);
                                }
                        }
                }
            } else if(StringUtils.equals(operateType,"2")){
                synchronized (obj) {
                    //解冻
                    LOGGER.info("[paySuccess] operateType is ice-out. .....");
                    BusinessNumberRecord recordSearch = businessNumberRecordRepository.findBySmbmsEqualsAndBusinessIdEquals(flag.get("uidnumber"), util.getBusinessKey());
                    int sumbms = recordSearch.getRegexId();
                    bind.setRegphone(phone);
                    //靓号
                    if (sumbms > 0) {
                        LOGGER.info("[paySuccess]ZhiZun number is going to ice-out " + "\n");
                        bind.setUidnumber(flag.get("uidnumber"));
                        result = numberService.recoverRelationZZ(bind);
                        if (result.getCode() == 200) {
                            JSONObject jobj = JSONObject.parseObject(result.getData().toString());
                            JSONObject res = jobj.getJSONObject("extend_Relation_response");
                            if (null == res) {
                                LOGGER.warn("[paySuccess]" + phone + " ZhiZun number ice-out wrong" + "\n");
                            }
                            BusinessNumberRecord record = businessNumberRecordRepository.findBySmbmsEqualsAndBusinessIdEquals(flag.get("uidnumber"), util.getBusinessKey());
                            String days = result.getMsg().toString();
                            Date date = new Date();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(days));
                            record.setValidTime(calendar.getTime());
                            businessNumberRecordRepository.saveAndFlush(record);
                        }
                    }
                    //普通号
                    if (sumbms == 0) {
                        LOGGER.info("[paySuccess]ice-out normal method......" + "\n");
                        bind.setUidnumber(flag.get("uidnumber"));
                        result = numberService.recoverRelation(bind);
                        if (result.getCode() == 200) {
                            JSONObject jobj = JSONObject.parseObject(result.getData().toString());
                            JSONObject res = jobj.getJSONObject("extend_Relation_response");
                            if (null == res) {
                                LOGGER.warn("[paySuccess]" + phone + ">>>>normal number ice-out  fail" + "\n");
                            }
                            BusinessNumberRecord record = businessNumberRecordRepository.findBySmbmsEqualsAndBusinessIdEquals(flag.get("uidnumber"), util.getBusinessKey());
                            String days = result.getMsg().toString();
                            Date date = new Date();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(days));
                            record.setValidTime(calendar.getTime());
                            businessNumberRecordRepository.saveAndFlush(record);
                        }
                    }
                }
            }//elseif over
             else if(StringUtils.equals(operateType,"3")){
                synchronized (obj) {
                    LOGGER.info("[paySuccess]operateType is buy regex card......");
                    //保存记录
                    BusinessNumberRecord  record2=new BusinessNumberRecord();
                    record2.setBusinessId(util.getBusinessKey());
                    record2.setPrtms(phone);
                    //临时95号
                    record2.setSmbms(flag.get("uidnumber"));
                    record2.setResult(1);
                    record2.setCallrestrict(0+"");
                    record2.setSubts(new Date());
                    record2.setUserPhone(customer.getPhone());
                    //有效时间
                    Date validTime=DateUtils.addDay(new Date(),bind.getExpiretime());
                    record2.setValidTime(validTime);
                    //临时套餐id
                    record2.setRegexId(255);
                    businessNumberRecordRepository.save(record2);
            }//elseif over(buy regex card)
        }
        }
        /**  业务流程处理完成返回微信消息*/
        /** 微信返回通知结果**/
        String returnRes=success();
        PrintWriter out = response.getWriter();
        out.print(returnRes);
        out.close();
    }

    public String success(){
        /**  业务流程成功*/
        StringBuffer  sb=new StringBuffer();
        sb.append("<xml>");
        sb.append("<return_code>").append("<![CDATA[SUCCESS]]>").append("</return_code>");
        sb.append("<return_msg>").append("<![CDATA[OK]]>").append("</return_msg>");
        sb.append("</xml>");
        return  sb.toString();
    }

    public String fail(){
        /**  业务流程失败*/
        StringBuffer  sb=new StringBuffer();
        sb.append("<xml>");
        sb.append("<return_code>").append("<![CDATA[FAIL]]>").append("</return_code>");
        sb.append("<return_msg>").append("<![CDATA[WRONG]]>").append("</return_msg>");
        sb.append("</xml>");
        return  sb.toString();
    }

    public  Map<String,String>  savePayOrderInfo(Map<String, String> info){
        String openid=info.get("openid");
        String out_trade_no=info.get("out_trade_no");
        LOGGER.info("[savePayOrderInfo]received prams openid is:"+openid+"\n");
        businessPayNotify  order=payRepository.findByOpenidEqualsAndOutTradeNo(openid,out_trade_no);
        String  return_code=info.get("return_code");
        order.setOpenid(openid);
        order.setAppid(info.get("appid"));
        order.setSubscribed(info.get("is_subscribe"));
        order.setBankType(info.get("bank_type"));
        order.setCashFee(Integer.parseInt(info.get("cash_fee")));
        order.setResultCode("SUCCESS".equals(return_code)?"":info.get("err_code"));
        order.setReturnCode("SUCCESS".equals(return_code)?"":info.get("err_code_des"));
        order.setOutTradeNo(info.get("out_trade_no"));
        order.setResultCode(return_code);
        order.setFeeType(info.get("fee_type"));
        order.setMchId(info.get("mch_id"));
        order.setNonce_str(info.get("nonce_str"));
        order.setReturnCode(info.get("return_code"));
        order.setTime_end(info.get("time_end"));
        order.setTotal_fee( info.get("total_fee"));
        order.setTransactionId(info.get("transaction_id"));
        order.setTrade_type(info.get("trade_type"));
        order.setBusiness(util.getBusinessKey());
        payRepository.saveAndFlush(order);
        Map<String,String> map=new HashMap();
        map.put("phone",order.getPhone());
        map.put("operateType",order.getOperateType());
        map.put("days",order.getDays());
        map.put("uidnumber",order.getUidNumber());
        return map;
    }


    @RequestMapping("/isOk")
    @ResponseBody
    public  String   isOk(){
        wxServer.getAccessToken();
        return  "ok";
    }


}
