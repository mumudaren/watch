package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.entity.RegisterVo;
import cn.cvtt.nuoche.entity.SNSUserInfo;
import cn.cvtt.nuoche.entity.WeixinOauth2Token;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.entity.business.BusinessNumberRecord;
import cn.cvtt.nuoche.entity.business.wx_product;
import cn.cvtt.nuoche.entity.gift.GiftCoupon;
import cn.cvtt.nuoche.entity.gift.GiftCouponQrcode;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.IRegexInterface;
import cn.cvtt.nuoche.reponsitory.IBusinessCusRepository;
import cn.cvtt.nuoche.reponsitory.IBusinessNumberRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCouponQrcodeRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCouponRepository;
import cn.cvtt.nuoche.service.QrcodeService;
import cn.cvtt.nuoche.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @decription OauthController
 * <p></p>
 * @date 2018/3/19 8:33
 */
@Controller
public class OauthController extends  BaseController{


    @Resource
    private ConfigUtil util;
    @Autowired
    IBusinessNumberRecordRepository recordRepository;
    @Autowired
    IProductInterface productInterface;
    @Autowired
    IBusinessCusRepository  businessCusRepository;
    @Autowired
    IRegexInterface  regexInterface;
    @Autowired
    IGiftCouponQrcodeRepository giftCouponQrcodeRepository;
    @Autowired
    IGiftCouponRepository giftCouponRepository;
    @Resource
    private QrcodeService qrcodeService;
    private static final Logger logger = LoggerFactory.getLogger(OauthController.class);
    /**
     * 获取网页授权用户信息
     * @param code
     * @param state
     * @return
     */
    @RequestMapping(value = "/oauth")
    public ModelAndView oauth(@RequestParam String code, @RequestParam String state) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(state);

