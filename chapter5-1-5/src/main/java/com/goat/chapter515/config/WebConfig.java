package com.goat.chapter515.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by 64274 on 2019/8/14.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/14---10:14
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.goat.chapter515.controller")
public class WebConfig{

}