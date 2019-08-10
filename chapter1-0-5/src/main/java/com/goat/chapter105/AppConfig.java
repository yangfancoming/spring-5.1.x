package com.goat.chapter105;

import org.springframework.context.annotation.*;

/**
 * Created by 64274 on 2019/8/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/10---21:39
 */
@Configuration
@ComponentScan("com.goat.chapter105")
public class AppConfig {

	@Bean(initMethod = "initialize")
	@DependsOn("eventListener")
	public EventPublisherBean eventPublisherBean () {
		return new EventPublisherBean();
	}

	@Bean(name = "eventListener", initMethod = "initialize")
	public EventListenerBean eventListenerBean () {
		return new EventListenerBean();
	}

	/**
	 * 加上 @DependsOn("eventListener")
	 * EventListenerBean initializing   事件监听者  初始化
	 * EventPublisherBean initializing   事件发布者 初始化
	 * event received in EventListenerBean : 监听到事件。。。
	 * event published from EventPublisherBean  发布事件咯~~
	 *
	 * 注释掉 @DependsOn("eventListener")
	 * EventPublisherBean initializing   事件发布者 初始化
	 * EventListenerBean initializing   事件监听者  初始化
	*/
	public static void main (String... strings) {
		new AnnotationConfigApplicationContext(AppConfig.class);
	}
}
