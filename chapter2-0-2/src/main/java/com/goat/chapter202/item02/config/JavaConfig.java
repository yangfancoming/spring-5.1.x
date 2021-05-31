package com.goat.chapter202.item02.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 64274 on 2019/8/12.
 * @ Description: ComponentScan 数组参数示例
 * @ author  山羊来了
 * @ date 2019/8/12---20:46
 */
@Configuration
@ComponentScan(value = "com.goat.chapter202.item02") // 等同于 <context:component-scan>
//@ComponentScan(value =  {"com.goat.chapter185.item","com.goat.chapter185.common"})
public class JavaConfig {



}
