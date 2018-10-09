package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.common.result.ResultMsg;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.entity.business.BusinessNumberRecord;
import cn.cvtt.nuoche.entity.business.SystemFeedBack;
import cn.cvtt.nuoche.entity.business.wx_product;
import cn.cvtt.nuoche.entity.gift.*;
import cn.cvtt.nuoche.facade.IBusinessCallRecordInterface;
import cn.cvtt.nuoche.facade.INumberInterface;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.IRegexInterface;
import cn.cvtt.nuoche.reponsitory.*;
import cn.cvtt.nuoche.service.QrcodeService;
import cn.cvtt.nuoche.util.ConfigUtil;
import cn.cvtt.nuoche.util.DateUtils;
import cn.cvtt.nuoche.util.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import cn.cvtt.nuoche.server.WxServer;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.cvtt.nuoche.util.WechatSignGenerator.jsapiSign;

@Controller
public class TestController extends  BaseController {
    @Autowired
    ConfigUtil   util;
    @Autowired
    IBusinessNumberRecordRepository  recordRepository;
    @Autowired
    IRegexInterface  regexInterface;
    @Autowired
    INumberInterface numberInterface;
    @Autowired
    ISystemFeedBack feedBackRepository;
    @Autowired
    IBusinessCallRecordInterface callRecordInterface;
    @Autowired
    IBusinessCusRepository   businessCusRepository;
    @Autowired
    IGiftCouponRepository giftCouponRepository;
    @Autowired
    IGiftCouponRecordRepository giftCouponRecordRepository;
    @Autowired
    IGiftPointRecordRepository giftPointRecordRepository;
    @Autowired
    IGiftPointRepository giftPointRepository;
    @Autowired
    IGiftCardRepository giftCardRepository;
    @Autowired
    IGiftCardRecordRepository giftCardRecordRepository;
    @Autowired
    IGiftCouponQrcodeRepository giftCouponQrcodeRepository;
    @Resource
    private QrcodeService qrcodeService;
    @Value("${wx.qrcode.download}")
    private  String   qrcodeDownload;

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    @RequestMapping("/getAll")
    @ResponseBody
    public  String   getRegex(String  business){
      return   regexInterface.findRegexByBusiness(business);
    }


    @RequestMapping("/getHeard")
    @ResponseBody
    public  String    getHeard(){
        String  string=callRecordInterface.findHeard("nuoche","95013340000297");
        return string;
    }


   /* @Autowired
    IProductRespository productRespository;*/

    @Autowired
    IProductInterface  productInterface;

    @RequestMapping("/testInterface")
    @ResponseBody
    public  String  testInterface(){
        //String str= productInterface.findProduct("nuoche");
        String str=callRecordInterface.findHeard("nuoche","95013340000297");
        return  str;
    }

    @RequestMapping("/testRegex")
    public  ModelAndView  testRegex(){
        ModelAndView  modelAndView=new ModelAndView();
        modelAndView.setViewName("buy_zhizun");
        String json=regexInterface.findRegexByBusiness(util.getBusinessKey());
        //  String json= productInterface.findProduct(util.getBusinessKey());
        List<Map<String,String>> map=JsonUtils.handlerNormalJson(json,"id","regexName");
        modelAndView.addObject("regexs",map);
        String productJson= productInterface.findRegexProduct(util.getBusinessKey(),map.get(0).get("key"));
        List<wx_product>  ls=JsonUtils.handlerRegexJson(productJson);
        modelAndView.addObject("products",ls);
        String numberJson=productInterface.findSpeNumber(util.getBusinessKey(),map.get(0).get("key"),"");
        List<Map<String,String>>  numbers=JsonUtils.handlerNumberJson(numberJson);
        modelAndView.addObject("numbers",numbers);
        Map<String,String> user=new HashMap<>();
        user.put("openid","oIFn90xXM4M-zUayrLI4hxLGZNKA");
        BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals("oIFn90xXM4M-zUayrLI4hxLGZNKA");
        modelAndView.addObject("phone",userInfo.getPhone());
        modelAndView.addObject("user",user);
        return  modelAndView;
    }

    @RequestMapping("" +
            "")
    public  ModelAndView  testSafeNumber(){
        ModelAndView  modelAndView=new ModelAndView();
        modelAndView.setViewName("buy_safenumber");
        String json= productInterface.findRegexProduct(util.getBusinessKey(),"0");
        List<wx_product> products=JsonUtils.handlerRegexJson(json);
               /* JSONObject  obj=JSONObject.parseObject(json);
                List<wx_product> products=new ArrayList<>();
                if(obj.getIntValue("code")==200){
                    JsonUtils.handlerArgs(products,obj);
                }*/
        modelAndView.addObject("ls",products);
        BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals("oIFn90xXM4M-zUayrLI4hxLGZNKA");
        modelAndView.addObject("phone",userInfo.getPhone());
        /****===>>**/
        Map<String, String> map = new HashMap<>();
        map.put("openid", "oIFn90xXM4M-zUayrLI4hxLGZNKA");
        modelAndView.addObject("user", map);
        return  modelAndView;
    }

    @RequestMapping("/validate_tel")
    public  String  goValidate(){

        return  "validate_tel";
    }
    //拨号盘
    @RequestMapping("/keyboardTest")
    public  String  keyboardTest(){

        return  "keyboard";
    }

