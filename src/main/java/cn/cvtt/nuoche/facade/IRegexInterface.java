package cn.cvtt.nuoche.facade;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="webService")
public interface IRegexInterface {

    @RequestMapping("/regex/findByBusiness")
    public  String  findRegexByBusiness(@RequestParam("business") String business);


}
