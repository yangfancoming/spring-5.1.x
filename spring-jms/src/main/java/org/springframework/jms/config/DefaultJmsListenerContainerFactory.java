

package org.springframework.jms.config;

import java.util.concurrent.Executor;

import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.backoff.BackOff;

/**
 * A {@link JmsListenerContainerFactory} implementation to build a regular
 * {@link DefaultMessageListenerContainer}.
 *
 * xmlBeanDefinitionReaderThis should be the default for most users and a good transition paths
 * for those that are used to build such container definition manually.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public class DefaultJmsListenerContainerFactory
		extends AbstractJmsListenerContainerFactory<DefaultMessageListenerContainer> {

	@Nullable
	private Executor taskExecutor;

	@Nullable
	private PlatformTransactionManager transactionManager;

	@Nullable
	private Integer cacheLevel;

	@Nullable
	private String cacheLevelName;

	@Nullable
	private String concurrency;

	@Nullable
	private Integer maxMessagesPerTask;

	@Nullable
	private Long receiveTimeout;

	@Nullable
	private Long recoveryInterval;

	@Nullable
	private BackOff backOff;


	/**
	 * @see DefaultMessageListenerContainer#setTaskExecutor
	 */
	public void setTaskExecutor(Executor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * @see DefaultMessageListenerContainer#setTransactionManager
	 */
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @see DefaultMessageListenerContainer#setCacheLevel
	 */
	public void setCacheLevel(Integer cacheLevel) {
		this.cacheLevel = cacheLevel;
	}

	/**
	 * @see DefaultMessageListenerContainer#setCacheLevelName
	 */
	public void setCacheLevelName(String cacheLevelName) {
		this.cacheLevelName = cacheLevelName;
	}

	/**
	 * @see DefaultMessageListenerContainer#setConcurrency
	 */
	public void setConcurrency(String concurrency) {
		this.concurrency = concurrency;
	}

	/**
	 * @see DefaultMessageListenerContainer#setMaxMessagesPerTask
	 */
	public void setMaxMessagesPerTask(Integer maxMessagesPerTask) {
		this.maxMessagesPerTask = maxMessagesPerTask;
	}

	/**
	 * @see DefaultMessageListenerContainer#setReceiveTimeout
	 */
	public void setReceiveTimeout(Long receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}

	/**
	 * @see DefaultMessageListenerContainer#setRecoveryInterval
	 */
	public void setRecoveryInterval(Long recoveryInterval) {
		this.recoveryInterval = recoveryInterval;
	}

	/**
	 * @see DefaultMessageListenerContainer#setBackOff
	 */
	public void setBackOff(BackOff backOff) {
		this.backOff = backOff;
	}


	@Override
	protected DefaultMessageListenerContainer createContainerInstance() {
		return new DefaultMessageListenerContainer();
	}

	@Override
	protected void initializeContainer(DefaultMessageListenerContainer container) {
		if (this.taskExecutor != null) {
			container.setTaskExecutor(this.taskExecutor);
		}
		if (this.transactionManager != null) {
			container.setTransactionManager(this.transactionManager);
		}

		if (this.cacheLevel != null) {
			container.setCacheLevel(this.cacheLevel);
		}
		else if (this.cacheLevelName != null) {
			container.setCacheLevelName(this.cacheLevelName);
		}

		if (this.concurrency != null) {
			container.setConcurrency(this.concurrency);
		}
		if (this.maxMessagesPerTask != null) {
			container.setMaxMessagesPerTask(this.maxMessagesPerTask);
		}
		if (this.receiveTimeout != null) {
			container.setReceiveTimeout(this.receiveTimeout);
		}

		if (this.backOff != null) {
			container.setBackOff(this.backOff);
			if (this.recoveryInterval != null) {
				logger.info("Ignoring recovery interval in DefaultJmsListenerContainerFactory in favor of BackOff");
			}
		}
		else if (this.recoveryInterval != null) {
			container.setRecoveryInterval(this.recoveryInterval);
		}
	}

}
