package cn.cvtt.nuoche.facade;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="webService")
public interface ISystemParamInterface {

    @RequestMapping("/business/getSystemConfigByBusiness")
    String getSystemApplication(@RequestParam("business") String business);

    @RequestMapping("/business/getSystemConfigByArgs")
    String  getSystemConfigByArgs(@RequestParam(value = "group") Integer group, @RequestParam(value = "business") String business);
}
