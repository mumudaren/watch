$(function(){

			//我的安全号页面选项卡
			$(".f_mynumber_nav li:eq(0)").click(function(){
				$(this).addClass("active");
				$(this).siblings().removeClass("active");
				$(".f_mynumber_content li:eq(0)").show();
				$(".f_mynumber_content li:eq(1)").hide();
			})
			$(".f_mynumber_nav li:eq(1)").click(function(){
				$(this).addClass("active");
				$(this).siblings().removeClass("active");
				$(".f_mynumber_content li:eq(1)").show();
				$(".f_mynumber_content li:eq(0)").hide();
			})

    $(".f_dayscontent ul li:eq(0)").click(function () {
        var oListpay = $(this).find("p:eq(1)").text();
        var days=$(this).find("p:eq(2)").text();
        console.log("days>>>>>"+days);
        $(".f_fastpay a").find("em").text(oListpay);
        $("#Area").text(oListpay);
        $("#validDays").text(days);
        $("#productId").text($(this).find("p:eq(4)").text());
		$(this).addClass('f_tiyan_active');
        $(".f_dayscontent ul li:eq(1)").removeClass("f_discount_active");
        $(".f_dayscontent ul li:eq(2)").removeClass("f_sales_active");
        $(this).find("p:eq(0)").addClass("z_requiredays_active");
        $(this).find("p:eq(1)").addClass("z_requiredays_active");
        $(this).find("p:eq(2)").addClass("z_limits3_active");
    });
    $(".f_dayscontent ul li:eq(1)").click(function () {
        var oListpay = $(this).find("p:eq(1)").text();
        var days=$(this).find("p:eq(2)").text();
        console.log("days>>>>>"+days);
        $(".f_fastpay a").find("em").text(oListpay);
        $("#Area").text(oListpay);
        $("#validDays").text(days);
        $("#productId").text($(this).find("p:eq(4)").text());
        $(this).addClass('f_discount_active');
        $(".f_dayscontent ul li:eq(0)").removeClass("f_tiyan_active");
        $(".f_dayscontent ul li:eq(2)").removeClass("f_sales_active");
        $(this).find("p:eq(0)").addClass("z_requiredays_active");
        $(this).find("p:eq(1)").addClass("z_requiredays_active");
        $(this).find("p:eq(2)").addClass("z_limits3_active");

    });
    $(".f_dayscontent ul li:eq(2)").click(function () {
        var oListpay = $(this).find("p:eq(1)").text();
        var days=$(this).find("p:eq(2)").text();
        console.log("days>>>>>"+days);
        $(".f_fastpay a").find("em").text(oListpay);
        $("#Area").text(oListpay);
        $("#validDays").text(days);
        $("#productId").text($(this).find("p:eq(4)").text());
        $(this).addClass('f_sales_active');
        $(".f_dayscontent ul li:eq(0)").removeClass("f_tiyan_active");
        $(".f_dayscontent ul li:eq(1)").removeClass("f_discount_active");
        $(this).find("p:eq(0)").addClass("z_requiredays_active");
        $(this).find("p:eq(1)").addClass("z_requiredays_active");
        $(this).find("p:eq(2)").addClass("z_limits3_active");
    });


	//点击号码延期选择天数
	$(".number_delay").click(function(){
		$(".f_dayscontent ul li:eq(0)").trigger('click');
        //alert($(".numType").text());
		$(".f_selectdays").show();
		var resourceObj=$(this).parent().parent().prev();
        var phone=$(resourceObj).find(".z_mynumber95013").text();
		$(".sessionPhone").text(phone);
	});

	$(".z_closeimg").click(function(){
		$(".f_selectdays").hide()
	});

	$(".tb_overflay").click(function(e){
		e.stopPropagation();
		$(".f_selectdays").hide();
		$(".f_numberdetail").hide();
	});

	//反馈意见页面选项卡
	$(".f_suggest_content ul li").hide();
	$(".f_suggest_content ul li:first").show();
	var oIndex;	
	$(".f_suggest_taocan ul li").click(function(){
		oIndex = $(this).index();
		$(this).addClass("f_suggest_active");
		$(this).siblings().removeClass("f_suggest_active");
		$(".f_suggest_content ul li").eq(oIndex).show().siblings().hide();
	});

	//点击分享弹窗
	$(".f_bindsuc_share").click(function(){
		$(".f_sharesuccess").show();
	});
	$(".f_bindsuc_sharebj").click(function(){
		$(".f_sharesuccess").show();
	});
	$(".f_shareclose").click(function(){
		$(".f_sharesuccess").hide();		
	});
	$(".tb_overflay").click(function(){
		$(".f_sharesuccess").hide();		
	});

})
