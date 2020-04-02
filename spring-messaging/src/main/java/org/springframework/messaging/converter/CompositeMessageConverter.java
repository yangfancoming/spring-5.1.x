

package org.springframework.messaging.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.Assert;

/**
 * A {@link MessageConverter} that delegates to a list of registered converters
 * to be invoked until one of them returns a non-null result.
 *
 * As of 4.2.1, this composite converter implements {@link SmartMessageConverter}
 * in order to support the delegation of conversion hints.
 *
 * @author Rossen Stoyanchev

 * @since 4.0
 */
public class CompositeMessageConverter implements SmartMessageConverter {

	private final List<MessageConverter> converters;


	/**
	 * Create an instance with the given converters.
	 */
	public CompositeMessageConverter(Collection<MessageConverter> converters) {
		Assert.notEmpty(converters, "Converters must not be empty");
		this.converters = new ArrayList<>(converters);
	}


	@Override
	@Nullable
	public Object fromMessage(Message<?> message, Class<?> targetClass) {
		for (MessageConverter converter : getConverters()) {
			Object result = converter.fromMessage(message, targetClass);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	@Nullable
	public Object fromMessage(Message<?> message, Class<?> targetClass, @Nullable Object conversionHint) {
		for (MessageConverter converter : getConverters()) {
			Object result = (converter instanceof SmartMessageConverter ?
					((SmartMessageConverter) converter).fromMessage(message, targetClass, conversionHint) :
					converter.fromMessage(message, targetClass));
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	@Nullable
	public Message<?> toMessage(Object payload, @Nullable MessageHeaders headers) {
		for (MessageConverter converter : getConverters()) {
			Message<?> result = converter.toMessage(payload, headers);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	@Nullable
	public Message<?> toMessage(Object payload, @Nullable MessageHeaders headers, @Nullable Object conversionHint) {
		for (MessageConverter converter : getConverters()) {
			Message<?> result = (converter instanceof SmartMessageConverter ?
					((SmartMessageConverter) converter).toMessage(payload, headers, conversionHint) :
					converter.toMessage(payload, headers));
			if (result != null) {
				return result;
			}
		}
		return null;
	}


	/**
	 * Return the underlying list of delegate converters.
	 */
	public List<MessageConverter> getConverters() {
		return this.converters;
	}

	@Override
	public String toString() {
		return "CompositeMessageConverter[converters=" + getConverters() + "]";
	}

}
