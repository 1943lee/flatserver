package controller.config;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import common.MyRequestFilter;

@Configuration
public class FilterConfig {
	
	@Bean
    public FilterRegistrationBean httpMethodFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(myHttpMethodFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("myHttpMethodFilter");
        return registration;
    }

    @Bean(name = "myHttpMethodFilter")
    public MyRequestFilter myHttpMethodFilter() {
        return new MyRequestFilter();
    }


}
