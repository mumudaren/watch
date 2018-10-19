package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.entity.RegisterVo;
import cn.cvtt.nuoche.entity.SNSUserInfo;
import cn.cvtt.nuoche.entity.WeixinOauth2Token;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.entity.business.BusinessNumberRecord;
import cn.cvtt.nuoche.entity.business.wx_product;
import cn.cvtt.nuoche.entity.gift.GiftAwards;
import cn.cvtt.nuoche.entity.gift.GiftAwardsRules;
import cn.cvtt.nuoche.entity.gift.GiftCard;
import cn.cvtt.nuoche.entity.gift.GiftCardRecord;
import cn.cvtt.nuoche.entity.gift.GiftCoupon;
import cn.cvtt.nuoche.entity.gift.GiftCouponQrcode;
import cn.cvtt.nuoche.entity.gift.GiftPoint;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.IRegexInterface;
import cn.cvtt.nuoche.reponsitory.*;
import cn.cvtt.nuoche.service.QrcodeService;
import cn.cvtt.nuoche.util.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.HashedMap;
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
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    IGiftCardRepository giftCardRepository;
    @Autowired
    IGiftCardRecordRepository giftCardRecordRepository;
    @Autowired
    IGiftAwardsRulesRepository giftAwardsRulesRepository;
    @Autowired
    IGiftAwardsRepository giftAwardsRepository;
    @Autowired
    IGiftPointRepository giftPointRepository;
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
                //查看我的95号
                modelAndView.setViewName("shareGift/my_safenumber");
                BusinessCustomer  userInfo=businessCusRepository.findByOpenidEquals(openId);
                String  url= userInfo.getHeadimgurl();
                modelAndView.addObject("url",url);
                modelAndView.addObject("phone",userInfo.getPhone());
                Date now=DateUtils.addDay(new Date(),"-60");
                List<BusinessNumberRecord>  ls=recordRepository.findAllByPrtmsEqualsAndBusinessIdEqualsAndValidTimeGreaterThanEqualOrderByValidTimeDesc(userInfo.getPhone(),util.getBusinessKey(),now);
                if(ls.size()>0) {
                    handlerList(modelAndView, ls);
                    modelAndView.addObject("ls", ls);
                }
                List<BusinessNumberRecord>  other=recordRepository.findAllByPrtmsNotAndUserPhoneEqualsAndBusinessIdEqualsAndValidTimeGreaterThanEqualOrderByValidTimeDesc(userInfo.getPhone(),userInfo.getPhone(),util.getBusinessKey(),now);
                if(other.size()>0) {
                    modelAndView.addObject("other", other);
                    handlerOther(other);
                }
                String json= productInterface.findRegexProduct(util.getBusinessKey(),"0");
                List<wx_product> products=JsonUtils.handlerRegexJson(json);
                modelAndView.addObject("products",products);
            }
        }else {
            //查看我的95号
            modelAndView.setViewName("shareGift/my_safenumber");
            BusinessCustomer  userInfo=businessCusRepository.findByOpenidEquals(openId);
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
            String json= productInterface.findRegexProduct(util.getBusinessKey(),"0");
            List<wx_product> products=JsonUtils.handlerRegexJson(json);
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
     * gift页面需要基本授权的请求（不含有参数的页面）
     * @param path
     * @return
     */
    @RequestMapping("/oauth/gift/{path}")
    public String redirectGift(@PathVariable String path) {
        logger.info("[redirectGift]util.getUrl"+util.getUrl());
        logger.info("[redirectGift]path:"+path);
        String requestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        requestUrl = requestUrl.replace("APPID", Constant.APP_ID)
                .replace("REDIRECT_URI", HttpClientUtil.urlEncodeUTF8(util.getUrl() + "/oauth/gift"))
                .replace("SCOPE", "snsapi_base")
                .replace("STATE", path);
        logger.info("[redirectGift]requestUrl:::"+requestUrl);
        return "redirect:" + requestUrl;
    }
    /**
     * gift页面需要基本授权的请求，需要一个参数，gift扫描二维码后领取页面
     * @param path
     * @return
     */
    @RequestMapping("/oauth/gift/{path}/{cardRecordId}")
    public String qrcodeAfterGift(@PathVariable String path,@PathVariable Long cardRecordId) {
        logger.info("[qrcodeAfterGift]path:"+path);
        String requestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        String state=path+"_"+cardRecordId;
        requestUrl = requestUrl.replace("APPID", Constant.APP_ID)
                .replace("REDIRECT_URI", HttpClientUtil.urlEncodeUTF8(util.getUrl() + "/oauth/gift"))
                .replace("SCOPE", "snsapi_base")
                .replace("STATE", state);
        logger.info("[redirectGift]requestUrl:::"+requestUrl);
        return "redirect:" + requestUrl;
    }
    /**
     * gift页面需要基本授权的请求,转发朋友圈后跳转页面,含有两个参数
     * @param path
     * @return
     */
    @RequestMapping("/oauth/gift/{path}/{couponId}/{senderId}")
    public String redirectGiftPram(@PathVariable String path,@PathVariable String couponId,@PathVariable String senderId) {
        logger.info("[redirectGiftPram]util.getUrl"+util.getUrl());
        String requestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        String state=path+"_"+couponId+"_"+senderId;
        logger.info("[redirectGiftPram]state"+state);
        requestUrl = requestUrl.replace("APPID", Constant.APP_ID)
                .replace("REDIRECT_URI", HttpClientUtil.urlEncodeUTF8(util.getUrl() + "/oauth/gift"))
                .replace("SCOPE", "snsapi_base")
                .replace("STATE",state );
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
        logger.info("[ouathGift]state is:"+state+"\n");
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
        /***==> 分享现金券页面*/
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
            //通过coupon查询，生成二维码
            GiftCouponQrcode giftCouponQrcode=giftCouponQrcodeRepository.findByCouponIdEqualsAndCreatorOpenidEquals(coupon.getId(),openId);
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
        }else if(StringUtils.equals(StringUtils.substringBefore(state,"_"),"giftReturn")){
            /***==> 转发朋友圈后点击（扫描优惠券二维码)跳转的页面*/
            // 从第1次出现"_"的位置向后截取，不包含第一次出现的"_",即type_couponId_openid中的couponId_openid中截取
            String subStringFirst=StringUtils.substringAfter(state, "_");
            logger.info("[/oauth/gift]subStringFirst:"+subStringFirst);
           Long couponId=Long.parseLong(StringUtils.substringBefore(subStringFirst,"_"));
            // 从第1次出现"_"的位置向后截取，不包含第1次出现的"_",即couponId_openid中获取openid
           String senderId=StringUtils.substringAfter(subStringFirst,"_");
           logger.info("[/oauth/gift]senderUser senderId，couponId:"+senderId+","+couponId);
           String receiverOpenid=openId;
            BusinessCustomer receiveUser= businessCusRepository.findByOpenidEquals(receiverOpenid);
            modelAndView.addObject("receiveUser",receiveUser);
            BusinessCustomer senderUser= businessCusRepository.findByOpenidEquals(senderId);
            modelAndView.addObject("senderUser",senderUser);
            GiftCoupon coupon=giftCouponRepository.findByIdEquals(couponId);
            modelAndView.addObject("coupon",coupon);
            modelAndView.addObject("openid",openId);
            modelAndView.setViewName("shareGift/share_number_info");
        }else if(StringUtils.equals(StringUtils.substringBefore(state,"_"),"qrcodeAfter")){
            /***==> gift扫描二维码后领取套餐卡、号码卡的页面*/
            String cardRecordId=StringUtils.substringAfterLast(state,"_");
            logger.info("[ouath qrcodeAfter]receive pram cardRecordId is:"+cardRecordId);
            String openid=openId;
            //查询giftCardRecord
            GiftCardRecord giftCardRecord=giftCardRecordRepository.findByIdEquals(Long.parseLong(cardRecordId));
            //根据cardType的不同跳转到不同的显示页面,套餐卡或者号码卡。
            Long cardId=giftCardRecord.getCardId();
            GiftCard giftCard=giftCardRepository.findByIdEquals(cardId);
            if(giftCard.getCardType()==1){
                //可购买的套餐名称
                JSONObject eachGiftArray= JSONObject.parseObject(giftCard.getRegexId());
                //遍历套餐，获取套餐名字
                String regexName="";
                for(String str:eachGiftArray.keySet()){
                    regexName=regexName+str+",";
                    logger.info("[qrcodeAfter]eachGiftRegex is:"+regexName);
                }
                String finalRegexName=regexName.substring(0,regexName.length()-1);
                logger.info("[qrcodeAfter]finalRegexName is:"+finalRegexName);
                giftCard.setRegexName(finalRegexName);
                modelAndView.addObject("card",giftCard);
                BusinessCustomer user= businessCusRepository.findByOpenidEquals(giftCardRecord.getSenderOpenid());
                modelAndView.addObject("user",user);
                modelAndView.addObject("openid",openid);
                modelAndView.addObject("giftCardRecord",giftCardRecord);
                modelAndView.setViewName("shareGift/recive_card");
            }else{
                //加载分享页面所需要的数据。
                modelAndView.addObject("card",giftCard);
                BusinessCustomer senderUser= businessCusRepository.findByOpenidEquals(giftCardRecord.getSenderOpenid());
                modelAndView.addObject("openid",openid);
                //senderUser
                modelAndView.addObject("user",senderUser);
                modelAndView.addObject("giftCardRecord",giftCardRecord);
                modelAndView.setViewName("shareGift/recive_gift");
            }

        }else if(StringUtils.equals(state,"history")){
            /***==> 套餐卡等历史记录*/
            //根据openId查询记录
            modelAndView.addObject("openid",openId);
            //自己未领取
            List<GiftCardRecord> giftCardRecordList =giftCardRecordRepository.findByGetStatusEqualsAndSenderOpenidEqualsOrderByGetTimeDesc(0,openId);
            logger.info("[ouathGift]GiftCardRecord size is:"+giftCardRecordList.size());
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
                        map2.put("id",giftCardRecord.getId());
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
                        map1.put("id",giftCardRecord.getId());
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
                        logger.info("[ouathGift]finalRegexName is:"+finalRegexName);
                        map1.put("finalRegexName",finalRegexName);
                        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String buyTime=simpleFormat.format(giftCardRecord.getBuyTime());
                        map1.put("buyTime",buyTime);
                        RegexCard.add(map1);
                    }
                    modelAndView.addObject("myGiftRegexCardRecordList",RegexCard);
                    modelAndView.addObject("myGiftNumberCardRecordList",NumberCard);
                }
            }
            //自己已领取
            List<GiftCardRecord> giftCardRecordList2 =giftCardRecordRepository.findByGetStatusEqualsAndSenderOpenidEqualsOrderByGetTimeDesc(1,openId);
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
                    modelAndView.addObject("cardHave",cardHave);
                }
            }
            //他人
            List<GiftCardRecord> giftCardOtherRecordList =giftCardRecordRepository.findByGetStatusEqualsAndReceiverOpenidEqualsOrderByGetTimeDesc(1,openId);
            List<Map<String,Object>> cardReceive=new ArrayList<>();
            if(giftCardOtherRecordList.size()>0) {
                for(GiftCardRecord giftCardRecord :giftCardOtherRecordList)
                {
                    logger.info("receiver openid:"+giftCardRecord.getReceiverOpenid());
                    BusinessCustomer sender= businessCusRepository.findByOpenidEquals(giftCardRecord.getSenderOpenid());
                    Long cardId=giftCardRecord.getCardId();
                    GiftCard giftCard=giftCardRepository.findByIdEquals(cardId);
                    if(giftCard.getCardType()==2){
                        //号码卡
                        Map<String,Object>receiveMap1=new HashedMap();
                        receiveMap1.put("id",giftCardRecord.getId());
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
                        receiveMap2.put("id",giftCardRecord.getId());
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
                    modelAndView.addObject("cardReceive",cardReceive);
                }
            }
            modelAndView.setViewName("shareGift/my_gift_give");
        }else if(StringUtils.equals(state,"giftCard")){
            modelAndView.addObject("openid",openId);
            modelAndView.setViewName("shareGift/gift");
        }else if(StringUtils.equals(state,"shareGiftLottery")){
            /***==> 九宫格抽奖页面*/
            modelAndView.addObject("openid",openId);
            //查询当前九宫格所使用的活动
            GiftAwardsRules activeNow=giftAwardsRulesRepository.findByIsAbleEquals(1);
            //加载当前活动奖品图片。
            List<GiftAwards> awards=giftAwardsRepository.findByRulesIdOrderByIndexOrder(activeNow.getId());
            modelAndView.addObject("awards",awards);
            //当前活动所需要消耗的积分
            int usePoints=activeNow.getPoints();
            modelAndView.addObject("usePoints",usePoints);
            //根据openid查找用户当前积分数
            GiftPoint userPointsInfo=giftPointRepository.findByOpenidEquals(openId);
            //抽奖次数,当前积分
            int times;
            int userPoints;
            if(userPointsInfo!=null){
                userPoints=userPointsInfo.getPointTotal()-userPointsInfo.getPointUsed();
                //计算可抽奖次数
                double a=userPoints;
                double b=usePoints;
                double c = a/b;
                times=(int)Math.floor(c);
                logger.info("[ouathController shareGiftLottery]userPoints/usePoints=times:"+a+"/"+b+"="+times);
            }else{
                //用户未获得过积分
                times=0;
                userPoints=0;
            }
            modelAndView.addObject("openid",openId);
            modelAndView.addObject("times",times);
            modelAndView.addObject("userPoints",userPoints);
            modelAndView.setViewName("shareGift/lottery");

        }

        return modelAndView;
    }






}
