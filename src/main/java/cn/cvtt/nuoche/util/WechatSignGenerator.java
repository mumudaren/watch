package cn.cvtt.nuoche.util;

import cn.cvtt.nuoche.common.Constant;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.DigestException;
import java.security.MessageDigest;
import java.util.*;
import java.util.Map.Entry;

import static cn.cvtt.nuoche.util.SignUtil.SHA1;

/**
 * weixin pay util
 * final
 * @author  mingxing
 * @timer   20180326
 *
 * */
public class WechatSignGenerator {


	 public  static   String    sign(String  openId,String totalFee,String remoteAddr,String business,String noId) throws Exception{
		 Map<String, Object> map=new  TreeMap<String, Object>(new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		 });
		 map.put("appid", Constant.APP_ID);
		 map.put("attach", Constant.ATTACH+"-"+business);
		 map.put("body", Constant.SENDBODY);
		 map.put("mch_id", Constant.MCH_ID);
		 map.put("nonce_str", Constant.NONCE_STR);
		 map.put("notify_url", Constant.NOTIFY_URL);
		 map.put("openid", openId);
		 map.put("out_trade_no",noId);
		 map.put("spbill_create_ip", remoteAddr);
		 map.put("total_fee",totalFee);
		 map.put("trade_type",Constant.TRADE_TYPE);
		 StringBuffer  sb=new StringBuffer();
		 for(Entry<String, Object> ty:map.entrySet()){
			 sb.append(ty.getKey()).append("=").append(ty.getValue()).append("&");
		 }
		 sb.append("key").append("=").append(Constant.KEY);
		 map.put("sign",MD5(sb.toString()).toUpperCase());
		 System.out.println("sb"+sb.toString());
		 System.out.println(MD5(sb.toString()).toUpperCase());
		 String  url=Constant.ORDERURL;
		 return  doXmlPost(url,map);
	 }
	
	 public  static   Map<String,Object>    JsSign(Map<String,Object> arg) throws Exception{
		 Map<String, Object> map=new  TreeMap<String, Object>(new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		 });
		 map.put("appId", Constant.APP_ID);
		 map.put("timeStamp", System.currentTimeMillis()+"");
		 map.put("nonceStr", Constant.NONCE_STR);
		 map.put("package", "prepay_id="+arg.get("prepay_id"));
		 map.put("signType", "MD5");
		 StringBuffer  sb=new StringBuffer();
		 for(Entry<String, Object> ty:map.entrySet()){
			 sb.append(ty.getKey()).append("=").append(ty.getValue()).append("&");
		 }
		 sb.append("key").append("=").append(Constant.KEY);
		 map.put("paySign",MD5(sb.toString()).toUpperCase());
		 System.out.println("sb:"+sb.toString());
		 return  map;
	 }
	public  static   String  jsapiSign(String url) throws Exception{
		String timeStamp=System.currentTimeMillis()+"";
		Map<String,Object> maps = new HashMap<String,Object>();
		maps.put("jsapi_ticket", WxUtils.getJSAPIToken().getJSAPIToken());
		maps.put("noncestr",Constant.NONCE_STR);
		maps.put("timestamp",timeStamp.substring(0,timeStamp.length()-3));
		maps.put("url", url);
		String str=null;
		try {
			str = SHA1(maps);
			System.out.println("timeStamp:"+timeStamp+"content:"+str);
		} catch (DigestException e) {
        // TODO Auto-generated catch block
			e.printStackTrace();
		}
//		MessageDigest md = null;
//		String sha = null;
//		md = MessageDigest.getInstance("SHA-1");
//		// 3. 将拼接后的字串进行SHA1加密
//		byte[] digest = md.digest(content.toString().getBytes());
//		sha = SignUtil.byteToStr(digest);
		return  str+"_"+Constant.NONCE_STR+"_"+timeStamp.substring(0,timeStamp.length()-3);
	}
	 
	 public static String doXmlPost(String httpUrl, Map<String, Object> params) throws IOException
		{
			StringBuilder result = new StringBuilder(256);
			HttpClient client = new HttpClient();
			PostMethod method = new PostMethod(httpUrl);
			try {
				 
				client.getParams().setContentCharset("UTF-8");
			    method.setRequestHeader("Content-Type","text/xml");
			    method.setRequestHeader("charset","utf-8");  
			    method.setRequestBody(mapConvertXml(params));
				int statusCode = client.executeMethod(method);
				if(HttpStatus.SC_OK == statusCode)
				{
					client.getParams().setContentCharset("UTF-8");
					result.append(method.getResponseBodyAsString());
				}
			}
			catch(Exception exp) {
				throw new IOException(exp.getMessage());
			}
			finally {
				method.releaseConnection();
				client.getHttpConnectionManager().closeIdleConnections(0);
			}
			return result.toString();
		}
	 
	 public static  String  mapConvertXml(Map<String, Object> params){
		 StringBuffer  sb=new StringBuffer();
		 sb.append("<xml>").append("\n");
		 for(Entry<String, Object> ty:params.entrySet()){
			 String key=ty.getKey();
			 String value=ty.getValue().toString();
			 sb.append("<").append(key).append(">").append(value).append("</").append(key).append(">").append("/n");
		 }
		 sb.append("</xml>");
		 return sb.toString();
	 }


	public  static  String   MD5(String  resource) throws Exception{
		MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(resource.getBytes("utf-8"));
        return toHex(bytes);
		
	}
	
	private static String toHex(byte[] bytes) {

	    final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
	    StringBuilder ret = new StringBuilder(bytes.length * 2);
	    for (int i=0; i<bytes.length; i++) {
	        ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
	        ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
	    }
	    return ret.toString();
	}

	/** ip 地址转换 */
	/**
	 * @Description: 获取客户端IP地址
	 */
	public  static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if(ip.equals("127.0.0.1")){
				//根据网卡取本机配置的IP
				InetAddress inet=null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (Exception e) {
					e.printStackTrace();
				}
				ip= inet.getHostAddress();
			}
		}
		// 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if(ip != null && ip.length() > 15){
			if(ip.indexOf(",")>0){
				ip = ip.substring(0,ip.indexOf(","));
			}
		}
		return ip;
	}

	/**
	 *  根据订单返回的结果 生成支付的对象信息
	 *  @auther mingxing
	 *  @timer 2018-03-26
	 *  @return     "appId":"wxd09a2af935c65854",     //公众号名称，由商户传入
	 *				"timeStamp":"1521790208318",         //时间戳，自1970年以来的秒数
	 *				"nonceStr":"tianzhoutongxingCommunication", //随机串
	 *				"package":"prepay_id=wx20180323163132eb7c01e84c0123327932",
	 *				"signType":"MD5",         //微信签名方式：
	 *				"paySign":"42BB84B648C21BCD2C70FEB4D5F976FA" //微信签名
	 * */
    public static JSONObject getPayRequest(String xml){

		SAXReader   sax=new SAXReader();
		JSONObject obj=new JSONObject();
		try {
		    Document  doc=sax.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		    Element  root=doc.getRootElement();
		    List<Element>  list=root.elements();
		    Map<String,Object> map=new HashMap<>();
		    String result_code=list.get(0).getText();
			System.out.println("result_code:"+result_code);
            if(!"SUCCESS".equals(result_code)){
				obj.put("status",false);
				obj.put("msg","Order invalid!");
			    return obj;
            }
            for(Element  el:list){
            	String  key=el.getName();
				String  value=el.getTextTrim();
                map.put(key,value);
			}
			Map<String,Object>  jsobj=JsSign(map);
            for(Entry<String,Object> ty:jsobj.entrySet()){
                 obj.put(ty.getKey(),ty.getValue());
			}
			obj.put("status",true);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  obj;
	}



}
