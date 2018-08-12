/**
 * 微信支付工具
 *  1.包含下单和支付订单
 *  @function
 * */
var  payUtil={
     payOrder:function(pay){
         console.log("payUtil");
        var  result={};
        $.ajax({
            url:"/createOrder",
            type:"POST",
            dataType:"JSON",
            data:pay,
            async:false,
        }).done(function(json){
             result=json;
        });
        return result;
    }

}
/**
 * @param
 *    openid    转账用户
 *    totalFee  转账金额,以分单位
 * */