    @RequestMapping("/testMessage")
    public  ModelAndView  message(){
        ModelAndView  model=new ModelAndView();
        String openid="oIFn90393PZMsIt-kprqw0GWmVko";
        BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals(openid);
        model.addObject("user",userInfo);
        model.setViewName("suggest");
        return  model;
    }
    //每日积分菜单接口，giftShareNumber
    @RequestMapping("/testWongPage")
    public  ModelAndView  testWong(){
        ModelAndView  model=new ModelAndView();
        //授权登录，获取头像
        String openid="oIFn90393PZMsIt-kprqw0GWmVko";
        BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals(openid);
        model.addObject("user",userInfo);
        GiftCoupon coupon=giftCouponRepository.findByIsAbleEquals(1);
        if(coupon==null){
            GiftCoupon couponNew=new GiftCoupon();
            couponNew.setAmount(0);
            couponNew.setPoint(0);
            couponNew.setId(0L);
            model.addObject("coupon",couponNew);
        }else{
            model.addObject("coupon",coupon);
        }
        //生成二维码
        GiftCouponQrcode giftCouponQrcode=giftCouponQrcodeRepository.findByCouponIdEquals(coupon.getId());
        if(giftCouponQrcode==null){
            logger.info("[create qrcode]"+"create qrcode");
            GiftCouponQrcode giftCouponQrcodeNew=new GiftCouponQrcode();
            giftCouponQrcodeNew.setCouponId(coupon.getId());
            giftCouponQrcodeNew.setCreateTime(new Date());
            giftCouponQrcodeNew.setCreatorOpenid(openid);
            GiftCouponQrcode giftCouponQrcodeFinal=giftCouponQrcodeRepository.saveAndFlush(giftCouponQrcodeNew) ;
            String qrcodeHref = qrcodeService.generatorQrcode(giftCouponQrcodeFinal.getId(),"coupon");
            model.addObject("href",qrcodeHref);
        }else{
            String qrcodeHref =giftCouponQrcode.getQrcodeUrl();
            model.addObject("href",qrcodeHref);
        }
        model.setViewName("shareGift/share_number");
        return  model;
    }

    //转发朋友圈后跳转页面
    @RequestMapping("/giftReturn")
    public  ModelAndView  giftReturn(@RequestParam(value ="couponId") Long couponId, @RequestParam(value ="senderId") String senderId){
        ModelAndView  model=new ModelAndView();
        //朋友圈转发后
        String receiverOpenid="oIFn90393PZMsIt-kprqw0GWmVko";
        logger.info("[giftReturn]couponId is:"+couponId+"senderId is:"+senderId);
        BusinessCustomer receiveUser= businessCusRepository.findByOpenidEquals(receiverOpenid);
        model.addObject("receiveUser",receiveUser);
        BusinessCustomer senderUser= businessCusRepository.findByOpenidEquals(senderId);
        model.addObject("senderUser",senderUser);
        GiftCoupon coupon=giftCouponRepository.findByIdEquals(couponId);
        model.addObject("coupon",coupon);
        model.addObject("openid",receiverOpenid);
        model.setViewName("shareGift/share_number_info");
        return  model;
    }


