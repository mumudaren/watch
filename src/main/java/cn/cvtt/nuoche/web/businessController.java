package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.aop.LogManager;
import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.common.result.ResultMsg;
import cn.cvtt.nuoche.entity.business.*;
import cn.cvtt.nuoche.facade.IBusinessCallRecordInterface;
import cn.cvtt.nuoche.facade.INumberInterface;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.ISystemParamInterface;
import cn.cvtt.nuoche.reponsitory.IBusinessCusRepository;
import cn.cvtt.nuoche.reponsitory.IBusinessPayRepository;
import cn.cvtt.nuoche.reponsitory.ISystemFeedBack;
import cn.cvtt.nuoche.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class businessController extends  BaseController{

    @Autowired
    ConfigUtil  util;
    @Autowired
    ISystemFeedBack  feedBackRepository;
    @Autowired
    IBusinessCusRepository   businessCusRepository;
    @Autowired
    JedisUtils  jedisUtils;
    @Autowired
    IBusinessPayRepository  payRepository;
    @Autowired
    ISystemParamInterface  systemParamInterface;
    @Autowired
    IProductInterface  productInterface;
    @Autowired
    IBusinessCallRecordInterface  callRecordInterface;
    @Autowired
    INumberInterface   numberInterface;



    @RequestMapping("/sendCode")
    @ResponseBody
    public  Result  sendCode(String openid,String phone){
        String json=systemParamInterface.getSystemConfigByArgs(2,util.getBusinessKey());
        logger.info("webService:json="+json);
        Map<String,String> map= JsonUtils.handlerJson(json);
        String code= SmsValidateUtils.randomCode(1111,9999);
        // 检查该手机号当天短信是否超出限制
        String smsCount = jedisUtils.hget(map.get("COUNT_MAP_KEY"), phone);
        if (!StringUtils.isBlank(smsCount) && Integer.parseInt(smsCount) >= Integer.parseInt(map.get("SMSCOUNTLIMIT")) ) {
            return Result.error("短信发送超出限制");
        }
        String  key="NUOCHE:"+phone;
        Result  result = null;
        try {
            result = SmsValidateUtils.sendMsg(phone, code,map);
        } catch (Exception e) {
            e.printStackTrace();
            result=new Result(ResultMsg.OPERATEXCEPTIN);
        }
        // 3. 将短信验证码保存到redis中，设置有效期
        if (200 == result.getCode()) {
            jedisUtils.setex(key, code, 1800);
            // 用户接受短信次数+1
            jedisUtils.hincr(map.get("COUNT_MAP_KEY"), phone, 1L);
        }
        return result;
    }


    @RequestMapping("/validPhone")
    @ResponseBody
    @LogManager(description ="validPhone")
    public  Result   validPhone(String openid,String  code,String phone){
        logger.info("openid"+openid);
        if(StringUtils.isEmpty(code)||StringUtils.isEmpty(phone)){
            return  new Result(ResultMsg.REQUESTPARAMEXCEPTION);
        }
        String  key="NUOCHE:"+phone;
        String  redisCode=jedisUtils.get(key,"");
        if(!StringUtils.equals(redisCode,code)){
            return  new Result(ResultMsg.CODENOTMATCH);
        }
        try {
            BusinessCustomer  customer=businessCusRepository.findByOpenidEquals(openid);
            customer.setPhone(phone);
            businessCusRepository.saveAndFlush(customer);
        }catch (Exception  e){
            e.printStackTrace();
            return new Result(ResultMsg.WXUSERNOTFOUND);
        }
        return  Result.ok();
    }

    /**
     * 创建订单支付
     * @param   openid  微信支付ID
     * @param    totalFee  金额  分单位
     * @param    phone   绑定的手机号码
     * @param    extend  1 延期  0 表示新购买
     *
     *
     * **/
    @SuppressWarnings("all")
    @RequestMapping("/createOrder")
    @ResponseBody
    @LogManager(description = "createOrder")
    public Result createGenerateOrder(@RequestParam("openid") String openid, @RequestParam("totalFee") String totalFee,@RequestParam("phone") String  phone,@RequestParam("uidNumber")  String uidNumber, @RequestParam("extend") String extend,@RequestParam("days") String days,@RequestParam("productId") Integer productId, HttpServletRequest request){
        logger.info("createOrder>>>>>>>>>>>>>>>>>>>>>> start ");
        /** 查看该用户绑定是否超过后台配置的最大绑定次数,超过则不让绑定*/
        String  json=systemParamInterface.getSystemConfigByArgs(2,util.getBusinessKey());
        Map<String,String>  map=JsonUtils.handlerJson(json);
        String  str=map.get("NUMBER_BIND_LIMIT");
        if(StringUtils.isNotEmpty(str)){
            BusinessCustomer  bcs=businessCusRepository.findByOpenidEquals(openid);
            String countJson=callRecordInterface.CountBindNumber(util.getBusinessKey(),bcs.getPhone());
            JSONObject  childObj=JSONObject.parseObject(countJson);
            if(childObj.getIntValue("code")==200){
                int  size=JSONObject.parseObject(childObj.getString("data")).getIntValue("size");
                if(size>Integer.parseInt(str)){
                    return  new Result(ResultMsg.MORETHANBINDNUMBER);
                }
            }
        }
        /**  判断结束 **/
        /**
         * 判断是否超过限购的次数
         *
         * */
        List<businessPayNotify>  ls=payRepository.findAllByOpenidEqualsAndPhoneEqualsAndProductIdEquals(util.getBusinessKey(),phone,productId);
        String BusinessJson=productInterface.findProductById(productId);
        JSONObject  productObj=JSONObject.parseObject(BusinessJson);
        if(productObj.getIntValue("code")==200){
            JSONObject product=JSONObject.parseObject(productObj.getString("data"));
            String  limit=product.getString("productLimit");
            if(StringUtils.isNotEmpty(product.getString("productLimit"))){
                if(Integer.parseInt(limit)<ls.size()){
                    return new Result(ResultMsg.MORETHANBUYNUMBER);
                }
            }
        }
        /** end*/
        String  bill_ip=  WechatSignGenerator.getIpAddr(request);
        totalFee=totalFee.substring(1);
        double  Fee=Double.parseDouble(totalFee)*100;
        Result  pojo=new Result();
        logger.info("{}{}"+totalFee);
        String  response="";
        String  noId= UUID.randomUUID().toString().replace("-","");
        try {
            response= WechatSignGenerator.sign(openid,new DecimalFormat("#").format(Fee),bill_ip,util.getBusiness(),noId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("response {}"+response);
        /** 根据生成的订单信息返回wexin支付的对象  */
        JSONObject obj= WechatSignGenerator.getPayRequest(response);
        logger.info("objResponse>>>"+obj.toJSONString());
        if(!(Boolean)obj.get("status")){
            pojo.setCode(500);
            pojo.error(obj.get("msg").toString());
            return pojo;
        }
        pojo.setCode(200);
        businessPayNotify  pay=new businessPayNotify();
        pay.setOpenid(openid);
        pay.setPhone(phone);
        pay.setCreateTime(new Date());
        pay.setOutTradeNo(noId);
        pay.setOperateType(extend);
        logger.info("days:>>>>>>>>>>>>>>>>>"+days);
        pay.setDays(days);
        pay.setUidNumber(uidNumber);
        payRepository.save(pay);
        pojo.setData(obj);
        logger.info("Result:"+pojo.toString());
        return   pojo;
    }


    @RequestMapping("/commitMessage")
    public  Result   commitMessage(String  type,String textArea,String openidKey){
        BusinessCustomer user=businessCusRepository.findByOpenidEquals(openidKey);
        logger.info("userToString:"+user.toString());
        SystemFeedBack  feedBack=new SystemFeedBack();
        feedBack.setContent(textArea);
        feedBack.setCreateTime(new Date());
        feedBack.setLastUpdateTime(new Date());
        feedBack.setPhone(user.getPhone());
        feedBack.setOpenid(openidKey);
        feedBack.setType(type);
        feedBack.setState(0);
        feedBackRepository.save(feedBack);
        return  new Result(ResultMsg.OPERATESUCEESS);
    }

    @RequestMapping("/changeModule")
    public  Result  changeModule(String regexId,String number){
        JSONArray  arr=new JSONArray();
        if(StringUtils.isNotEmpty(number)){
              String  str=productInterface.findSpeNumber(util.getBusinessKey(),regexId,number);
              List<Map<String,String>> map=JsonUtils.handlerNumberJson(str);
              arr.add(map);
              return  new Result(ResultMsg.OPERATESUCEESS,arr);
        }else {
            String  str=productInterface.findSpeNumber(util.getBusinessKey(),regexId,"");
            List<Map<String,String>> map=JsonUtils.handlerNumberJson(str);
            arr.add(map);
        }
        String json= productInterface.findRegexProduct(util.getBusinessKey(),regexId);
        List<wx_product>  ls=JsonUtils.handlerRegexJson(json);
        arr.add(ls);
        return  new Result(ResultMsg.OPERATESUCEESS,arr);
    }

    /** arc_95013  */
    @RequestMapping("/callRecordService")
    @ResponseBody
    public  JSONObject  CallManager(CallReleaseVo vo){
        logger.info("CallRecord:{}"+vo.toString());
        JSONObject  obj=new JSONObject();
        JSONObject  innerObj=new JSONObject();
        try {
             String json=JSONObject.toJSON(vo).toString();
             String result=callRecordInterface.saveCallRecord(json,util.getBusinessKey());
             logger.info("resultObj{}:"+result);
            logger.info("arc_95013"+vo.getCall_id());
            innerObj.put("result",true);
            obj.put("secret_call_release_response",innerObj);
        }catch (Exception e){
            e.printStackTrace();
            logger.info("exception:"+e.getMessage());
            innerObj.put("code",50);
            innerObj.put("msg","recordService exception");
            innerObj.put("sub_code","");
            innerObj.put("sub_msg","");
            obj.put("error_response",innerObj);
        }
        return obj;
    }


    @RequestMapping("/changeRelation")
    public  Result   changeRelation(String uidNumber ,String restrict){
        if(StringUtils.isEmpty(uidNumber)&&StringUtils.isEmpty(restrict)){
            return   new  Result(ResultMsg.REQUESTPARAMEXCEPTION);
        }
        String  rest=numberInterface.changeRelation(util.getBusinessKey(),uidNumber,restrict);
        String result="";
        JSONObject obj=JSONObject.parseObject(rest);
        if(obj.getIntValue("code")==200){
            JSONObject  childObj=JSONObject.parseObject(obj.getString("msg"));
            result=childObj.getString("change_Relation_response");
            return  new Result(ResultMsg.OPERATESUCEESS,result);
        }else {
            return  new Result(ResultMsg.OPERATEXCEPTIN);
        }
    }




    public static void main(String[] args) {
        CallReleaseVo  vo=new CallReleaseVo();
        vo.setCall_id("123");
        System.out.println("json:"+ JSON.toJSONString(vo));
    }




}
