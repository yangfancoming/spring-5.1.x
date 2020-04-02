

package org.springframework.aop.target;

import java.util.HashSet;
import java.util.Set;

import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.NamedThreadLocal;

/**
 * Alternative to an object pool. This {@link org.springframework.aop.TargetSource}
 * uses a threading model in which every thread has its own copy of the target.
 * There's no contention for targets. Target object creation is kept to a minimum
 * on the running server.
 *
 * Application code is written as to a normal pool; callers can't assume they
 * will be dealing with the same instance in invocations in different threads.
 * However, state can be relied on during the operations of a single thread:
 * for example, if one caller makes repeated calls on the AOP proxy.
 *
 * Cleanup of thread-bound objects is performed on BeanFactory destruction,
 * calling their {@code DisposableBean.destroy()} method if available.
 * Be aware that many thread-bound objects can be around until the application actually shuts down.
 * @see ThreadLocalTargetSourceStats
 * @see org.springframework.beans.factory.DisposableBean#destroy()
 */
@SuppressWarnings("serial")
public class ThreadLocalTargetSource extends AbstractPrototypeBasedTargetSource implements ThreadLocalTargetSourceStats, DisposableBean {

	/**
	 * ThreadLocal holding the target associated with the current
	 * thread. Unlike most ThreadLocals, which are static, this variable
	 * is meant to be per thread per instance of the ThreadLocalTargetSource class.
	 * // 保存目标对象的ThreadLocal对象
	 */
	private final ThreadLocal<Object> targetInThread = new NamedThreadLocal<>("Thread-local instance of bean '" + getTargetBeanName() + "'");

	/**
	 * Set of managed targets, enabling us to keep track of the targets we've created.
	 * // 将生成过的目标对象保存起来，以便于后续进行统一销毁
	 */
	private final Set<Object> targetSet = new HashSet<>();

	private int invocationCount;

	private int hitCount;


	/**
	 * Implementation of abstract getTarget() method.
	 * We look for a target held in a ThreadLocal. If we don't find one,
	 * we create one and bind it to the thread. No synchronization is required.
	 * 	// 生成目标对象，这里的生成方式是ThreadLocal很典型的一种使用策略，即首先从ThreadLocal中取，
	 * 	// 如果取到了，则直接返回，如果没取到，则使用“消耗“大一些的方式获取，并缓存到ThreadLocal中
	 */
	@Override
	public Object getTarget() throws BeansException {
		// 记录目标对象的获取次数
		++this.invocationCount;
		// 从ThreadLocal中获取
		Object target = this.targetInThread.get();
		if (target == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No target for prototype '" + getTargetBeanName() + "' bound to thread: " + "creating one and binding it to thread '" + Thread.currentThread().getName() + "'");
			}
			// Associate target with ThreadLocal.
			// 如果ThreadLocal中不存在，则通过最基本的方式获取目标对象，并将生成的对象保存到ThreadLocal中
			target = newPrototypeInstance();
			this.targetInThread.set(target);
			// 将生成的对象进行缓存
			synchronized (this.targetSet) {
				this.targetSet.add(target);
			}
		}
		else {
			++this.hitCount;
		}
		return target;
	}

	/**
	 * Dispose of targets if necessary; clear ThreadLocal.
	 * // 销毁当前TargetSource对象和生成的目标对象
	 * @see #destroyPrototypeInstance
	 */
	@Override
	public void destroy() {
		logger.debug("Destroying ThreadLocalTargetSource bindings");
		synchronized (this.targetSet) {
			for (Object target : this.targetSet) {
				// 销毁生成的目标对象
				destroyPrototypeInstance(target);
			}
			this.targetSet.clear();
		}
		// Clear ThreadLocal, just in case. // 清除ThreadLocal中的缓存
		this.targetInThread.remove();
	}


	@Override
	public int getInvocationCount() {
		return this.invocationCount;
	}

	@Override
	public int getHitCount() {
		return this.hitCount;
	}

	@Override
	public int getObjectCount() {
		synchronized (this.targetSet) {
			return this.targetSet.size();
		}
	}


	/**
	 * Return an introduction advisor mixin that allows the AOP proxy to be
	 * cast to ThreadLocalInvokerStats.
	 */
	public IntroductionAdvisor getStatsMixin() {
		DelegatingIntroductionInterceptor dii = new DelegatingIntroductionInterceptor(this);
		return new DefaultIntroductionAdvisor(dii, ThreadLocalTargetSourceStats.class);
	}

}
