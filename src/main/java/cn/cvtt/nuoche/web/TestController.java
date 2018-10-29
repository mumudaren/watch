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
        String openid="oIFn90393PZMsIt-kprqw0GWmVko";
        modelAndView.setViewName("buy_zhizun");
        String json=regexInterface.findRegexByBusiness(util.getBusinessKey());
        //  String json= productInterface.findProduct(util.getBusinessKey());
        List<Map<String,String>> map=JsonUtils.handlerNormalJson(json,"id","regexName");
        modelAndView.addObject("regexs",map);
        String productJson= productInterface.findRegexProduct(util.getBusinessKey(),map.get(0).get("key"));
        List<wx_product>  ls=JsonUtils.handlerRegexJson(productJson);
        modelAndView.addObject("products",ls);
        String numberJson=productInterface.findSpeNumberTop10(util.getBusinessKey(),map.get(0).get("key"),"",0,10,UUID.randomUUID().toString());
        List<Map<String,String>>  numbers=JsonUtils.handlerNumberJson(numberJson);
        modelAndView.addObject("numbers",numbers);
        Map<String,String> user=new HashMap<>();
        user.put("openid","oIFn90393PZMsIt-kprqw0GWmVko");
        BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals(openid);
        modelAndView.addObject("phone",userInfo.getPhone());
        modelAndView.addObject("user",user);
        //查找该用户所有未使用过的并且在有效期内的优惠券。
        List<GiftCouponRecord> giftRecordList=giftCouponRecordRepository.findAllByReceiverOpenidEqualsAndIsUsedEqualsOrderByGetTimeDesc(openid,0);
        for(GiftCouponRecord eachGiftCouponRecord :giftRecordList)
        {
            Long couponId=eachGiftCouponRecord.getCouponId();
            GiftCoupon giftCoupon=giftCouponRepository.findByIdEquals(couponId);
            eachGiftCouponRecord.setGiftCoupon(giftCoupon);
        }
        modelAndView.addObject("giftRecord",giftRecordList);
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
    //test每日积分菜单接口，giftShareNumber
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

    //test转发朋友圈后跳转测试页面
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

    //当日分享给好友或朋友圈后领取积分奖励测试页面，真实接口为/receivePoint
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

    //选择两种套餐赠送测试页面
    @RequestMapping("/testCard")
    public  ModelAndView  testReceivePoint(){
        ModelAndView  model=new ModelAndView();
        model.addObject("openid","oIFn90393PZMsIt-kprqw0GWmVko");
        model.setViewName("shareGift/gift");
        return  model;
    }


    //测试gift扫描二维码后领取页面，已授权，地址为/oauth/gift/{qrcodeAfter}/{cardRecordId}
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

    //历史记录测试页面
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
                    SimpleDateFormat simpleFormat2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
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
                    SimpleDateFormat simpleFormat2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
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
                    SimpleDateFormat simpleFormat2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
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
                    SimpleDateFormat simpleFormat2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
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
//    //测试领取功能
//    @RequestMapping("/testCoupon")
//    public String  testCoupon(){
//    List<GiftCouponRecord> giftRecordList=giftCouponRecordRepository.findByNamedParam("oIFn90393PZMsIt-kprqw0GWmVko",0,new Date());
//    logger.info("testCoupon:"+giftRecordList.toString());
//    return "validate_tel";
//    }
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
