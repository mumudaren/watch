package cn.cvtt.nuoche.web;

import cn.cvtt.nuoche.common.result.Result;
import cn.cvtt.nuoche.entity.business.CallReleaseVo;
import cn.cvtt.nuoche.entity.game.Cat;
import cn.cvtt.nuoche.reponsitory.ICatRepository;
import cn.cvtt.nuoche.util.ConfigUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MiaoController extends  BaseController{
    @Autowired
    ICatRepository catRepository;
    private static final Logger logger = LoggerFactory.getLogger(MiaoController.class);

    @RequestMapping("/miao")
    @ResponseBody
    public  void miao(){
        System.out.println("查询基因库页面");
        //1、分别查询7个基因类别下的所有基因,组成list，返给前台的数据。
    }
    @RequestMapping("/miaoSave")
    @ResponseBody
    public  Boolean miaoSave(@RequestParam Cat cat){
        System.out.println("录入猫");
        //2、将猫的基因id以及猫的信息录入到数据库中
        Cat result=catRepository.saveAndFlush(cat);
        return result.getId() != 0;
    }

    public static void main(String[] args) {
        CallReleaseVo  vo=new CallReleaseVo();
        vo.setCall_id("123");
    }




}
