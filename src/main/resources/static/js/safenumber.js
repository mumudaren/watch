
$(function(){

			//我的海牛助手页面选项卡
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
        //有效期小时数
        var hours=$(this).find("p:eq(5)").text();
        console.log("days>>>>>"+days);
        $(".f_fastpay a").find("em").text(oListpay);
        $("#Area").text(oListpay);
        $("#validDays").text(days);
        $("#validHours").text(hours);
        $("#productId").text($(this).find("p:eq(4)").text());
        // $(this).addClass('f_tiyan_active');
        // $(".f_dayscontent ul li:eq(1)").removeClass("f_discount_active");
        // $(".f_dayscontent ul li:eq(2)").removeClass("f_sales_active");
        var classNew2=$(this).attr("class").split(' ')[0];
        $(this).addClass(classNew2+'_active').addClass(classNew2);
        $(".f_dayscontent ul li:eq(1)").removeClass("f_tiyan_active").removeClass("f_discount_active").removeClass("f_sales_active");
        $(".f_dayscontent ul li:eq(2)").removeClass("f_tiyan_active").removeClass("f_discount_active").removeClass("f_sales_active");

        $(this).find("p:eq(0)").addClass("z_requiredays_active");
        $(this).find("p:eq(1)").addClass("z_requiredays_active");
        $(this).find("p:eq(2)").addClass("z_limits3_active");
    });
    $(".f_dayscontent ul li:eq(1)").click(function () {
        var oListpay = $(this).find("p:eq(1)").text();
        var days=$(this).find("p:eq(2)").text();
        //有效期小时数
        var hours=$(this).find("p:eq(5)").text();
        console.log("hours>>>>>"+days);
        $(".f_fastpay a").find("em").text(oListpay);
        $("#Area").text(oListpay);
        $("#validDays").text(days);
        $("#validHours").text(hours);
        $("#productId").text($(this).find("p:eq(4)").text());
        // $(this).addClass('f_discount_active');
        // $(".f_dayscontent ul li:eq(0)").removeClass("f_tiyan_active");
        // $(".f_dayscontent ul li:eq(2)").removeClass("f_sales_active");
        var classNew2=$(this).attr("class").split(' ')[0];
        $(this).addClass(classNew2+'_active').addClass(classNew2);
        $(".f_dayscontent ul li:eq(0)").removeClass("f_tiyan_active").removeClass("f_discount_active").removeClass("f_sales_active");
        $(".f_dayscontent ul li:eq(2)").removeClass("f_tiyan_active").removeClass("f_discount_active").removeClass("f_sales_active");
        $(this).find("p:eq(0)").addClass("z_requiredays_active");
        $(this).find("p:eq(1)").addClass("z_requiredays_active");
        $(this).find("p:eq(2)").addClass("z_limits3_active");

    });
    $(".f_dayscontent ul li:eq(2)").click(function () {
        var oListpay = $(this).find("p:eq(1)").text();
        var days=$(this).find("p:eq(2)").text();
        //有效期小时数
        var hours=$(this).find("p:eq(5)").text();
        console.log("hours>>>>>"+hours);
        $(".f_fastpay a").find("em").text(oListpay);
        $("#Area").text(oListpay);
        $("#validDays").text(days);
        $("#validHours").text(hours);
        $("#productId").text($(this).find("p:eq(4)").text());
        // $(this).addClass('f_sales_active');
        // $(".f_dayscontent ul li:eq(0)").removeClass("f_tiyan_active");
        // $(".f_dayscontent ul li:eq(1)").removeClass("f_discount_active");
        var classNew2=$(this).attr("class").split(' ')[0];
        $(this).addClass(classNew2+'_active').addClass(classNew2);
        $(".f_dayscontent ul li:eq(0)").removeClass("f_tiyan_active").removeClass("f_discount_active").removeClass("f_sales_active");
        $(".f_dayscontent ul li:eq(1)").removeClass("f_tiyan_active").removeClass("f_discount_active").removeClass("f_sales_active");
        $(this).find("p:eq(0)").addClass("z_requiredays_active");
        $(this).find("p:eq(1)").addClass("z_requiredays_active");
        $(this).find("p:eq(2)").addClass("z_limits3_active");
    });


	//点击号码延期选择天数
	$(".number_delay").click(function(){
		//触发套餐的点击事件。
        //alert($(this).parent(".z_number_dalay").parent(".f_delay_detail").text());
        //alert($(this).parent(".z_number_dalay").parent(".f_delay_detail").children(".numIndex").text());
        //通过颜色判断是否失效
        //console.log("isValid:"+$(this).parent(".z_number_dalay").parent(".f_delay_detail").parent(".f_mynumber_list").find(".z_imgkey").hasClass("z_imgkey_gray"));
		isValidValue=$(this).parent(".z_number_dalay").parent(".f_delay_detail").parent(".f_mynumber_list").find(".z_imgkey").hasClass("z_imgkey_gray");
		console.log("isValid:"+isValidValue);
        var orderIndex=$(this).parent(".z_number_dalay").parent(".f_delay_detail").children(".numIndex").text();
        console.log("orderIndex:"+orderIndex);
		//获取套餐的电话号码等。
        var resourceObj=$(this).parent().parent().prev();
        console.log("resourceObj   is:"+resourceObj.attr("class"));
        var phone=$(resourceObj).find(".z_mynumber77889").text();
        sendPhone=$(resourceObj).find(".z_mynumber_telphone").text();
        console.log("95phone   is:"+phone+"send phone is :"+sendPhone);
        $(".sessionPhone").text(phone);
		//获取套餐
        $.ajax({
			url:"/findRegexOrder"
            ,data:{
                orderIndex: orderIndex
            }
            ,async:false
            , dataType: 'json'
            , success: function (data) {
                var htmlStr = "";
                var  htmlStrStrart='<ul>';
                var  htmlStrEnd='</ul>';
                console.log("ajax success and return data is:"+JSON.stringify(data.products));
                console.log("data length is:"+JSON.stringify(data.products.length));
                console.log("data.products.productName is:"+JSON.stringify(data.products[0].productName));
                $.each(data.products, function (i, value) {
                    if(value.productLimit="0") {
                        htmlStr +=
                            '<li  class="' + value.productType + '">' +
                            '<p  class="z_requiredays">' + "有效期" + value.validDay + "天" + '</p>' +
                            '<p >' + "￥" + value.productPrice / (100 * 1.0) + '</p>' +
                            '<p style="display: none;">' + value.validDay + '</p>' +
                            '<p class="z_limits3 z_limits3_active">' +
                            '<del >' + "￥" + value.initPrice / (100 * 1.0) + '</del>' +
                            '<span>' + "不限次数" + '</span>' +
                            '</p>' +
                            '<p class="productId"  style="display: none">' + value.id + '</p>' +
                            '</li>';
                    }else{
                        htmlStr +=
                            '<li  class="' + value.productType + '">' +
                            '<p  class="z_requiredays">' + "有效期" + value.validDay + "天" + '</p>' +
                            '<p >' + "￥" + value.productPrice / (100 * 1.0) + '</p>' +
                            '<p style="display: none;">' + value.validDay + '</p>' +
                            '<p class="z_limits3 z_limits3_active">' +
                            '<del >' + "￥" + value.initPrice / (100 * 1.0) + '</del>' +
                            '<span>' + "限购" +value.productLimit+"次"+ '</span>' +
                            '</p>' +
                            '<p class="productId"  style="display: none">' + value.id + '</p>' +
                            '</li>';
                    }
                });
                $(".f_selectdays").children(".f_select_content").children(".f_dayscontent").html(htmlStrStrart+htmlStr+htmlStrEnd);
                //默认选择第一个套餐
                $(".f_selectdays").show();
                initialPay();
                changePay();
                $(".f_dayscontent ul li:eq(0)").trigger('click');
            }
        });
		//获取套餐结束
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
