package cn.cvtt.nuoche.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @decription WebAppConfig
 * <p>配置Springboot图片下载服务器</p>
 * @author Yampery
 * @date 2018/3/15 15:29
 */
@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {

    // 获取配置文件中图片的路径，项目外访问图片。
    @Value("${wx.qrcode.path}")
    private String QRCODE_DOWNLOAD;
    private static final Logger logger = LoggerFactory.getLogger(WebAppConfig.class);

    // 访问图片
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(QRCODE_DOWNLOAD.equals("")
                || QRCODE_DOWNLOAD.equals("${wx.qrcode.download}")) {
            String path = WebAppConfig.class
                    .getClassLoader().getResource("").getPath();
            if (path.indexOf(".jar") > 0) {
                path = path.substring(0, path.indexOf(".jar"));
            }
            else if (path.indexOf("classes") > 0) {
                path = "file:" + path.substring(0, path.indexOf("classes"));
            }
            path = path.substring(0, path.lastIndexOf("/")) + "/qrcode/";
            QRCODE_DOWNLOAD = path;
        }
        logger.info("二维码下载 qrcodePath:{}", QRCODE_DOWNLOAD);
        registry.addResourceHandler("/qrcode/**").addResourceLocations(QRCODE_DOWNLOAD);
        super.addResourceHandlers(registry);
    }
}