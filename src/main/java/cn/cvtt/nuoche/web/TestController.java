package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.common.result.ResultMsg;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.entity.business.BusinessNumberRecord;
import cn.cvtt.nuoche.entity.business.SystemFeedBack;
import cn.cvtt.nuoche.entity.business.wx_product;
import cn.cvtt.nuoche.entity.gift.GiftCard;
import cn.cvtt.nuoche.entity.gift.GiftCoupon;
import cn.cvtt.nuoche.entity.gift.GiftCouponRecord;
import cn.cvtt.nuoche.entity.gift.GiftPoint;
import cn.cvtt.nuoche.entity.gift.GiftPointRecord;
import cn.cvtt.nuoche.facade.IBusinessCallRecordInterface;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.IRegexInterface;
import cn.cvtt.nuoche.reponsitory.IBusinessCusRepository;
import cn.cvtt.nuoche.reponsitory.IBusinessNumberRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCardRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCouponRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCouponRepository;
import cn.cvtt.nuoche.reponsitory.IGiftPointRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftPointRepository;
import cn.cvtt.nuoche.reponsitory.ISystemFeedBack;
import cn.cvtt.nuoche.util.ConfigUtil;
import cn.cvtt.nuoche.util.DateUtils;
import cn.cvtt.nuoche.util.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import cn.cvtt.nuoche.server.WxServer;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    //转发朋友圈等
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
        model.setViewName("gift/share_number");
        return  model;
    }
    //转发朋友圈后跳转页面
    @RequestMapping("/testReturn")
    public  ModelAndView  testReturn(@RequestParam(value ="couponId",defaultValue ="1") Long couponId, @RequestParam(value ="senderId",defaultValue ="oIFn90393PZMsIt-kprqw0GWmVko") String senderId){
        ModelAndView  model=new ModelAndView();
        //朋友圈转发后
        String receiverOpenid="oIFn90393PZMsIt-kprqw0GWmVko";
        logger.info("[testReturn]couponId is:"+couponId+"senderId is:"+senderId);
        BusinessCustomer receiveUser= businessCusRepository.findByOpenidEquals(receiverOpenid);
        model.addObject("receiveUser",receiveUser);
        BusinessCustomer senderUser= businessCusRepository.findByOpenidEquals(senderId);
        model.addObject("senderUser",senderUser);
        GiftCoupon coupon=giftCouponRepository.findByIdEquals(couponId);
        model.addObject("coupon",coupon);
        model.setViewName("gift/share_number_info_content");
        return  model;
    }

    //领取优惠券
    @RequestMapping("/testReceive")
    public  ModelAndView  testReceive(@RequestParam(value = "coupon",defaultValue ="1" ) Long coupon, @RequestParam(value = "senderUser",defaultValue ="oIFn90393PZMsIt-kprqw0GWmVko") String senderUser,@RequestParam(value = "receiveUser",defaultValue ="oIFn90393PZMsIt-kprqw0GWmVko") String receiveUser){

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
            model.setViewName("gift/share_number_success");
        }else{
            logger.info("[testReceive]you have received a coupon buy the same sender before.");
            //领取过优惠券，跳转到错误页面。
            model.setViewName("/OwnerSafeNumber.html");
        }
        return  model;
    }
    //当日分享给好友或朋友圈后领取积分奖励
    @RequestMapping("/testReceivePoint")
    public  ModelAndView  testReceivePoint(@RequestParam("coupon") Long coupon, @RequestParam("senderUser") String senderUser,@RequestParam("receiveUser") String receiveUser){
        ModelAndView  model=new ModelAndView();
        //如当天未分享过，则该用户增加一次积分。

        model.setViewName("gift/share_number_success");
        return  model;
    }
    //送卡首页
    @RequestMapping("/testCard")
    public  ModelAndView  testReceivePoint(){
        ModelAndView  model=new ModelAndView();

        model.setViewName("shareGift/gift");
        return  model;
    }
    //送套餐卡
    @RequestMapping("/testRegexGift")
    public  ModelAndView  testRegexGift(){
        ModelAndView  model=new ModelAndView();
        //如当天未分享过，则该用户增加一次积分。

        model.setViewName("gift/gift_card");
        return  model;
    }
    //送套餐卡选套餐
    @RequestMapping("/testChooseRegex")
    public  ModelAndView  testChooseRegex(){
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
        }
        //遍历套餐id,查询套餐名字。
        model.addObject("giftCardList",giftCardList);
        model.setViewName("gift/card_choice");
        return  model;
    }

    //选中套餐卡
    @RequestMapping("/testChooseCard")
    public  ModelAndView  testChooseCard(@RequestParam("cardId") Long cardId){
        ModelAndView  model=new ModelAndView();
        if(cardId!=null){
            logger.info("[testChooseCard]testChooseCard is:"+cardId);
        }
        model.setViewName("gift/testCard");
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
