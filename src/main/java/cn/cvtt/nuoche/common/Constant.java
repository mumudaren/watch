package cn.cvtt.nuoche.common;

/**
 * @decription Constant
 * <p>常量、枚举</p>
 * @author Yampery
 * @date 2018/3/6 15:38
 */
public class Constant {
    public static final String TOKEN = "nuonuotianzhou95013";
    public static final String APP_ID = "wx9151b43f4906eefc";
    public static final String APP_SECRET = "43dd356026afd3d8f9c18b8d94388ed5";

    public  static  final String  userInfoUrl="https://api.weixin.qq.com/cgi-bin/user/info";
    /**
     *
     *
     * 手机号正则表达式
     */
    public static final String REGIX_PHONE = "^1[356784]\\d{9}$";
    public static final String DATETEMPLATE="%d年%d月%d日";




    /**  wexin pay  constant  start*/
    public  static  final String  MCH_ID="1508066891"; //商户号
    public  static  final String  NOTIFY_URL="http://testcar.95013.com/paySuccess";
    public  static  final String  TRADE_TYPE="JSAPI";
    public  static  final String  ATTACH="天舟通信安全号支付";
    public  static  final String  SENDBODY="天舟-安全号";
    public  static  final String  NONCE_STR="tianzhoutongxingCommunication";
    public  static  final String  KEY="tianzhou95013cvtttianzhou95013cv";
    public  static  final String  ORDERURL="https://api.mch.weixin.qq.com/pay/unifiedorder";
    /**  end  */


    /***================================================*/






}
