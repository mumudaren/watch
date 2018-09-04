package cn.cvtt.nuoche.web;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BaseErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";
    //异常
    @RequestMapping("/error")
    public  String  handerError(){
         return "error";
    }

    @Override
    public String getErrorPath() {

        return ERROR_PATH;
    }
}
