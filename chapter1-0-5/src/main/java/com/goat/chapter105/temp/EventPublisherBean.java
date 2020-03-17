package com.goat.chapter105.temp;

/**
 * Created by 64274 on 2019/8/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/10---21:39
 */
public class EventPublisherBean {

	public void initialize() {
		System.out.println("EventPublisherBean initializing   事件发布者 初始化 ");
		EventManager.getInstance().publish("event published from EventPublisherBean  发布事件咯~~");
	}
}
