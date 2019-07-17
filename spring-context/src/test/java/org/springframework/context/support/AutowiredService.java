

package org.springframework.context.support;

import org.springframework.context.MessageSource;


public class AutowiredService {

	private MessageSource messageSource;

	public void setMessageSource(MessageSource messageSource) {
		if (this.messageSource != null) {
			throw new IllegalArgumentException("MessageSource should not be set twice");
		}
		this.messageSource = messageSource;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

}
