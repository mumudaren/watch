package cn.cvtt.nuoche.facade;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="webService")
public interface IBusinessCallRecordInterface {

    @RequestMapping("/business/saveCallRecord")
    public  String   saveCallRecord(@RequestParam("json") String json,@RequestParam("business") String  business);

    @RequestMapping("/business/findCallRecordLately")
    public  String   findCallRecordLately(@RequestParam("business") String business,@RequestParam("nox")String x,@RequestParam("isAll")boolean  isAll);

    @RequestMapping("/business/findVoiceRecordLately")
    public  String   findVoiceRecordLately(@RequestParam("business")String  business,@RequestParam("nox")String x,@RequestParam("isAll")boolean isAll);

    @RequestMapping("/business/findHeard")
    public  String   findHeard(@RequestParam("business")String business,@RequestParam("x")String x);

    @RequestMapping("/business/CountBindNumber")
    public  String   CountBindNumber(@RequestParam("business") String business,@RequestParam("phone") String phone);

}
