package com.goat.chapter201.dependson.item02;

/**
 * Created by 64274 on 2019/8/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/10---21:39
 */
public class EventPublisherBean {

	public EventPublisherBean() {
		System.out.println(" 学生 初始化 ");
	}

	public void publish() {

		EventManager.getInstance().publish("学生迟到~~");
	}
}
