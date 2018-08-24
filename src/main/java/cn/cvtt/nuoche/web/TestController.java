package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.common.result.ResultMsg;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.entity.business.BusinessNumberRecord;
import cn.cvtt.nuoche.entity.business.SystemFeedBack;
import cn.cvtt.nuoche.entity.business.wx_product;
import cn.cvtt.nuoche.facade.IBusinessCallRecordInterface;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.IRegexInterface;
import cn.cvtt.nuoche.reponsitory.IBusinessCusRepository;
import cn.cvtt.nuoche.reponsitory.IBusinessNumberRecordRepository;
import cn.cvtt.nuoche.reponsitory.ISystemFeedBack;
import cn.cvtt.nuoche.util.ConfigUtil;
import cn.cvtt.nuoche.util.DateUtils;
import cn.cvtt.nuoche.util.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequestMapping("/testSafe")
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



    @RequestMapping("/testMessage")
    public  ModelAndView  message(){
        ModelAndView  model=new ModelAndView();
        String openid="oIFn90393PZMsIt-kprqw0GWmVko";
        BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals(openid);
        model.addObject("user",userInfo);
        model.setViewName("suggest");
        return  model;
    }

    @RequestMapping("/testWongPage")
    public  ModelAndView  testWong(){
        ModelAndView  model=new ModelAndView();
        model.setViewName("wrongPage");
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
        List<BusinessNumberRecord>  ls=recordRepository.findAllByPrtmsEqualsAndBusinessIdEqualsOrderBySubtsDesc(userInfo.getPhone(),util.getBusinessKey());
        handlerList(model,ls);
        model.addObject("ls",ls);
        logger.info("record is:"+ls.get(0).getIsValid().toString());
        List<BusinessNumberRecord>  other=recordRepository.findAllByPrtmsNotAndUserPhoneEqualsAndBusinessIdEqualsOrderBySubtsDesc(userInfo.getPhone(),userInfo.getPhone(),util.getBusinessKey());

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
