package com.goat.spring.demo.annotation.item01;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 64274 on 2019/6/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/30---14:15
 */
//定义注解
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAliasAnnotation {

	@AliasFor(value = "location")
	String value() default "";

	@AliasFor(value = "value")
	String location() default "";
}

