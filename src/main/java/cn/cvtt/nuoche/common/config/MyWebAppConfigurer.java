package cn.cvtt.nuoche.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyWebAppConfigurer extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/MP_verify_bASPYh8PfQoTHBzm.txt").addResourceLocations("classpath:static/mp/MP_verify_bASPYh8PfQoTHBzm.txt");
        super.addResourceHandlers(registry);
    }

}