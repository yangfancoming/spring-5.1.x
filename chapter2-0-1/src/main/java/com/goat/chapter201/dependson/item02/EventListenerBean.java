package com.goat.chapter201.dependson.item02;

/**
 * Created by 64274 on 2019/8/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/10---21:39
 */
public class EventListenerBean {

	public void addListener() {
		System.out.println(" 事件监听者  初始化 ");
		EventManager.getInstance().addListener(s ->System.out.println("event received in EventListenerBean : 监听到事件。。。" + s));
	}
}