    //领取优惠券测试页面
    @RequestMapping("/couponReceiveTest")
    public ModelAndView testReceiveTest(@RequestParam(value = "coupon" ,defaultValue = "1") Long coupon,
                                    @RequestParam(value = "senderUser",defaultValue="oIFn90393PZMsIt-kprqw0GWmVko") String senderUser,
                                    @RequestParam(value = "receiveUser",defaultValue="oIFn90393PZMsIt-kprqw0GWmVko") String receiveUser){

        ModelAndView  model=new ModelAndView();
        //如未领取过同一用户发送的优惠券，优惠券领取表增加一条记录
        GiftCouponRecord couponRecord=giftCouponRecordRepository.findGiftCouponRecordBySenderOpenidEqualsAndReceiverOpenidEquals(senderUser,receiveUser);
        if(couponRecord==null){
            //优惠券领取记录表增加receiver的记录
            GiftCouponRecord couponRecordNew=new GiftCouponRecord();
            couponRecordNew.setSenderOpenid(senderUser);
            couponRecordNew.setReceiverOpenid(receiveUser);
            couponRecordNew.setGetTime(new Date());
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
    @RequestMapping("/testReceivePoint")
    public  void  testReceivePoint(@RequestParam(value = "couponId" ,defaultValue = "1") Long couponId,
                                   @RequestParam(value = "openid" ,defaultValue = "oIFn90393PZMsIt-kprqw0GWmVko") String openid){
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

        GiftPointRecord couponRecord=giftPointRecordRepository.findByResourceEqualsAndUpdateTimeGreaterThanEqualAndUpdateTimeLessThan(2,todayReset,todayPlusReset);
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

    //选择两种套餐赠送页面
    @RequestMapping("/testCard")
    public  ModelAndView  testReceivePoint(){
        ModelAndView  model=new ModelAndView();
        model.addObject("openid","oIFn90393PZMsIt-kprqw0GWmVko");
        model.setViewName("shareGift/gift");
        return  model;
    }
    //套餐卡页面
    @RequestMapping("/testRegexGift")
    public  ModelAndView  testRegexGiftMethod(@RequestParam(value ="isHideOldDiv",defaultValue ="false") boolean isHideOldDiv,
                                              @RequestParam(value ="cardId",defaultValue ="noId") String cardId,
                                              @RequestParam(value ="openid") String openid){
        ModelAndView  model=new ModelAndView();
        logger.info("[testRegexGift]isHideOldDiv is:"+isHideOldDiv);
        if(!StringUtils.equals(cardId,"noId")){
          long cardIdSearch=Long.parseLong(cardId);
          GiftCard card=giftCardRepository.findByIdEquals(cardIdSearch);
          //可购买的套餐名称
            JSONObject eachGiftArray= JSONObject.parseObject(card.getRegexId());
            //遍历套餐，获取套餐名字
            String regexName="";
            for(String str:eachGiftArray.keySet()){
                regexName=regexName+str+",";
                logger.info("[testRegexGift]eachGiftRegex is:"+regexName);
            }
            String finalRegexName=regexName.substring(0,regexName.length()-1);
            logger.info("[testRegexGift]finalRegexName is:"+finalRegexName);
            card.setRegexName(finalRegexName);
            //判断有效期是年、月、日
            switch(card.getValidTimeUnit()){
                case 1:
                    card.setValidTimeUnitName("日");
                    break;
                case 2:
                    card.setValidTimeUnitName("月");
                    break;
                case 3:
                    card.setValidTimeUnitName("年");
                    break;
            }
          model.addObject("card",card);
        }else{
            GiftCard card=new  GiftCard();
            card.setId(0l);
            model.addObject("card",card);
        }
        model.addObject("isHideOldDiv",isHideOldDiv);
        //查找该用户所有的优惠券。
        List<GiftCouponRecord> giftRecordList=giftCouponRecordRepository.findAllByReceiverOpenidEquals(openid);
        for(GiftCouponRecord eachGiftCouponRecord :giftRecordList)
        {
            Long couponId=eachGiftCouponRecord.getCouponId();
            GiftCoupon giftCoupon=giftCouponRepository.findByIdEquals(couponId);
            eachGiftCouponRecord.setGiftCoupon(giftCoupon);
        }
        model.addObject("giftRecord",giftRecordList);
        model.addObject("openid",openid);
        model.setViewName("shareGift/gift_card");
        return  model;
    }
    //送套餐卡选套餐
    @RequestMapping("/testChooseRegex")
    public  ModelAndView  testChooseRegex(@RequestParam(value ="openid") String openid){
        ModelAndView  model=new ModelAndView();
//送套餐卡的规则数据
//        JSONObject obj=new JSONObject();
//        obj.put("三连号",1);
//        obj.put("四连号",2);
//        GiftCard giftCard=new GiftCard();
//        giftCard.setRegexId(obj.toString());
//        giftCard.setCardName("大吉大利卡");
//        giftCard.setCardType(1);
//        giftCard.setPrice(5000);
//        giftCard.setValidTimeNumber(1);
//        giftCard.setValidTimeUnit(1);
//        giftCardRepository.save(giftCard);
        //查询type是套餐卡的所有套餐
        List<GiftCard>  giftCardList=giftCardRepository.findAllByCardTypeEquals(1);
        for(GiftCard eachGift :giftCardList)
        {
            JSONObject eachGiftArray= JSONObject.parseObject(eachGift.getRegexId());
            //遍历套餐，获取套餐名字
            String regexName="";
            for(String str:eachGiftArray.keySet()){
                regexName=regexName+str+",";
                logger.info("[testChooseRegex]eachGiftRegex is:"+regexName);
            }
            String finalRegexName=regexName.substring(0,regexName.length()-1);
            logger.info("[testChooseRegex]finalRegexName is:"+finalRegexName);
            eachGift.setRegexName(finalRegexName);
            //判断有效期是年、月、日
            switch(eachGift.getValidTimeUnit()){
                case 1:
                    logger.info("[testChooseRegex]eachGift getValidTimeUnit is:"+1);
                    eachGift.setValidTimeUnitName("日");
                    break;
                case 2:
                    logger.info("[testChooseRegex]eachGift getValidTimeUnit is:"+2);
                    eachGift.setValidTimeUnitName("月");
                    break;
                case 3:
                    logger.info("[testChooseRegex]eachGift getValidTimeUnit is:"+3);
                    eachGift.setValidTimeUnitName("年");
                    break;
            }
        }
        //遍历套餐id,查询套餐名字。
        model.addObject("giftCardList",giftCardList);
        model.addObject("openid",openid);
        model.setViewName("shareGift/card_choice");
        return  model;
    }

    //选中套餐卡
    @RequestMapping("/testChooseCard")
    public  String  testChooseCard(@RequestParam("cardId") String cardId,
                                   @RequestParam("openid") String openid){
        if(cardId!=null){
            logger.info("[testChooseCard]testChooseCard is:"+cardId);
        }
        boolean isHideOldDiv=true;
        return  "redirect:"+"testRegexGift?isHideOldDiv="+isHideOldDiv+"&cardId="+cardId+"&openid="+openid;
    }

    //分享套餐卡页面
    @RequestMapping("/card_give.html")
    public  ModelAndView  cardGive(@RequestParam(value ="openid",defaultValue ="0") String SenderOpenid,
                                   @RequestParam(value ="cardId") String cardId,
                                   @RequestParam(value ="message",defaultValue ="") String message){
        ModelAndView  model=new ModelAndView();
        //支付成功后将套餐卡信息保存数据库中
        GiftCardRecord giftCardRecord=new GiftCardRecord();
        giftCardRecord.setSenderOpenid(SenderOpenid);
        giftCardRecord.setMessage(message);
        giftCardRecord.setCardId(Long.parseLong(cardId));
        giftCardRecord.setGetStatus(0);
        GiftCardRecord cardRecordId=giftCardRecordRepository.saveAndFlush(giftCardRecord);
        //加载分享页面所需要的数据。
        GiftCard card=giftCardRepository.findByIdEquals(Long.parseLong(cardId));
        //可购买的套餐名称
        JSONObject eachGiftArray= JSONObject.parseObject(card.getRegexId());
        //遍历套餐，获取套餐名字
        String regexName="";
        for(String str:eachGiftArray.keySet()){
            regexName=regexName+str+",";
            logger.info("[cardGive]eachGiftRegex is:"+regexName);
        }
        String finalRegexName=regexName.substring(0,regexName.length()-1);
        logger.info("[cardGive]finalRegexName is:"+finalRegexName);
        card.setRegexName(finalRegexName);
        model.addObject("card",card);
        model.addObject("cardRecordId",cardRecordId.getId());
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(SenderOpenid);
        model.addObject("user",user);
        model.addObject("message",message);
        model.setViewName("shareGift/card_give");
        return  model;
    }
    //分享号码卡页面
    @RequestMapping("/testNumberGift")
    public ModelAndView  numberGiftMethod( ){
        ModelAndView  model=new ModelAndView();
        model.setViewName("shareGift/gift_number");
        return  model;
    }
    //选中号码卡,加载号码卡套餐等数据。
    @RequestMapping("/testChooseNumberRegex")
    public  ModelAndView  testChooseNumberRegex(@RequestParam("openid") String openid){
        ModelAndView  model=new ModelAndView();
        //String json=regexInterface.findRegexByBusiness(util.getBusinessKey());
        String json=regexInterface.findRegexByBusiness(util.getBusinessKey());
        List<Map<String,String>> map=JsonUtils.handlerNormalJson(json,"id","regexName");
        model.addObject("regexs",map);
        model.addObject("openid",openid);
        logger.info("[testChooseNumberRegex]map is:"+map.toString());
        //加载user
        model.setViewName("shareGift/number_choice");
        return  model;
    }

    //赠送号码卡、留言、选优惠券等。
    @RequestMapping("/testNumberRegex")
    public  ModelAndView  testNumberGiftMethod(@RequestParam(value ="isHideOldDiv",defaultValue ="false") boolean isHideOldDiv,
                                              @RequestParam(value ="chooseNumber",defaultValue ="0") String chooseNumber,
                                               @RequestParam(value ="openid") String openid) {
        ModelAndView  model=new ModelAndView();
         //openid="oIFn90393PZMsIt-kprqw0GWmVko";
        //查找该用户所有的优惠券。
        List<GiftCouponRecord> giftRecordList=giftCouponRecordRepository.findAllByReceiverOpenidEquals(openid);
        for(GiftCouponRecord eachGiftCouponRecord :giftRecordList)
        {
            Long couponId=eachGiftCouponRecord.getCouponId();
            GiftCoupon giftCoupon=giftCouponRepository.findByIdEquals(couponId);
            eachGiftCouponRecord.setGiftCoupon(giftCoupon);
        }
        model.addObject("giftRecord",giftRecordList);
        if(!StringUtils.equals(chooseNumber,"0")){
            logger.info("[testNumberRegex]pram are:"+util.getBusinessKey()+","+chooseNumber);
            String number=numberInterface.searchNumber(util.getBusinessKey(),chooseNumber);
            Map<String,String> mapNumber=JsonUtils.handlerOriginalNumberJson(number);
            model.addObject("mapNumber",mapNumber);
            logger.info("[testNumberRegex]mapNumber is:"+mapNumber);
        }else{
            Map<String,String> mapNumber=new HashMap<>();
            mapNumber.put("number","");
            mapNumber.put("numberPrice","0");
            model.addObject("mapNumber",mapNumber);
        }
        model.addObject("openid",openid);
        model.addObject("isHideOldDiv",isHideOldDiv);
        model.setViewName("shareGift/gift_number");
        return  model;
    }

    //分享号码卡页面
    @RequestMapping("/number_give.html")
    public  ModelAndView  numberGive(@RequestParam(value ="openid",defaultValue ="0") String SenderOpenid,
                                     @RequestParam(value ="number") String number,
                                     @RequestParam(value ="message",defaultValue ="") String message){
        ModelAndView  model=new ModelAndView();
        //生成号码卡规则数据
        GiftCard giftCard=new GiftCard();
        giftCard.setCardName("号码卡");
        giftCard.setCardType(2);
        giftCard.setNumber(number);
        giftCard.setPrice(5000);
        giftCard.setValidTimeNumber(1);
        giftCard.setValidTimeUnit(3);
        GiftCard Cardid=giftCardRepository.save(giftCard);
        //支付成功后将套餐卡信息保存数据库中
        GiftCardRecord giftCardRecord=new GiftCardRecord();
        giftCardRecord.setSenderOpenid(SenderOpenid);
        giftCardRecord.setMessage(message);
        giftCardRecord.setGetStatus(0);
        giftCardRecord.setCardId(Cardid.getId());
        GiftCardRecord cardRecordId=giftCardRecordRepository.saveAndFlush(giftCardRecord);
        //加载分享页面所需要的数据。
        GiftCard card=giftCardRepository.findByIdEquals(Cardid.getId());
        model.addObject("card",card);
        model.addObject("cardRecordId",cardRecordId.getId());
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(SenderOpenid);
        model.addObject("user",user);
        model.addObject("message",message);
        model.setViewName("shareGift/gift_give");
        return  model;
    }
    //gift模块扫二维码领取号码卡、套餐卡接口、通过qrcodeId查找号码卡的id。
    @RequestMapping("/sweep")
    public String  toCallReceivePage(String id){
        logger.info("[sweep]"+id);
        String type=StringUtils.substringAfter(id,"_");
        String realId=StringUtils.substringBefore(id,"_");
        if(StringUtils.equals(type,"card") ){
            GiftCardRecord giftCardRecord = giftCardRecordRepository.findByQrcodeEquals(realId);
            Long cardRecordId=giftCardRecord.getId();
            return  "redirect:"+"qrcodeAfter?cardRecordId="+cardRecordId;
        }else if(StringUtils.equals(type,"coupon")){
            GiftCouponQrcode giftCouponRecord = giftCouponQrcodeRepository.findByQrcodeEquals(realId);
            Long couponId=giftCouponRecord.getCouponId();
            String senderId=giftCouponRecord.getCreatorOpenid();
            return  "redirect:"+"oauth/gift/giftReturn?couponId="+couponId+"&senderId="+senderId;
        }else return null;

    }
    //gift扫描二维码后领取页面
    @RequestMapping("/qrcodeAfter")
    public ModelAndView  qrcodeAfter(@RequestParam("cardRecordId") Long cardRecordId){
        ModelAndView  model=new ModelAndView();
        //根据cardId查询qrcode，如果值为空，则未生成过二维码。
        GiftCardRecord giftCardRecord=giftCardRecordRepository.findByIdEquals(cardRecordId);
        if(giftCardRecord.getQrcode()==null){
            //生成二维码
            logger.info("[qrcode]"+"create qrcode");
            String qrcodeHref = qrcodeService.generatorQrcode(cardRecordId,"card");
            model.addObject("href",qrcodeHref);
        }else{
            //提取二维码href
            logger.info("[qrcode]"+"find qrcode");
            String qrcodeHref=giftCardRecord.getQrcodeUrl();
            model.addObject("href",qrcodeHref);
            logger.info("[qrcode]"+model.getModel().get("href"));
        }
        //根据cardType的不同跳转到不同的显示页面。
        Long cardId=giftCardRecord.getCardId();
        GiftCard giftCard=giftCardRepository.findByIdEquals(cardId);
        if(giftCard.getCardType()==1){
            //可购买的套餐名称
            JSONObject eachGiftArray= JSONObject.parseObject(giftCard.getRegexId());
            //遍历套餐，获取套餐名字
            String regexName="";
            for(String str:eachGiftArray.keySet()){
                regexName=regexName+str+",";
                logger.info("[qrcode]eachGiftRegex is:"+regexName);
            }
            String finalRegexName=regexName.substring(0,regexName.length()-1);
            logger.info("[qrcode]finalRegexName is:"+finalRegexName);
            giftCard.setRegexName(finalRegexName);
            model.addObject("card",giftCard);
            BusinessCustomer user= businessCusRepository.findByOpenidEquals(giftCardRecord.getSenderOpenid());
            model.addObject("user",user);
            model.addObject("giftCardRecord",giftCardRecord);
            model.setViewName("shareGift/recive_card");
            //model.setViewName("shareGift/card_qrcode");
        }else{
            //加载分享页面所需要的数据。
            model.addObject("card",giftCard);
            BusinessCustomer user= businessCusRepository.findByOpenidEquals(giftCardRecord.getSenderOpenid());
            model.addObject("user",user);
            model.addObject("giftCardRecord",giftCardRecord);
            model.setViewName("shareGift/recive_gift");
            //model.setViewName("shareGift/gift_qrcode");
        }
        return model;
    }
    //页面点击生成礼品卡接口。生成相应的二维码。
    @RequestMapping("/qrcode")
    public ModelAndView  qrcode(@RequestParam("cardRecordId") Long cardRecordId){
        ModelAndView  model=new ModelAndView();
        //根据cardId查询qrcode，如果值为空，则未生成过二维码。
        GiftCardRecord giftCardRecord=giftCardRecordRepository.findByIdEquals(cardRecordId);
        if(giftCardRecord.getQrcode()==null){
            //生成二维码
            logger.info("[qrcode]"+"create qrcode");
            String qrcodeHref = qrcodeService.generatorQrcode(cardRecordId,"card");
            model.addObject("href",qrcodeHref);
        }else{
            //提取二维码href
            logger.info("[qrcode]"+"find qrcode");
            String qrcodeHref=giftCardRecord.getQrcodeUrl();
            model.addObject("href",qrcodeHref);
            logger.info("[qrcode]"+model.getModel().get("href"));
        }
        //根据cardType的不同跳转到不同的显示页面。
        Long cardId=giftCardRecord.getCardId();
        GiftCard giftCard=giftCardRepository.findByIdEquals(cardId);
        if(giftCard.getCardType()==1){
            //可购买的套餐名称
            JSONObject eachGiftArray= JSONObject.parseObject(giftCard.getRegexId());
            //遍历套餐，获取套餐名字
            String regexName="";
            for(String str:eachGiftArray.keySet()){
                regexName=regexName+str+",";
                logger.info("[qrcode]eachGiftRegex is:"+regexName);
            }
            String finalRegexName=regexName.substring(0,regexName.length()-1);
            logger.info("[qrcode]finalRegexName is:"+finalRegexName);
            giftCard.setRegexName(finalRegexName);
            model.addObject("card",giftCard);
            BusinessCustomer user= businessCusRepository.findByOpenidEquals(giftCardRecord.getSenderOpenid());
            model.addObject("user",user);
            model.addObject("giftCardRecord",giftCardRecord);
           // model.setViewName("shareGift/recive_card");
           model.setViewName("shareGift/card_qrcode");
        }else{


            //加载分享页面所需要的数据。
            model.addObject("card",giftCard);
            BusinessCustomer user= businessCusRepository.findByOpenidEquals(giftCardRecord.getSenderOpenid());
            model.addObject("user",user);
            model.addObject("giftCardRecord",giftCardRecord);
           // model.setViewName("shareGift/recive_gift");
           model.setViewName("shareGift/gift_qrcode");
        }
        return model;
    }
    //gift模块扫二维码领取号码卡、套餐卡接口、通过qrcodeId查找号码卡的id。
    @RequestMapping("/testSweep")
    public String  toTestCallReceivePage(String id){
        logger.info("[sweep]"+id);
        String type=StringUtils.substringAfter(id,"_");
        String realId=StringUtils.substringBefore(id,"_");
        if(StringUtils.equals(type,"card") ){
            GiftCardRecord giftCardRecord = giftCardRecordRepository.findByQrcodeEquals(realId);
            Long cardRecordId=giftCardRecord.getId();
            return  "redirect:"+"qrcode?cardRecordId="+cardRecordId;
        }else if(StringUtils.equals(type,"coupon")){
            GiftCouponQrcode giftCouponRecord = giftCouponQrcodeRepository.findByQrcodeEquals(realId);
            Long couponId=giftCouponRecord.getCouponId();
            String senderId=giftCouponRecord.getCreatorOpenid();
            return  "redirect:"+"giftReturn?couponId="+couponId+"&senderId="+senderId;
        }else return null;

    }
    //领取号码卡、套餐卡成功的页面
    @RequestMapping("/receiveCardSuccess")
    public ModelAndView  receiveCardSuccess(@RequestParam("giftCardRecordId") Long giftCardRecordId,
                                            @RequestParam("giftCardId") Long giftCardId,
                                            @RequestParam("openid") String openid){
        ModelAndView  model=new ModelAndView();
        //根据95号查找号码卡record，填写receiver。
        GiftCardRecord giftCardRecord=giftCardRecordRepository.findByIdEquals(giftCardRecordId);
        giftCardRecord.setReceiverOpenid(openid);
        giftCardRecordRepository.saveAndFlush(giftCardRecord);

        GiftCard giftCard=giftCardRepository.findByIdEquals(giftCardId);
        //根据95号码查询有效期等。
        BusinessNumberRecord  numberRecord=recordRepository.findBySmbmsEqualsAndBusinessIdEquals(giftCard.getNumber(),util.getBusinessKey());
        Date validTime=numberRecord.getValidTime();
        Date  now=new Date();
        //使用更改接口更改手机号。（查询是否更改成功。修改userphone和phone）

        model.addObject("openid",openid);
        model.addObject("giftCard",giftCard);
        model.addObject("giftCardRecord",giftCardRecord);
        model.setViewName("shareGift/recive_gift_info_success");
        return  model;
    }
    //历史记录
    @RequestMapping("/my_gift_give.html")
    public ModelAndView  giftRecord(@RequestParam("openid") String openid){
        ModelAndView  model=new ModelAndView();
        //测试数据
        openid="oIFn90393PZMsIt-kprqw0GWmVko";
        //根据openId查询记录
        //自己未领取
        List<GiftCardRecord> giftCardRecordList =giftCardRecordRepository.findByGetStatusEqualsAndSenderOpenidEqualsOrderByGetTimeDesc(0,openid);
        logger.info("[giftRecord]GiftCardRecord size is:"+giftCardRecordList.size());
        List<Map<String,Object>> RegexCard=new ArrayList<>();
        List<Map<String,Object>> NumberCard=new ArrayList<>();
        if(giftCardRecordList.size()>0){
            for(GiftCardRecord giftCardRecord :giftCardRecordList)
            {
                Long cardId=giftCardRecord.getCardId();
                GiftCard giftCard=giftCardRepository.findByIdEquals(cardId);
                if(giftCard.getCardType()==2){
                    //号码卡
                    Map<String,Object>map2=new HashedMap();
                    map2.put("cardName",giftCard.getNumber());
                    map2.put("cardMessage",giftCardRecord.getMessage());
                    map2.put("price",giftCard.getPrice());
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String buyTime=simpleFormat.format(giftCardRecord.getBuyTime());
                    map2.put("buyTime",buyTime);
                    NumberCard.add(map2);
                }else if(giftCard.getCardType()==1){
                    //套餐卡
                    Map<String,Object>map1=new HashedMap();
                    map1.put("cardName",giftCard.getCardName());
                    map1.put("cardMessage",giftCardRecord.getMessage());
                    map1.put("price",giftCard.getPrice());
                    //可购买的套餐名称
                    JSONObject eachGiftArray= JSONObject.parseObject(giftCard.getRegexId());
                    //遍历套餐，获取套餐名字
                    String regexName="";
                    for(String str:eachGiftArray.keySet()){
                        regexName=regexName+str+",";
                    }
                    String finalRegexName=regexName.substring(0,regexName.length()-1);
                    logger.info("[my_gift_give]finalRegexName is:"+finalRegexName);
                    map1.put("finalRegexName",finalRegexName);
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String buyTime=simpleFormat.format(giftCardRecord.getBuyTime());
                    map1.put("buyTime",buyTime);
                    RegexCard.add(map1);
                }
                model.addObject("myGiftRegexCardRecordList",RegexCard);
                model.addObject("myGiftNumberCardRecordList",NumberCard);
            }
        }
        //自己已领取
        List<GiftCardRecord> giftCardRecordList2 =giftCardRecordRepository.findByGetStatusEqualsAndSenderOpenidEqualsOrderByGetTimeDesc(1,openid);
        List<Map<String,Object>> cardHave=new ArrayList<>();
        if(giftCardRecordList2.size()>0) {
            for(GiftCardRecord giftCardRecord :giftCardRecordList2)
            {
                logger.info("receiver openid:"+giftCardRecord.getReceiverOpenid());
                BusinessCustomer receiver= businessCusRepository.findByOpenidEquals(giftCardRecord.getReceiverOpenid());
                Long cardId=giftCardRecord.getCardId();
                GiftCard giftCard=giftCardRepository.findByIdEquals(cardId);
                if(giftCard.getCardType()==2){
                    //号码卡
                    Map<String,Object>haveMap1=new HashedMap();
                    haveMap1.put("cardName",giftCard.getNumber());
                    haveMap1.put("cardMessage",giftCardRecord.getMessage());
                    haveMap1.put("price",giftCard.getPrice());
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String buyTime=simpleFormat.format(giftCardRecord.getBuyTime());
                    haveMap1.put("buyTime",buyTime);
                    SimpleDateFormat simpleFormat2 = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
                    String getTime=simpleFormat2.format(giftCardRecord.getGetTime());
                    haveMap1.put("getTime",getTime);
                    haveMap1.put("receiverImg",receiver.getHeadimgurl());
                    haveMap1.put("receiverNickname",receiver.getNickname());
                    cardHave.add(haveMap1);
                }else if(giftCard.getCardType()==1){
                    //套餐卡
                    Map<String,Object>haveMap2=new HashedMap();
                    haveMap2.put("cardName",giftCard.getCardName());
                    haveMap2.put("cardMessage",giftCardRecord.getMessage());
                    haveMap2.put("price",giftCard.getPrice());
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String buyTime=simpleFormat.format(giftCardRecord.getBuyTime());
                    haveMap2.put("buyTime",buyTime);
                    SimpleDateFormat simpleFormat2 = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
                    String getTime=simpleFormat2.format(giftCardRecord.getGetTime());
                    haveMap2.put("getTime",getTime);
                   logger.info("receiver size is:"+receiver.toString());
                    haveMap2.put("receiverNickname",receiver.getNickname());
                    haveMap2.put("receiverImg",receiver.getHeadimgurl());
                    cardHave.add(haveMap2);
                }
                model.addObject("cardHave",cardHave);
            }
        }
        //他人
        List<GiftCardRecord> giftCardOtherRecordList =giftCardRecordRepository.findByGetStatusEqualsAndReceiverOpenidEqualsOrderByGetTimeDesc(1,openid);
        List<Map<String,Object>> cardReceive=new ArrayList<>();
        if(giftCardOtherRecordList.size()>0) {
            for(GiftCardRecord giftCardRecord :giftCardOtherRecordList)
            {
                logger.info("receiver openid:"+giftCardRecord.getReceiverOpenid());
                BusinessCustomer sender= businessCusRepository.findByOpenidEquals(giftCardRecord.getSenderOpenid());
                Long cardId=giftCardRecord.getCardId();
                GiftCard giftCard=giftCardRepository.findByIdEquals(cardId);
                if(giftCard.getCardType()==0){
                    //号码卡
                    Map<String,Object>receiveMap1=new HashedMap();
                    receiveMap1.put("cardName",giftCard.getNumber());
                    receiveMap1.put("cardNumber",giftCard.getNumber());
                    receiveMap1.put("cardMessage",giftCardRecord.getMessage());
                    receiveMap1.put("price",giftCard.getPrice());
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try{
                        String buyTime=simpleFormat.format(giftCardRecord.getBuyTime());
                        receiveMap1.put("buyTime",buyTime);
                    }catch(Exception e){}
                    SimpleDateFormat simpleFormat2 = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
                    String getTime=simpleFormat2.format(giftCardRecord.getGetTime());
                    receiveMap1.put("getTime",getTime);
                    receiveMap1.put("receiverImg",sender.getHeadimgurl());
                    receiveMap1.put("receiverNickname",sender.getNickname());
                    cardReceive.add(receiveMap1);
                }else if(giftCard.getCardType()==1){
                    //套餐卡
                    Map<String,Object>receiveMap2=new HashedMap();
                    receiveMap2.put("cardName",giftCard.getCardName());
                    receiveMap2.put("cardNumber",giftCard.getNumber());
                    receiveMap2.put("cardMessage",giftCardRecord.getMessage());
                    receiveMap2.put("price",giftCard.getPrice());
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try{
                        String buyTime=simpleFormat.format(giftCardRecord.getBuyTime());
                        receiveMap2.put("buyTime",buyTime);
                    }catch(Exception e){}
                    SimpleDateFormat simpleFormat2 = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
                    String getTime=simpleFormat2.format(giftCardRecord.getGetTime());
                    receiveMap2.put("getTime",getTime);
                    logger.info("receiver size is:"+sender.toString());
                    receiveMap2.put("receiverNickname",sender.getNickname());
                    receiveMap2.put("receiverImg",sender.getHeadimgurl());
                    cardReceive.add(receiveMap2);
                }
                model.addObject("cardReceive",cardReceive);
            }
        }
        model.setViewName("shareGift/my_gift_give");
        return  model;
    }

    //抽奖
    @RequestMapping("/lottery")
    public  ModelAndView  lottery( @RequestParam(value = "openid",defaultValue = "oIFn90393PZMsIt-kprqw0GWmVko") String openid){
        ModelAndView  model=new ModelAndView();
        model.addObject("openid",openid);
        GiftPoint userPointSearch=giftPointRepository.findByOpenidEquals(openid);
        //计算可抽奖次数
        double oldPoint=userPointSearch.getPointTotal();
        double a=10;
        double times = oldPoint/a;
        int clickNumber=(int)Math.floor(times);
        model.addObject("clickNumber",clickNumber);
        model.addObject("index",4);
        model.setViewName("shareGift/lottery");
        return  model;
    }

    @RequestMapping("/OwnerSafeNumber.html")
    public ModelAndView  OwnerSafeNumber( ){
        ModelAndView  model=new ModelAndView();
        String openid="oIFn90393PZMsIt-kprqw0GWmVko";
        BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals(openid);
        String  url= userInfo.getHeadimgurl();
        model.addObject("url",url);
        model.addObject("phone",userInfo.getPhone());
        Date now=DateUtils.addDay(new Date(),"-60");
        List<BusinessNumberRecord>  ls=recordRepository.findAllByPrtmsEqualsAndBusinessIdEqualsAndValidTimeGreaterThanEqualOrderByValidTimeDesc(userInfo.getPhone(),util.getBusinessKey(),now);
        handlerList(model,ls);
        model.addObject("ls",ls);
        logger.info("record is:"+ls.get(0).getIsValid().toString());
        List<BusinessNumberRecord>  other=recordRepository.findAllByPrtmsNotAndUserPhoneEqualsAndBusinessIdEqualsAndValidTimeGreaterThanEqualOrderByValidTimeDesc(userInfo.getPhone(),userInfo.getPhone(),util.getBusinessKey(),now);

        model.addObject("other",other);
        handlerOther(other);
       /* List<wx_product> products= productRespository.findAll();
        model.addObject("products",products);
       */
        String businessId=util.getBusinessKey();
        logger.info("bussinessId is:"+businessId);
        String json= productInterface.findRegexProduct(businessId,"0");
        List<wx_product> products=JsonUtils.handlerRegexJson(json);
        model.addObject("products",products);
        Map<String,String> map=new HashMap<>();
        map.put("openid",openid);
        model.addObject("user",map);
        model.setViewName("my_safenumber");
        return model;
    }




       public  void  handlerOther(List<BusinessNumberRecord > records){
           for(BusinessNumberRecord  record:records){
               record.setTime(DateUtils.format(record.getValidTime(),"yyyy-MM-dd"));
           }
       }

        @SuppressWarnings("all")
       public  void  handlerList(ModelAndView  model,List<BusinessNumberRecord > records){
            model.addObject("sum",records.size());
            int  will=0;
            int  expired=0;
            for(BusinessNumberRecord  record:records){
                record.setIsValid(0);
                Long   time=record.getValidTime().getTime()/1000;
                Long   currentTime=System.currentTimeMillis()/1000;
                Long  result=time-currentTime;
                if((result)<3*24*60*60&&result>0){
                    will++;
                }
                if(record.getValidTime().getTime()<System.currentTimeMillis()){
                    expired++;
                    record.setIsValid(1);
                }
                record.setTime(DateUtils.format(record.getValidTime(),"yyyy-MM-dd"));
                System.out.println("自己的安全号:"+record.getRegexId().toString()+"record.prtms:"+record.getPrtms().toString()+"record.smbms:"+record.getSmbms().toString());
            }
            model.addObject("will",will);
            model.addObject("expired",expired);
       }


}
