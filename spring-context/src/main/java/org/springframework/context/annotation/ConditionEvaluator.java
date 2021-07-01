

package org.springframework.context.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ConfigurationCondition.ConfigurationPhase;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

/**
 * Internal class used to evaluate {@link Conditional} annotations.
 * @since 4.0
 */
// modify- 添加 public
public class ConditionEvaluator {

	private final ConditionContextImpl context;

	/**
	 * Create a new {@link ConditionEvaluator} instance.
	 */
	public ConditionEvaluator(@Nullable BeanDefinitionRegistry registry,@Nullable Environment environment, @Nullable ResourceLoader resourceLoader) {
		this.context = new ConditionContextImpl(registry, environment, resourceLoader);
	}

	/**
	 * Determine if an item should be skipped based on {@code @Conditional} annotations.
	 * The {@link ConfigurationPhase} will be deduced from the type of item (i.e. a {@code @Configuration} class will be {@link ConfigurationPhase#PARSE_CONFIGURATION})
	 * @param metadata the meta data
	 * @return if the item should be skipped
	 */
	public boolean shouldSkip(AnnotatedTypeMetadata metadata) {
		return shouldSkip(metadata, null);
	}

	/**
	 * Determine if an item should be skipped based on {@code @Conditional} annotations.
	 * //判定基于@Conditional注解的配置类是否应该忽略
	 * @param metadata the meta data
	 * @param phase the phase of the call
	 * @return if the item should be skipped  true 代表跳过被注解组件的注册或解析
	 */
	public boolean shouldSkip(@Nullable AnnotatedTypeMetadata metadata, @Nullable ConfigurationPhase phase) {
		// 如果配置类没有被@Conditional注解标注，则不能跳过，需要进一步解析
		if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
			return false;
		}
		// 若没有明确传递当前调用阶段，则根据配置类元信息分析当前应处于哪个阶段
		if (phase == null) {
			// 如果元数据是AnnotationMetadata的实例，并且是通过注解方式（@Configuration或@Bean等注解标注）作为候选配置类的，则判定当前阶段为解析配置类阶段
			if (metadata instanceof AnnotationMetadata && ConfigurationClassUtils.isConfigurationCandidate((AnnotationMetadata) metadata)) {
				// 将当前注册阶段标记为转换为配置类阶段继续判定
				return shouldSkip(metadata, ConfigurationPhase.PARSE_CONFIGURATION);
			}
			// 否则判定为将ConfigurationClass转换为BeanDefinition阶段
			return shouldSkip(metadata, ConfigurationPhase.REGISTER_BEAN);
		}
		List<Condition> conditions = new ArrayList<>();
		//首先获取@Conditional注解属性value指定的条件判定类
		for (String[] conditionClasses : getConditionClasses(metadata)) {
			for (String conditionClass : conditionClasses) {
				//获取条件判定类的Condition实例对象
				Condition condition = getCondition(conditionClass, this.context.getClassLoader());
				conditions.add(condition);
			}
		}
		// 将条件判定类排序
		AnnotationAwareOrderComparator.sort(conditions);
		for (Condition condition : conditions) {
			ConfigurationPhase requiredPhase = null;
			if (condition instanceof ConfigurationCondition) {
				requiredPhase = ((ConfigurationCondition) condition).getConfigurationPhase();
			}
			// 调用具体条件判定类的matches方法判定是否匹配
			// 只有Condition的requiredPhase与当前调用阶段一致，matches方法才生效，否则所有配置类都解析有一个condition返回false就跳过类的解析或注册
			if ((requiredPhase == null || requiredPhase == phase) && !condition.matches(this.context, metadata)) {
				return true;
			}
		}
		return false;
	}

	//获取@Conditional条件注解的属性值，即条件判定类
	@SuppressWarnings("unchecked")
	private List<String[]> getConditionClasses(AnnotatedTypeMetadata metadata) {
		MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
		Object values = (attributes != null ? attributes.get("value") : null);
		return (List<String[]>) (values != null ? values : Collections.emptyList());
	}

	//获取条件判定类的Condition实例对象
	private Condition getCondition(String conditionClassName, @Nullable ClassLoader classloader) {
		Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName, classloader);
		return (Condition) BeanUtils.instantiateClass(conditionClass);
	}

	/**
	 * Implementation of a {@link ConditionContext}.
	 */
	private static class ConditionContextImpl implements ConditionContext {

		@Nullable
		private final BeanDefinitionRegistry registry;

		@Nullable
		private final ConfigurableListableBeanFactory beanFactory;

		private final Environment environment;

		private final ResourceLoader resourceLoader;

		@Nullable
		private final ClassLoader classLoader;

		public ConditionContextImpl(@Nullable BeanDefinitionRegistry registry,@Nullable Environment environment, @Nullable ResourceLoader resourceLoader) {
			this.registry = registry;
			this.beanFactory = deduceBeanFactory(registry);
			this.environment = (environment != null ? environment : deduceEnvironment(registry));
			this.resourceLoader = (resourceLoader != null ? resourceLoader : deduceResourceLoader(registry));
			this.classLoader = deduceClassLoader(resourceLoader, this.beanFactory);
		}

		@Nullable
		private ConfigurableListableBeanFactory deduceBeanFactory(@Nullable BeanDefinitionRegistry source) {
			if (source instanceof ConfigurableListableBeanFactory) {
				return (ConfigurableListableBeanFactory) source;
			}
			if (source instanceof ConfigurableApplicationContext) {
				return (((ConfigurableApplicationContext) source).getBeanFactory());
			}
			return null;
		}

		private Environment deduceEnvironment(@Nullable BeanDefinitionRegistry source) {
			if (source instanceof EnvironmentCapable) {
				return ((EnvironmentCapable) source).getEnvironment();
			}
			return new StandardEnvironment();
		}

		private ResourceLoader deduceResourceLoader(@Nullable BeanDefinitionRegistry source) {
			if (source instanceof ResourceLoader) {
				return (ResourceLoader) source;
			}
			return new DefaultResourceLoader();
		}

		@Nullable
		private ClassLoader deduceClassLoader(@Nullable ResourceLoader resourceLoader,@Nullable ConfigurableListableBeanFactory beanFactory) {
			if (resourceLoader != null) {
				ClassLoader classLoader = resourceLoader.getClassLoader();
				if (classLoader != null) {
					return classLoader;
				}
			}
			if (beanFactory != null) {
				return beanFactory.getBeanClassLoader();
			}
			return ClassUtils.getDefaultClassLoader();
		}

		@Override
		public BeanDefinitionRegistry getRegistry() {
			Assert.state(this.registry != null, "No BeanDefinitionRegistry available");
			return this.registry;
		}

		@Override
		@Nullable
		public ConfigurableListableBeanFactory getBeanFactory() {
			return this.beanFactory;
		}

		@Override
		public Environment getEnvironment() {
			return this.environment;
		}

		@Override
		public ResourceLoader getResourceLoader() {
			return this.resourceLoader;
		}

		@Override
		@Nullable
		public ClassLoader getClassLoader() {
			return this.classLoader;
		}
	}
}
