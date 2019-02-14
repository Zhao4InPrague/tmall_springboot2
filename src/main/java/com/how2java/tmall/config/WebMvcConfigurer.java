package com.how2java.tmall.config;

import com.how2java.tmall.interceptor.LoginInterceptor;
import com.how2java.tmall.interceptor.OtherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter{

    //这个就记住吧和前面的跨域一样,拦截器这个概念记一下，具体写法查
    @Bean
    public LoginInterceptor getLoginIntercepter(){
        return new LoginInterceptor();
    }

    @Bean
    public OtherInterceptor getOtherIntercepter() {
        return new OtherInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getOtherIntercepter()).addPathPatterns("/**");
        registry.addInterceptor(getLoginIntercepter()).addPathPatterns("/**");
    }

}
