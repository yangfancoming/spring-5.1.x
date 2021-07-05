package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Beans on which the current bean depends.
 * Any beans specified are guaranteed to be created by the container before this bean.
 * Used infrequently in cases where a bean does not explicitly depend on another through properties or constructor arguments,
 * but rather depends on the side effects of another bean's initialization.
 *
 * A depends-on declaration can specify both an initialization-time dependency and,
 * in the case of singleton beans only, a corresponding destruction-time dependency.
 * Dependent beans that define a depends-on relationship with a given bean are destroyed first,
 * prior to the given bean itself being destroyed. Thus, a depends-on declaration can also control shutdown order.
 *
 * May be used on any class directly or indirectly annotated with {@link org.springframework.stereotype.Component} or on methods annotated with {@link Bean}.
 * Using {@link DependsOn} at the class level has no effect unless component-scanning is being used. If a {@link DependsOn}-annotated class is declared via XML,
 * {@link DependsOn} annotation metadata is ignored, and {@code <bean depends-on="..."/>} is respected instead.
 * @since 3.0
 *
 * 当作用在类上时，通常会与@Component及其衍生注解等注解配合使用。
 * 当作用在方法上时，通常会与@Bean注解配合使用。
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DependsOn {

	// 要依赖的bean id，是个数组，也就是说可以依赖多个bean。效果是 value指定的bean总是先被创建。
	String[] value() default {};
}
