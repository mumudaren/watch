
    //点击按钮显示浮层，gift_give,card_give
    $(".tb_overflay").click(function(){
        $(".f_sharewrap").hide();
    });
    $(".f_sharewrap").click(function(){
        $(this).hide();
    });
    $(".z_send_wxfriend").click(function(){
        $(".f_sharewrap").show();
    });
    $(".z_dothing_share").click(function(){
        window.location.href="/qrcode?cardRecordId="+cardRecordId;
    });
