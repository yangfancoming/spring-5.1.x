package com.goat.spring.postprocessor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages = "com.goat.spring.postprocessor")
public class MainConfig {

    @Bean(initMethod = "init")
    public Compent compent() {
        return new Compent();
    }
}
