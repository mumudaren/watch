(function(){
    init();
    console.log("init app.js ...");
})();

function  init(){
    $(".f_zhizuntab ").find("li").click(function(){
        console.log("....");
        $(".f_zhizuntab ul li").removeClass("z_actived");
        $(this).addClass("z_actived");



    });



}
