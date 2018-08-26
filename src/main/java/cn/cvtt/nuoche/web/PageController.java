package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.entity.business.BindVo;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.entity.business.BusinessNumberRecord;
import cn.cvtt.nuoche.entity.business.wx_product;
import cn.cvtt.nuoche.facade.IBusinessCallRecordInterface;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.ISystemParamInterface;
import cn.cvtt.nuoche.reponsitory.IBusinessCusRepository;
import cn.cvtt.nuoche.reponsitory.IBusinessNumberRecordRepository;
import cn.cvtt.nuoche.server.impl.NumberServiceImpl;
import cn.cvtt.nuoche.util.ConfigUtil;
import cn.cvtt.nuoche.util.DateUtils;
import cn.cvtt.nuoche.util.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    @Autowired  private NumberServiceImpl numberService;
    @Autowired
    IBusinessNumberRecordRepository  recordRepository;
    @Autowired
    IBusinessCallRecordInterface  callRecordInterface;
    private static final Logger logger = LoggerFactory.getLogger(PageController.class);
     @SuppressWarnings("all")
     @RequestMapping("/getNumber")
     public ModelAndView  getNumber(@ModelAttribute("openid") String openid){
         ModelAndView  model=new ModelAndView();
//         openid="oIFn90xXM4M-zUayrLI4hxLGZNKA";
         logger.info("[getNumber]openid is:"+openid);
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
             model.addObject("phone",customer.getPhone());
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
        //该号码由谁提供字段查询方法。

        Map<String,String> map=new HashMap<>();
        map.put("number",number);
        map.put("smbms",smbms);
        map.put("userPhone",record.getUserPhone());
        map.put("numberSub", DateUtils.formatString(record.getSubts(), Constant.DATETEMPLATE));
        map.put("validDate",DateUtils.formatString(record.getValidTime(),Constant.DATETEMPLATE));
        map.put("callRestict",record.getCallrestrict());
        logger.info("[toDetail]Prams is:"+map);
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
        logger.info("[toDetail]recordLoading:info:"+obCall.get("code"));
        String tt=obCall.getString("data");
        if(StringUtils.isEmpty(tt.replace("[","").replace("]",""))){
            model.addObject("isEmpty",1);
            re.put("recordNumberNotHide","无");
            re.put("recordNumber","无");
            re.put("nox","无");
            re.put("recordTime","无");
            re.put("recordDuration","无");
        }else {
            JSONObject child= JSONObject.parseObject(tt.replace("[","").replace("]",""));
            logger.info("[toDetail]record child is:"+child+"\n");
            String  temp=child.getString("a");
            re.put("recordNumberNotHide",temp);
            re.put("recordNumber",temp.substring(0,3)+"****"+temp.substring(7));
            re.put("nox",child.getString("x"));
            re.put("recordTime",DateUtils.format(child.getDate("ringTime")));
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String durationRecord="0";
            try {
                String releaseTime=simpleFormat.format(child.getDate("releaseTime"));
                String answerTime=simpleFormat.format(child.getDate("answerTime"));
                long from = simpleFormat.parse(releaseTime).getTime();
                long to = simpleFormat.parse(answerTime).getTime();
                int duration=(int)(from - to);
                durationRecord= DateUtils.formatDurationNew(duration);
                logger.info("[toDetail] durationRecord formatTime  is:"+DateUtils.formatDurationNew(duration)+"\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
            re.put("recordDuration",durationRecord);
        }
        model.addObject("record",re);
        String voiceBusiness=callRecordInterface.findVoiceRecordLately(util.getBusinessKey(),smbms,false);
        obCall= JSONObject.parseObject(voiceBusiness);
        logger.info("[toDetail]voiceLoading:info:"+obCall.get("code")+"\n");
        String vv=obCall.getString("data");
        logger.info("[toDetail]vv is:"+vv+"\n");
        re=new HashMap<>();
        if(StringUtils.isEmpty(vv.replace("[","").replace("]",""))){
            model.addObject("isEmpty",1);
            re.put("recordNumberNotHide","无");
            re.put("recordNumber","无");
            re.put("nox","无");
            re.put("voiceNox","无");
            re.put("voiceNumber","无");
            model.addObject("isEmptyVoice",1);
            re.put("recordTime","无");
            re.put("recordDuration","无");
            re.put("voicePath","1");

        }else {
            JSONObject child= JSONObject.parseObject(vv.replace("[","").replace("]",""));
            //录音文件路径为空
            if(StringUtils.isEmpty(child.getString("voicemailFile"))){
                model.addObject("isEmptyVoice",1);
                logger.info("[toDetail]voicePath is empty:"+"\n");
            }
            re.put("voiceNox",child.getString("a"));
            re.put("voiceNumber",child.getString("x"));
            re.put("recordTime",DateUtils.format(child.getDate("ringTime")));
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String durationRecord="0";
            try {
                String releaseTime=simpleFormat.format(child.getDate("releaseTime"));
                String answerTime=simpleFormat.format(child.getDate("answerTime"));
                long from = simpleFormat.parse(releaseTime).getTime();
                long to = simpleFormat.parse(answerTime).getTime();
                int duration=(int)(from - to);
                durationRecord= DateUtils.formatDurationNew(duration);
                logger.info("[toDetail]durationRecord formatTime  is:"+DateUtils.formatDurationNew(duration)+"\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
            re.put("recordDuration",durationRecord);
            /**  放入留言  start */
            String  dbVoicePath=child.getString("voicemailFile");
            String param=systemParamInterface.getSystemConfigByArgs(2,util.getBusinessKey());
            Map<String,String>  maps= JsonUtils.handlerJson(param);
            re.put("voicePath",maps.get("APPLICATION_VOICE_PATH")+dbVoicePath);
            logger.info("[toDetail]voicePath is:"+re.get("voicePath"));
            /**end*/
        }
        model.addObject("voice",re);
        String productJson= productInterface.findRegexProduct(util.getBusinessKey(),"0");
        List<wx_product> products=JsonUtils.handlerRegexJson(productJson);
//        //通话记录size
//        String recordSize=callRecordInterface.findCallRecordCount(util.getBusinessKey(),smbms);
//        model.addObject("recordSize",recordSize);
        model.addObject("products",products);
        model.addObject("openid",openid);
        return  model;
    }

    @SuppressWarnings("all")
    @RequestMapping("/CallRecord.html")
    public  ModelAndView   toCallList(@RequestParam("number") String smbms){
        ModelAndView modelAndView=new ModelAndView();
        logger.info("[toDetail]smbms is:"+smbms);
        modelAndView.setViewName("message_call");
        //Map<String,Object>  map=new HashMap<>();
        JSONArray  arrList=new JSONArray();
        String json=callRecordInterface.findCallRecordLately(util.getBusinessKey(),smbms,true);
        JSONObject object=JSONObject.parseObject(json);
        if(StringUtils.isEmpty(object.getString("data"))){
            modelAndView.addObject("isEmpty",1);
            logger.info("[toDetail]isEmpty"+modelAndView.getModel().get("isEmpty"));
            //map.put("isEmpty",1);
        }else {
            modelAndView.addObject("isEmpty",0);
            logger.info("[toDetail]isEmpty"+modelAndView.getModel().get("isEmpty"));
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
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String durationRecord="0";
                try {
                    String releaseTime=simpleFormat.format(o.getDate("releaseTime"));
                    String answerTime=simpleFormat.format(o.getDate("answerTime"));
                    long from = simpleFormat.parse(releaseTime).getTime();
                    long to = simpleFormat.parse(answerTime).getTime();
                    int duration=(int)(from - to);
                    durationRecord= DateUtils.formatDurationNew(duration);
                    logger.info("[toDetail]durationRecord formatTime  is:"+DateUtils.formatDurationNew(duration));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                children.put("duration",durationRecord);
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
            logger.info("[toDetail]isEmpty"+modelAndView.getModel().get("isEmpty"));
           // map.put("isEmpty",1);
        }else {
            modelAndView.addObject("isEmpty",0);
            logger.info("[toDetail]isEmpty"+modelAndView.getModel().get("isEmpty"));
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
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String durationRecord="0";
                try {
                    String releaseTime=simpleFormat.format(o.getDate("releaseTime"));
                    String answerTime=simpleFormat.format(o.getDate("answerTime"));
                    long from = simpleFormat.parse(releaseTime).getTime();
                    long to = simpleFormat.parse(answerTime).getTime();
                    int duration=(int)(from - to);
                    durationRecord= DateUtils.formatDurationNew(duration);
                    logger.info("[toDetail]durationRecord formatTime  is:"+DateUtils.formatDurationNew(duration));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                children.put("duration",durationRecord);
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

    @RequestMapping("/wrong.html")
    public  String  wrongPage(){

        return  "wrongPage";
    }

    @RequestMapping("/findStatus.html")
    public  String  findStatusMethod(String number) throws IOException {
        BindVo bind=new BindVo();
        bind.setUidnumber(number);
        Result result=numberService.queryRelation(bind);
        if(result.getCode()==200) {
            JSONObject jobj = JSONObject.parseObject(result.getData().toString());
            JSONObject res = jobj.getJSONObject("query_Relation_response");
            JSONArray items = JSONArray.parseArray(res.getJSONArray("items").toString());
            //遍历items
            JSONObject buyTime = null;
            if (items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    JSONObject job = items.getJSONObject(i);
                    logger.info("[findStatusMethod]job is:" + job.get("subts"));
                    if (!StringUtils.isEmpty(job.get("subts").toString())) {
                        buyTime = job;
                    }
                }
            }else{
                return "wrongPage";
            }
            //JSONObject buyTime=job.getJSONObject("subts");
            logger.info("[findStatusMethod]result is:" + res.toString());
            logger.info("[findStatusMethod]buyTime is:" + buyTime.getDate("subts"));
            //查询该号码的buyTime是空。绑定不成功。
            if (StringUtils.isEmpty(buyTime.getDate("subts").toString())) {
                logger.info("[findStatusMethod]bind or bindZhiZun fail:");
                return "wrongPage";
            }
            //订购时间小于今天（考虑调用绑定接口，秒的误差），说明今天没有订购记录，延期、解冻、绑定等均不成功。
            Date butDate = buyTime.getDate("subts");
            Date nowTime = new Date();
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(nowTime);
            // 将分钟、秒、毫秒域清零
            cal1.set(Calendar.MINUTE, 0);
            cal1.set(Calendar.SECOND, 0);
            cal1.set(Calendar.MILLISECOND, 0);
            Date nowReset = cal1.getTime();
            logger.info("[findStatusMethod]nowReset SECOND  is:" + nowReset);
            //购买时间小于当前时间，说明接口调用失败。
            if (butDate.getTime() < nowTime.getTime()) {
                return "wrongPage";
            }
            return "redirect:/oauth/admin/OwnerSafeNumber";
        }else {
            logger.info("[findStatusMethod]buy operation fail,will return wrongPage");
            return "wrongPage";
        }

    }
}
