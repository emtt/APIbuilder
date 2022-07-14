package com.mobilize.apibuilder

import com.mobilize.apibuilder.config.JwtFilter
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ApIbuilderApplication {

    @Bean
    FilterRegistrationBean jwtFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean()
        registrationBean.setFilter(new JwtFilter())
        registrationBean.addUrlPatterns("/api/*")
        return registrationBean
    }

    static void main(String[] args) {
        SpringApplication.run(ApIbuilderApplication, args)
    }

}
