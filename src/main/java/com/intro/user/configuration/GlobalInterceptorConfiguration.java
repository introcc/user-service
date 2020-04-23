package com.intro.user.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.intro.user.interceptor.LogInterceptor;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;

@Configuration
public class GlobalInterceptorConfiguration {

    @Bean
    public GlobalServerInterceptorConfigurer globalInterceptorConfigurerAdapter() {
        return registry -> registry.addServerInterceptors(new LogInterceptor());
    }

}