package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.entity.RegisterVo;
import cn.cvtt.nuoche.entity.SNSUserInfo;
import cn.cvtt.nuoche.entity.WeixinOauth2Token;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.entity.business.BusinessNumberRecord;
import cn.cvtt.nuoche.entity.business.wx_product;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.IRegexInterface;
import cn.cvtt.nuoche.reponsitory.IBusinessCusRepository;
import cn.cvtt.nuoche.reponsitory.IBusinessNumberRecordRepository;
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
            logger.info("snsUserInfo{}"+snsUserInfo.toString());
            BusinessCustomer  info=businessCusRepository.findByOpenidEquals(openId);
            //WxUserInfo info=userService.findByOpenidEquals(openId);
            logger.info("WxUserInfo{}"+info.toString());
            RegisterVo vo= WxUtils.convertEntityVo(snsUserInfo,info);
            // 设置要传递的参数
            modelAndView.addObject("user", vo);
            logger.info("获取用户信息：" + JsonUtils.objectToJson(snsUserInfo));
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
        logger.info("openid++++"+openId);
        // 判断如果openid为空，则引导用户重新授权
        if (StringUtils.isBlank(openId)) {
            logger.info("code:{}已使用，转入重新授权。", code);
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
            logger.info("json oBJect>>>>>>>>"+products.size());
            modelAndView.addObject("ls",products);
        }
        /****===>>**/
        Map<String, String> map = new HashMap<>();
        map.put("openid", openId);
        // 设置要传递的参数
        modelAndView.addObject("user", map);
        modelAndView.addObject("openid",openId);
        logger.info("获取用户信息：" + JsonUtils.objectToJson(map));
        return modelAndView;
    }


    /**
     * 需要基本授权的请求
     * @param path
     * @return
     */
    @RequestMapping("/oauth/base/{path}")
    public String redirectBase(@PathVariable String path) {
        logger.info("util.getUrl"+util.getUrl());

        String requestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        requestUrl = requestUrl.replace("APPID", Constant.APP_ID)
                .replace("REDIRECT_URI", HttpClientUtil.urlEncodeUTF8(util.getUrl() + "/oauth/base"))
                .replace("SCOPE", "snsapi_base")
                .replace("STATE", path);
        logger.info("requestUrl:::"+requestUrl);
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
        logger.info("openid++++"+openId);
        // 判断如果openid为空，则引导用户重新授权
        if (StringUtils.isBlank(openId)) {
            logger.info("code:{}已使用，转入重新授权。", code);
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
        logger.info("获取用户信息：" + JsonUtils.objectToJson(map));
        return modelAndView;
    }


    /**
     * 需要基本授权的请求
     * @param path
     * @return
     */
    @RequestMapping("/oauth/other/{path}")
    public String redirectOther(@PathVariable String path) {
        logger.info("util.getUrl"+util.getUrl());

        String requestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        requestUrl = requestUrl.replace("APPID", Constant.APP_ID)
                .replace("REDIRECT_URI", HttpClientUtil.urlEncodeUTF8(util.getUrl() + "/oauth/other"))
                .replace("SCOPE", "snsapi_base")
                .replace("STATE", path);
        logger.info("requestUrl:::"+requestUrl);
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
        logger.info("requestUrl:::"+requestUrl);
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
        logger.info("requestUrl:::"+requestUrl);
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
        logger.info("openid++++"+openId);
        // 判断如果openid为空，则引导用户重新授权
        if (StringUtils.isBlank(openId)) {
            logger.info("code:{}已使用，转入重新授权。", code);
            modelAndView.setViewName("redirect:/oauth/regex/" + state);
            return modelAndView;
        }
        /***==> 先判断当前用户是否绑定手机号,如果没有则跳转绑定页面*/
        BusinessCustomer  customer=businessCusRepository.findByOpenidEquals(openId);
        if(customer==null||StringUtils.isEmpty(customer.getPhone())){
            modelAndView.setViewName("validate_tel");
            modelAndView.addObject("openid",openId);
            return  modelAndView;
        }else {
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
        logger.info("openid++++"+openId);
        // 判断如果openid为空，则引导用户重新授权
        if (StringUtils.isBlank(openId)) {
            logger.info("code:{}已使用，转入重新授权。", code);
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
        logger.info("获取用户信息：" + JsonUtils.objectToJson(map));
        BusinessCustomer  cus=businessCusRepository.findByOpenidEquals(openId);
       // WxUserInfo  wxuser=userRepository.findByOpenidEquals(openId);
        String regphone=cus.getPhone();
        logger.info(regphone+" =>>>> "+util.getBusinessKey());
        List<BusinessNumberRecord> records=recordRepository.findAllByUserPhoneEqualsAndBusinessIdEquals(regphone,util.getBusinessKey());
        if(records.size()<1){
            modelAndView.setViewName("buy_safenumber");
            String json= productInterface.findRegexProduct(util.getBusinessKey(),"0");
            List<wx_product> products=JsonUtils.handlerRegexJson(json);
           /* JSONObject  obj=JSONObject.parseObject(json);
            List<wx_product> products=new ArrayList<>();
            if(obj.getIntValue("code")==200){
                JsonUtils.handlerArgs(products,obj);
            }*/
            modelAndView.addObject("ls",products);
        }else {
            modelAndView.setViewName("my_safenumber");
            BusinessCustomer  userInfo=businessCusRepository.findByOpenidEquals(openId);
            //WxUserInfo userInfo=userRepository.findByOpenidEquals(openId);
            String  url= userInfo.getHeadimgurl();
            modelAndView.addObject("url",url);
            modelAndView.addObject("phone",userInfo.getPhone());
            List<BusinessNumberRecord>  ls=recordRepository.findAllByPrtmsEqualsAndBusinessIdEqualsOrderBySubtsDesc(userInfo.getPhone(),util.getBusinessKey());
            handlerList(modelAndView,ls);
            modelAndView.addObject("ls",ls);
            List<BusinessNumberRecord>  other=recordRepository.findAllByPrtmsNotAndUserPhoneEqualsAndBusinessIdEqualsOrderBySubtsDesc(userInfo.getPhone(),userInfo.getPhone(),util.getBusinessKey());
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
            logger.info("products>>>>>>>>>"+products.size());
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





}
