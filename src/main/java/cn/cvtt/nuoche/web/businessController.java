package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.common.aop.LogManager;
import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.common.result.ResultMsg;
import cn.cvtt.nuoche.entity.AccessToken;
import cn.cvtt.nuoche.entity.business.BindVo;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.entity.business.BusinessNumberRecord;
import cn.cvtt.nuoche.entity.business.CallReleaseVo;
import cn.cvtt.nuoche.entity.business.SystemFeedBack;
import cn.cvtt.nuoche.entity.business.businessPayNotify;
import cn.cvtt.nuoche.entity.business.wx_product;
import cn.cvtt.nuoche.entity.gift.GiftAwards;
import cn.cvtt.nuoche.entity.gift.GiftAwardsRecords;
import cn.cvtt.nuoche.entity.gift.GiftAwardsRules;
import cn.cvtt.nuoche.entity.gift.GiftCoupon;
import cn.cvtt.nuoche.entity.gift.GiftCouponRecord;
import cn.cvtt.nuoche.entity.gift.GiftPoint;
import cn.cvtt.nuoche.entity.gift.GiftPointRecord;
import cn.cvtt.nuoche.entity.watch.NameCount;
import cn.cvtt.nuoche.facade.IBusinessCallRecordInterface;
import cn.cvtt.nuoche.facade.INumberInterface;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.ISystemParamInterface;
import cn.cvtt.nuoche.reponsitory.IBusinessCusRepository;
import cn.cvtt.nuoche.reponsitory.IBusinessNumberRecordRepository;
import cn.cvtt.nuoche.reponsitory.IBusinessPayRepository;
import cn.cvtt.nuoche.reponsitory.IGiftAwardsRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftAwardsRepository;
import cn.cvtt.nuoche.reponsitory.IGiftAwardsRulesRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCardRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCardRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCouponQrcodeRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCouponRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCouponRepository;
import cn.cvtt.nuoche.reponsitory.IGiftPointRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftPointRepository;
import cn.cvtt.nuoche.reponsitory.INamaCountRepository;
import cn.cvtt.nuoche.reponsitory.ISystemFeedBack;
import cn.cvtt.nuoche.server.impl.NumberServiceImpl;
import cn.cvtt.nuoche.service.QrcodeService;
import cn.cvtt.nuoche.util.ApiSignUtils;
import cn.cvtt.nuoche.util.ConfigUtil;
import cn.cvtt.nuoche.util.DateUtils;
import cn.cvtt.nuoche.util.HttpClientUtil;
import cn.cvtt.nuoche.util.JedisUtils;
import cn.cvtt.nuoche.util.JsonUtils;
import cn.cvtt.nuoche.util.LotteryUtil;
import cn.cvtt.nuoche.util.SmsValidateUtils;
import cn.cvtt.nuoche.util.WechatSignGenerator;
import cn.cvtt.nuoche.util.WxUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.cvtt.nuoche.server.WxServer.getAccessToken;
import static cn.cvtt.nuoche.util.SendPhone.MobileMessageCheck.checkMsg;
import static cn.cvtt.nuoche.util.SendPhone.SendMessage.send;
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
    @Autowired  private IBusinessNumberRecordRepository  businessNumberRecordRepository;
    @Autowired  private NumberServiceImpl numberService;
    @Autowired IGiftAwardsRecordRepository giftAwardsRecordRepository;
    @Autowired
    IGiftAwardsRepository giftAwardsRepository;
    @Autowired
    private INamaCountRepository iNamaCountRepository;
    @Resource
    private QrcodeService qrcodeService;
    private static final Logger logger = LoggerFactory.getLogger(businessController.class);

    @RequestMapping("/table/testSendCode")
    @ResponseBody
    public  Result  testSendCode(@RequestParam("phone") String phone) throws Exception {
        System.out.println("接收到的手机号码为："+phone);
        Result  result = new Result();
        String resultMsg=send(phone);
        System.out.println("result:"+resultMsg);
        JSONObject object=JSONObject.parseObject(resultMsg);
        String code=object.getString("code");
        if (StringUtils.equals(code,"200")) {
            result.setMsg("验证码已发到" + phone + "号码中，请查收");
            result.setCode(200);
            return result;
        } else if(StringUtils.equals(code,"315")) {
            result.setMsg("您绑定的手机号" + phone + "IP限制;");
        } else if (StringUtils.equals(code,"301")) {
            result.setMsg("您绑定的手机号" + phone + "被封禁!");
        } else if (StringUtils.equals(code,"403")) {
            result.setMsg("您绑定的手机号" + phone + "非法操作或没有权限!");
        } else if (StringUtils.equals(code,"404")) {
            result.setMsg("您绑定的手机号" + phone + "对象不存在!");
        } else if (StringUtils.equals(code,"414")) {
            result.setMsg("手机号码有误，请输入正确的手机号码!");
        } else if (StringUtils.equals(code,"500")) {
            result.setMsg("您绑定的手机号" + phone + "服务器内部错误!");
        } else if (StringUtils.equals(code,"408")) {
            result.setMsg("您绑定的手机号" + phone + "客户端请求超时!");
        } else if (StringUtils.equals(code,"419")) {
            result.setMsg("您绑定的手机号" + phone + "数量超过上限!");
        }else{
            result.setMsg("未知错误");
        }

        System.out.println("resultFinal:"+resultMsg);
        result.setData(phone);
        result.setCode(0);
        return result;
    }

    @RequestMapping("/table/verifyCode")
    @ResponseBody
    public  Result  verifyCode(@RequestParam("phone") String phone,@RequestParam("code") String code) throws Exception {
        System.out.println("接收到的手机号码为："+phone+","+code);
        Result  result = new Result();
        String resultMsg=checkMsg(phone,code);
        System.out.println("resultMsg:"+resultMsg);
        result.setData(phone);
        result.setCode(0);
        result.setMsg(resultMsg);
        return result;
    }



    @RequestMapping("/table/user2/")
    @ResponseBody
    public  Result  tableUser(){

        Result  result = new Result();
        List<NameCount> nameCounts=iNamaCountRepository.findAll();
        System.out.println("result:"+nameCounts);
        JSONArray json = new JSONArray();
        for(NameCount a : nameCounts){
            JSONObject jo = new JSONObject();
            jo.put("id", a.getId());
            jo.put("name", a.getName());
            jo.put("openid", a.getOpenid());
            jo.put("createTime", a.getCreateTime());
            json.add(jo);
        }
        System.out.println("result jsonArray:"+json);
        result.setData(nameCounts);
        result.setCount(nameCounts.size());
        result.setCode(0);
        return result;
    }
    @RequestMapping("/table/user/{days}/")
    @ResponseBody
    public  Result  nameCount(@PathVariable String days){
        Result  result = new Result();
        List<NameCount> nameCounts = new ArrayList<>();
        List _list;
        if(StringUtils.equals("all",days)){
            _list=iNamaCountRepository.findGroupByName();
        }else {
            Date endDate=DateUtils.addDay(new Date(),"-"+days);
            _list=iNamaCountRepository.findGroupByNameAndTime(endDate);
        }

        for(Object row:_list){
            Object[] cells = (Object[]) row;
            NameCount orderGoods = new NameCount();
            orderGoods.setNum(cells[0].toString() );
            orderGoods.setName((String) cells[1]);
            nameCounts.add(orderGoods);
        }

        JSONArray json = new JSONArray();
        for(NameCount a : nameCounts){
            JSONObject jo = new JSONObject();
            jo.put("name", a.getName());
            jo.put("number", a.getNum());
            json.add(jo);
        }
        System.out.println("result jsonArray:"+json);
        result.setData(nameCounts);
        result.setCount(nameCounts.size());
        result.setCode(0);




        return result;
    }

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

    @RequestMapping("/validPhone")
    @ResponseBody
    @LogManager(description ="validPhone")
    public  Result   validPhone(String openid,String  code,String phone) throws Exception {
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
            loadUserInfo(customer);
            businessCusRepository.saveAndFlush(customer);
        }catch (Exception  e){
            e.printStackTrace();
//            logger.info("validPhone method:There is an error when find customer by openid.So ResultMsg:"+ResultMsg.WXUSERNOTFOUND);
//            return new Result(ResultMsg.WXUSERNOTFOUND);
            /**  先删除 已经存在的用户 再保存**/
            BusinessCustomer  cus=new BusinessCustomer();
            cus.setOpenid(openid);
            cus.setCreateTime(new Date());
            cus.setIsAble(1);
            cus.setPhone(phone);
            cus.setPassword(ApiSignUtils.getMessageMD5("123456"));
            loadUserInfo(cus);
            businessCusRepository.saveAndFlush(cus);

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
    public Result createGenerateOrder(@RequestParam("openid") String openid,
                                      @RequestParam("totalFee") String totalFee,
                                      @RequestParam("phone") String  phone,
                                      @RequestParam("uidNumber")  String uidNumber,
                                      @RequestParam("extend") String extend,
                                      @RequestParam("days") String days,
                                      @RequestParam(value = "hours",defaultValue="0") String hours,
                                      @RequestParam("productId") Integer productId, HttpServletRequest request){
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
         * 判断是否超过当天限购的次数
         *
         * */
        Date today=new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(today);
        // 将分钟、秒、毫秒域清零
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        Date todayReset = cal1.getTime();
        cal1.setTime(DateUtils.addDay(today,"1"));
        // 将分钟、秒、毫秒域清零
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        Date todayPlusReset = cal1.getTime();
        logger.info("[createGenerateOrder]businessPayNotify search prams are:"+util.getBusinessKey()+","+phone+","+productId+","+todayReset+","+todayPlusReset);
        List<businessPayNotify>  ls=payRepository.findAllByBusinessEqualsAndPhoneEqualsAndProductIdEqualsAndCreateTimeGreaterThanEqualAndCreateTimeLessThan
                (util.getBusinessKey(),phone,productId,todayReset,todayPlusReset);
        logger.info("[createGenerateOrder]businessPayNotify list size is:"+ls.size());
        String BusinessJson=productInterface.findProductById(productId);
        JSONObject  productObj=JSONObject.parseObject(BusinessJson);
        logger.info("[createGenerateOrder]productObj  is:"+productObj);
        if(productObj.getIntValue("code")==200){
            JSONObject product=JSONObject.parseObject(productObj.getString("data"));
            String  limit=product.getString("productLimit");
            logger.info("[createGenerateOrder]limit  is:"+limit);
            if(StringUtils.isNotEmpty(product.getString("productLimit"))){
                //当天查询的限购次数记录>=后台设置的限购次数
                if(ls.size()>=Integer.parseInt(limit)){
                    logger.info("[createGenerateOrder]limit record is:"+ls.size());
                    return new Result(ResultMsg.MORETHANBUYNUMBER);
                }
            }
        }
        /** end*/
        String  bill_ip=  WechatSignGenerator.getIpAddr(request);
        totalFee=totalFee.substring(1);
        double  Fee=Double.parseDouble(totalFee)*100;
        Result  pojo=new Result();
        logger.info("[create Wechat Order method],request Fee is: "+Fee+"\n");
            String response = "";
            String noId = UUID.randomUUID().toString().replace("-", "");
        //调用微信签名接口
        if(Fee>0) {
            try {
                response = WechatSignGenerator.sign(openid, new DecimalFormat("#").format(Fee), bill_ip, util.getBusiness(), noId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info("[createOrder method]:WechatSignGenerator's response is:" + response + "\n");
            /** 根据生成的订单信息返回wexin支付的对象  */
            JSONObject obj = WechatSignGenerator.getPayRequest(response);
            logger.info("[createOrder method] WechatSignGenerator's json response is:" + obj + "\n");
            if (!(Boolean) obj.get("status")) {
                pojo.setCode(500);
                pojo.error("支付失败，请重新支付。");
                logger.info("[createOrder method]:pojo is:" + pojo.toString() + "\n");
                /** 根据生成的订单信息返回wexin支付的对象  */
                return pojo;
            }
            pojo.setCode(200);
            pojo.setData(obj);
        }else{
            //价格为0时不调用微信签名接口
            pojo.setCode(888);
        }
        businessPayNotify  pay=new businessPayNotify();
        pay.setOpenid(openid);
        pay.setPhone(phone);
        pay.setCreateTime(new Date());
        pay.setOutTradeNo(noId);
        pay.setOperateType(extend);
        pay.setDays(days);
        pay.setUidNumber(uidNumber);
        pay.setProductId(productId);
        payRepository.save(pay);
        logger.info("[createOrder method]just payRepository.save(pay),fianlly return pojo Result is:"+pojo.toString()+"\n");
        pojo.setMsg(pay.getId().toString());
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
    public Result createGiftOrder(@RequestParam("openid") String openid,
                                  @RequestParam("totalFee") String totalFee,
                                  @RequestParam("phone") String  phone,
                                  @RequestParam("uidNumber")  String uidNumber,
                                  @RequestParam("extend") String extend,
                                  @RequestParam("days") String days,HttpServletRequest request){
        logger.info("you are in createGiftOrder method: "+"\n");
        /** 查看该用户绑定是否超过后台配置的最大绑定次数,超过则不让绑定*/
//        String  json=systemParamInterface.getSystemConfigByArgs(2,util.getBusinessKey());
//        logger.info("[createGiftOrder method]:received RequestParam extend is:"+extend+"\n");
//        Map<String,String>  map=JsonUtils.handlerJson(json);
//        String  str=map.get("NUMBER_BIND_LIMIT");
//        if(StringUtils.isNotEmpty(str)){
//            logger.info("[createGiftOrder method]:NUMBER_BIND_LIMIT is:"+str+"\n");
//            BusinessCustomer  bcs=businessCusRepository.findByOpenidEquals(openid);
//            String countJson=callRecordInterface.CountBindNumber(util.getBusinessKey(),bcs.getPhone());
//            logger.info("[createGiftOrder method]:countJson is:"+countJson+"\n");
//            JSONObject  childObj=JSONObject.parseObject(countJson);
//            if(childObj.getIntValue("code")==200){
//                int  size=JSONObject.parseObject(childObj.getString("data")).getIntValue("size");
//                if(size>Integer.parseInt(str)){
//                    return  new Result(ResultMsg.MORETHANBINDNUMBER);
//                }
//            }
//        }
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
    public  Result  changeModule(String regexId,String number,int page,int size){
        JSONArray  arr=new JSONArray();
        //查找号码
        if(StringUtils.isNotEmpty(number)){
            //查找所有号码
            //String  str=productInterface.findSpeNumber(util.getBusinessKey(),regexId,number);
            //查找top10
            logger.info("[changeModule]isNotEmpty(number)"+page+size);
            String  str=null;
            try{str=productInterface.findSpeNumberTop10(util.getBusinessKey(),regexId,number,page,size,UUID.randomUUID().toString());}
            catch (FeignException e){
                logger.info("FeignException so try again");
                str=productInterface.findSpeNumberTop10(util.getBusinessKey(),regexId,number,page,size,UUID.randomUUID().toString());
            }
              logger.info("[changeModule]number... is:"+str);
              List<Map<String,String>> map=JsonUtils.handlerNumberJson(str);
              arr.add(map);
              return  new Result(ResultMsg.OPERATESUCEESS,arr);
        }else {
            logger.info("[changeModule]isEmpty(number),page,size:"+page+size);
            String  str=null;
            try{str= productInterface.findSpeNumberTop10(util.getBusinessKey(),regexId,"",page,size,UUID.randomUUID().toString());
                logger.info("[changeModule]str:"+str);
            }catch (FeignException e)
            {
                logger.info("FeignException so try again");
                str= productInterface.findSpeNumberTop10(util.getBusinessKey(),regexId,"",page,size,UUID.randomUUID().toString());
            }
            List<Map<String,String>> map=JsonUtils.handlerNumberJson(str);
            logger.info("[changeModule]json map is:"+map.toString());
            arr.add(map);
        }
        //查找套餐
        String json= productInterface.findRegexProductByType(util.getBusinessKey(),regexId,"1");
        List<wx_product>  ls=JsonUtils.handlerRegexJson(json);
        arr.add(ls);
        return  new Result(ResultMsg.OPERATESUCEESS,arr);
    }

    /** arc_77889  */
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
             logger.info("[callRecordService]arc_77889 vo.getCallId:"+vo.getCall_id()+"\n");
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
    //根据原来号码查询原号码的套餐。
    @RequestMapping("/findOrder")
    @ResponseBody
    public JSONObject findOrder(@RequestParam("orderIndex") String orderIndex){
        JSONObject  obj=new JSONObject();
        logger.info("[findOrder]receive pram regexId is: "+orderIndex+"\n");
        String json= productInterface.findRegexProductByType(util.getBusinessKey(),orderIndex,"1");
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
    //根据regexId分别普通号和靓号查询相应套餐
    @RequestMapping("/findRegexOrder")
    @ResponseBody
    public JSONObject findRegexOrder(@RequestParam("orderIndex") String regexId){
        JSONObject  obj=new JSONObject();
        logger.info("[findRegexOrder]receive pram regexId is: "+regexId+"\n");
            String json= productInterface.findRegexProductByType(util.getBusinessKey(),regexId,"4");
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
//            GiftPointRecord pointRecord= new GiftPointRecord();
//            pointRecord.setChangePoint(couponItem.getPoint());
//            pointRecord.setOpenid(receiveUser);
//            pointRecord.setResource(1);
//            pointRecord.setUpdateTime(new Date());
//            giftPointRecordRepository.saveAndFlush(pointRecord);
            //积分变更表增加sender的记录
            GiftPointRecord pointSenderRecord= new GiftPointRecord();
            pointSenderRecord.setChangePoint(couponItem.getPoint());
            pointSenderRecord.setOpenid(senderUser);
            pointSenderRecord.setResource(1);
            pointSenderRecord.setRecordId(couponRecordNew.getId());
            pointSenderRecord.setUpdateTime(new Date());
            giftPointRecordRepository.saveAndFlush(pointSenderRecord);
            //积分表修改receiver的积分
//            GiftPoint userPointSearch=giftPointRepository.findByOpenidEquals(receiveUser);
//            if(userPointSearch==null) {
//                //从未得到过积分。
//                GiftPoint userPoint = new GiftPoint();
//                userPoint.setOpenid(receiveUser);
//                userPoint.setPointTotal(couponItem.getPoint());
//                userPoint.setPointUsed(0);
//                giftPointRepository.saveAndFlush(userPoint);
//            }else{
//                //曾有积分，则增加本次分享所得积分。
//                int oldUserPoint=userPointSearch.getPointTotal();
//                userPointSearch.setPointTotal(oldUserPoint+couponItem.getPoint());
//                giftPointRepository.saveAndFlush(userPointSearch);
//            }
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
            model.addObject("isAlert",false);
            model.setViewName("shareGift/share_number_success");
        }else{
            logger.info("[couponReceive]you have received a coupon buy the same sender before.");
            //领取过优惠券。
            //优惠券领取记录表增加receiver的记录
            GiftCoupon couponItem=giftCouponRepository.findByIdEquals(coupon);
            model.addObject("coupon",couponItem);
            model.addObject("isAlert",true);
            model.setViewName("shareGift/share_number_success");
        }
        return  model;
    }

    //当日分享给好友后领取积分奖励
    @RequestMapping("/receivePoint")
    public  void  receivePoint(@RequestParam(value = "couponId") Long couponId,
                                   @RequestParam(value = "openid") String openid,
                                   @RequestParam(value = "resource") Integer resource){
        //如当天未分享过，则该用户增加一次积分。（查询积分变更表中是否存在resource=2的当天的记录。）
        Date today=new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(today);
        // 将分钟、秒、毫秒域清零
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        Date todayReset = cal1.getTime();
        cal1.setTime(DateUtils.addDay(today,"1"));
        // 将分钟、秒、毫秒域清零
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        Date todayPlusReset = cal1.getTime();
        logger.info("testReceivePoint date are:"+todayReset+","+todayPlusReset);
        logger.info("resource is:"+resource);
        GiftPointRecord couponRecord=giftPointRecordRepository.findByResourceEqualsAndUpdateTimeGreaterThanEqualAndUpdateTimeLessThanAndOpenidEqualsOrderByUpdateTimeDesc(resource,todayReset,todayPlusReset,openid);
        if(couponRecord==null) {
            GiftCoupon couponItem = giftCouponRepository.findByIdEquals(couponId);
            //积分变更表增加sender的记录
            GiftPointRecord pointSenderRecord = new GiftPointRecord();
            pointSenderRecord.setChangePoint(couponItem.getPoint());
            pointSenderRecord.setOpenid(openid);
            pointSenderRecord.setResource(resource);
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
            logger.info("couponRecord"+couponRecord.getUpdateTime());
            logger.info("you have share before.");
        }
    }
    //使用优惠券0元购买至尊号
    @RequestMapping("/bindNumberZZ")
    public  Result   bindNumberZZMethod(String couponRecordId ,String uidNumber,String days,String openid) throws ParseException {
        logger.info("[bindNumberZZMethod]receive prams:couponRecordId,uidNumber,days,phone:"+couponRecordId+","+uidNumber+","+days+","+openid);
        //支付成功后删除优惠券，即isUsed设置为1.
        if(!StringUtils.isEmpty(couponRecordId)) {
            logger.info("[card_give.html]couponRecordId is:"+couponRecordId);
            GiftCouponRecord giftCouponRecord = giftCouponRecordRepository.findGiftCouponRecordByIdEquals(Long.parseLong(couponRecordId));
            giftCouponRecord.setIsUsed(1);
            giftCouponRecordRepository.saveAndFlush(giftCouponRecord);
        }
        //根据openid查找用户信息
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(openid);

        //绑定数据信息
        BindVo bindVo = new BindVo();
        bindVo.setUidnumber(uidNumber);
        bindVo.setRegphone(user.getPhone());
        bindVo.setExpiretime(days);
        //绑定咕咕、删除号码池数据、numberRecord写入数据
        Result result = null;
        try {
            result=numberService.bindZhiZun(bindVo);
        } catch (IOException e) {
            //调用绑定接口失败
            return  new Result(ResultMsg.OPERATEXCEPTIN);
        }
        if(result.getCode()==200){
                    //删除号码池中的号码。
                    String  json=productInterface.deleteSpeNumber(util.getBusinessKey(),uidNumber);
                    String regexId=JsonUtils.handlerNumberReturnRegexJson(json);
                    //获取返回结果。
                    JSONObject jobjZhiZun = JSONObject.parseObject(result.getData().toString());
                    JSONObject resZhiZun = jobjZhiZun.getJSONObject("binding_Relation_response");
                    if (null == resZhiZun) {
                        return  new Result(ResultMsg.OPERATEXCEPTIN);
                    }
                    //保存记录
                    BusinessNumberRecord  record2=new BusinessNumberRecord();
                    record2.setBusinessId(util.getBusinessKey());
                    record2.setPrtms(resZhiZun.getString("prtms"));
                    record2.setSmbms(resZhiZun.getString("smbms"));
                    record2.setResult(1);
                    record2.setCallrestrict(0+"");
                    record2.setSubts(new Date());
                    record2.setUserPhone(user.getPhone());
                    record2.setValidTime(DateUtils.parse(resZhiZun.getString("validitytime")));
                    record2.setRegexId(Integer.parseInt(StringUtils.equals(regexId,"")?"0":regexId));
                    businessNumberRecordRepository.save(record2);
                }
        return  new Result(ResultMsg.OPERATESUCEESS);
    }

    //0元购买体验普通号，绑定接口。
    @RequestMapping("/bindNumberNormal")
    public  Result   bindNumberNormal(String uidNumber,String phone,String days,String hours,String productId,String openid,String notifyId) throws ParseException {
        logger.info("[bindNumberNormal]receive prams:uidNumber,days,phone:"+","+uidNumber+","+days+","+openid+","+hours+",days:"+days+",productId:"+productId);
        //根据openid查找用户信息
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(openid);
        //绑定数据信息
        BindVo bindVo = new BindVo();
        bindVo.setUidnumber(uidNumber);
        bindVo.setRegphone(phone);
        //根据小时数设置有效期
        Date validTime=DateUtils.addHour(new Date(),hours);
        String validTimePrase=DateUtils.format(validTime);
        logger.info("[bindNumberNormal]"+validTimePrase);
        bindVo.setExpiretime(validTimePrase);
        //绑定咕咕、删除号码池数据、numberRecord写入数据
        Result result = null;
        try {
            result=numberService.bind(bindVo);
        } catch (IOException e) {
            //调用绑定接口失败
            return  new Result(ResultMsg.OPERATEXCEPTIN);
        }
        if(result.getCode()==200){
            //获取返回结果。
            JSONObject jobjZhiZun = JSONObject.parseObject(result.getData().toString());
            JSONObject resZhiZun = jobjZhiZun.getJSONObject("binding_Relation_response");
            if (null == resZhiZun) {
                return  new Result(ResultMsg.OPERATEXCEPTIN);
            }
            //save模拟微信购买信息记录
            businessPayNotify  order=payRepository.findByOpenidEqualsAndId(openid,Long.parseLong(notifyId));
            String  return_code="SUCCESS";
            order.setOpenid(openid);
            order.setAppid(Constant.APP_ID);
            order.setSubscribed("1");
            order.setCashFee(0);
            order.setResultCode("SUCCESS");
            order.setReturnCode("SUCCESS");
            order.setResultCode(return_code);
            order.setFeeType("CNY");
            order.setMchId(Constant.MCH_ID);
            order.setTime_end(DateUtils.format(new Date()));
            order.setTotal_fee("0");
            //order.setTransactionId("");
            //order.setTrade_type(info.get("trade_type"));
            order.setBusiness(util.getBusinessKey());
            payRepository.saveAndFlush(order);
            //保存绑定记录
            BusinessNumberRecord  record2=new BusinessNumberRecord();
            record2.setBusinessId(util.getBusinessKey());
            record2.setPrtms(resZhiZun.getString("prtms"));
            record2.setSmbms(resZhiZun.getString("smbms"));
            record2.setResult(1);
            record2.setCallrestrict(0+"");
            record2.setSubts(new Date());
            record2.setUserPhone(user.getPhone());
            record2.setValidTime(DateUtils.parse(resZhiZun.getString("validitytime")));
            record2.setRegexId(0);
            businessNumberRecordRepository.save(record2);

        }
        return  new Result(ResultMsg.OPERATESUCEESS);
    }
    /**
     * 抽奖
     *
     * @return
     */
    @RequestMapping("/lottery")
    @ResponseBody
    public  Result  lotteryMethod(String openid){
        BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals(openid);
        //查询当前九宫格所使用的活动。
        GiftAwardsRules activeNow=giftAwardsRulesRepository.findByIsAbleEquals(1);
        //当前活动抽一次奖消耗的积分。
        int usePoints=activeNow.getPoints();
        //根据openid查找用户当前积分情况。
        GiftPoint userPointsInfo=giftPointRepository.findByOpenidEquals(openid);
        //抽奖次数,当前积分，奖品index。
        int times;
        int userPoints=userPointsInfo.getPointTotal();
        int userPointsJS;
        int resultIndex;
        if(userPointsInfo!=null){
            userPointsJS=userPointsInfo.getPointTotal()-userPointsInfo.getPointUsed();
            //计算可抽奖次数
            double a=userPointsJS;
            double b=usePoints;
            double c = a/b;
            times=(int)Math.floor(c);
            logger.info("[lottery]userPoints/usePoints=times:"+a+"/"+b+"="+times);
            if(times>0){
                //当前活动的所有活动奖品。
                List<GiftAwards> awards=giftAwardsRepository.findByRulesIdOrderByIndexOrder(activeNow.getId());
                //得到各奖品的概率列表
                List<Double> orignalRates = new ArrayList<Double>(awards.size());
                for (GiftAwards award : awards) {
                    logger.info("[lottery]awards now:"+award.getGiftName());
                    //库存
                    Integer remainNumer = award.getStock();
                    //概率
                    Double probability = award.getProbability();
                    if (remainNumer==null||remainNumer <= 0) {//剩余数量为零，需使它不能被抽到
                        probability = new Double(0);
                    }
                    if(probability==null){
                        probability = new Double(0);
                    }
                    orignalRates.add(probability);
                }
                //根据概率等计算奖品结果。
                //根据概率产生奖品
                GiftAwards tuple = new GiftAwards();
                int orignalRatesIndex = LotteryUtil.lottery(orignalRates);
                logger.info("[lottery]orignalRatesIndex:"+orignalRatesIndex);
                if (orignalRatesIndex>=0) {//中奖啦
                    logger.info("[lottery]awards name:"+tuple.getGiftName());
                    tuple= awards.get(orignalRatesIndex);
                }else{
                    //未中奖时，默认中奖产品为index为0的奖品。
                    tuple=giftAwardsRepository.findByRulesIdEqualsAndIndexOrderEquals(activeNow.getId(),0);
                }
                //生成用户的抽奖记录。
                GiftAwardsRecords awardsRecords=new GiftAwardsRecords();
                awardsRecords.setAwardsId(tuple.getId());
                awardsRecords.setAwardsName(tuple.getGiftName());
                awardsRecords.setGetTime(new Date() );
                awardsRecords.setNickname(userInfo.getNickname());
                awardsRecords.setOpenid(openid);
                awardsRecords.setPhone(userInfo.getPhone());
                giftAwardsRecordRepository.saveAndFlush(awardsRecords);
                //返回前台的奖品顺序（indexOrder）
                resultIndex=tuple.getIndexOrder();
                //减少奖品的库存。
                tuple.setStock(tuple.getStock()-1);
                giftAwardsRepository.saveAndFlush(tuple);
                //修改用户已使用的积分。
                logger.info("a+b=c"+userPointsInfo.getPointUsed()+"+"+usePoints+"="+(userPointsInfo.getPointUsed()+usePoints));
                userPointsInfo.setPointUsed(userPointsInfo.getPointUsed()+usePoints);
                //修改用户总积分，抽奖后的剩余积分
                logger.info("userPoints-usePoints:"+userPoints+"-"+usePoints);
                userPoints=userPoints-usePoints;
                logger.info("setPointTotal:"+userPoints);
                //userPointsInfo.setPointTotal(userPoints);
                logger.info("save userPointsInfo id:"+userPointsInfo.getId());
                giftPointRepository.saveAndFlush(userPointsInfo);
                //用户积分历史记录表变更。
                GiftPointRecord userPointsRecord=new GiftPointRecord();
                userPointsRecord.setChangePoint(-usePoints);
                userPointsRecord.setOpenid(openid);
                userPointsRecord.setResource(3);
                userPointsRecord.setUpdateTime(new Date());
                userPointsRecord.setRecordId(awardsRecords.getId());
                giftPointRecordRepository.saveAndFlush(userPointsRecord);
                //最新用户总积分
                int userPointsFinal=giftPointRepository.findByOpenidEquals(openid).getPointTotal()-giftPointRepository.findByOpenidEquals(openid).getPointUsed();
                //剩余可抽奖次数
                times=times-1;
                //返回前台数据
                Map<String,Object> map=new HashMap<>();
                map.put("resultIndex",resultIndex);
                map.put("times",times);
                map.put("userPoints",userPointsFinal);
                return  new Result(ResultMsg.OPERATESUCEESS,map);
            }else{
                resultIndex=0;
                times=0;
            }
        }else{
            //用户未获得过积分
            resultIndex=0;
            times=0;
            userPoints=0;
        }
        //返回前台数据
        Map<String,Object> map=new HashMap<>();
        map.put("resultIndex",resultIndex);
        map.put("times",times);
        map.put("userPoints",userPoints);
        return  new Result(ResultMsg.OPERATEXCEPTIN,map);
    }

    public static void main(String[] args) {
        CallReleaseVo  vo=new CallReleaseVo();
        vo.setCall_id("123");
    }




}
