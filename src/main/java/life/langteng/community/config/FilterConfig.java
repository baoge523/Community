package life.langteng.community.config;

import com.cyou.common.base.log.filter.AsyncManagerIntegrationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig{

    /**
     *
     *  如果我们需要向IOC容器中注意一个原生的Filter
     *
     *  我们只需要在java 配置类中注入一个 FilterRegistrationBean 对象
     *
     *  通过 FilterRegistrationBean对象 添加我们的原生的Filter对象，并指定过滤的路径
     *
     *
     *   FilterRegistrationBean   添加   Filter
     *
     *   ServletRegistrationBean  添加   Servlet
     *
     *   ServletListenerRegistrationBean  添加 Listener
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration
                .setFilter(new AsyncManagerIntegrationFilter());  // 还拦截器是用来支持异步的，将父线程中的MDC信息复制到子类线程中去
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        registration.setEnabled(true);
        return registration;
    }







}
