/**
 * @author   mingxing
 * js here have common method
 * */
var util={  }

util.validPhone=function (sPhone) {
    if(/^1[3|4|5|8]\d{9}$/.test(sPhone)){
        return true;
    }
    return false;
}
/**
 * obj is text js DOM
 * context  showText
 * */
util.showText=function (context,obj){
    obj.text(context);
    obj.parent().slideDown(1500);
    window.setTimeout(function(){
        obj.parent().hide();
    },3000);
}