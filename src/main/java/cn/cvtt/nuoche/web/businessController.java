package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.common.aop.LogManager;
import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.common.result.ResultMsg;
import cn.cvtt.nuoche.entity.business.*;
import cn.cvtt.nuoche.entity.gift.*;
import cn.cvtt.nuoche.facade.IBusinessCallRecordInterface;
import cn.cvtt.nuoche.facade.INumberInterface;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.ISystemParamInterface;
import cn.cvtt.nuoche.reponsitory.*;
import cn.cvtt.nuoche.service.QrcodeService;
import cn.cvtt.nuoche.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.*;

import static cn.cvtt.nuoche.util.WechatSignGenerator.jsapiSign;

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
    @Autowired
    IBusinessNumberRecordRepository recordRepository;
    @Autowired
    IGiftCardRecordRepository giftCardRecordRepository;
    @Autowired
    IGiftCouponQrcodeRepository giftCouponQrcodeRepository;
    @Autowired
    IGiftCouponRecordRepository giftCouponRecordRepository;
    @Autowired
    IGiftPointRecordRepository giftPointRecordRepository;
    @Autowired
    IGiftPointRepository giftPointRepository;
    @Autowired
    IGiftCouponRepository giftCouponRepository;
    @Autowired
    IGiftCardRepository giftCardRepository;
    @Autowired
    IGiftAwardsRulesRepository giftAwardsRulesRepository;
    @Resource
    private QrcodeService qrcodeService;
    private static final Logger logger = LoggerFactory.getLogger(businessController.class);


    @RequestMapping("/sendCode")
    @ResponseBody
    public  Result  sendCode(String openid,String phone){
        String json=systemParamInterface.getSystemConfigByArgs(2,util.getBusinessKey());
        logger.info("sendCode method：getSystemConfig By Group and businessKey ,return json="+json);
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
        if(StringUtils.isEmpty(code)||StringUtils.isEmpty(phone)){
            logger.info("validPhone method:code or phone is empty.So ResultMsg is:"+ResultMsg.REQUESTPARAMEXCEPTION);
            return  Result.error("验证码不能为空。");
        }
        String  key="NUOCHE:"+phone;
        String  redisCode=jedisUtils.get(key,"");
        if(!StringUtils.equals(redisCode,code)){
            logger.info("validPhone method:code and phone is not equal. So ResultMsg is:"+ResultMsg.CODENOTMATCH);
            return  new Result(ResultMsg.CODENOTMATCH);
        }
        try {
            BusinessCustomer  customer=businessCusRepository.findByOpenidEquals(openid);
            customer.setPhone(phone);
            businessCusRepository.saveAndFlush(customer);
        }catch (Exception  e){
            e.printStackTrace();
            logger.info("validPhone method:There is an error when find customer by openid.So ResultMsg:"+ResultMsg.WXUSERNOTFOUND);
            return new Result(ResultMsg.WXUSERNOTFOUND);
        }
        return  Result.ok();
    }

    /**
     * 创建订单支付
     * @param   openid  微信支付ID
     * @param    totalFee  金额  分单位
     * @param    phone   绑定的手机号码
     * @param    extend  1 延期  0 表示新购买 2 解冻
     *
     *
     * **/
    @SuppressWarnings("all")
    @RequestMapping("/createOrder")
    @ResponseBody
    @LogManager(description = "createOrder")
    public Result createGenerateOrder(@RequestParam("openid") String openid, @RequestParam("totalFee") String totalFee,@RequestParam("phone") String  phone,@RequestParam("uidNumber")  String uidNumber, @RequestParam("extend") String extend,@RequestParam("days") String days,@RequestParam("productId") Integer productId, HttpServletRequest request){
        logger.info("create Wechat Order method: "+"\n");
        /** 查看该用户绑定是否超过后台配置的最大绑定次数,超过则不让绑定*/
        String  json=systemParamInterface.getSystemConfigByArgs(2,util.getBusinessKey());
        logger.info("[createOrder method]:received RequestParam extend is:"+extend+"\n");
        Map<String,String>  map=JsonUtils.handlerJson(json);
        String  str=map.get("NUMBER_BIND_LIMIT");
        if(StringUtils.isNotEmpty(str)){
            logger.info("[createOrder method]:NUMBER_BIND_LIMIT is:"+str+"\n");
            BusinessCustomer  bcs=businessCusRepository.findByOpenidEquals(openid);
            String countJson=callRecordInterface.CountBindNumber(util.getBusinessKey(),bcs.getPhone());
            logger.info("[createOrder method]:countJson is:"+countJson+"\n");
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
        logger.info("[create Wechat Order method],request totalFee is: "+totalFee+"\n");
        String  response="";
        String  noId= UUID.randomUUID().toString().replace("-","");
        try {
            response= WechatSignGenerator.sign(openid,new DecimalFormat("#").format(Fee),bill_ip,util.getBusiness(),noId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("[createOrder method]:WechatSignGenerator's response is:"+response+"\n");
        /** 根据生成的订单信息返回wexin支付的对象  */
        JSONObject obj= WechatSignGenerator.getPayRequest(response);
        logger.info("[createOrder method] WechatSignGenerator's json response is:"+obj+"\n");
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
        pay.setDays(days);
        pay.setUidNumber(uidNumber);
        payRepository.save(pay);
        pojo.setData(obj);
        logger.info("[createOrder method]just payRepository.save(pay),fianlly return pojo Result is:"+pojo.toString()+"\n");
        return   pojo;
    }

    /**
     * 创建订单支付
     * @param   openid  微信支付ID
     * @param    totalFee  金额  分单位
     * @param    phone   绑定的手机号码
     * @param    extend  1 延期  0 表示新购买 2 解冻
     *
     *
     * **/
    @SuppressWarnings("all")
    @RequestMapping("/createGiftOrder")
    @ResponseBody
    @LogManager(description = "createGiftOrder")
    public Result createGiftOrder(@RequestParam("openid") String openid, @RequestParam("totalFee") String totalFee,@RequestParam("phone") String  phone,@RequestParam("uidNumber")  String uidNumber, @RequestParam("extend") String extend,@RequestParam("days") String days,HttpServletRequest request){
        logger.info("you are in createGiftOrder method: "+"\n");
        /** 查看该用户绑定是否超过后台配置的最大绑定次数,超过则不让绑定*/
        String  json=systemParamInterface.getSystemConfigByArgs(2,util.getBusinessKey());
        logger.info("[createGiftOrder method]:received RequestParam extend is:"+extend+"\n");
        Map<String,String>  map=JsonUtils.handlerJson(json);
        String  str=map.get("NUMBER_BIND_LIMIT");
        if(StringUtils.isNotEmpty(str)){
            logger.info("[createGiftOrder method]:NUMBER_BIND_LIMIT is:"+str+"\n");
            BusinessCustomer  bcs=businessCusRepository.findByOpenidEquals(openid);
            String countJson=callRecordInterface.CountBindNumber(util.getBusinessKey(),bcs.getPhone());
            logger.info("[createGiftOrder method]:countJson is:"+countJson+"\n");
            JSONObject  childObj=JSONObject.parseObject(countJson);
            if(childObj.getIntValue("code")==200){
                int  size=JSONObject.parseObject(childObj.getString("data")).getIntValue("size");
                if(size>Integer.parseInt(str)){
                    return  new Result(ResultMsg.MORETHANBINDNUMBER);
                }
            }
        }
        /**  判断结束 **/
        String  bill_ip=  WechatSignGenerator.getIpAddr(request);
        totalFee=totalFee.substring(1);
        double  Fee=Double.parseDouble(totalFee)*100;
        Result  pojo=new Result();
        logger.info("[createGiftOrder method],request totalFee is: "+totalFee+"\n");
        String  response="";
        String  noId= UUID.randomUUID().toString().replace("-","");
        try {
            response= WechatSignGenerator.sign(openid,new DecimalFormat("#").format(Fee),bill_ip,util.getBusiness(),noId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("[createGiftOrder method]:WechatSignGenerator's response is:"+response+"\n");
        /** 根据生成的订单信息返回wexin支付的对象  */
        JSONObject obj= WechatSignGenerator.getPayRequest(response);
        logger.info("[createGiftOrder method] WechatSignGenerator's json response is:"+obj+"\n");
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
        pay.setDays(days);
        pay.setUidNumber(uidNumber);
        payRepository.save(pay);
        pojo.setData(obj);
        logger.info("[createGiftOrder method]just payRepository.save(pay),fianlly return pojo Result is:"+pojo.toString()+"\n");
        return   pojo;
    }


    @RequestMapping("/shareToFriend")
    public  JSONObject   shareToFriend(String  url){
        JSONObject obj=new JSONObject();
        String jsapiSign=null;
        try {
            jsapiSign=jsapiSign(url);
            logger.info("[shareToFriend]jsapiSign is:"+jsapiSign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String signature=StringUtils.substringBefore(jsapiSign,"_");
        String timeStamp=StringUtils.substringAfterLast(jsapiSign,"_");
        String nonceStr=StringUtils.substringBetween(jsapiSign,"_","_");

        obj.put("appId", Constant.APP_ID);
        obj.put("timeStamp", timeStamp);
        obj.put("nonceStr", nonceStr);
        obj.put("signature", signature);
        logger.info("[shareToFriend]return is:"+obj.toString());
        return obj;
    }

    @RequestMapping("/commitMessage")
    public  Result   commitMessage(String  type,String textArea,String openidKey){
        BusinessCustomer user=businessCusRepository.findByOpenidEquals(openidKey);
        logger.info("[commitMessage method]get user by openidKey.user is: "+user.toString()+"\n");
        SystemFeedBack  feedBack=new SystemFeedBack();
        feedBack.setContent(textArea);
        feedBack.setCreateTime(new Date());
        feedBack.setLastUpdateTime(new Date());
        feedBack.setPhone(user.getPhone());
        feedBack.setOpenid(openidKey);
        feedBack.setType(type);
        feedBack.setState(0);
        try{
            feedBackRepository.save(feedBack);
        }catch(Exception e){
            return  new Result(ResultMsg.OPERATEXCEPTIN);
        }
        return  new Result(ResultMsg.OPERATESUCEESS);
    }

    @RequestMapping("/changeModule")
    public  Result  changeModule(String regexId,String number){
        JSONArray  arr=new JSONArray();
        //查找号码
        if(StringUtils.isNotEmpty(number)){
            //查找所有号码
            //String  str=productInterface.findSpeNumber(util.getBusinessKey(),regexId,number);
            //查找top10
            String  str=null;
            try{str=productInterface.findSpeNumberTop10(util.getBusinessKey(),regexId,number);}
            catch (FeignException e){
                logger.info("FeignException so try again");
                str=productInterface.findSpeNumberTop10(util.getBusinessKey(),regexId,number);
            }
              logger.info("[changeModule]number... is:"+str);
              List<Map<String,String>> map=JsonUtils.handlerNumberJson(str);
              arr.add(map);
              return  new Result(ResultMsg.OPERATESUCEESS,arr);
        }else {
            String  str=null;
            try{str= productInterface.findSpeNumberTop10(util.getBusinessKey(),regexId,"");
            }catch (FeignException e)
            {
                logger.info("FeignException so try again");
                str= productInterface.findSpeNumberTop10(util.getBusinessKey(),regexId,"");
            }
            List<Map<String,String>> map=JsonUtils.handlerNumberJson(str);
            logger.info("[changeModule]json map is:"+map.toString());
            arr.add(map);
        }
        //查找套餐
        String json= productInterface.findRegexProduct(util.getBusinessKey(),regexId);
        List<wx_product>  ls=JsonUtils.handlerRegexJson(json);
        arr.add(ls);
        return  new Result(ResultMsg.OPERATESUCEESS,arr);
    }

    /** arc_95013  */
    @RequestMapping("/callRecordService")
    @ResponseBody
    public  JSONObject  CallManager(CallReleaseVo vo){
        logger.info("[callRecordService]received Pram CallRecord vo is:{}"+vo.toString()+"\n");
        JSONObject  obj=new JSONObject();
        JSONObject  innerObj=new JSONObject();
        try {
             String json=JSONObject.toJSON(vo).toString();
             logger.info("[callRecordService]turn vo to json is{}:"+json+",business is:{}"+util.getBusinessKey()+"\n");
             String result=callRecordInterface.saveCallRecord(json,util.getBusinessKey());
             logger.info("[callRecordService]saveCallRecord by json and business then resultObj is:{}:"+result+"\n");
             logger.info("[callRecordService]arc_95013 vo.getCallId:"+vo.getCall_id()+"\n");
             innerObj.put("result",true);
             obj.put("secret_call_release_response",innerObj);
        }catch (Exception e){
            e.printStackTrace();
            logger.info("[callRecordService]save call record wrong.and exception is:"+e.getMessage()+"\n");
            innerObj.put("code",50);
            innerObj.put("msg","recordService exception");
            innerObj.put("sub_code","");
            innerObj.put("sub_msg","");
            obj.put("error_response",innerObj);
        }
        return obj;
    }


    @RequestMapping("/changeRelationMethod")
    public  Result   changeRelation(String uidNumber ,String restrict){
        logger.info("[changeRelationMethod]received pram  uidNumber,restrict is:"+uidNumber+","+restrict+"\n");
        if(StringUtils.isEmpty(uidNumber)&&StringUtils.isEmpty(restrict)){
            return   new  Result(ResultMsg.REQUESTPARAMEXCEPTION);
        }
        BusinessNumberRecord  record=recordRepository.findBySmbmsEqualsAndBusinessIdEquals(uidNumber,util.getBusinessKey());
        int  sumbms=record.getRegexId();
        String  rest="";
        if(sumbms==0){
            try {
                rest = numberInterface.changeRelation(util.getBusinessKey(), uidNumber, restrict);
            }catch (Exception e){
                e.printStackTrace();
                logger.info("[changeRelationMethod]changeRelation wrong and exception is:"+e.toString()+"\n");
                rest = numberInterface.changeRelation(util.getBusinessKey(), uidNumber, restrict);
            }
        }
        if(sumbms>0){
            try {
            rest=numberInterface.changeRelationZZ(util.getBusinessKey(),uidNumber,restrict);
            }catch (Exception e){
                e.printStackTrace();
                logger.info("[changeRelationMethod]changeRelation ZhiZun number exception is:"+e.toString()+"\n");
                rest=numberInterface.changeRelationZZ(util.getBusinessKey(),uidNumber,restrict);
            }
        }
        String result="";
        JSONObject obj=JSONObject.parseObject(rest);
        if(obj.getIntValue("code")==200){
            JSONObject  childObj=JSONObject.parseObject(obj.getString("msg"));
            result=childObj.getString("change_Relation_response");
            logger.info("[changeRelationMethod]code=200 and change_Relation_response:"+ResultMsg.OPERATESUCEESS+"\n");
            return  new Result(ResultMsg.OPERATESUCEESS,result);
        }else {
            logger.info("[changeRelationMethod]code!=200 andchange_Relation_response:"+ResultMsg.OPERATEXCEPTIN+"\n");
            return  new Result(ResultMsg.OPERATEXCEPTIN);
        }
    }

    @RequestMapping("/findOrder")
    @ResponseBody
    public JSONObject findOrder(@RequestParam("orderIndex") String orderIndex){
        JSONObject  obj=new JSONObject();
        logger.info("[findOrder]receive pram orderIndex is: "+orderIndex+"\n");
        String json= productInterface.findRegexProduct(util.getBusinessKey(),orderIndex);
        List<wx_product> products=JsonUtils.handlerRegexJson(json);
        products.forEach((wx_product item) -> {
            switch (item.getProductType()) {
                case "1":
                    item.setProductType("f_tiyan");
                    break;
                case "2":
                    item.setProductType("f_discount");
                    break;
                case "3":
                    item.setProductType("f_sales");
                    break;
            }
        });
        obj.put("products",products);
        Map<String,String> map=new HashMap<>();
        obj.put("user",map);
        logger.info("[findOrder]return result is :"+obj+"\n");
        return obj;
    }



    //领取优惠券
    @RequestMapping("/couponReceive")
    public ModelAndView testReceive(@RequestParam(value = "coupon" ) Long coupon,
                                    @RequestParam(value = "senderUser") String senderUser,
                                    @RequestParam(value = "receiveUser") String receiveUser){

        ModelAndView  model=new ModelAndView();
        //如未领取过同一用户发送的优惠券，优惠券领取表增加一条记录
        GiftCouponRecord couponRecord=giftCouponRecordRepository.findGiftCouponRecordBySenderOpenidEqualsAndReceiverOpenidEquals(senderUser,receiveUser);
        if(couponRecord==null){
            //优惠券领取记录表增加receiver的记录
            GiftCouponRecord couponRecordNew=new GiftCouponRecord();
            couponRecordNew.setSenderOpenid(senderUser);
            couponRecordNew.setReceiverOpenid(receiveUser);
            couponRecordNew.setGetTime(new Date());
            couponRecordNew.setIsUsed(0);
            couponRecordNew.setCouponId(coupon);
            giftCouponRecordRepository.saveAndFlush(couponRecordNew);
            GiftCoupon couponItem=giftCouponRepository.findByIdEquals(coupon);
            model.addObject("coupon",couponItem);
            //积分变更表增加receiver的记录
            GiftPointRecord pointRecord= new GiftPointRecord();
            pointRecord.setChangePoint(couponItem.getPoint());
            pointRecord.setOpenid(receiveUser);
            pointRecord.setResource(1);
            pointRecord.setUpdateTime(new Date());
            giftPointRecordRepository.saveAndFlush(pointRecord);
            //积分变更表增加sender的记录
            GiftPointRecord pointSenderRecord= new GiftPointRecord();
            pointSenderRecord.setChangePoint(couponItem.getPoint());
            pointSenderRecord.setOpenid(senderUser);
            pointSenderRecord.setResource(1);
            pointSenderRecord.setUpdateTime(new Date());
            giftPointRecordRepository.saveAndFlush(pointSenderRecord);
            //积分表修改receiver的积分
            GiftPoint userPointSearch=giftPointRepository.findByOpenidEquals(receiveUser);
            if(userPointSearch==null) {
                //从未得到过积分。
                GiftPoint userPoint = new GiftPoint();
                userPoint.setOpenid(receiveUser);
                userPoint.setPointTotal(couponItem.getPoint());
                userPoint.setPointUsed(0);
                giftPointRepository.saveAndFlush(userPoint);
            }else{
                //曾有积分，则增加本次分享所得积分。
                int oldUserPoint=userPointSearch.getPointTotal();
                userPointSearch.setPointTotal(oldUserPoint+couponItem.getPoint());
                giftPointRepository.saveAndFlush(userPointSearch);
            }
            //积分表修改sender的积分
            GiftPoint senderPointSearch=giftPointRepository.findByOpenidEquals(senderUser);
            if(senderPointSearch==null) {
                //该sender从未得到过积分。
                GiftPoint senderPoint = new GiftPoint();
                senderPoint.setOpenid(senderUser);
                senderPoint.setPointTotal(couponItem.getPoint());
                senderPoint.setPointUsed(0);
                giftPointRepository.saveAndFlush(senderPoint);
            }else{
                int oldSenderPoint=senderPointSearch.getPointTotal();
                senderPointSearch.setPointTotal(oldSenderPoint+couponItem.getPoint());
                giftPointRepository.saveAndFlush(senderPointSearch);
            }
            model.setViewName("shareGift/share_number_success");
        }else{
            logger.info("[couponReceive]you have received a coupon buy the same sender before.");
            //领取过优惠券。
            //优惠券领取记录表增加receiver的记录
            GiftCoupon couponItem=giftCouponRepository.findByIdEquals(coupon);
            model.addObject("coupon",couponItem);
            model.setViewName("shareGift/share_number_success");
        }
        return  model;
    }

    //当日分享给好友或朋友圈后领取积分奖励
    @RequestMapping("/receivePoint")
    public  void  receivePoint(@RequestParam(value = "couponId") Long couponId,
                                   @RequestParam(value = "openid") String openid){
        //如当天未分享过，则该用户增加一次积分。（查询积分变更表中是否存在resource=2的当天的记录。）
        Date today=new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(today);
        // 将分钟、秒、毫秒域清零
        cal1.set(Calendar.HOUR, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        Date todayReset = cal1.getTime();
        cal1.setTime(DateUtils.addDay(today,"1"));
        // 将分钟、秒、毫秒域清零
        cal1.set(Calendar.HOUR, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        Date todayPlusReset = cal1.getTime();
        logger.info("testReceivePoint date are:"+todayReset+","+todayPlusReset);

        GiftPointRecord couponRecord=giftPointRecordRepository.findByResourceEqualsAndUpdateTimeGreaterThanEqualAndUpdateTimeLessThanOrderByUpdateTimeDesc(2,todayReset,todayPlusReset);
        if(couponRecord==null) {
            GiftCoupon couponItem = giftCouponRepository.findByIdEquals(couponId);
            //积分变更表增加sender的记录
            GiftPointRecord pointSenderRecord = new GiftPointRecord();
            pointSenderRecord.setChangePoint(couponItem.getPoint());
            pointSenderRecord.setOpenid(openid);
            pointSenderRecord.setResource(2);
            pointSenderRecord.setUpdateTime(new Date());
            giftPointRecordRepository.saveAndFlush(pointSenderRecord);
            //积分表修改sender的积分
            GiftPoint senderPointSearch = giftPointRepository.findByOpenidEquals(openid);
            if (senderPointSearch == null) {
                //从未得到过积分。
                GiftPoint senderPoint = new GiftPoint();
                senderPoint.setOpenid(openid);
                senderPoint.setPointTotal(couponItem.getPoint());
                senderPoint.setPointUsed(0);
                giftPointRepository.saveAndFlush(senderPoint);
            } else {
                int oldSenderPoint = senderPointSearch.getPointTotal();
                senderPointSearch.setPointTotal(oldSenderPoint + couponItem.getPoint());
                giftPointRepository.saveAndFlush(senderPointSearch);
            }
        }else{
            logger.info("you have share before.");
        }
    }


    public static void main(String[] args) {
        CallReleaseVo  vo=new CallReleaseVo();
        vo.setCall_id("123");
    }




}
