package cn.cvtt.nuoche.common.result;

public enum ResultMsg {

    OPERATESUCEESS("200","操作成功"),
    EMAILSENDSUCCESS("201","邮件发送成功"),
    OPERATEXCEPTIN("500","操作失败"),
    WXUSERNOTFOUND("1013","该用户未关注微信公众号!"),
    EMAILUSED("1000","邮箱已被使用"),
    USERREGISTERED("1005","用户已被注册"),
    USERNOTFOUND("1006","用户不存在"),
    PASSWORDINVALID("1007","密码错误"),
    DATAREQUESTSUCCES("0","数据请求成功"),
    UPLOADFILEEXCEPTION("1008","上传文件异常"),
    LOADFILEEXCEPTION("1009","文件加载异常"),
    USERNOTFOUNDBYEMAIL("1010","此邮箱用户不存在,请输入你的个人邮箱!"),
    USEREXISTED("1011","该应用已存在,不能添加重复的应用!"),
    CODENOTMATCH("1012","验证码不匹配请重新输入!"),
    REQUESTPARAMEXCEPTION("2000","请求参数无效或者不存在!"),
    LOGINSUCCESS("2048","登陆成功"),
    PHONEREGISTERED("2049","该手机号已经注册,请登陆!"),
    MORETHANBINDNUMBER("2050","超过安全号码绑定次数!"),
    MORETHANBUYNUMBER("2051","超过安全号码限购次数!");






    private  String  code;
    private  String  desc;

    ResultMsg(String code,String desc){
        this.code=code;
        this.desc=desc;
    }
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


}
