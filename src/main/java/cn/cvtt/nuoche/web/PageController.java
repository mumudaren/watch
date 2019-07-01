package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.Constant;
import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.common.result.ResultMsg;
import cn.cvtt.nuoche.entity.business.BindVo;
import cn.cvtt.nuoche.entity.business.BusinessCustomer;
import cn.cvtt.nuoche.entity.business.BusinessNumberRecord;
import cn.cvtt.nuoche.entity.business.wx_product;
import cn.cvtt.nuoche.entity.gift.GiftAwards;
import cn.cvtt.nuoche.entity.gift.GiftAwardsRecords;
import cn.cvtt.nuoche.entity.gift.GiftCard;
import cn.cvtt.nuoche.entity.gift.GiftCardRecord;
import cn.cvtt.nuoche.entity.gift.GiftCardRules;
import cn.cvtt.nuoche.entity.gift.GiftCoupon;
import cn.cvtt.nuoche.entity.gift.GiftCouponQrcode;
import cn.cvtt.nuoche.entity.gift.GiftCouponRecord;
import cn.cvtt.nuoche.entity.gift.GiftNumberQRcode;
import cn.cvtt.nuoche.entity.gift.GiftPoint;
import cn.cvtt.nuoche.entity.gift.GiftPointRecord;
import cn.cvtt.nuoche.entity.watch.NameCount;
import cn.cvtt.nuoche.facade.IBusinessCallRecordInterface;
import cn.cvtt.nuoche.facade.INumberInterface;
import cn.cvtt.nuoche.facade.IProductInterface;
import cn.cvtt.nuoche.facade.IRegexInterface;
import cn.cvtt.nuoche.facade.ISystemParamInterface;
import cn.cvtt.nuoche.reponsitory.IBusinessCusRepository;
import cn.cvtt.nuoche.reponsitory.IBusinessNumberRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftAwardsRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCardRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCardRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCardRulesRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCouponQrcodeRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCouponRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftCouponRepository;
import cn.cvtt.nuoche.reponsitory.IGiftNumberQRcodeRepository;
import cn.cvtt.nuoche.reponsitory.IGiftPointRecordRepository;
import cn.cvtt.nuoche.reponsitory.IGiftPointRepository;
import cn.cvtt.nuoche.reponsitory.INamaCountRepository;
import cn.cvtt.nuoche.server.impl.NumberServiceImpl;
import cn.cvtt.nuoche.service.QrcodeService;
import cn.cvtt.nuoche.util.ConfigUtil;
import cn.cvtt.nuoche.util.DateUtils;
import cn.cvtt.nuoche.util.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PageController extends  BaseController{
    @Autowired IProductInterface productInterface;
    @Autowired IBusinessCusRepository businessCusRepository;
    @Autowired IGiftPointRepository giftPointRepository;
    @Autowired IGiftPointRecordRepository giftPointRecordRepository;
    @Autowired ISystemParamInterface  systemParamInterface;
    @Autowired ConfigUtil  util;
    @Autowired  private NumberServiceImpl numberService;
    @Autowired IBusinessNumberRecordRepository  recordRepository;
    @Resource private QrcodeService qrcodeService;
    @Autowired IGiftNumberQRcodeRepository giftNumberQRcodeRepository;
    @Autowired IBusinessCallRecordInterface  callRecordInterface;
    @Autowired IGiftCardRepository giftCardRepository;
    @Autowired IGiftCardRecordRepository giftCardRecordRepository;
    @Autowired IGiftCouponRecordRepository giftCouponRecordRepository;
    @Autowired IGiftAwardsRecordRepository giftAwardsRecordRepository;
    @Autowired IGiftCardRulesRepository giftCardRulesRepository;
    @Autowired IGiftCouponRepository giftCouponRepository;
    @Autowired  private IBusinessNumberRecordRepository  businessNumberRecordRepository;
    @Autowired IRegexInterface regexInterface;
    @Autowired INumberInterface numberInterface;
    @Autowired IGiftCouponQrcodeRepository giftCouponQrcodeRepository;
    @Autowired
    private INamaCountRepository iNamaCountRepository;
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
             String json= productInterface.findRegexProductByType(util.getBusinessKey(),"0","1");
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
            logger.info("[toDetail]temp is:"+temp+"\n");
            re.put("recordNumberNotHide",temp);
            re.put("recordNumber",temp.substring(0,3)+"****"+temp.substring(7));
            logger.info("[toDetail]nox is:"+child.getString("x")+"\n");
            re.put("nox",child.getString("x"));
            re.put("recordTime",DateUtils.format(child.getDate("startTime")));
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            re.put("recordTime",DateUtils.format(child.getDate("startTime")));
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        String productJson= productInterface.findRegexProductByType(util.getBusinessKey(),"0","1");
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
               children.put("recordDate",DateUtils.formatString(o.getDate("startTime"),Constant.DATETEMPLATE));
               children.put("recordTime",DateUtils.formatTime(o.getDate("startTime")));
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                children.put("recordDate",DateUtils.formatString(o.getDate("startTime"),Constant.DATETEMPLATE)+" "+DateUtils.formatTime(o.getDate("startTime")));
                /**  放入留言  start */
                String  dbVoicePath=o.getString("voicemailFile");
                String param=systemParamInterface.getSystemConfigByArgs(2,util.getBusinessKey());
                Map<String,String>  maps= JsonUtils.handlerJson(param);
                children.put("voicePath",maps.get("APPLICATION_VOICE_PATH")+dbVoicePath);
                /**end*/
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
    public  ModelAndView  index(){
         ModelAndView modelAndView=new ModelAndView();
         modelAndView.setViewName("index");
         Result  result = new Result(ResultMsg.OPERATESUCEESS);;
         List<NameCount> nameCounts=iNamaCountRepository.findAll();
//         System.out.println("result:"+nameCounts);
//         JSONArray json = new JSONArray();
//         for(NameCount a : nameCounts){
//             JSONObject jo = new JSONObject();
//             jo.put("id", a.getId());
//             jo.put("name", a.getName());
//             jo.put("openid", a.getOpenid());
//             jo.put("createTime", a.getCreateTime());
//             json.add(jo);
//         }
//         System.out.println("result jsonArray:"+json);
         result.setData(nameCounts);
         modelAndView.addObject("data",nameCounts);
         return modelAndView;
     }


    @RequestMapping("/keyboardPage")
    public  String  keyboardPage(){
         return "keyboard";
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
    @RequestMapping("/wrong2.html")
    public  String  wrongPage2(){

        return  "gift/wrongPage";
    }
    @RequestMapping("/hainiu.html")
    public  String  hainiuIndex(){
        return  "shareGift/hainiu";
    }
    //使用说明页面
    @RequestMapping("/activity_description.html")
    public  String  activityDescription(){
        return  "shareGift/activity_description";
    }

    //延期、解冻支付成功后跳转的接口。根据号码绑定记录判断是靓号还是普通号。
    @RequestMapping("/findNumnberType.html")
    public  String  findNumnberTypeMethod(String number) throws IOException {
         String phoneType="normal";
        BusinessNumberRecord  record=recordRepository.findBySmbmsEqualsAndBusinessIdEquals(number,util.getBusinessKey());
        if(record.getRegexId()>0){
            phoneType="ZhiZun";
        }
        logger.info("[findNumnberTypeMethod]number type is:"+phoneType);
        return "redirect:/findStatus.html?myNumber="+number+"&phoneType="+phoneType;
    }
    //判断延期、购买、解冻等是否成功。
    @RequestMapping("/findStatus.html")
    public  String  findStatusMethod(@RequestParam("myNumber") String number,
                                     @RequestParam("phoneType") String type,
                                     @RequestParam(value ="couponRecordId",defaultValue ="") String couponRecordId) throws IOException {
         logger.info("[findStatus.html]this is findStatusMethod");
        //支付成功后删除优惠券，即isUsed设置为1.
        if(!StringUtils.isEmpty(couponRecordId)) {
            logger.info("[findStatus.html]couponRecordId is:"+couponRecordId);
            GiftCouponRecord giftCouponRecord = giftCouponRecordRepository.findGiftCouponRecordByIdEquals(Long.parseLong(couponRecordId));
            giftCouponRecord.setIsUsed(1);
            giftCouponRecordRepository.saveAndFlush(giftCouponRecord);
        }
//        BindVo bind=new BindVo();
//        bind.setUidnumber(number);
//        Result result=null;
//        if(StringUtils.equals(type,"normal")){
//            logger.info("[findStatusMethod]number's type is normal");
//            result=numberService.queryNormalRelation(bind);
//        }else{
//            logger.info("[findStatusMethod]number's type is ZhiZun");
//            result=numberService.queryRelation(bind);
//        }
//
//        logger.info("[findStatusMethod]numberService result is:"+result.toString());
//        if(result.getCode()==200) {
//            JSONObject jobj = JSONObject.parseObject(result.getData().toString());
//            JSONObject res = jobj.getJSONObject("query_Relation_response");
//            JSONArray items = JSONArray.parseArray(res.getJSONArray("items").toString());
//            //遍历items
//            JSONObject buyTime = null;
//            if (items.size() > 0) {
//                for (int i = 0; i < items.size(); i++) {
//                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
//                    JSONObject job = items.getJSONObject(i);
//                    if (!StringUtils.isEmpty(job.get("subts").toString())) {
//                        Date dateRecent=job.getDate("subts");
//                        Date dateTemp=new Date(0);
//                        if(dateRecent.getTime()>dateTemp.getTime()){
//                            dateTemp=dateRecent;
//                            buyTime = job;
//                        }
//                    }
//                }
//            }else{
//                return "redirect:/oauth/admin/OwnerSafeNumber";
//            }
//            //JSONObject buyTime=job.getJSONObject("subts");
//            logger.info("[findStatusMethod]final buyTime is:" + buyTime.getDate("subts"));
//            //查询该号码的buyTime是空。绑定不成功。
//            if (StringUtils.isEmpty(buyTime.getDate("subts").toString())) {
//                logger.info("[findStatusMethod]bind or bindZhiZun fail:");
//                return "wrongPage";
//            }
//            //订购时间小于今天（考虑调用绑定接口，秒的误差），说明今天没有订购记录，延期、解冻、绑定等均不成功。
//            Date butDate = buyTime.getDate("subts");
//            Date nowTime = new Date();
//            Calendar cal1 = Calendar.getInstance();
//            cal1.setTime(nowTime);
//            // 将分钟、秒、毫秒域清零
//            cal1.add(Calendar.MINUTE, -4);
//            cal1.add(Calendar.SECOND, -59);
//            Date nowReset = cal1.getTime();
//            logger.info("[findStatusMethod]nowReset SECOND  is:" + nowReset);
//            //购买时间小于当前时间，说明接口调用失败。
////            if (butDate.getTime() < nowTime.getTime()) {
////                return "wrongPage";
////            }
//            return "redirect:/oauth/admin/OwnerSafeNumber";
//        }else {
//            logger.info("[findStatusMethod]buy operation fail,will return wrongPage");
//            //return "wrongPage";
//            return "redirect:/oauth/admin/OwnerSafeNumber";
//        }
        return "redirect:/oauth/admin/OwnerSafeNumber";

    }

    //gift判断延期、购买、解冻等是否成功。
    @RequestMapping("/findGiftStatus.html")
    public  String  findGiftStatusMethod(@RequestParam("myNumber") String number,@RequestParam("phoneType") String type) throws IOException {
//        BindVo bind=new BindVo();
//        bind.setUidnumber(number);
//        Result result=null;
//        if(StringUtils.equals(type,"normal")){
//            logger.info("[findStatusMethod]number's type is normal");
//            result=numberService.queryNormalRelation(bind);
//        }else{
//            logger.info("[findStatusMethod]number's type is ZhiZun");
//            result=numberService.queryRelation(bind);
//        }
//
//        logger.info("[findStatusMethod]numberService result is:"+result.toString());
//        if(result.getCode()==200) {
//            JSONObject jobj = JSONObject.parseObject(result.getData().toString());
//            JSONObject res = jobj.getJSONObject("query_Relation_response");
//            JSONArray items = JSONArray.parseArray(res.getJSONArray("items").toString());
//            //遍历items
//            JSONObject buyTime = null;
//            if (items.size() > 0) {
//                for (int i = 0; i < items.size(); i++) {
//                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
//                    JSONObject job = items.getJSONObject(i);
//                    if (!StringUtils.isEmpty(job.get("subts").toString())) {
//                        Date dateRecent=job.getDate("subts");
//                        Date dateTemp=new Date(0);
//                        if(dateRecent.getTime()>dateTemp.getTime()){
//                            dateTemp=dateRecent;
//                            buyTime = job;
//                            logger.info("[findStatusMethod]foreach buyTime is:" + buyTime.get("subts"));
//                        }
//                    }
//                }
//            }else{
//                return "wrongPage";
//            }
//            //JSONObject buyTime=job.getJSONObject("subts");
//            logger.info("[findStatusMethod]final buyTime is:" + buyTime.getDate("subts"));
//            //查询该号码的buyTime是空。绑定不成功。
//            if (StringUtils.isEmpty(buyTime.getDate("subts").toString())) {
//                logger.info("[findStatusMethod]bind or bindZhiZun fail:");
//                return "wrongPage";
//            }
//            //订购时间小于今天（考虑调用绑定接口，秒的误差），说明今天没有订购记录，延期、解冻、绑定等均不成功。
//            Date butDate = buyTime.getDate("subts");
//            Date nowTime = new Date();
//            Calendar cal1 = Calendar.getInstance();
//            cal1.setTime(nowTime);
//            // 将分钟、秒、毫秒域清零
//            cal1.add(Calendar.MINUTE, -4);
//            cal1.add(Calendar.SECOND, -59);
//            Date nowReset = cal1.getTime();
//            logger.info("[findStatusMethod]nowReset SECOND  is:" + nowReset);
//            //购买时间小于当前时间，说明接口调用失败。
////            if (butDate.getTime() < nowTime.getTime()) {
////                return "wrongPage";
////            }
//            return "redirect:/oauth/admin/OwnerSafeNumber";
//        }else {
//            logger.info("[findStatusMethod]buy operation fail,will return wrongPage");
//            //return "wrongPage";
//            return "redirect:/oauth/admin/OwnerSafeNumber";
//        }
        return "redirect:/oauth/admin/OwnerSafeNumber";

    }
    //套餐卡、号码卡转发朋友圈后跳转sweep接口
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

    //积分记录
    @RequestMapping("/myPoints")
    public  ModelAndView  myPoint( @RequestParam(value = "openid") String openid){
        ModelAndView  modelAndView=new ModelAndView();
        //根据openId查找用户积分
        GiftPoint userPointsInfo=giftPointRepository.findByOpenidEquals(openid);
        int points;
        if(userPointsInfo!=null) {
            points = userPointsInfo.getPointTotal() - userPointsInfo.getPointUsed();
        }else{
            points=0;
        }
        Date nowBefore30=DateUtils.addDay(new Date(),"-30");
        logger.info("[myPoints]nowBefore30 is:"+nowBefore30);
        //查找用户的积分消耗记录
        List<GiftPointRecord> lessThanZeroRecord=giftPointRecordRepository.findByOpenidEqualsAndChangePointLessThanAndUpdateTimeGreaterThanOrderByUpdateTimeDesc(openid,0,nowBefore30);
        if(lessThanZeroRecord!=null){
            for(GiftPointRecord res:lessThanZeroRecord){
                switch(res.getResource()){
                    case 3:
                        String name="";
                        //查找中奖奖品名称。
                        if(res.getRecordId()!=null){

                            GiftAwardsRecords giftAwardsRecord=giftAwardsRecordRepository.findByIdEquals(res.getRecordId());
                            name = ",奖品："+giftAwardsRecord.getAwardsName();
                        }
                        res.setResourceName("幸运大抽奖"+name);
                        break;
                }
                String datePrase=DateUtils.format(res.getUpdateTime());
                res.setDatePrase(datePrase);
            }
        }
        //查找用户的积分增长记录
        List<GiftPointRecord> moreThanZeroRecord=giftPointRecordRepository.findByOpenidEqualsAndChangePointGreaterThanAndUpdateTimeGreaterThanOrderByUpdateTimeDesc(openid,0,nowBefore30);
        if(moreThanZeroRecord!=null){
            logger.info("[myPoints]moreThanZeroRecord is not null.");
            for(GiftPointRecord temp:moreThanZeroRecord){
                switch(temp.getResource()){
                    case 1:
                        //查找领取现金券的好友名称
                        String name="好友";
                        if(temp.getRecordId()!=null){
                            GiftCouponRecord giftCouponRecord=giftCouponRecordRepository.findGiftCouponRecordByIdEquals(temp.getRecordId());
                            BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals(giftCouponRecord.getReceiverOpenid());
                            if(userInfo!=null) {
                                if(!StringUtils.isEmpty(userInfo.getNickname())) {
                                    name = userInfo.getNickname();
                                }
                            }
                        }
                        temp.setResourceName(name+"领取现金券");
                        break;
                    case 2:
                        temp.setResourceName("每日分享到朋友圈");
                        break;
                    case 4:
                        temp.setResourceName("每日分享给好友");
                        break;
                }
                String datePrase2=DateUtils.format(temp.getUpdateTime());
                temp.setDatePrase(datePrase2);
            }
        }
        modelAndView.addObject("lessThanRecord",lessThanZeroRecord);
        modelAndView.addObject("moreThanRecord",moreThanZeroRecord);
        modelAndView.addObject("userPoints",points);
        modelAndView.setViewName("shareGift/my_points");
        return modelAndView;
    }
    //套餐卡页面
    @RequestMapping("/regexGift")
    public  ModelAndView  regexGiftMethod(@RequestParam(value ="isHideOldDiv",defaultValue ="false") boolean isHideOldDiv,
                                              @RequestParam(value ="cardId",defaultValue ="noId") String cardId,
                                              @RequestParam(value ="openid") String openid){
        ModelAndView  model=new ModelAndView();
        logger.info("[regexGift]isHideOldDiv is:"+isHideOldDiv);
        if(!StringUtils.equals(cardId,"noId")){
            long cardIdSearch=Long.parseLong(cardId);
            GiftCardRules card=giftCardRulesRepository.findByIdEquals(cardIdSearch);
            //可购买的套餐名称
            JSONObject eachGiftArray= JSONObject.parseObject(card.getRegexId());
            //遍历套餐，获取套餐名字
            String regexName="";
            for(String str:eachGiftArray.keySet()){
                regexName=regexName+str+",";
                logger.info("[regexGift]eachGiftRegex is:"+regexName);
            }
            String finalRegexName=regexName.substring(0,regexName.length()-1);
            logger.info("[regexGift]finalRegexName is:"+finalRegexName);
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
            GiftCoupon giftCoupon=giftCouponRepository.findByIdEqualsAndEndTimeGreaterThanEqual(couponId,new Date());
            eachGiftCouponRecord.setGiftCoupon(giftCoupon);
        }
        model.addObject("giftRecord",giftRecordList);
        model.addObject("openid",openid);
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(openid);
        //model.addObject("userPhone",user.getPhone());
        model.setViewName("shareGift/gift_card");
        return  model;
    }
    //送套餐卡选套餐
    @RequestMapping("/chooseRegex")
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
    @RequestMapping("/chooseCard")
    public  String  chooseCard(@RequestParam("cardId") String cardId,
                                   @RequestParam("openid") String openid){
        if(cardId!=null){
            logger.info("[chooseCard]chooseCard is:"+cardId);
        }
        boolean isHideOldDiv=true;
        return  "redirect:"+"regexGift?isHideOldDiv="+isHideOldDiv+"&cardId="+cardId+"&openid="+openid;
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
        //临时海牛助手
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
            model.setViewName("shareGift/card_qrcode");
        }else{
            //加载分享页面所需要的数据。号码卡
            model.addObject("card",giftCard);
            BusinessCustomer user= businessCusRepository.findByOpenidEquals(giftCardRecord.getSenderOpenid());
            model.addObject("user",user);
            model.addObject("giftCardRecord",giftCardRecord);
            model.setViewName("shareGift/gift_qrcode");
        }
        return model;
    }
    //领取号码卡、套餐卡成功的页面
    @RequestMapping("/receiveCardSuccess")
    public ModelAndView  receiveCardSuccess(@RequestParam("giftCardRecordId") Long giftCardRecordId,
                                            @RequestParam("giftCardId") Long giftCardId,
                                            @RequestParam(value = "number" ,defaultValue = "") String number,
                                            @RequestParam("openid") String openid){
        ModelAndView  model=new ModelAndView();
        logger.info("[receiveCardSuccess]openid:"+openid);
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
                //使用真实的海牛助手调用绑定接口
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
                //BusinessNumberRecord 更新真实的海牛助手
                record.setSmbms(number);
                recordRepository.saveAndFlush(record);
                logger.info("save record operation over.");
            }
            //giftCard 更新真实的海牛助手
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
            //BusinessNumberRecord 更新真实的海牛助手
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
    @RequestMapping("/numberGift")
    public ModelAndView  numberGiftMethod( ){
        ModelAndView  model=new ModelAndView();
        model.setViewName("shareGift/gift_number");
        return  model;
    }

    //选中号码卡,加载号码卡套餐等数据。
    @RequestMapping("/chooseNumberRegex")
    public  ModelAndView  chooseNumberRegex(@RequestParam("openid") String openid){
        ModelAndView  model=new ModelAndView();
        //String json=regexInterface.findRegexByBusiness(util.getBusinessKey());
        String json=regexInterface.findRegexByBusiness(util.getBusinessKey());
        //加载所有套餐，id和regexName
        List<Map<String,String>> map=JsonUtils.handlerImgJson(json,"id","regexName");
        model.addObject("regex",map);
        model.addObject("openid",openid);
        logger.info("[chooseNumberRegex]map is:"+map.toString());
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

            logger.info("[chooseNumberRegex]map is:"+regex);
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
    @RequestMapping("/numberRegex")
    public  ModelAndView  numberGiftMethod(@RequestParam(value ="isHideOldDiv",defaultValue ="false") boolean isHideOldDiv,
                                           @RequestParam(value ="chooseNumber",defaultValue ="0") String chooseNumber,
                                           @RequestParam(value ="openid") String openid) {
        ModelAndView  model=new ModelAndView();
        //查找该用户所有未使用的优惠券。
        List<GiftCouponRecord> giftRecordList=giftCouponRecordRepository.findAllByReceiverOpenidEqualsAndIsUsedEqualsOrderByGetTimeDesc(openid,0);
        for(GiftCouponRecord eachGiftCouponRecord :giftRecordList)
        {
            Long couponId=eachGiftCouponRecord.getCouponId();
            GiftCoupon giftCoupon=giftCouponRepository.findByIdEqualsAndEndTimeGreaterThanEqual(couponId,new Date());
            eachGiftCouponRecord.setGiftCoupon(giftCoupon);
        }
        model.addObject("giftRecord",giftRecordList);
        if(!StringUtils.equals(chooseNumber,"0")){
            //选择号码后
            logger.info("[numberRegex]pram are:"+util.getBusinessKey()+","+chooseNumber);
            String number=numberInterface.searchNumber(util.getBusinessKey(),chooseNumber);
            Map<String,String> mapNumber=JsonUtils.handlerOriginalNumberJson(number);
            model.addObject("mapNumber",mapNumber);
            logger.info("[numberRegex]mapNumber is:"+mapNumber);
        }else{
            //未选择号码
            Map<String,String> mapNumber=new HashMap<>();
            mapNumber.put("number","");
            mapNumber.put("numberPrice","0");
            model.addObject("mapNumber",mapNumber);
        }
        model.addObject("openid",openid);
        BusinessCustomer user= businessCusRepository.findByOpenidEquals(openid);
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
        GiftCard cardId=giftCardRepository.save(giftCard);
        //支付成功后将套餐卡信息保存数据库中
        GiftCardRecord giftCardRecord=new GiftCardRecord();
        giftCardRecord.setSenderOpenid(SenderOpenid);
        giftCardRecord.setMessage(message);
        giftCardRecord.setGetStatus(0);
        giftCardRecord.setCardId(cardId.getId());
        giftCardRecord.setBuyTime(new Date());
        //加载分享页面所需要的数据。
        GiftCard card=giftCardRepository.findByIdEquals(cardId.getId());
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

    @RequestMapping("/myNumberQrcode")
    public ModelAndView  myNumberQRCode( @RequestParam(value = "openid") String openid,
                                         @RequestParam(value = "number95") String number95){
        ModelAndView  model=new ModelAndView();
        BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals(openid);
        //根据openid和number查询二维码，没有则创建新的二维码。
        GiftNumberQRcode qrcode=giftNumberQRcodeRepository.findByOpenidEqualsAndNumber95Equals(openid,number95);
        if(qrcode==null){
            //创建二维码
            GiftNumberQRcode giftNumberQRcode=new GiftNumberQRcode();
            giftNumberQRcode.setNumber95(number95);
            giftNumberQRcode.setOpenid(openid);
            GiftNumberQRcode giftNumberQRcodeId=giftNumberQRcodeRepository.saveAndFlush(giftNumberQRcode) ;
            String qrcodeHref = qrcodeService.generatorQrcode(giftNumberQRcodeId.getId(),"call");
            model.addObject("href",qrcodeHref);
        }else{
            //读取二维码
            String qrcodeHref =qrcode.getQrcodeUrl();
            model.addObject("href",qrcodeHref);
        }
        model.addObject("number",number95);
        model.addObject("userInfo",userInfo);
        model.setViewName("shareGift/card");
        return model;
    }
    @RequestMapping("/showNumber95")
    public ModelAndView  showNumber95(@RequestParam(value = "qrcode") String qrcode) {
        ModelAndView  model=new ModelAndView();
        //根据qrcodeId查找相应的信息(GiftNumberQRcode表)。
        GiftNumberQRcode qrcodeMsg=giftNumberQRcodeRepository.findByQrcodeEquals(qrcode);
        //根据讯息查找海牛助手是否过期，如果过期则跳转到无法拨打页面。没过期跳转到拨打海牛助手页面。
        String number=qrcodeMsg.getNumber95();
        BusinessNumberRecord  record=recordRepository.findBySmbmsEqualsAndBusinessIdEquals(number,util.getBusinessKey());
        if(record.getValidTime().getTime()<System.currentTimeMillis()){
            model.setViewName("shareGift/erweima");
        }else{
            model.setViewName("shareGift/95number");
        }
        BusinessCustomer userInfo= businessCusRepository.findByOpenidEquals(qrcodeMsg.getOpenid());
        String nickName=userInfo.getNickname();
        model.addObject("number",number);
        model.addObject("nickName",nickName);
        return model;
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
        }else if(StringUtils.equals(type,"call")){
            //识别二维码显示海牛助手页面
            return  "redirect:"+"/showNumber95?qrcode="+realId;
        } else if(StringUtils.equals(type,"coupon")){
            //优惠券
            GiftCouponQrcode giftCouponRecord = giftCouponQrcodeRepository.findByQrcodeEquals(realId);
            Long couponId=giftCouponRecord.getCouponId();
            String senderId=giftCouponRecord.getCreatorOpenid();
            return  "redirect:"+"/oauth/gift/giftReturn/"+couponId+"/"+senderId;
        }else return null;
    }
    @RequestMapping("/wechatsite/helpcenter/scene")
    public  String  scene(){

        return  "scene/scene";
    }
    @RequestMapping("/wechatsite/helpcenter/help")
    public  String  guide(){

        return  "guide/guide";
    }
    @RequestMapping("/zhi")
    public  String  zhi(){

        return  "watch/zhi";
    }
}
