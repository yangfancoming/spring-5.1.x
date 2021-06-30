package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a component is only eligible for registration when all {@linkplain #value specified conditions} match.
 * A <em>condition</em> is any state that can be determined programmatically before the bean definition is due to be registered (see {@link Condition} for details).
 *
 * The {@code @Conditional} annotation may be used in any of the following ways:
 * <li>as a type-level annotation on any class directly or indirectly annotated with {@code @Component}, including {@link Configuration @Configuration} classes</li>
 * <li>as a meta-annotation, for the purpose of composing custom stereotypeannotations</li>
 * <li>as a method-level annotation on any {@link Bean @Bean} method</li>
 *
 * If a {@code @Configuration} class is marked with {@code @Conditional},all of the {@code @Bean} methods, {@link Import @Import} annotations,
 * and {@link ComponentScan @ComponentScan} annotations associated with that class will be subject to the conditions.
 *
 * <strong>NOTE</strong>: Inheritance of {@code @Conditional} annotations is not supported;
 * any conditions from superclasses or from overridden methods will not be considered.
 * In order to enforce these semantics,{@code @Conditional} itself is not declared as {@link java.lang.annotation.Inherited @Inherited};
 * furthermore, any custom <em>composed annotation</em> that is meta-annotated with {@code @Conditional} must not be declared as {@code @Inherited}.
 * @since 4.0
 * @see Condition
 * 表示一个组件只有在所有value指定的条件匹配时，才有资格被注册到容器中。
 * 条件 是在bean定义注册之前可以通过编程确定的任何状态
 * 如果 @Configuration 主配置类上标有 @Conditional 注解，那么主配置类中的所有与@Bean @Import @ComponentScan相关联的类的注入容器动作， 都将受到此Conditional条件的影响
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conditional {

	/**
	 * All {@link Condition Conditions} that must {@linkplain Condition#matches match}
	 * in order for the component to be registered.
	 */
	Class<? extends Condition>[] value();
}
