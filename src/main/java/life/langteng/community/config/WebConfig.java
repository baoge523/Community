package life.langteng.community.config;

import life.langteng.community.interception.CheckLoginInterception;
import life.langteng.community.interception.CustomizeErrorViewInterceptor;
import life.langteng.community.interception.NotificationCountInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * 注入拦截器
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private CheckLoginInterception checkLoginInterception;

    @Autowired
    private NotificationCountInterceptor notificationCountInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         *  spring 底层 会将 这些 信息 封装成一个 MappedInterceptor
         *
         *    然后会执行里面的 public boolean matches(String lookupPath, PathMatcher pathMatcher)
         *
         *          lookupPath  请求地址
         *          pathMatcher  路径匹配器
         *
         */
        registry.addInterceptor(checkLoginInterception)
                .addPathPatterns("/**")
                .excludePathPatterns("/callback");

        registry.addInterceptor(notificationCountInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/callback");

        registry.addInterceptor(new CustomizeErrorViewInterceptor()).addPathPatterns("/error");

//                .excludePathPatterns("/static/");  // 将 静态资源放行了，配置规则在 application.properties中
//                 spring.mvc.static-path-pattern=/static/**
    }

    /**
     * 注意格式呀!!!
     *
     *      资源路径: /images/**     以 / 开头，
     *
     *      映射路径: file: 文件路径  classpath: 类路径
     *              需要以 \\ 或者 / 结尾
     *
     *
     *   System.getProperty("user.dir"); 获取当前项目的路径
     *
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/images/**").addResourceLocations("file:"+System.getProperty("user.dir")+"\\src\\main\\resources\\static\\images\\");
    }
}
