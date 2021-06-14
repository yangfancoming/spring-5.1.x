package com.goat.chapter200.autoconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 如果没有其他配置的话， sos @ComponentScan 默认会扫描这个包以及这个包下的所有子包。
 * 因为CDPlayerConfig类位于soundsystem包中， 因此Spring将会扫描这个包以及这个包下的所有子包，并查找带有@Component注解的类。
 * 这样的话，就能发现CompactDisc， 并且会在Spring中自动为其创建一个bean
 * 多个基础包扫描  basePackageClasses = { CDPlayer.class,CompactDisc.class }
 * @Date:   2018/7/25
 */
@Configuration
//@ComponentScan( basePackageClasses = {App.class} ) // doit 用这个为啥会报错？？？ 单个基础包扫描 （将指定类所在的包作为基础包） 可以使用类  但是一般都使用接口
@ComponentScan( "com.goat.chapter200.autoconfig" ) //  单个基础包扫描 （将指定类所在的包作为基础包） 可以使用类  但是一般都使用接口
public class CDPlayerConfig {
}
