package cn.cvtt.nuoche.util;

import cn.cvtt.nuoche.entity.business.wx_product;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * json转换工具
 * @author Yampery
 * @date 2017-6-6 15:24:16
 */
public class JsonUtils {

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串。
     * <p>Title: pojoToJson</p>
     * <p>Description: </p>
     * @param data
     * @return
     */
    public static String objectToJson(Object data) {
    	try {
			String string = MAPPER.writeValueAsString(data);
			return string;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * 将json结果集转化为对象
     * 
     * @param jsonData json数据
     * @param beanType 对象中的object类型
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            T t = MAPPER.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T>List<T> jsonToList(String jsonData, Class<T> beanType) {
    	JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
    	try {
    		List<T> list = MAPPER.readValue(jsonData, javaType);
    		return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }

    public static void  handlerArgs(List<wx_product> ls, JSONObject obj){
        JSONArray arr=JSONArray.parseArray(obj.getString("data"));
        for(Object ob:arr){
            wx_product  wx=new wx_product();
            JSONObject o=JSONObject.parseObject(ob.toString());
            wx.setProductName(o.getString("name"));
            wx.setInitPrice(o.getIntValue("oldPrice"));
            wx.setProductPrice(o.getIntValue("price"));
            wx.setProductType(o.getString("promotion"));
            wx.setValidDay(o.getIntValue("days"));
            wx.setProductInfo(o.getString("productInfo"));
            ls.add(wx);
        }
    }

    /** 解析服务数据 返回map组*/
    public  static Map<String,String> handlerJson(String json){
        JSONObject  obj=JSONObject.parseObject(json);
        //TODO  绑定手机号发送验证码 20180706
        if(obj.getIntValue("code")!=200){
             return  new HashMap<>();
        }
        Map<String,String>  args=new HashMap<>();
        JSONArray  arr=JSONArray.parseArray(obj.getString("data"));
        for(Object o:arr){
             JSONObject  param=JSONObject.parseObject(o.toString());
             args.put(param.get("keyName").toString(),param.get("keyValue").toString());
        }
        return args;
    }

    /** 解析Regex正则表达式JSON**/
    public  static  List<Map<String,String>>  handlerNormalJson(String  json,String key,String valueKey){
        JSONObject  obj=JSONObject.parseObject(json);
        if(obj.getIntValue("code")!=200){
            return  new ArrayList<>();
        }

        JSONArray  arr=JSONArray.parseArray(obj.getString("data"));
        List<Map<String,String>>  list=new ArrayList<>();
        for(Object o:arr){
            Map<String,String> args=new HashMap<>();
            JSONObject  param=JSONObject.parseObject(o.toString());
            args.put("key",param.get(key).toString());
            args.put("value",param.get(valueKey).toString());
            list.add(args);
        }
        return  list;
    }

    /**  解析 RegexProduct JSON*/
    public  static  List<wx_product>  handlerRegexJson(String json){
        JSONObject  obj=JSONObject.parseObject(json);
        int code= obj.getIntValue("code");
        List<wx_product>  ls=new ArrayList<>();
        if(code==200){
           JSONArray  arr=JSONArray.parseArray(obj.getString("data"));
           for(Object  o:arr){
                JSONObject entity=JSONObject.parseObject(o.toString());
                wx_product  product=new wx_product();
                product.setId(entity.getLong("id"));
                product.setProductBusiness(entity.getString("business"));
                product.setProductInfo(entity.getString("productInfo"));
                product.setValidDay(entity.getIntValue("days"));
                product.setProductType(entity.getString("promotion"));
                product.setProductPrice(entity.getIntValue("price"));
                product.setInitPrice(entity.getIntValue("oldPrice"));
                product.setProductName(entity.getString("name"));
                product.setCreateTime(entity.getDate("createTime"));
                product.setLeaveMessage(entity.getIntValue("leaveMessage"));
                product.setMessage(entity.getIntValue("message"));
                product.setProductLimit(entity.getIntValue("productLimit"));
                ls.add(product);
           }
        }
        return ls;
    }

    public  static  List<Map<String,String>>   handlerNumberJson(String json){
        JSONObject  obj=JSONObject.parseObject(json);
        int  code=obj.getIntValue("code");
        List<Map<String,String>>  ls=new ArrayList<>();
        if(code==200){
                JSONArray  arr=JSONArray.parseArray(obj.getString("data"));
                for(Object ob:arr){
                    JSONObject  entity=JSONObject.parseObject(ob.toString());
                    Map<String,String>  map=new HashMap<>();
                    map.put("number",entity.getString("number"));
                    map.put("numberPrice",entity.getString("numberPrice"));
                    ls.add(map);
                }
        }
        return  ls;
    }

    public  static  Map<String,String>  handlerOriginalNumberJson(String json){
        JSONObject  obj=JSONObject.parseObject(json);
        int  code=obj.getIntValue("code");
        Map<String,String>  map=new HashMap<>();
        if(code==200){
            JSONObject  entity=JSONObject.parseObject(obj.getString("data"));
            map.put("number",entity.getString("number"));
            map.put("numberPrice",entity.getString("numberPrice"));
            map.put("regexId",entity.getString("regexId"));
        }
        return  map;
    }

    public  static String  handlerNumberReturnRegexJson(String json){
        JSONObject  obj=JSONObject.parseObject(json);
        int  code=obj.getIntValue("code");
        if(code==200){
            String  childData=obj.getString("data");
            JSONObject  entity=JSONObject.parseObject(childData);
            return  entity.getString("regexId");
        }
        return "";
    }

}