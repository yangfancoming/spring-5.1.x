package com.goat.chapter201.lookupmethod.item03;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by 64274 on 2019/8/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/16---12:32
 */
@Component
public class NewsProvider implements ApplicationContextAware {

	private ApplicationContext ac;

	public News getNews(){
		return ac.getBean(News.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ac = applicationContext;
	}
}