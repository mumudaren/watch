package cn.cvtt.nuoche.common;

/**
 * @decription Constant
 * <p>常量、枚举</p>
 * @author Yampery
 * @date 2018/3/6 15:38
 */
public class Constant {
    public static final String TOKEN = "95013cvtt";
    //yechangmin@cvtt.cn公众号
//    public static final String APP_ID = "wxda98f445dcbd711a";
//    public static final String APP_SECRET = "62f4c67b484b422187c53238dd3d3dd2";
//cuiguoying@cvtt.cn公众号
//    public static final String APP_ID = "wx9151b43f4906eefc";
//    public static final String APP_SECRET = "a03894d8afb3d19aaef944b436828609";
//    public  static  final String  userInfoUrl="https://api.weixin.qq.com/cgi-bin/user/info";
    public static final String APP_ID = "wx3baaf33eec3b639e";
    public static final String APP_SECRET = "c620ab930c52a142ec6d9e33efe855fe";
    public  static  final String  userInfoUrl="https://api.weixin.qq.com/cgi-bin/user/info";
    /**
     *
     *
     * 手机号正则表达式
     */
    public static final String REGIX_PHONE = "^1[356784]\\d{9}$";
    public static final String DATETEMPLATE="%d年%d月%d日";




    /**  wexin pay  constant  start*/
    public  static  final String  MCH_ID="1398897702"; //商户号
    public  static  final String  NOTIFY_URL="http://i.95013.com/paySuccess";
    public  static  final String  TRADE_TYPE="JSAPI";
    public  static  final String  ATTACH="天舟通信安全号支付";
    public  static  final String  SENDBODY="天舟-安全号";
    public  static  final String  NONCE_STR="tianzhoutongxingCommunication";
    public  static  final String  KEY="sjzljr7571jdjjjxjjgjg14881186111";
    public  static  final String  ORDERURL="https://api.mch.weixin.qq.com/pay/unifiedorder";
    /**  end  */


    /***================================================*/






}
