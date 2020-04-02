

package org.springframework.transaction.event;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AliasFor;

/**
 * An {@link EventListener} that is invoked according to a {@link TransactionPhase}.
 *
 * If the event is not published within the boundaries of a managed transaction, the
 * event is discarded unless the {@link #fallbackExecution} flag is explicitly set. If a
 * transaction is running, the event is processed according to its {@code TransactionPhase}.
 *
 * Adding {@link org.springframework.core.annotation.Order @Order} to your annotated
 * method allows you to prioritize that listener amongst other listeners running before
 * or after transaction completion.

 * @since 4.2
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EventListener
public @interface TransactionalEventListener {

	/**
	 * Phase to bind the handling of an event to.
	 * The default phase is {@link TransactionPhase#AFTER_COMMIT}.
	 * If no transaction is in progress, the event is not processed at
	 * all unless {@link #fallbackExecution} has been enabled explicitly.
	 *  // 指定当前标注方法处理事务的类型
	 */
	TransactionPhase phase() default TransactionPhase.AFTER_COMMIT;

	/**
	 * Whether the event should be processed if no transaction is running.
	 * // 用于指定当前方法如果没有事务，是否执行相应的事务事件监听器
	 */
	boolean fallbackExecution() default false;

	/**
	 * Alias for {@link #classes}.
	 * // 与classes属性一样，指定了当前事件传入的参数类型，指定了这个参数之后就可以在监听方法上
	 *     // 直接什么一个这个参数了
	 */
	@AliasFor(annotation = EventListener.class, attribute = "classes")
	Class<?>[] value() default {};

	/**
	 * The event classes that this listener handles.
	 * If this attribute is specified with a single value, the annotated
	 * method may optionally accept a single parameter. However, if this
	 * attribute is specified with multiple values, the annotated method
	 * must <em>not</em> declare any parameters.
	 * // 作用与value属性一样，用于指定当前监听方法的参数类型
	 */
	@AliasFor(annotation = EventListener.class, attribute = "classes")
	Class<?>[] classes() default {};

	/**
	 * Spring Expression Language (SpEL) attribute used for making the event
	 * handling conditional.
	 * The default is {@code ""}, meaning the event is always handled.
	 * @see EventListener#condition
	 * // 这个属性使用Spring Expression Language对目标类和方法进行匹配，对于不匹配的方法将会过滤掉
	 */
	String condition() default "";

}
