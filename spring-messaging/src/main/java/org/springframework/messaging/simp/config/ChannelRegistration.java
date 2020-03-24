

package org.springframework.messaging.simp.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * A registration class for customizing the configuration for a
 * {@link org.springframework.messaging.MessageChannel}.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class ChannelRegistration {

	@Nullable
	private TaskExecutorRegistration registration;

	private final List<ChannelInterceptor> interceptors = new ArrayList<>();


	/**
	 * Configure the thread pool backing this message channel.
	 */
	public TaskExecutorRegistration taskExecutor() {
		return taskExecutor(null);
	}

	/**
	 * Configure the thread pool backing this message channel using a custom
	 * ThreadPoolTaskExecutor.
	 * @param taskExecutor the executor to use (or {@code null} for a default executor)
	 */
	public TaskExecutorRegistration taskExecutor(@Nullable ThreadPoolTaskExecutor taskExecutor) {
		if (this.registration == null) {
			this.registration = (taskExecutor != null ? new TaskExecutorRegistration(taskExecutor) :
					new TaskExecutorRegistration());
		}
		return this.registration;
	}

	/**
	 * Configure the given interceptors for this message channel,
	 * adding them to the channel's current list of interceptors.
	 * @since 4.3.12
	 */
	public ChannelRegistration interceptors(ChannelInterceptor... interceptors) {
		this.interceptors.addAll(Arrays.asList(interceptors));
		return this;
	}

	/**
	 * Configure interceptors for the message channel.
	 * @deprecated as of 4.3.12, in favor of {@link #interceptors(ChannelInterceptor...)}
	 */
	@Deprecated
	public ChannelRegistration setInterceptors(@Nullable ChannelInterceptor... interceptors) {
		if (interceptors != null) {
			this.interceptors.addAll(Arrays.asList(interceptors));
		}
		return this;
	}


	protected boolean hasTaskExecutor() {
		return (this.registration != null);
	}

	protected boolean hasInterceptors() {
		return !this.interceptors.isEmpty();
	}

	protected List<ChannelInterceptor> getInterceptors() {
		return this.interceptors;
	}

}
