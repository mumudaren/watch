package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.entity.business.BusinessNumberRecord;
import cn.cvtt.nuoche.entity.business.wx_product;
import cn.cvtt.nuoche.facade.IBusinessCallRecordInterface;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.ISystemParamInterface;
import cn.cvtt.nuoche.reponsitory.IBusinessCusRepository;
import cn.cvtt.nuoche.reponsitory.IBusinessNumberRecordRepository;
import cn.cvtt.nuoche.util.ConfigUtil;
import cn.cvtt.nuoche.util.DateUtils;
import cn.cvtt.nuoche.util.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PageController extends  BaseController{
    @Autowired
    IProductInterface productInterface;
    @Autowired
    IBusinessCusRepository businessCusRepository;
    @Autowired
    ISystemParamInterface  systemParamInterface;
    @Autowired
    ConfigUtil  util;
    @Autowired
    IBusinessNumberRecordRepository  recordRepository;
    @Autowired
    IBusinessCallRecordInterface  callRecordInterface;

     @SuppressWarnings("all")
     @RequestMapping("/getNumber")
     public ModelAndView  getNumber(@ModelAttribute("openid") String openid){
         ModelAndView  model=new ModelAndView();
         openid="oIFn90xXM4M-zUayrLI4hxLGZNKA";
         logger.info("openid+++++"+openid);
         BusinessCustomer  customer=businessCusRepository.findByOpenidEquals(openid);
         if(customer!=null){
             model.setViewName("buy_safenumber");
             String json= productInterface.findRegexProduct(util.getBusinessKey(),"0");
             List<wx_product> products=JsonUtils.handlerRegexJson(json);
            /* List<wx_product> products=new ArrayList<>();
             if(obj.getIntValue("code")==200){
                 JsonUtils.handlerArgs(products,obj);
             }*/
             model.addObject("ls",products);
         }else {
             model.setViewName("validate_tel");
         }
         Map<String,String> map=new HashMap<>();
         map.put("openid",openid);
         model.addObject("user",map);
         return model;
     }

    @RequestMapping("/toDetail.html")
    public  ModelAndView  toDetail(@RequestParam("number") String number,@RequestParam("smbms;") String smbms,@RequestParam("openid;") String  openid){
        ModelAndView  model=new ModelAndView();
        model.setViewName("detail");
        BusinessCustomer  customer=businessCusRepository.findByOpenidEquals(openid);
        BusinessNumberRecord  record=recordRepository.findBySmbmsEqualsAndBusinessIdEquals(smbms,util.getBusinessKey());
        Map<String,String> map=new HashMap<>();
        map.put("number",number);
        map.put("smbms",smbms);
        map.put("userPhone",customer.getPhone());
        map.put("numberSub", DateUtils.formatString(record.getSubts(), Constant.DATETEMPLATE));
        map.put("validDate",DateUtils.formatString(record.getValidTime(),Constant.DATETEMPLATE));
        map.put("callRestict",record.getCallrestrict());
        System.out.println("参数为："+map);
       /* map.put("voiceRecord",callRecordInterface.findVoiceRecordLately(util.getBusinessKey(),smbms,false));
        map.put("callRecord",callRecordInterface.findCallRecordLately(util.getBusinessKey(),smbms,false));*/
        String json=callRecordInterface.findHeard(util.getBusinessKey(),smbms);
        JSONObject  obj=JSONObject.parseObject(json);
        if(obj.getIntValue("code")==200 ){
            JSONObject  ob=JSONObject.parseObject(obj.getString("data"));
            map.put("voiceUnheard",ob.getString("voice"));
            map.put("recordUnheard",ob.getString("record"));
        }
        model.addObject("user",map);
        Map<String,Object>  re=new HashMap<>();
        String callBusiness=callRecordInterface.findCallRecordLately(util.getBusinessKey(),smbms,false);
        JSONObject obCall=JSONObject.parseObject(callBusiness);
        logger.info("recordLoading:info:"+obCall.get("code"));
        String tt=obCall.getString("data");
        if(StringUtils.isEmpty(tt.replace("[","").replace("]",""))){
            model.addObject("isEmpty",1);
        }else {
            JSONObject child= JSONObject.parseObject(tt.replace("[","").replace("]",""));
            logger.info("record child is:"+child);
            String  temp=child.getString("a");
            re.put("recordNumberNotHide",temp);
            re.put("recordNumber",temp.substring(0,3)+"****"+temp.substring(7));
            re.put("nox",child.getString("x"));
            re.put("recordTime",DateUtils.format(child.getDate("ringTime")));
            re.put("recordDuration",DateUtils.formatDuration(child.getIntValue("duration")));
        }
        System.out.println("号码详情中值为："+re);
        model.addObject("record",re);
        String voiceBusiness=callRecordInterface.findVoiceRecordLately(util.getBusinessKey(),smbms,false);
        obCall= JSONObject.parseObject(voiceBusiness);
        logger.info("voiceLoading:info:"+obCall.get("code"));
        String vv=obCall.getString("data");
        logger.info("vv is:"+vv);
        re=new HashMap<>();
        if(StringUtils.isEmpty(vv.replace("[","").replace("]",""))){
            model.addObject("isEmpty",1);
        }else {
            JSONObject child= JSONObject.parseObject(vv.replace("[","").replace("]",""));
            //录音文件路径为空
            if(StringUtils.isEmpty(child.getString("voicemailFile"))){
                model.addObject("isEmptyVoice",1);
                logger.info("voicePath is empty:");
            }
            re.put("voiceNox",child.getString("a"));
            re.put("voiceNumber",child.getString("x"));
            re.put("recordTime",DateUtils.format(child.getDate("ringTime")));
            re.put("recordDuration",child.getString("duration"));
            /**  放入留言  start */
            String  dbVoicePath=child.getString("voicemailFile");
            String param=systemParamInterface.getSystemConfigByArgs(2,util.getBusinessKey());
            Map<String,String>  maps= JsonUtils.handlerJson(param);
            re.put("voicePath",maps.get("APPLICATION_VOICE_PATH")+dbVoicePath);
            logger.info("voicePath is:"+re.get("voicePath"));
            /**end*/
        }
        model.addObject("voice",re);
        String productJson= productInterface.findRegexProduct(util.getBusinessKey(),"0");
        List<wx_product> products=JsonUtils.handlerRegexJson(productJson);
        model.addObject("products",products);
        model.addObject("openid",openid);
        return  model;
    }

    @SuppressWarnings("all")
    @RequestMapping("/CallRecord.html")
    public  ModelAndView   toCallList(@RequestParam("number") String smbms){
        ModelAndView modelAndView=new ModelAndView();
        logger.info("smbms is:"+smbms);
        modelAndView.setViewName("message_call");
        //Map<String,Object>  map=new HashMap<>();
        JSONArray  arrList=new JSONArray();
        String json=callRecordInterface.findCallRecordLately(util.getBusinessKey(),smbms,true);
        JSONObject object=JSONObject.parseObject(json);
        if(StringUtils.isEmpty(object.getString("data"))){
            modelAndView.addObject("isEmpty",1);
            logger.info("isEmpty"+modelAndView.getModel().get("isEmpty"));
            //map.put("isEmpty",1);
        }else {
            modelAndView.addObject("isEmpty",0);
            logger.info("isEmpty"+modelAndView.getModel().get("isEmpty"));
            String data=object.getString("data");
            JSONArray arr=JSONArray.parseArray(data);
            for(Object  obj:arr){
               JSONObject o=JSONObject.parseObject(obj.toString());
               Map<String,Object> children=new HashMap<>();
               String temp=o.getString("a");
                children.put("childNumberRecordNotHide",temp);
               children.put("childNumber",temp.substring(0,3)+"****"+temp.substring(7));
               children.put("recordDate",DateUtils.formatString(o.getDate("ringTime"),Constant.DATETEMPLATE));
               children.put("recordTime",DateUtils.formatTime(o.getDate("ringTime")));
               children.put("duration",DateUtils.formatDuration(o.getIntValue("duration")));
               arrList.add(children);
            }
        }
        modelAndView.addObject("smbms",smbms);
        modelAndView.addObject("records",arrList);
        return modelAndView;
    }
    @SuppressWarnings("all")
    @RequestMapping("/VoiceRecord.html")
    public  ModelAndView   toMessageList(@RequestParam("number") String smbms){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("message");
        JSONArray  arrList=new JSONArray();
        //Map<String,Object>  map=new HashMap<>();
        String json=callRecordInterface.findVoiceRecordLately(util.getBusinessKey(),smbms,true);
        JSONObject object=JSONObject.parseObject(json);
        if(StringUtils.isEmpty(object.getString("data"))){
            modelAndView.addObject("isEmpty",1);
            logger.info("isEmpty"+modelAndView.getModel().get("isEmpty"));
           // map.put("isEmpty",1);
        }else {
            modelAndView.addObject("isEmpty",0);
            logger.info("isEmpty"+modelAndView.getModel().get("isEmpty"));
            String data=object.getString("data");
            JSONArray arr=JSONArray.parseArray(data);
            for(Object  obj:arr){
                JSONObject o=JSONObject.parseObject(obj.toString());
                Map<String,Object> children=new HashMap<>();
//                children.put("childNumber", o.getString("x"));
                String tempVoice=o.getString("a");
                children.put("childNumber",tempVoice.substring(0,3)+"****"+tempVoice.substring(7));
                children.put("childA",o.getString("a"));
                children.put("recordDate",DateUtils.formatString(o.getDate("ringTime"),Constant.DATETEMPLATE)+" "+DateUtils.formatTime(o.getDate("ringTime")));
                /**  放入留言  start */
                String  dbVoicePath=o.getString("voicemailFile");
                String param=systemParamInterface.getSystemConfigByArgs(2,util.getBusinessKey());
                Map<String,String>  maps= JsonUtils.handlerJson(param);
                children.put("voicePath",maps.get("APPLICATION_VOICE_PATH")+dbVoicePath);
                /**end*/
                children.put("duration",o.getIntValue("duration"));
                arrList.add(children);
            }
        }
        modelAndView.addObject("smbms",smbms);
        modelAndView.addObject("voices",arrList);
        return modelAndView;
    }

     @RequestMapping("/index")
    public  String  index(){
         return "index";
     }


    @RequestMapping("/suggest")
    public  String  suggest(){
         return "suggest";
    }

    @RequestMapping("/complain.html")
    public  String  complain(){

         return  "complain";
    }






}
