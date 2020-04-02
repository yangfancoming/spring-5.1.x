

package org.springframework.transaction.event;

import java.lang.reflect.Method;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ApplicationListenerMethodAdapter;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * {@link GenericApplicationListener} adapter that delegates the processing of
 * an event to a {@link TransactionalEventListener} annotated method. Supports
 * the exact same features as any regular {@link EventListener} annotated method
 * but is aware of the transactional context of the event publisher.
 *
 * Processing of {@link TransactionalEventListener} is enabled automatically
 * when Spring's transaction management is enabled. For other cases, registering
 * a bean of type {@link TransactionalEventListenerFactory} is required.
 *
 * @author Stephane Nicoll

 * @since 4.2
 * @see ApplicationListenerMethodAdapter
 * @see TransactionalEventListener
 */
class ApplicationListenerMethodTransactionalAdapter extends ApplicationListenerMethodAdapter {

	private final TransactionalEventListener annotation;


	public ApplicationListenerMethodTransactionalAdapter(String beanName, Class<?> targetClass, Method method) {
		super(beanName, targetClass, method);
		TransactionalEventListener ann = AnnotatedElementUtils.findMergedAnnotation(method, TransactionalEventListener.class);
		if (ann == null) {
			throw new IllegalStateException("No TransactionalEventListener annotation found on method: " + method);
		}
		this.annotation = ann;
	}


	@Override
	public void onApplicationEvent(ApplicationEvent event) {

		// 如果当前TransactionManager已经配置开启事务事件监听，
		// 此时才会注册TransactionSynchronization对象
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			// 通过当前事务事件发布的参数，创建一个TransactionSynchronization对象
			TransactionSynchronization transactionSynchronization = createTransactionSynchronization(event);
			// 注册TransactionSynchronization对象到TransactionManager中
			TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);
		}
		else if (this.annotation.fallbackExecution()) {
			// 如果当前TransactionManager没有开启事务事件处理，但是当前事务监听方法中配置了
			// fallbackExecution属性为true，说明其需要对当前事务事件进行监听，无论其是否有事务
			if (this.annotation.phase() == TransactionPhase.AFTER_ROLLBACK && logger.isWarnEnabled()) {
				logger.warn("Processing " + event + " as a fallback execution on AFTER_ROLLBACK phase");
			}
			processEvent(event);
		}
		else {
			// No transactional event execution at all
			// 走到这里说明当前是不需要事务事件处理的，因而直接略过
			if (logger.isDebugEnabled()) {
				logger.debug("No transaction is active - skipping " + event);
			}
		}
	}

	private TransactionSynchronization createTransactionSynchronization(ApplicationEvent event) {
		return new TransactionSynchronizationEventAdapter(this, event, this.annotation.phase());
	}


	private static class TransactionSynchronizationEventAdapter extends TransactionSynchronizationAdapter {

		private final ApplicationListenerMethodAdapter listener;

		private final ApplicationEvent event;

		private final TransactionPhase phase;

		public TransactionSynchronizationEventAdapter(ApplicationListenerMethodAdapter listener,ApplicationEvent event, TransactionPhase phase) {
			this.listener = listener;
			this.event = event;
			this.phase = phase;
		}

		@Override
		public int getOrder() {
			return this.listener.getOrder();
		}

		// 在目标方法配置的phase属性为BEFORE_COMMIT时，处理before commit事件
		@Override
		public void beforeCommit(boolean readOnly) {
			if (this.phase == TransactionPhase.BEFORE_COMMIT) {
				processEvent();
			}
		}

		// 这里对于after completion事件的处理，虽然分为了三个if分支，但是实际上都是执行的processEvent()
		// 方法，因为after completion事件是事务事件中一定会执行的，因而这里对于commit，
		// rollback和completion事件都在当前方法中处理也是没问题的
		@Override
		public void afterCompletion(int status) {
			if (this.phase == TransactionPhase.AFTER_COMMIT && status == STATUS_COMMITTED) {
				processEvent();
			}
			else if (this.phase == TransactionPhase.AFTER_ROLLBACK && status == STATUS_ROLLED_BACK) {
				processEvent();
			}
			else if (this.phase == TransactionPhase.AFTER_COMPLETION) {
				processEvent();
			}
		}

		// 执行事务事件
		protected void processEvent() {
			this.listener.processEvent(this.event);
		}
	}

}
