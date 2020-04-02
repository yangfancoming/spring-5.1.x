

package org.springframework.scheduling.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that marks a method to be scheduled. Exactly one of
 * the {@link #cron()}, {@link #fixedDelay()}, or {@link #fixedRate()}
 * attributes must be specified.
 *
 * The annotated method must expect no arguments. It will typically have
 * a {@code void} return type; if not, the returned value will be ignored
 * when called through the scheduler.
 *
 * Processing of {@code @Scheduled} annotations is performed by
 * registering a {@link ScheduledAnnotationBeanPostProcessor}. This can be
 * done manually or, more conveniently, through the {@code <task:annotation-driven/>}
 * element or @{@link EnableScheduling} annotation.
 *
 * This annotation may be used as a <em>meta-annotation</em> to create custom
 * <em>composed annotations</em> with attribute overrides.
 *
 * @author Mark Fisher

 * @author Dave Syer

 * @since 3.0
 * @see EnableScheduling
 * @see ScheduledAnnotationBeanPostProcessor
 * @see Schedules
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Schedules.class)
public @interface Scheduled {

	/**
	 * A special cron expression value that indicates a disabled trigger: {@value}.
	 * This is primarily meant for use with ${...} placeholders, allowing for
	 * external disabling of corresponding scheduled methods.
	 * @since 5.1
	 */
	String CRON_DISABLED = "-";


	/**
	 * A cron-like expression, extending the usual UN*X definition to include triggers
	 * on the second as well as minute, hour, day of month, month and day of week.
	 * E.g. {@code "0 * * * * MON-FRI"} means once per minute on weekdays
	 * (at the top of the minute - the 0th second).
	 * The special value {@link #CRON_DISABLED "-"} indicates a disabled cron trigger,
	 * primarily meant for externally specified values resolved by a ${...} placeholder.
	 * @return an expression that can be parsed to a cron schedule
	 * @see org.springframework.scheduling.support.CronSequenceGenerator
	 */
	String cron() default "";

	/**
	 * A time zone for which the cron expression will be resolved. By default, this
	 * attribute is the empty String (i.e. the server's local time zone will be used).
	 * @return a zone id accepted by {@link java.util.TimeZone#getTimeZone(String)},
	 * or an empty String to indicate the server's default time zone
	 * @since 4.0
	 * @see org.springframework.scheduling.support.CronTrigger#CronTrigger(String, java.util.TimeZone)
	 * @see java.util.TimeZone
	 */
	String zone() default "";

	/**
	 * Execute the annotated method with a fixed period in milliseconds between the
	 * end of the last invocation and the start of the next.
	 * @return the delay in milliseconds
	 */
	long fixedDelay() default -1;

	/**
	 * Execute the annotated method with a fixed period in milliseconds between the
	 * end of the last invocation and the start of the next.
	 * @return the delay in milliseconds as a String value, e.g. a placeholder
	 * or a {@link java.time.Duration#parse java.time.Duration} compliant value
	 * @since 3.2.2
	 */
	String fixedDelayString() default "";

	/**
	 * Execute the annotated method with a fixed period in milliseconds between
	 * invocations.
	 * @return the period in milliseconds
	 */
	long fixedRate() default -1;

	/**
	 * Execute the annotated method with a fixed period in milliseconds between
	 * invocations.
	 * @return the period in milliseconds as a String value, e.g. a placeholder
	 * or a {@link java.time.Duration#parse java.time.Duration} compliant value
	 * @since 3.2.2
	 */
	String fixedRateString() default "";

	/**
	 * Number of milliseconds to delay before the first execution of a
	 * {@link #fixedRate()} or {@link #fixedDelay()} task.
	 * @return the initial delay in milliseconds
	 * @since 3.2
	 */
	long initialDelay() default -1;

	/**
	 * Number of milliseconds to delay before the first execution of a
	 * {@link #fixedRate()} or {@link #fixedDelay()} task.
	 * @return the initial delay in milliseconds as a String value, e.g. a placeholder
	 * or a {@link java.time.Duration#parse java.time.Duration} compliant value
	 * @since 3.2.2
	 */
	String initialDelayString() default "";

}
