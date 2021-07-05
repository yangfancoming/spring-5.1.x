package com.goat.chapter201.dependson.item02;

/**
 * Created by 64274 on 2019/8/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/10---21:39
 */
public class EventPublisherBean {

	public void publish() {
		System.out.println("事件发布者 初始化 ");
		EventManager.getInstance().publish("发布事件咯~~");
	}
}
