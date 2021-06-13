

package org.springframework.beans.factory.config;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.lang.Nullable;

/**
 *
 * sos Instantiation	表示实例化,对象还未生成
 * sos Initialization	表示初始化,对象已经生成
 * BeanPostProcessor的子接口，在明确的设置bean属性或自动注入发生之前，添加了实例化前和实例化后的回调方法。
 * 通常用于抑制特定目标bean的默认实例化，例如，为特定的目标源对象创建代理（连接池，懒加载等），或者为了实现额外的注入策略，比如字段注入。
 * 此接口是一个专用接口，主要用于框架内的内部使用。建议尽量实现普通的{@link BeanPostProcessor}接口，或者从{@link InstantiationAwareBeanPostProcessorAdapter}派生，以便屏蔽对此接口的扩展。
 *
 * Subinterface of {@link BeanPostProcessor} that adds a before-instantiation callback,and a callback after instantiation but before explicit properties are set or autowiring occurs.
 * Typically used to suppress default instantiation for specific target beans,for example to create proxies with special TargetSources (pooling targets,lazily initializing targets, etc),
 * or to implement additional injection strategies such as field injection.
 * <b>NOTE:</b> This interface is a special purpose interface, mainly for internal use within the framework.
 * It is recommended to implement the plain {@link BeanPostProcessor} interface as far as possible,
 * or to derive from {@link InstantiationAwareBeanPostProcessorAdapter} in order to be shielded from extensions to this interface.
 * @since 1.2
 * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#setCustomTargetSourceCreators
 * @see org.springframework.aop.framework.autoproxy.target.LazyInitTargetSourceCreator
 *
 * InstantiationAwareBeanPostProcessor代表了Spring的另外一段生命周期：实例化。
 * 先区别一下Spring Bean的实例化和初始化两个阶段的主要作用：
 * 1、实例化—-实例化的过程是一个创建Bean的过程，即调用Bean的构造函数，单例的Bean放入单例池中
 * 2、初始化—-初始化的过程是一个赋值的过程，即调用Bean的setter，设置Bean的属性
 * 之前的BeanPostProcessor作用于过程（2）前后，现在的InstantiationAwareBeanPostProcessor则作用于过程（1）前后；
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

	/**
	 * postProcessBeforeInstantiation方法是最先执行的方法，它在目标对象实例化之前调用，该方法的返回值类型是Object，我们可以返回任何类型的值。
	 * 由于这个时候目标对象还未实例化，所以这个返回值可以用来代替原本该生成的目标对象的实例(比如代理对象)。
	 * 如果该方法的返回值代替原本该生成的目标对象，后续只有postProcessAfterInitialization方法会调用，其它方法不再调用；否则按照正常的流程走
	 *
	 * 如果此方法返回非空对象，则bean创建过程将被短路。 （即后续的Bean的创建流程【实例化、初始化afterProperties】都不会执行，而是直接使用返回的快捷Bean）
	 * Apply this BeanPostProcessor <i>before the target bean gets instantiated</i>.
	 * The returned bean object may be a proxy to use instead of the target bean,effectively suppressing default instantiation of the target bean.
	 * If a non-null object is returned by this method, the bean creation process will be short-circuited.
	 * The only further processing applied is the {@link #postProcessAfterInitialization} callback from the configured {@link BeanPostProcessor BeanPostProcessors}.
	 * This callback will be applied to bean definitions with their bean class,as well as to factory-method definitions in which case the returned bean type will be passed in here.
	 * Post-processors may implement the extended {@link SmartInstantiationAwareBeanPostProcessor} interface in order to predict the type of the bean object that they are going to return here.
	 * The default implementation returns {@code null}.
	 * @param beanClass the class of the bean to be instantiated
	 * @param beanName the name of the bean
	 * @return the bean object to expose instead of a default instance of the target bean,or {@code null} to proceed with default instantiation
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see #postProcessAfterInstantiation
	 * @see AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition)
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getBeanClass()
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getFactoryMethodName()
	 */
	@Nullable
	default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	/**
	 * postProcessAfterInstantiation方法在目标对象实例化之后调用，这个时候对象已经被实例化，但是该实例的属性还未被设置，都是null。
	 * 因为它的返回值是决定要不要调用postProcessPropertyValues方法的其中一个因素（因为还有一个因素是mbd.getDependencyCheck()）；
	 * 如果该方法返回false,并且不需要check，那么postProcessPropertyValues就会被忽略不执行；如果返回true, postProcessPropertyValues就会被执行
	 *
	 * Perform operations after the bean has been instantiated, via a constructor or factory method,
	 * but before Spring property population (from explicit properties or autowiring) occurs.
	 * This is the ideal callback for performing custom field injection on the given bean instance, right before Spring's autowiring kicks in.
	 * The default implementation returns {@code true}.
	 * @param bean the bean instance created, with properties not having been set yet
	 * @param beanName the name of the bean
	 * @return {@code true} if properties should be set on the bean; {@code false} if property population should be skipped. Normal implementations should return {@code true}.
	 * Returning {@code false} will also prevent any subsequent InstantiationAwareBeanPostProcessor instances being invoked on this bean instance.
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see #postProcessBeforeInstantiation
	 */
	default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return true;
	}

	/**
	 * Post-process the given property values before the factory applies them to the given bean, without any need for property descriptors.
	 * Implementations should return {@code null} (the default) if they provide a custom {@link #postProcessPropertyValues} implementation, and {@code pvs} otherwise.
	 * In a future version of this interface (with {@link #postProcessPropertyValues} removed),the default implementation will return the given {@code pvs} as-is directly.
	 * @param pvs the property values that the factory is about to apply (never {@code null})
	 * @param bean the bean instance created, but whose properties have not yet been set
	 * @param beanName the name of the bean
	 * @return the actual property values to apply to the given bean (can be the passed-in PropertyValues instance), or {@code null} which proceeds with the existing properties
	 * but specifically continues with a call to {@link #postProcessPropertyValues} (requiring initialized {@code PropertyDescriptor}s for the current bean class)
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @since 5.1
	 * @see #postProcessPropertyValues
	 */
	@Nullable
	default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
		return null;
	}

	/**
	 * 主要是后缀处理通过注解注入属性的
	 * InstantiationAwareBeanPostProcessor # postProcessProperties 方法被实现的了也比较多， 这里主要讲两个 ：
	 * CommonAnnotationBeanPostProcessor ： 主要 注册带有 @Resource 注解的 属性
	 * AutowiredAnnotationBeanPostProcessor ： 主要解决 带有 @Autowired，@Value，@Lookup，@Inject 注解的属性
	 *
	 * postProcessPropertyValues方法对属性值进行修改(这个时候属性值还未被设置，但是我们可以修改原本该设置进去的属性值)。
	 * 如果postProcessAfterInstantiation方法返回false，该方法可能不会被调用。可以在该方法内对属性值进行修改
	 *
	 * Post-process the given property values before the factory applies them to the given bean.
	 * Allows for checking whether all dependencies have been satisfied, for example based on a "Required" annotation on bean property setters.
	 * Also allows for replacing the property values to apply, typically through
	 * creating a new MutablePropertyValues instance based on the original PropertyValues,adding or removing specific values.
	 * The default implementation returns the given {@code pvs} as-is.
	 * @param pvs the property values that the factory is about to apply (never {@code null})
	 * @param pds the relevant property descriptors for the target bean (with ignored dependency types - which the factory handles specifically - already filtered out)
	 * @param bean the bean instance created, but whose properties have not yet been set
	 * @param beanName the name of the bean
	 * @return the actual property values to apply to the given bean (can be the passed-in PropertyValues instance), or {@code null} to skip property population
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see #postProcessProperties
	 * @see org.springframework.beans.MutablePropertyValues
	 * @deprecated as of 5.1, in favor of {@link #postProcessProperties(PropertyValues, Object, String)}
	 */
	@Deprecated
	@Nullable
	default PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
		return pvs;
	}
}
