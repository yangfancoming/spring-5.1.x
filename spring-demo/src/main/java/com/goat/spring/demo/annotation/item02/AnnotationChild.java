package com.goat.spring.demo.annotation.item02;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//编写子注解，其中子注解打上了元注解@AnnotaionBase标识

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AnnotaionBase
public @interface AnnotationChild {

	/**
	 简略意思是注解中使用了元注解时，可以对元注解的值进行重写，目的是为了能达到和类继承中override相似的功能
	 子注解的  attribute = "value"  对应 父注解的 String value() default "";
	*/
	@AliasFor(annotation = AnnotaionBase.class, attribute = "value")
	String extendValue() default "";

	/**

	 此方式  省略了 attribute = "value" 属性 但是父注解中必须也要有 value() 才能省略
	 @AliasFor(annotation = Component.class)
	 参考  {@link org.springframework.stereotype.Servic}.
	*/
	@AliasFor(annotation = AnnotaionBase.class)
	String value() default "";
}
