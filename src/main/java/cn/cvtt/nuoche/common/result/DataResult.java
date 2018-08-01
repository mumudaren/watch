package cn.cvtt.nuoche.common.result;

public class DataResult extends  Result{

    private  long  count;
    public  DataResult(){

    }

    public  DataResult(int code,String msg,long count,Object obj){
        this.code=code;
        this.msg=msg;
        this.count=count;
        this.data=obj;
    }

    public  DataResult(ResultMsg msg,long count,Object obj){
        this.code=Integer.parseInt(msg.getCode());
        this.msg=msg.getDesc();
        this.count=count;
        this.data=obj;
    }

    public static  Result   ok(long count,Object obj){
        return  new DataResult(ResultMsg.DATAREQUESTSUCCES,count,obj);
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
