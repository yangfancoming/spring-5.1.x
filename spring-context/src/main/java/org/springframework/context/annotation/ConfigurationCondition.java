

package org.springframework.context.annotation;

/**
 * A {@link Condition} that offers more fine-grained control when used with {@code @Configuration}.
 * Allows certain {@link Condition Conditions} to adapt when they match based on the configuration phase.
 * For example, a condition that checks if a bean  has already been registered might choose to only be evaluated during the
 * {@link ConfigurationPhase#REGISTER_BEAN REGISTER_BEAN} {@link ConfigurationPhase}.
 * @since 4.0
 * @see Configuration
 *
 * 代表当前调用的阶段，该类共有两个值
 * PARSE_CONFIGURATION：代表解析配置类阶段，也就是将配置类转换为ConfigurationClass阶段
 * REGISTER_BEAN：代表配置类注册为bean阶段，也就是将配置类是否需要在将其注册到IOC容器阶段
 */
public interface ConfigurationCondition extends Condition {

	/**
	 * Return the {@link ConfigurationPhase} in which the condition should be evaluated.
	 */
	ConfigurationPhase getConfigurationPhase();

	/**
	 * The various configuration phases where the condition could be evaluated.
	 */
	enum ConfigurationPhase {

		/**
		 * The {@link Condition} should be evaluated as a {@code @Configuration} class is being parsed.
		 * If the condition does not match at this point, the {@code @Configuration} class will not be added.
		 */
		PARSE_CONFIGURATION,

		/**
		 * The {@link Condition} should be evaluated when adding a regular (non {@code @Configuration}) bean.
		 * The condition will not prevent {@code @Configuration} classes from being added.
		 * At the time that the condition is evaluated, all {@code @Configuration}s will have been parsed.
		 */
		REGISTER_BEAN
	}
}
