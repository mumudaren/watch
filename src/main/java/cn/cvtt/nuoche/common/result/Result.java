package cn.cvtt.nuoche.common.result;

public class Result {

    protected   Integer  code;
    protected  String   msg;
    protected  Object   data;
    protected  Object   count;

    public  Result(){

    }

    public  Result(ResultMsg  msg){
        this.code=Integer.parseInt(msg.getCode());
        this.msg=msg.getDesc();
    }
    public  Result(ResultMsg  msg,Object obj){
        this.code=Integer.parseInt(msg.getCode());
        this.msg=msg.getDesc();
        this.data=obj;
    }
    public  Result(Integer code){
        this.code=code;
    }

    public  Result(Integer code,String msg){
       this.code=code;
       this.msg=msg;
    }

    public  Result(Integer code,String msg,Object  obj){
        this.code=code;
        this.msg=msg;
        this.data=obj;
    }

    public static  Result   ok(){
        return  new Result(ResultCode.REQUEST_SUCCESS,"请求成功");
    }

    public static  Result   ok(Object obj){
        return  new Result(ResultCode.REQUEST_SUCCESS,"请求成功",obj);
    }

    public static  Result  ok(String msg,Object obj){
        return  new Result(ResultCode.REQUEST_SUCCESS,msg,obj);
    }
    public static  Result   error(){
        return  new Result(ResultCode.APP_EXE_EXCEPTION,"请求异常");
    }
    public static  Result   error(String msg){
        return  new Result(ResultCode.APP_EXE_EXCEPTION,msg);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getCount() {
        return count;
    }

    public void setCount(Object count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
