package com.goat.chapter201.dependson.item02;

/**
 * Created by 64274 on 2019/8/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/10---21:39
 */
public class EventListenerBean {

	public EventListenerBean() {
		System.out.println(" 老师 初始化 ");
	}

	public void addListener() {
		EventManager.getInstance().addListener(s ->System.out.println("老师: 监听到事件：" + s));
	}
}
