

package org.springframework.transaction.event;

import java.lang.reflect.Method;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * {@link EventListenerFactory} implementation that handles {@link TransactionalEventListener} annotated methods.

 * @since 4.2
 */
public class TransactionalEventListenerFactory implements EventListenerFactory, Ordered {
	// 指定当前监听器的顺序
	private int order = 50;


	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	// 指定目标方法是否是所支持的监听器的类型，这里的判断逻辑就是如果目标方法上包含有
	// TransactionalEventListener注解，则说明其是一个事务事件监听器
	@Override
	public boolean supportsMethod(Method method) {
		return AnnotatedElementUtils.hasAnnotation(method, TransactionalEventListener.class);
	}

	// 为目标方法生成一个事务事件监听器，这里ApplicationListenerMethodTransactionalAdapter实现了
	// ApplicationEvent接口
	@Override
	public ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method) {
		return new ApplicationListenerMethodTransactionalAdapter(beanName, type, method);
	}

}
