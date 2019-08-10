package com.goat.chapter105;

import org.springframework.context.annotation.*;

/**
 * Created by 64274 on 2019/8/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/10---21:39
 *
 * 如果我们注释掉@DependsOn("eventListener")，我们可能不确定获得相同结果。尝试多次运行main方法，偶尔我们将看到EventListenerBean 没有收到事件。为什么是偶尔呢？因为容器启动过程中，spring按任意顺序加载bean。
 * 那么当不使用@DependsOn可以让其100%确定吗？可以使用@Lazy注解放在eventListenerBean ()上。因为EventListenerBean在启动阶段不加载，当其他bean需要其时才加载。这次我们仅EventListenerBean被初始化。
 * EventPublisherBean initializing
 * 现在从新增加@DependsOn，也不删除@Lazy注解，输出结果和第一次一致，虽然我们使用了@Lazy注解，eventListenerBean在启动时仍然被加载，因为@DependsOn表明需要EventListenerBean。
 *
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
