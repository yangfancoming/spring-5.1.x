package com.goat.chapter201.dependson.item02;

import org.junit.Test;
import org.springframework.context.annotation.*;

/**
 * Created by 64274 on 2019/8/10.
 * @ Description: @DependsOn 注解测试用例
 * @ author  山羊来了
 * @ date 2019/8/10---21:39
 *  示例场景中，分为学生和老师，其中老师是监听者，学生是事件发布者。
 *  老师需要监听学生是否迟到，那么就要求容器在创建时，要确保先创建老师，这样才能保证监听到所有学生的迟到。
 *  如果先创建学生，后创建老师的话，那么老师很有可能会遗漏掉某些学生的迟到。
 *
 *  鉴于此场景，@DependsOn 注解应运而生。 在学生类上添加@DependsOn("teacher")，这样一来，容器会保证在创建学生之前，会先去创建老师。
 */
@Configuration
@ComponentScan("com.goat.chapter201.dependson.item02")
public class App {

	@Bean(initMethod = "publish")
	@DependsOn("teacher")
	public EventPublisherBean student () {
		return new EventPublisherBean();
	}

	@Bean(name = "teacher", initMethod = "addListener")
	public EventListenerBean teacher () {
		return new EventListenerBean();
	}

	/**
	 * 加上 @DependsOn("teacher")
	 *  老师 初始化
	 *  学生 初始化
	 *  老师: 监听到事件：学生迟到~~
	 *
	 * 注释掉 @DependsOn("teacher")
	 * 学生 初始化
	 * 老师 初始化
	*/
	@Test
	public void test(){
		new AnnotationConfigApplicationContext(App.class);
	}
}
