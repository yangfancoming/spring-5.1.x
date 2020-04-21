package com.goat.chapter185.item03;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * Created by Administrator on 2020/4/21.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/21---13:48
 */
public class MessageSourceAwareTest implements MessageSourceAware {
	@Override
	public void setMessageSource(MessageSource messageSource) {
		System.out.println("setMessageSource..." + messageSource);
	}
}
