package cn.cvtt.nuoche.facade;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="webService")
public interface IProductInterface {


    @RequestMapping("/business/findProduct")
    String    findProduct(@RequestParam("business") String business);

    @RequestMapping("/business/findRegexProduct")
    String    findRegexProduct(@RequestParam("business") String business,@RequestParam("regex") String regex);

    @RequestMapping("/business/findSpeNumber")
    String  findSpeNumber(@RequestParam("business") String  business,@RequestParam("regex") String  regex,@RequestParam("number") String number);

    @RequestMapping("/business/deleteSpeNumber")
    String  deleteSpeNumber(@RequestParam("business") String business,@RequestParam("smbms") String number);

    @RequestMapping("/business/findProductById")
    String  findProductById(@RequestParam("productId") Integer  productId);

}
