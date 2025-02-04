package com.gmail.at.ankyhe.my.workflow.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfiguration {

    @Bean
    @Qualifier("deepSeekRestTemplate")
    public RestTemplate deepSeekRestTemplate() {
        return new RestTemplate();
    }
}
