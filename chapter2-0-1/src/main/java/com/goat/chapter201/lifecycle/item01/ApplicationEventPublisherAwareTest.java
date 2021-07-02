package com.goat.chapter201.lifecycle.item01;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Created by Administrator on 2020/4/21.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/21---13:47
 */
public class ApplicationEventPublisherAwareTest implements ApplicationEventPublisherAware {
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		System.out.println("setApplicationEventPublisher..." + applicationEventPublisher);
	}
}
