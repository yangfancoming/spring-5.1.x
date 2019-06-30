package com.goat.spring.demo.annotation.item01;

import org.springframework.core.annotation.AnnotationUtils;

import java.util.function.Consumer;

/**
 * Created by 64274 on 2019/6/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/30---14:16
 */

//编写测试代码
public class App {

	public static void main(String[] args) throws NoSuchMethodException {

		Consumer<MyAliasAnnotation> logic = a->{
			System.out.println("我是 value 属性 ：" + a.value());
			System.out.println("我是 location 属性 ：" + a.location());
		};

//		我是 value 属性 ：我是 location
//		我是 location 属性 ：我是 location
		MyAliasAnnotation aliasAnnotation = AnnotationUtils.findAnnotation(MyClass.class.getMethod("one"), MyAliasAnnotation.class);
		logic.accept(aliasAnnotation);

//		我是 value 属性 ：我是 value
//		我是 location 属性 ：我是 value
		MyAliasAnnotation aliasAnnotation2 = AnnotationUtils.findAnnotation(MyClass.class.getMethod("one2"), MyAliasAnnotation.class);
		logic.accept(aliasAnnotation2);
	}

}