        // 用户同意授权
        if (!"authdeny".equals(code)) {
            // 获取网页授权access_token
            WeixinOauth2Token weixinOauth2Token = WxUtils.getOauth2AccessToken(code);
            // 网页授权接口访问凭证
            String accessToken = weixinOauth2Token.getAccessToken();
            // 用户标识
            String openId = weixinOauth2Token.getOpenId();
            // 判断如果openid为空，则引导用户重新授权
            if (StringUtils.isBlank(openId)) {
                modelAndView.setViewName("redirect:/oauth/" + state);
                return modelAndView;
            }
            // 获取用户信息
            SNSUserInfo snsUserInfo = WxUtils.getSNSUserInfo(accessToken, openId);
            logger.info("[oauth]snsUserInfo{}"+snsUserInfo.toString()+"\n");
            BusinessCustomer  info=businessCusRepository.findByOpenidEquals(openId);
            //WxUserInfo info=userService.findByOpenidEquals(openId);
            logger.info("[oauth]WxUserInfo{}"+info.toString()+"\n");
            RegisterVo vo= WxUtils.convertEntityVo(snsUserInfo,info);
            // 设置要传递的参数
            modelAndView.addObject("user", vo);
            logger.info("[oauth]access user message：" + JsonUtils.objectToJson(snsUserInfo)+"\n");
        }
        return modelAndView;
    }





    /**
     * 基本授权方式获取
     * @param code
     * @param state
     * @return
     */
    @SuppressWarnings("all")
    @RequestMapping("/oauth/base")
    public ModelAndView ouathBase(@RequestParam String code, @RequestParam String state,RedirectAttributes attr) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取网页授权access_token
        WeixinOauth2Token weixinOauth2Token = WxUtils.getOauth2AccessToken(code);
        String openId = weixinOauth2Token.getOpenId();
        logger.info("[ouathBase]openid++++"+openId);
        // 判断如果openid为空，则引导用户重新授权
        if (StringUtils.isBlank(openId)) {
            logger.info("[ouathBase]not find openId,error code is:", code+"\n");
            modelAndView.setViewName("redirect:/oauth/base/" + state);
            return modelAndView;
        }
        /***==> 先判断当前用户是否绑定手机号,如果没有则跳转绑定页面*/
        BusinessCustomer  customer=businessCusRepository.findByOpenidEquals(openId);
        if(customer==null||StringUtils.isEmpty(customer.getPhone())){
            modelAndView.setViewName("validate_tel");
            modelAndView.addObject("openid",openId);
            return  modelAndView;
        }else {
            modelAndView.setViewName("buy_safenumber");
            String json= productInterface.findRegexProduct(util.getBusinessKey(),"0");
            List<wx_product>  products=JsonUtils.handlerRegexJson(json);
         /*   List<wx_product> products=new ArrayList<>();
            if(obj.getIntValue("code")==200){
                JsonUtils.handlerArgs(products,obj);
            }*/
            logger.info("[ouathBase]wx_product size is:"+products.size()+"\n");
            modelAndView.addObject("ls",products);
            modelAndView.addObject("phone",customer.getPhone());
        }
        /****===>>**/
        Map<String, String> map = new HashMap<>();
        map.put("openid", openId);
        // 设置要传递的参数
        modelAndView.addObject("user", map);
        modelAndView.addObject("openid",openId);
        logger.info("[ouathBase] user message：" + JsonUtils.objectToJson(map)+"\n");
        return modelAndView;
    }


    /**
     * 需要基本授权的请求
     * @param path
     * @return
     */
    @RequestMapping("/oauth/base/{path}")
    public String redirectBase(@PathVariable String path) {
        logger.info("[redirectBase]util.getUrl"+util.getUrl()+"\n");
        String requestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        requestUrl = requestUrl.replace("APPID", Constant.APP_ID)
                .replace("REDIRECT_URI", HttpClientUtil.urlEncodeUTF8(util.getUrl() + "/oauth/base"))
                .replace("SCOPE", "snsapi_base")
                .replace("STATE", path);
        logger.info("[redirectBase]requestUrl:::"+requestUrl+"\n");
        return "redirect:" + requestUrl;
    }



    /**
     * 基本授权方式获取
     * @param code
     * @param state
     * @return
     */
    @SuppressWarnings("all")
    @RequestMapping("/oauth/other")
    public ModelAndView ouathOther(@RequestParam String code, @RequestParam String state,RedirectAttributes attr) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取网页授权access_token
        WeixinOauth2Token weixinOauth2Token = WxUtils.getOauth2AccessToken(code);
        String openId = weixinOauth2Token.getOpenId();
        logger.info("[ouathOther]receive pram openid is:"+openId+"\n");
        // 判断如果openid为空，则引导用户重新授权
        if (StringUtils.isBlank(openId)) {
            logger.info("[ouathOther]openid is empty,error code is:", code);
            modelAndView.setViewName("redirect:/oauth/other/" + state);
            return modelAndView;
        }
        /***==> 先判断当前用户是否绑定手机号,如果没有则跳转绑定页面*/
        BusinessCustomer  customer=businessCusRepository.findByOpenidEquals(openId);
        if(customer==null||StringUtils.isEmpty(customer.getPhone())){
            modelAndView.setViewName("validate_tel");
            modelAndView.addObject("openid",openId);
            return  modelAndView;
        }
        Map<String, String> map = new HashMap<>();
        map.put("openid", openId);
        modelAndView.setViewName(state);
        modelAndView.addObject("user", map);
        modelAndView.addObject("openid",openId);
        logger.info("[ouathOther]user message is：" + JsonUtils.objectToJson(map)+"\n");
        return modelAndView;
    }


    /**
     * 需要基本授权的请求
     * @param path
     * @return
     */
    @RequestMapping("/oauth/other/{path}")
    public String redirectOther(@PathVariable String path) {
        logger.info("[redirectOther]util.getUrl"+util.getUrl());
        String requestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        requestUrl = requestUrl.replace("APPID", Constant.APP_ID)
                .replace("REDIRECT_URI", HttpClientUtil.urlEncodeUTF8(util.getUrl() + "/oauth/other"))
                .replace("SCOPE", "snsapi_base")
                .replace("STATE", path);
        logger.info("[redirectOther]requestUrl:::"+requestUrl);
        return "redirect:" + requestUrl;
    }


    /**
     * 需要基本授权的请求
     * @param path
     * @return
     */
    @RequestMapping("/oauth/admin/{path}")
    public String redirectAdminBase(@PathVariable String path) {
        String requestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        requestUrl = requestUrl.replace("APPID", Constant.APP_ID)
                .replace("REDIRECT_URI", HttpClientUtil.urlEncodeUTF8(util.getUrl() + "/oauth/admin"))
                .replace("SCOPE", "snsapi_base")
                .replace("STATE", path);
        logger.info("[redirectOther]requestUrl:::"+requestUrl);
        return "redirect:" + requestUrl;
    }

    /**
     *  获取靓号请求
     *  @param  path
     *  @return
     * */

    @RequestMapping("/oauth/regex/{path}")
    public  String  redirectRegexBase(@PathVariable  String path){
        String  requestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        requestUrl = requestUrl.replace("APPID", Constant.APP_ID)
                .replace("REDIRECT_URI", HttpClientUtil.urlEncodeUTF8(util.getUrl() + "/oauth/regex"))
                .replace("SCOPE", "snsapi_base")
                .replace("STATE", path);
        logger.info("[redirectOther]requestUrl:::"+requestUrl);
        return "redirect:" + requestUrl;
    }
    /**
     * 基本授权方式获取
     * @param code
     * @param state
     * @return
     */
    @SuppressWarnings("all")
    @RequestMapping("/oauth/regex")
    public ModelAndView ouathRegexBase(@RequestParam String code, @RequestParam String state) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取网页授权access_token
        WeixinOauth2Token weixinOauth2Token = WxUtils.getOauth2AccessToken(code);
        String openId = weixinOauth2Token.getOpenId();
        logger.info("[ouathRegexBase]user openid is:"+openId);
        // 判断如果openid为空，则引导用户重新授权
        if (StringUtils.isBlank(openId)) {
            logger.info("[ouathRegexBase]openId is empty and code is:", code);
            modelAndView.setViewName("redirect:/oauth/regex/" + state);
            logger.info("[ouathRegexBase]redirect:/oauth/regex/" + state);
            return modelAndView;
        }
        /***==> 先判断当前用户是否绑定手机号,如果没有则跳转绑定页面*/
        BusinessCustomer  customer=businessCusRepository.findByOpenidEquals(openId);
        if(customer==null||StringUtils.isEmpty(customer.getPhone())){
            logger.info("[ouathRegexBase]access openid but sever dont have this data .customer is empty.so return validate_tel.html");
            modelAndView.setViewName("validate_tel");
            modelAndView.addObject("openid",openId);
            modelAndView.addObject("path",state);
            return  modelAndView;
        }else {
            logger.info("[ouathRegexBase]will go to buy_zhizun.html");
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
            modelAndView.addObject("phone",customer.getPhone());
        }
        /****===>>**/
        Map<String, String> map = new HashMap<>();
        map.put("openid", openId);
        // 设置要传递的参数
        modelAndView.addObject("user", map);
        logger.info("获取用户信息：" + JsonUtils.objectToJson(map));
        return modelAndView;
    }




    /**
     * 基本授权方式获取
     * @param code
     * @param state
     * @return
     */
    @SuppressWarnings("all")
    @RequestMapping("/oauth/admin")
    public ModelAndView ouathAdminBase(@RequestParam String code, @RequestParam String state) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(state);
        // 获取网页授权access_token
        WeixinOauth2Token weixinOauth2Token = WxUtils.getOauth2AccessToken(code);
        String openId = weixinOauth2Token.getOpenId();
        logger.info("[ouathAdminBase]user openid is:"+openId);
        // 判断如果openid为空，则引导用户重新授权
        if (StringUtils.isBlank(openId)) {
            logger.info("openid is empty,error code is :", code);
            modelAndView.setViewName("redirect:/oauth/admin/" + state);
            return modelAndView;
        }
        /***==> 先判断当前用户是否绑定手机号,如果没有则跳转绑定页面*/
        BusinessCustomer  customer=businessCusRepository.findByOpenidEquals(openId);
        if(customer==null||StringUtils.isEmpty(customer.getPhone())){
            modelAndView.setViewName("validate_tel");
            modelAndView.addObject("openid",openId);
            return  modelAndView;
        }
        /****===>>**/
        Map<String, String> map = new HashMap<>();
        map.put("openid", openId);
        // 设置要传递的参数
        modelAndView.addObject("user", map);
        logger.info("[ouathAdminBase]access user message：" + JsonUtils.objectToJson(map));
        BusinessCustomer  cus=businessCusRepository.findByOpenidEquals(openId);
       // WxUserInfo  wxuser=userRepository.findByOpenidEquals(openId);
        String regphone=cus.getPhone();
        logger.info("[ouathAdminBase]"+regphone+" =>>>> "+util.getBusinessKey());
        List<BusinessNumberRecord> records=recordRepository.findAllByPrtmsEqualsAndBusinessIdEquals(regphone,util.getBusinessKey());
        if(records.size()<1){
            if(StringUtils.equals(state,"buy_safenumber")){
                modelAndView.setViewName("buy_safenumber");
                String json= productInterface.findRegexProduct(util.getBusinessKey(),"0");
                List<wx_product> products=JsonUtils.handlerRegexJson(json);
               /* JSONObject  obj=JSONObject.parseObject(json);
                List<wx_product> products=new ArrayList<>();
                if(obj.getIntValue("code")==200){
                    JsonUtils.handlerArgs(products,obj);
                }*/
                modelAndView.addObject("ls",products);
                modelAndView.addObject("phone",regphone);
            }else{
                modelAndView.setViewName("buy_zhizun");
                String json=regexInterface.findRegexByBusiness(util.getBusinessKey());
                //  String json= productInterface.findProduct(util.getBusinessKey());
                List<Map<String,String>> mapZhiZun=JsonUtils.handlerNormalJson(json,"id","regexName");
                modelAndView.addObject("regexs",mapZhiZun);
                String productJson= productInterface.findRegexProduct(util.getBusinessKey(),mapZhiZun.get(0).get("key"));
                List<wx_product>  ls=JsonUtils.handlerRegexJson(productJson);
                modelAndView.addObject("products",ls);
                String numberJson=productInterface.findSpeNumber(util.getBusinessKey(),mapZhiZun.get(0).get("key"),"");
                List<Map<String,String>>  numbers=JsonUtils.handlerNumberJson(numberJson);
                modelAndView.addObject("numbers",numbers);
                Map<String,String> user=new HashMap<>();
                user.put("openid",openId);
                modelAndView.addObject("user",user);
                modelAndView.addObject("phone",regphone);
            }
        }else {
            modelAndView.setViewName("my_safenumber");
            BusinessCustomer  userInfo=businessCusRepository.findByOpenidEquals(openId);
            //WxUserInfo userInfo=userRepository.findByOpenidEquals(openId);
            String  url= userInfo.getHeadimgurl();
            modelAndView.addObject("url",url);
            modelAndView.addObject("phone",userInfo.getPhone());
            Date now=DateUtils.addDay(new Date(),"-60");
            List<BusinessNumberRecord>  ls=recordRepository.findAllByPrtmsEqualsAndBusinessIdEqualsAndValidTimeGreaterThanEqualOrderByValidTimeDesc(userInfo.getPhone(),util.getBusinessKey(),now);
            handlerList(modelAndView,ls);
            modelAndView.addObject("ls",ls);
            List<BusinessNumberRecord>  other=recordRepository.findAllByPrtmsNotAndUserPhoneEqualsAndBusinessIdEqualsAndValidTimeGreaterThanEqualOrderByValidTimeDesc(userInfo.getPhone(),userInfo.getPhone(),util.getBusinessKey(),now);
            modelAndView.addObject("other",other);
            handlerOther(other);
            logger.info("businessKey"+util.getBusinessKey());
            String json= productInterface.findRegexProduct(util.getBusinessKey(),"0");
            List<wx_product> products=JsonUtils.handlerRegexJson(json);
           /* String json= productInterface.findProduct(util.getBusinessKey());
            JSONObject  obj=JSONObject.parseObject(json);
            List<wx_product> products=new ArrayList<>();
            if(obj.getIntValue("code")==200){
                JsonUtils.handlerArgs(products,obj);
            }*/
            logger.info("[ouathAdminBase]products size is:"+products.size());
            modelAndView.addObject("products",products);
        }

        return modelAndView;
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

        }
        model.addObject("will",will);
        model.addObject("expired",expired);
    }

    /**
     * 需要获取用户信息授权的请求
     * @param path
     * @return
     */
    @RequestMapping("/oauth/{path}")
    public String redirect(@PathVariable String path) {
        String requestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        requestUrl = requestUrl.replace("APPID", Constant.APP_ID)
                .replace("REDIRECT_URI", HttpClientUtil.urlEncodeUTF8(util.getUrl() + "/oauth"))
                .replace("SCOPE", "snsapi_userinfo")
                .replace("STATE", path);
        return "redirect:" + requestUrl;
    }
    /**
     * gift页面需要基本授权的请求
     * @param path
     * @return
     */
    @RequestMapping("/oauth/gift/{path}")
    public String redirectGift(@PathVariable String path) {
        logger.info("[redirectOther]util.getUrl"+util.getUrl());
        String requestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        requestUrl = requestUrl.replace("APPID", Constant.APP_ID)
                .replace("REDIRECT_URI", HttpClientUtil.urlEncodeUTF8(util.getUrl() + "/oauth/other"))
                .replace("SCOPE", "snsapi_base")
                .replace("STATE", path);
        logger.info("[redirectGift]requestUrl:::"+requestUrl);
        return "redirect:" + requestUrl;
    }
    /**
     * gift页面需要基本授权的请求(需要验证是否存在手机号。)
     * @param code
     * @param state
     * @return
     */
    @SuppressWarnings("all")
    @RequestMapping("/oauth/gift")
    public ModelAndView ouathGift(@RequestParam String code, @RequestParam String state,RedirectAttributes attr) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取网页授权access_token
        WeixinOauth2Token weixinOauth2Token = WxUtils.getOauth2AccessToken(code);
        String openId = weixinOauth2Token.getOpenId();
        logger.info("[ouathGift]receive pram openid is:"+openId+"\n");
        // 判断如果openid为空，则引导用户重新授权
        if (StringUtils.isBlank(openId)) {
            logger.info("[ouathGift]openid is empty,error code is:", code);
            modelAndView.setViewName("redirect:/oauth/gift/" + state);
            return modelAndView;
        }
        /***==> 先判断当前用户是否绑定手机号,如果没有则跳转绑定页面*/
        BusinessCustomer  customer=businessCusRepository.findByOpenidEquals(openId);
        if(customer==null||StringUtils.isEmpty(customer.getPhone())){
            modelAndView.setViewName("validate_tel");
            modelAndView.addObject("openid",openId);
            return  modelAndView;
        }
        /***==> 分享代金券页面*/
        if(StringUtils.equals(state,"giftShareNumber")){
            BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals(openId);
            modelAndView.addObject("user",userInfo);
            GiftCoupon coupon=giftCouponRepository.findByIsAbleEquals(1);
            if(coupon==null){
                GiftCoupon couponNew=new GiftCoupon();
                couponNew.setAmount(0);
                couponNew.setPoint(0);
                couponNew.setId(0L);
                modelAndView.addObject("coupon",couponNew);
            }else{
                modelAndView.addObject("coupon",coupon);
            }
            //生成二维码
            GiftCouponQrcode giftCouponQrcode=giftCouponQrcodeRepository.findByCouponIdEquals(coupon.getId());
            if(giftCouponQrcode==null){
                logger.info("[create qrcode]"+"create qrcode");
                GiftCouponQrcode giftCouponQrcodeNew=new GiftCouponQrcode();
                giftCouponQrcodeNew.setCouponId(coupon.getId());
                giftCouponQrcodeNew.setCreateTime(new Date());
                giftCouponQrcodeNew.setCreatorOpenid(openId);
                GiftCouponQrcode giftCouponQrcodeFinal=giftCouponQrcodeRepository.saveAndFlush(giftCouponQrcodeNew) ;
                String qrcodeHref = qrcodeService.generatorQrcode(giftCouponQrcodeFinal.getId(),"coupon");
                modelAndView.addObject("href",qrcodeHref);
            }else{
                String qrcodeHref =giftCouponQrcode.getQrcodeUrl();
                modelAndView.addObject("href",qrcodeHref);
            }
            modelAndView.setViewName("shareGift/share_number");
        }else if(StringUtils.equals(StringUtils.substringBefore(state,"?"),"giftReturn")){
            /***==> 转发朋友圈后点击（扫描优惠券二维码)跳转的页面*/
           Long couponId=Long.parseLong(StringUtils.substringBetween(state,"couponId=","&senderId="));
           String senderId=StringUtils.substringAfter(state,"senderId=");
           String receiverOpenid=openId;
            BusinessCustomer receiveUser= businessCusRepository.findByOpenidEquals(receiverOpenid);
            modelAndView.addObject("receiveUser",receiveUser);
            BusinessCustomer senderUser= businessCusRepository.findByOpenidEquals(senderId);
            modelAndView.addObject("senderUser",senderUser);
            GiftCoupon coupon=giftCouponRepository.findByIdEquals(couponId);
            modelAndView.addObject("coupon",coupon);
            modelAndView.setViewName("shareGift/share_number_info");
        }

        return modelAndView;
    }






}
