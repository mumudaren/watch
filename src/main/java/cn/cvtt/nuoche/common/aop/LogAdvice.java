package cn.cvtt.nuoche.common.aop;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Aspect
public class LogAdvice {
    Logger logger= LoggerFactory.getLogger(LogAdvice.class);


    @Before("within(cn.cvtt..*) && @annotation(logManager)")
    public  void  methodBefore(JoinPoint joinpoint, LogManager  logManager){
        logger.info("Signature:"+joinpoint.getSignature().toString());
        logger.info("params:"+parseParams(joinpoint.getArgs()));
    }


    public  String  parseParams(Object [] objs){
        if(objs==null||objs.length<1){
            return "";
        }
        StringBuffer  sb=new StringBuffer();
        for(Object obj:objs){
            sb.append(ToStringBuilder.reflectionToString(obj)).append(" ");
        }
        return sb.toString();
    }
}
