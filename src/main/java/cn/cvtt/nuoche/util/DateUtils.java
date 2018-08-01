package cn.cvtt.nuoche.util;

import cn.cvtt.nuoche.common.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private   static  ThreadLocal<SimpleDateFormat> local=new ThreadLocal<>();


    public  static  String format(Date date){
        local.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return local.get().format(date);
    }

    public  static  String  formatDuration(int  duration){
        int minute=duration/60;
        int second=duration%60;
        return  minute>9?minute+"":"0"+minute+":"+(second>9?second+"":"0"+second);
    }
    public  static  String formatTime(Date date){
        local.set(new SimpleDateFormat("HH:mm:ss"));
        return local.get().format(date);
    }


    public  static  String  formatString(Date date,String template){
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH)+1;
        int days=cal.get(Calendar.DAY_OF_MONTH);
        return String.format(template,year,month,days);
    }

    public static void main(String[] args) {
        System.out.println(formatString(new Date(),Constant.DATETEMPLATE));


    }

    public  static  String format(Date date,String patten){
        local.set(new SimpleDateFormat(patten));
        return local.get().format(date);
    }



    public  static  Date   parse(String date) throws ParseException {
        local.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return  local.get().parse(date);
    }

    public   static  String   orderFormat(Date date){
        local.set(new SimpleDateFormat("yyyyMMddHHmmss"));
        return local.get().format(date);
    }


     /**
      * @auther  mingxing
      * @info    获取当前增加有效期的时间
      *
      * */
    public  static  String  validDate(Integer day){
        Calendar  calendar=Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,day);
        return format(calendar.getTime());
    }


    /**
     *  @author   mingxing
     *  @info  对当前时间进行增加 返回增加后的时间
     *  @param  day date
     *  @return time
     * */
    public  static   Date  addDay(Date  date,String day){
        if(date==null){ date=new Date(); }
        Calendar  calendar=Calendar.getInstance();
        calendar.setTime(date);
        try {
            calendar.add(Calendar.DAY_OF_MONTH,Integer.parseInt(day));
        }catch (Exception e){
            calendar.add(Calendar.DAY_OF_MONTH,0);
        }
        return  calendar.getTime();
    }

}
