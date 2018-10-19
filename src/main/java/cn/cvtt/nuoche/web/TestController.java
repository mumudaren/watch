package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.common.result.ResultMsg;
import cn.cvtt.nuoche.entity.business.BindVo;
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
import cn.cvtt.nuoche.server.impl.NumberServiceImpl;
import cn.cvtt.nuoche.service.QrcodeService;
import cn.cvtt.nuoche.util.ConfigUtil;
import cn.cvtt.nuoche.util.DateUtils;
import cn.cvtt.nuoche.util.JsonUtils;
import cn.cvtt.nuoche.util.LotteryUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import cn.cvtt.nuoche.server.WxServer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
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
    IGiftCardRulesRepository giftCardRulesRepository;
    @Autowired
    IGiftCardRecordRepository giftCardRecordRepository;
    @Autowired
    IGiftCouponQrcodeRepository giftCouponQrcodeRepository;
    @Autowired
    IGiftAwardsRulesRepository giftAwardsRulesRepository;
    @Autowired
    IGiftAwardsRecordRepository giftAwardsRecordRepository;
    @Autowired  private IBusinessNumberRecordRepository  businessNumberRecordRepository;
    @Autowired
    IGiftAwardsRepository giftAwardsRepository;
    @Autowired  private NumberServiceImpl numberService;
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


    //套餐卡、号码卡转发朋友圈后跳转页面
    @RequestMapping("/giftCardReturn")
    public  String  giftCardReturn(@RequestParam("cardRecordId") Long cardRecordId){
        //朋友圈转发后
        //根据cardId查询qrcode，如果值为空，则未生成过二维码。
        GiftCardRecord giftCardRecord=giftCardRecordRepository.findByIdEquals(cardRecordId);
        if(giftCardRecord.getQrcode()==null) {
            //生成二维码
            qrcodeService.generatorQrcode(cardRecordId, "card");
        }
        return "redirect:"+"sweep?id="+giftCardRecord.getQrcode()+"_card";
    }


    //领取优惠券测试页面,/couponReceive为真实接口
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
            couponRecordNew.setIsUsed(0);
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
            model.addObject("isAlert",false);
            model.setViewName("shareGift/share_number_success");
        }else{
            logger.info("[couponReceive]you have received a coupon buy the same sender before.");
            //领取过优惠券。
            //优惠券领取记录表增加receiver的记录
            GiftCoupon couponItem=giftCouponRepository.findByIdEquals(coupon);
            model.addObject("isAlert",true);
            model.addObject("coupon",couponItem);
            model.setViewName("shareGift/share_number_success");
        }
        return  model;
    }

    //当日分享给好友或朋友圈后领取积分奖励
    @RequestMapping("/testReceivePoint")
    public  void  testReceivePoint(@RequestParam(value = "couponId" ,defaultValue = "1") Long couponId,
                                   @RequestParam(value = "openid" ,defaultValue = "oIFn90393PZMsIt-kprqw0GWmVko") String openid,
                                   @RequestParam(value = "resource") Integer resource){
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
        logger.info("resource is:"+resource);
        logger.info("testReceivePoint date are:"+todayReset+","+todayPlusReset);
        GiftPointRecord couponRecord=giftPointRecordRepository.findByResourceEqualsAndUpdateTimeGreaterThanEqualAndUpdateTimeLessThanAndOpenidEqualsOrderByUpdateTimeDesc(resource,todayReset,todayPlusReset,openid);
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
          GiftCardRules card=giftCardRulesRepository.findByIdEquals(cardIdSearch);
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
          model.addObject("card",card);
        }else{
            GiftCard card=new  GiftCard();
            card.setId(0l);
            model.addObject("card",card);
        }
        model.addObject("isHideOldDiv",isHideOldDiv);
        //查找该用户所有未使用过的并且在有效期内的优惠券。
        //List<GiftCouponRecord> giftRecordList=giftCouponRecordRepository.findAllByReceiverOpenidEquals(openid);
        List<GiftCouponRecord> giftRecordList=giftCouponRecordRepository.findAllByReceiverOpenidEqualsAndIsUsedEqualsOrderByGetTimeDesc(openid,0);
        for(GiftCouponRecord eachGiftCouponRecord :giftRecordList)
        {
            Long couponId=eachGiftCouponRecord.getCouponId();
            GiftCoupon giftCoupon=giftCouponRepository.findByIdEquals(couponId);
            eachGiftCouponRecord.setGiftCoupon(giftCoupon);
        }
        model.addObject("giftRecord",giftRecordList);
        model.addObject("openid",openid);
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(openid);
        model.addObject("userPhone",user.getPhone());
        model.setViewName("shareGift/gift_card");
        return  model;
    }
    //送套餐卡选套餐
    @RequestMapping("/testChooseRegex")
    public  ModelAndView  testChooseRegex(@RequestParam(value ="openid") String openid){
        ModelAndView  model=new ModelAndView();
        //查询type是套餐卡的所有套餐
        List<GiftCardRules>  giftCardRulesList=giftCardRulesRepository.findAllByCardTypeEquals(1);
        for(GiftCardRules eachGift :giftCardRulesList)
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
        }
        //遍历套餐id,查询套餐名字。
        model.addObject("giftCardList",giftCardRulesList);
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

    //套餐卡支付成功后页面
    @RequestMapping("/card_give.html")
    public  ModelAndView  cardGive(@RequestParam(value ="openid",defaultValue ="0") String SenderOpenid,
                                   @RequestParam(value ="cardId") String cardId,
                                   @RequestParam(value ="couponRecordId") String couponRecordId,
                                   @RequestParam(value ="uidNumber") String uidNumber,
                                   @RequestParam(value ="message",defaultValue ="") String message){
        ModelAndView  model=new ModelAndView();
        logger.info("[card_give.html]this is card_give.html method.");
        GiftCardRules card=giftCardRulesRepository.findByIdEquals(Long.parseLong(cardId));
        //支付成功后删除优惠券，即isUsed设置为1.
        if(!StringUtils.isEmpty(couponRecordId)) {
            logger.info("[card_give.html]couponRecordId is:"+couponRecordId);
            GiftCouponRecord giftCouponRecord = giftCouponRecordRepository.findGiftCouponRecordByIdEquals(Long.parseLong(couponRecordId));
            giftCouponRecord.setIsUsed(1);
            giftCouponRecordRepository.saveAndFlush(giftCouponRecord);
        }
        //支付成功后保存新购买卡片。
        logger.info("[cardGive]uidNumber is:"+uidNumber);
        GiftCard card2=new GiftCard();
        card2.setRegexId(card.getRegexId());
        card2.setPrice(card.getPrice());
        card2.setCardName(card.getCardName());
        card2.setCardType(card.getCardType());
        card2.setNumber(uidNumber);
        //支付成功后将套餐卡信息保存数据库中
        GiftCardRecord giftCardRecord=new GiftCardRecord();
        giftCardRecord.setCardId(giftCardRepository.saveAndFlush(card2).getId());
        giftCardRecord.setSenderOpenid(SenderOpenid);
        giftCardRecord.setMessage(message);
        giftCardRecord.setGetStatus(0);
        giftCardRecord.setBuyTime(new Date());
        //加载分享页面所需要的数据。
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
        card2.setRegexName(finalRegexName);
        model.addObject("card",card2);
        model.addObject("cardRecordId",giftCardRecordRepository.saveAndFlush(giftCardRecord).getId());
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(SenderOpenid);
        model.addObject("user",user);
        model.addObject("message",message);
        model.setViewName("shareGift/card_give");
        return  model;
    }

    //套餐卡价格为0无需支付直接跳转的页面
    @RequestMapping("/card_give_price.html")
    public  ModelAndView  cardGivePrice(@RequestParam(value ="openid",defaultValue ="0") String SenderOpenid,
                                   @RequestParam(value ="cardId") String cardId,
                                   @RequestParam(value ="couponRecordId") String couponRecordId,
                                   @RequestParam(value ="uidNumber") String uidNumber,
                                   @RequestParam(value ="message",defaultValue ="") String message){
        ModelAndView  model=new ModelAndView();
        Date now=new Date();
        //当前用户
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(SenderOpenid);
        //正常情况微信支付成功后的操作
        //保存记录
        BusinessNumberRecord  record2=new BusinessNumberRecord();
        record2.setBusinessId(util.getBusinessKey());
        record2.setPrtms("010123456");
        //临时95号
        record2.setSmbms(uidNumber);
        record2.setResult(1);
        record2.setCallrestrict(0+"");
        record2.setSubts(now);
        record2.setUserPhone("010123456");
        //有效时间
        Date validTime=DateUtils.addDay(now,"365");
        record2.setValidTime(validTime);
        //临时套餐id
        record2.setRegexId(255);
        businessNumberRecordRepository.save(record2);
        //正常情况前台获取微信支付成功后的操作,cardId实际为cardRulesId，根据rules查找号码卡规则。
        GiftCardRules cardRules=giftCardRulesRepository.findByIdEquals(Long.parseLong(cardId));
        //支付成功后删除优惠券，即isUsed设置为1.
        if(!StringUtils.isEmpty(couponRecordId)) {
            GiftCouponRecord giftCouponRecord = giftCouponRecordRepository.findGiftCouponRecordByIdEquals(Long.parseLong(couponRecordId));
            giftCouponRecord.setIsUsed(1);
            giftCouponRecordRepository.saveAndFlush(giftCouponRecord);
        }
        //支付成功后保存新购买卡片。
        logger.info("[cardGive]uidNumber is:"+uidNumber);
        GiftCard card2=new GiftCard();
        card2.setRegexId(cardRules.getRegexId());
        card2.setPrice(cardRules.getPrice());
        card2.setCardName(cardRules.getCardName());
        card2.setCardType(cardRules.getCardType());
        card2.setNumber(uidNumber);
        //支付成功后将套餐卡信息保存数据库中
        GiftCardRecord giftCardRecord=new GiftCardRecord();
        giftCardRecord.setCardId(giftCardRepository.saveAndFlush(card2).getId());
        giftCardRecord.setSenderOpenid(SenderOpenid);
        giftCardRecord.setMessage(message);
        giftCardRecord.setGetStatus(0);
        giftCardRecord.setBuyTime(now);
        //加载分享页面所需要的数据。
        //可购买的套餐名称
        JSONObject eachGiftArray= JSONObject.parseObject(cardRules.getRegexId());
        //遍历套餐，获取套餐名字
        String regexName="";
        for(String str:eachGiftArray.keySet()){
            regexName=regexName+str+",";
            logger.info("[cardGive]eachGiftRegex is:"+regexName);
        }
        String finalRegexName=regexName.substring(0,regexName.length()-1);
        logger.info("[cardGive]finalRegexName is:"+finalRegexName);
        card2.setRegexName(finalRegexName);
        model.addObject("card",card2);
        model.addObject("cardRecordId",giftCardRecordRepository.saveAndFlush(giftCardRecord).getId());
        model.addObject("user",user);
        model.addObject("message",message);
        model.setViewName("shareGift/card_give");
        return  model;
    }
    //历史记录页面点击送朋友按钮
    @RequestMapping("/chooseToFriend")
    public ModelAndView  chooseToFriend(@RequestParam(value ="openid") String openid,
                                        @RequestParam(value ="callRecordId") Long callRecordId){
        ModelAndView  model=new ModelAndView();
        //历史记录中根据cardRecordId查询cardId,根据cardId查card。
        GiftCardRecord giftCardRecord=giftCardRecordRepository.findByIdEquals(callRecordId);
        GiftCard card2=giftCardRepository.findByIdEquals(giftCardRecord.getCardId());
        //判断card Type
        if(card2.getCardType()==1){
            //card type为1,套餐卡时。需要遍历名字
            //可购买的套餐名称
            JSONObject eachGiftArray= JSONObject.parseObject(card2.getRegexId());
            //遍历套餐，获取套餐名字
            String regexName="";
            for(String str:eachGiftArray.keySet()){
                regexName=regexName+str+",";
                logger.info("[chooseToFriend]eachGiftRegex is:"+regexName);
            }
            String finalRegexName=regexName.substring(0,regexName.length()-1);
            card2.setRegexName(finalRegexName);
            model.setViewName("shareGift/card_give");
        }else{
            //card type为2时，号码卡跳转不同页面
            model.setViewName("shareGift/gift_give");
        }
        //当前用户
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(openid);
        model.addObject("card",card2);
        model.addObject("cardRecordId",callRecordId);
        model.addObject("user",user);
        model.addObject("message",giftCardRecord.getMessage());
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
        //加载所有套餐，id和regexName
        List<Map<String,String>> map=JsonUtils.handlerImgJson(json,"id","regexName");
        model.addObject("regex",map);
        model.addObject("openid",openid);
        logger.info("[testChooseNumberRegex]map is:"+map.toString());
        //加载user
        model.setViewName("shareGift/number_choice");
        return  model;
    }

    //扫描二维码,领取套餐卡页面，加载套餐等数据。
    @RequestMapping("/chooseRegexCard")
    public  ModelAndView  chooseRegexCard(@RequestParam("cardId") String cardId,
                                          @RequestParam("giftCardRecordId") String giftCardRecordId,
                                          @RequestParam(value = "openid",defaultValue = "oIFn90393PZMsIt-kprqw0GWmVko") String openid){
        ModelAndView  model=new ModelAndView();
        //根据card_id获取套餐名字和id
        GiftCard giftCard=giftCardRepository.findByIdEquals(Long.parseLong(cardId));
        //可购买的套餐
        JSONObject eachGiftArray= JSONObject.parseObject(giftCard.getRegexId());
        List<Map<String,Object>> regexs=new ArrayList<>();
        //遍历套餐，获取套餐名字和值
        for(String name:eachGiftArray.keySet()){
            Map<String,Object> regex=new HashedMap();
            String value=name;
            Object key=eachGiftArray.get(name);
            regex.put("value",value);
            regex.put("key",key);
            //根据regexId寻找套餐图标

            logger.info("[testChooseNumberRegex]map is:"+regex);
            regexs.add(regex);
        }
        model.addObject("regexs",regexs);
        model.addObject("cardId",cardId);
        model.addObject("giftCardRecordId",giftCardRecordId);
        model.addObject("openid",openid);
        //加载user
        model.setViewName("shareGift/choice_number");
        return  model;
    }

    //赠送号码卡、留言、选优惠券等。
    @RequestMapping("/testNumberRegex")
    public  ModelAndView  testNumberGiftMethod(@RequestParam(value ="isHideOldDiv",defaultValue ="false") boolean isHideOldDiv,
                                              @RequestParam(value ="chooseNumber",defaultValue ="0") String chooseNumber,
                                               @RequestParam(value ="openid") String openid) {
        ModelAndView  model=new ModelAndView();
         //openid="oIFn90393PZMsIt-kprqw0GWmVko";
        //查找该用户所有未使用的优惠券。
        List<GiftCouponRecord> giftRecordList=giftCouponRecordRepository.findAllByReceiverOpenidEqualsAndIsUsedEqualsOrderByGetTimeDesc(openid,0);
        for(GiftCouponRecord eachGiftCouponRecord :giftRecordList)
        {
            Long couponId=eachGiftCouponRecord.getCouponId();
            GiftCoupon giftCoupon=giftCouponRepository.findByIdEquals(couponId);
            eachGiftCouponRecord.setGiftCoupon(giftCoupon);
        }
        model.addObject("giftRecord",giftRecordList);
        if(!StringUtils.equals(chooseNumber,"0")){
            //选择号码后
            logger.info("[testNumberRegex]pram are:"+util.getBusinessKey()+","+chooseNumber);
            String number=numberInterface.searchNumber(util.getBusinessKey(),chooseNumber);
            Map<String,String> mapNumber=JsonUtils.handlerOriginalNumberJson(number);
            model.addObject("mapNumber",mapNumber);
            logger.info("[testNumberRegex]mapNumber is:"+mapNumber);
        }else{
            //未选择号码
            Map<String,String> mapNumber=new HashMap<>();
            mapNumber.put("number","");
            mapNumber.put("numberPrice","0");
            model.addObject("mapNumber",mapNumber);
        }
        model.addObject("openid",openid);
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(openid);
        model.addObject("userPhone",user.getPhone());
        model.addObject("isHideOldDiv",isHideOldDiv);
        model.setViewName("shareGift/gift_number");
        return  model;
    }

    //分享号码卡页面
    @RequestMapping("/number_give.html")
    public  ModelAndView  numberGive(@RequestParam(value ="openid",defaultValue ="0") String SenderOpenid,
                                     @RequestParam(value ="number") String number,
                                     @RequestParam(value ="couponRecordId") String couponRecordId,
                                     @RequestParam(value ="message",defaultValue ="") String message){
        ModelAndView  model=new ModelAndView();
        //支付成功后删除优惠券，即isUsed设置为1.
        if(!StringUtils.isEmpty(couponRecordId)) {
            GiftCouponRecord giftCouponRecord = giftCouponRecordRepository.findGiftCouponRecordByIdEquals(Long.parseLong(couponRecordId));
            giftCouponRecord.setIsUsed(1);
            giftCouponRecordRepository.saveAndFlush(giftCouponRecord);
        }
        //生成号码卡数据
        GiftCard giftCard=new GiftCard();
        giftCard.setCardName("号码卡");
        giftCard.setCardType(2);
        giftCard.setNumber(number);
        giftCard.setPrice(5000);
        //有效期一年。
        giftCard.setValidTimeNumber(1);
        giftCard.setValidTimeUnit(1);
        GiftCard Cardid=giftCardRepository.save(giftCard);
        //支付成功后将套餐卡信息保存数据库中
        GiftCardRecord giftCardRecord=new GiftCardRecord();
        giftCardRecord.setSenderOpenid(SenderOpenid);
        giftCardRecord.setMessage(message);
        giftCardRecord.setGetStatus(0);
        giftCardRecord.setCardId(Cardid.getId());
        giftCardRecord.setBuyTime(new Date());
        //加载分享页面所需要的数据。
        GiftCard card=giftCardRepository.findByIdEquals(Cardid.getId());
        model.addObject("card",card);
        model.addObject("cardRecordId",giftCardRecordRepository.saveAndFlush(giftCardRecord).getId());
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(SenderOpenid);
        model.addObject("user",user);
        model.addObject("message",message);
        model.setViewName("shareGift/gift_give");
        return  model;
    }
    //号码卡支付成功，价格为0的情况
    @RequestMapping("/number_give_price.html")
    public  ModelAndView  numberGivePrice(@RequestParam(value ="openid",defaultValue ="0") String SenderOpenid,
                                     @RequestParam(value ="number") String number,
                                     @RequestParam(value ="couponRecordId") String couponRecordId,
                                     @RequestParam(value ="message",defaultValue ="") String message) throws IOException {
        ModelAndView  model=new ModelAndView();
        //根据openid查找用户信息
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(SenderOpenid);
        //微信支付不为0时，所用的流程复制到这里
        //绑定靓号参数
        BindVo bindVo = new BindVo();
        bindVo.setUidnumber(number);
        bindVo.setRegphone("01012345678");
        //待更新真实时间
        bindVo.setExpiretime("365");
        Result result = null;
        try {
            result = numberService.bindZhiZun(bindVo);
            if(result.getCode()!=200){
                model.setViewName("wrongPage");
                return model;
            }else{
                //绑定成功
                String  jsonReturn=productInterface.deleteSpeNumber(util.getBusinessKey(),number);
                //获得真实的号码所属套餐ID
                String regexId=JsonUtils.handlerNumberReturnRegexJson(jsonReturn);
                JSONObject jobj = JSONObject.parseObject(result.getData().toString());
                logger.info("[receiveCardSuccess]jobj======" + jobj.toJSONString());
                JSONObject res = jobj.getJSONObject("binding_Relation_response");
                if (null == res) {
                    model.setViewName("wrongPage");
                    return model;
                } else {
                    logger.info("[receiveCardSuccess]change result is :" + res.toJSONString());
                }
                //获取返回结果。
                JSONObject jobjZhiZun = JSONObject.parseObject(result.getData().toString());
                JSONObject resZhiZun = jobjZhiZun.getJSONObject("binding_Relation_response");
                if (null == resZhiZun) {
                    logger.warn("[number_give_price paySuccess]"+number+" bind wrong."+"\n");
                }
                //保存记录
                BusinessNumberRecord  record2=new BusinessNumberRecord();
                record2.setBusinessId(util.getBusinessKey());
                record2.setPrtms(resZhiZun.getString("prtms"));
                record2.setSmbms(resZhiZun.getString("smbms"));
                record2.setResult(1);
                record2.setCallrestrict(0+"");
                record2.setSubts(new Date());
                record2.setUserPhone("01012345678");
                record2.setValidTime(DateUtils.parse(resZhiZun.getString("validitytime")));
                record2.setRegexId(Integer.parseInt(StringUtils.equals(regexId,"")?"0":regexId));
                businessNumberRecordRepository.save(record2);
            }
        } catch (IOException e) {
            model.setViewName("wrongPage");
            return model;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //支付成功后删除优惠券，即isUsed设置为1.
        if(!StringUtils.isEmpty(couponRecordId)) {
            GiftCouponRecord giftCouponRecord = giftCouponRecordRepository.findGiftCouponRecordByIdEquals(Long.parseLong(couponRecordId));
            giftCouponRecord.setIsUsed(1);
            giftCouponRecordRepository.saveAndFlush(giftCouponRecord);
        }
        //生成号码卡数据
        GiftCard giftCard=new GiftCard();
        giftCard.setCardName("号码卡");
        giftCard.setCardType(2);
        giftCard.setNumber(number);
        giftCard.setPrice(5000);
        //有效期一年。
        giftCard.setValidTimeNumber(1);
        giftCard.setValidTimeUnit(1);
        GiftCard Cardid=giftCardRepository.save(giftCard);
        //支付成功后将套餐卡信息保存数据库中
        GiftCardRecord giftCardRecord=new GiftCardRecord();
        giftCardRecord.setSenderOpenid(SenderOpenid);
        giftCardRecord.setMessage(message);
        giftCardRecord.setGetStatus(0);
        giftCardRecord.setCardId(Cardid.getId());
        giftCardRecord.setBuyTime(new Date());
        //加载分享页面所需要的数据。
        GiftCard card=giftCardRepository.findByIdEquals(Cardid.getId());
        model.addObject("card",card);
        model.addObject("cardRecordId",giftCardRecordRepository.saveAndFlush(giftCardRecord).getId());
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
            //根据二维码id找到相应的套餐卡或者号码卡记录。
            GiftCardRecord giftCardRecord = giftCardRecordRepository.findByQrcodeEquals(realId);
            if(giftCardRecord.getGetStatus()==1){
                //跳转到已经被人领取的页面
                 return  "shareGift/used";
            }
            Long cardRecordId=giftCardRecord.getId();
            return  "redirect:"+"/oauth/gift/qrcodeAfter/"+cardRecordId;
            //return  "redirect:"+"qrcodeAfter?cardRecordId="+cardRecordId;
        }else if(StringUtils.equals(type,"call")){
            //识别二维码显示95号码页面
            return  "redirect:"+"/showNumber95?qrcode="+realId;
        } else if(StringUtils.equals(type,"coupon")){
            //优惠券
            GiftCouponQrcode giftCouponRecord = giftCouponQrcodeRepository.findByQrcodeEquals(realId);
            Long couponId=giftCouponRecord.getCouponId();
            String senderId=giftCouponRecord.getCreatorOpenid();
            return  "redirect:"+"/oauth/gift/giftReturn/"+couponId+"/"+senderId;
            //return  "redirect:"+"oauth/gift/giftReturn?couponId="+couponId+"&senderId="+senderId;
        }else return null;

    }
    //gift扫描二维码后领取页面，已授权，地址为/oauth/gift/{qrcodeAfter}/{cardRecordId}
    @RequestMapping("/qrcodeAfter")
    public ModelAndView  qrcodeAfter(@RequestParam("cardRecordId") Long cardRecordId,
                                     @RequestParam(value = "openid",defaultValue = "oIFn9005fexvo3QP1HIBaQozdf7Q") String openid){
        ModelAndView  model=new ModelAndView();
        //查询giftCardRecord
        GiftCardRecord giftCardRecord=giftCardRecordRepository.findByIdEquals(cardRecordId);
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
            model.addObject("openid",openid);
            model.addObject("giftCardRecord",giftCardRecord);
            model.setViewName("shareGift/recive_card");
            //model.setViewName("shareGift/card_qrcode");
        }else{
            //加载分享页面所需要的数据。
            model.addObject("card",giftCard);
            BusinessCustomer user= businessCusRepository.findByOpenidEquals(giftCardRecord.getSenderOpenid());
            //senderUser
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
            //加载分享页面所需要的数据。号码卡
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
                                            @RequestParam(value = "number" ,defaultValue = "") String number,
                                            @RequestParam("openid") String openid){
        ModelAndView  model=new ModelAndView();
        logger.info("openid:"+openid);
        //根据giftCardRecordId查找号码卡record，填写receiver和卡片领取时间。
        GiftCardRecord giftCardRecord = giftCardRecordRepository.findByIdEquals(giftCardRecordId);
        giftCardRecord.setReceiverOpenid(openid);
        giftCardRecord.setGetStatus(1);
        giftCardRecord.setGetTime(new Date());
        //保存receiver
        giftCardRecordRepository.saveAndFlush(giftCardRecord);
        //查找giftCard
        GiftCard giftCard=giftCardRepository.findByIdEquals(giftCardId);
        //查找receiver user信息
        BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals(openid);
        //根据套餐卡和号码卡领取情况不同选择绑定或者修改绑定。
        if(!StringUtils.isEmpty(number)){
            //套餐卡选号后，使用绑定接口绑定手机号。
            //查询本地record数据
            String oldNumber=giftCard.getNumber();
            BusinessNumberRecord record=recordRepository.findBySmbmsEqualsAndBusinessIdEquals(oldNumber,util.getBusinessKey());
            if(record==null){
                model.setViewName("wrongPage");
                return  model;
            }else{
                //绑定靓号参数
                BindVo bindVo = new BindVo();
                logger.info("[receiveCardSuccess]bind phone:>>>>>>>>"+userInfo.getPhone());
                bindVo.setUidnumber(number);
                bindVo.setRegphone(userInfo.getPhone());
                //待更新真实时间
                String realTime=record.getValidTime().toString();
                logger.info("[receiveCardSuccess]bind realTime is:"+realTime);
                bindVo.setExpiretime(realTime);
                //使用真实的95号调用绑定接口
                try {
                    Result result = numberService.bindZhiZun(bindVo);
                    logger.info("[receiveCardSuccess]bind result======" + result.getMsg());
                    if (200 != result.getCode()) {
                        model.setViewName("wrongPage");
                        return model;
                    } else {
                        //绑定成功
                        //删除号码池中的号码。
                        String  jsonReturn=productInterface.deleteSpeNumber(util.getBusinessKey(),number);
                        //获得真实的号码所属套餐ID
                        String regexId=JsonUtils.handlerNumberReturnRegexJson(jsonReturn);
                        //跟新真实的号码所属套餐ID
                        record.setRegexId(Integer.parseInt(regexId));
                        JSONObject jobj = JSONObject.parseObject(result.getData().toString());
                        logger.info("[receiveCardSuccess]jobj======" + jobj.toJSONString());
                        JSONObject res = jobj.getJSONObject("binding_Relation_response");
                        if (null == res) {
                            model.setViewName("wrongPage");
                            return model;

                        } else {
                            logger.info("[receiveCardSuccess]change result is :" + res.toJSONString());
                        }
                    }
                }//绑定靓号结束
                catch (IOException e) {
                    model.setViewName("wrongPage");
                    return model;
                }
                //修改record信息并更新数据库
                logger.info("save record operation.");
                record.setPrtms(userInfo.getPhone());
                record.setUserPhone(userInfo.getPhone());
                //BusinessNumberRecord 更新真实的95号
                record.setSmbms(number);
                recordRepository.saveAndFlush(record);
                logger.info("save record operation over.");
            }
            //giftCard 更新真实的95号
            giftCard.setNumber(number);
            giftCardRepository.saveAndFlush(giftCard);
        }else{
                //number不为空，号码卡选号后付款成功后。
                //查询本地record数据
                BusinessNumberRecord record=recordRepository.findBySmbmsEqualsAndBusinessIdEquals(giftCard.getNumber(),util.getBusinessKey());
                if(record==null){
                    model.setViewName("wrongPage");
                    return  model;
                }
                BindVo bindVo2 = new BindVo();
                logger.info("NewPhone:>>>>>>>>"+giftCard.getNumber());
                bindVo2.setUidnumber(giftCard.getNumber());
                bindVo2.setField("regphone");
                bindVo2.setValue(userInfo.getPhone());
                try {
                    Result result = numberService.changeBindNew(bindVo2);
                    logger.info("msg======"+result.getMsg());
                    if (200 != result.getCode()) {
                        model.setViewName("wrongPage");
                        return  model;
                    }
                    JSONObject jobj = JSONObject.parseObject(result.getData().toString());
                    logger.info("jobj======"+jobj.toJSONString());
                    JSONObject res = jobj.getJSONObject("change_Relation_response");
                    if (null == res) {
                        model.setViewName("wrongPage");
                        return  model;

                    }else{
                        logger.info("change result is :"+res.toJSONString());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            //本地数据库修改userphone和phone更改成功后跳转。
            //修改record信息并更新数据库
            logger.info("save number record operation.");
            record.setPrtms(userInfo.getPhone());
            record.setUserPhone(userInfo.getPhone());
            //BusinessNumberRecord 更新真实的95号
            //record.setSmbms(number);
            recordRepository.saveAndFlush(record);
        }
        logger.info("save record operation over.");
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


    //抽奖页面
    @RequestMapping("/testLottery")
    public  ModelAndView  testLottery( @RequestParam(value = "openid",defaultValue = "oIFn90393PZMsIt-kprqw0GWmVko") String openid){
        ModelAndView  modelAndView=new ModelAndView();
        modelAndView.addObject("openid",openid);
        //查询当前九宫格所使用的活动
        GiftAwardsRules activeNow=giftAwardsRulesRepository.findByIsAbleEquals(1);
        //加载当前活动奖品图片。
        logger.info("activeNow index:"+activeNow.getId());
        List<GiftAwards> awards=giftAwardsRepository.findByRulesIdOrderByIndexOrder(activeNow.getId());
        logger.info("GiftAwards :"+awards.toString());
        modelAndView.addObject("awards",awards);
        //当前活动所需要消耗的积分
        int usePoints=activeNow.getPoints();
        //根据openid查找用户当前积分数
        GiftPoint userPointsInfo=giftPointRepository.findByOpenidEquals(openid);
        modelAndView.addObject("usePoints",usePoints);
        //抽奖次数,当前积分
        int times;
        int userPoints=userPointsInfo.getPointTotal();
        int userPointsJS;
        if(userPointsInfo!=null){
            userPointsJS=userPointsInfo.getPointTotal()-userPointsInfo.getPointUsed();
            //计算可抽奖次数
            double a=userPointsJS;
            double b=usePoints;
            double c = a/b;
            times=(int)Math.floor(c);
            logger.info("[ouathController shareGiftLottery]userPoints/usePoints=times:"+a+"/"+b+"="+times);
        }else{
            //用户未获得过积分
            times=0;
            userPoints=0;
        }
        modelAndView.addObject("openid",openid);
        modelAndView.addObject("times",times);
        modelAndView.addObject("userPoints",userPoints);
        modelAndView.setViewName("shareGift/lottery");
        return modelAndView;
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
                userPointsInfo.setPointTotal(userPoints);
                logger.info("save userPointsInfo id:"+userPointsInfo.getId());
                giftPointRepository.saveAndFlush(userPointsInfo);
                //用户积分历史记录表变更。
                GiftPointRecord userPointsRecord=new GiftPointRecord();
                userPointsRecord.setChangePoint(-usePoints);
                userPointsRecord.setOpenid(openid);
                userPointsRecord.setResource(3);
                userPointsRecord.setUpdateTime(new Date());
                giftPointRecordRepository.saveAndFlush(userPointsRecord);
                //剩余可抽奖次数
                times=times-1;
                //返回前台数据
                Map<String,Object> map=new HashMap<>();
                map.put("resultIndex",resultIndex);
                map.put("times",times);
                map.put("userPoints",userPoints);
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



    //个人中心
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
        if(ls.size()>0) {
        handlerList(model,ls);
        model.addObject("ls",ls);
        }
        List<BusinessNumberRecord>  other=recordRepository.findAllByPrtmsNotAndUserPhoneEqualsAndBusinessIdEqualsAndValidTimeGreaterThanEqualOrderByValidTimeDesc(userInfo.getPhone(),userInfo.getPhone(),util.getBusinessKey(),now);
        if(other.size()>0) {
        model.addObject("other",other);
        handlerOther(other);
        }
        String businessId=util.getBusinessKey();
        logger.info("bussinessId is:"+businessId);
        String json= productInterface.findRegexProduct(businessId,"0");
        List<wx_product> products=JsonUtils.handlerRegexJson(json);
        model.addObject("products",products);
        Map<String,String> map=new HashMap<>();
        map.put("openid",openid);
        model.addObject("user",map);
        model.setViewName("shareGift/my_safenumber");
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
