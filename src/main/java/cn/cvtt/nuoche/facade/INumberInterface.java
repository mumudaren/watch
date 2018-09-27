package cn.cvtt.nuoche.facade;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="webService")
public interface INumberInterface {

    @RequestMapping("/safe/bind")
    public String bind(@RequestParam("business") String business, @RequestParam("phone") String phone);

    @RequestMapping("/safe/extend")
    public String extend(@RequestParam("business") String business, @RequestParam("phone") String phone, @RequestParam("smbms") String smbms);

    @RequestMapping("/safe/queryNumber")
    public String query(@RequestParam("business") String business, @RequestParam("phone") String phone);

    //查询未出售的号码信息
    @RequestMapping("/safe/searchNumber")
    public String searchNumber(@RequestParam("business") String business, @RequestParam("phone") String phone);

    @RequestMapping("/safe/changeRelation")
    public  String  changeRelation(@RequestParam("business") String  business,@RequestParam("uidNumber")String  uidNumber,@RequestParam("restrict") String  restrict);

    @RequestMapping("/safe/changeRelationZZ")
    public  String  changeRelationZZ(@RequestParam("business") String  business,@RequestParam("uidNumber")String  uidNumber,@RequestParam("restrict") String  restrict);
}
