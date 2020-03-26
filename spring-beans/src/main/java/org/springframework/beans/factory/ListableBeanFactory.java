

package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * Extension of the {@link BeanFactory} interface to be implemented by bean factories that can enumerate all their bean instances,
 * rather than attempting bean lookup by name one by one as requested by clients.
 * BeanFactory implementations that preload all their bean definitions (such as XML-based factories) may implement this interface.
 *
 * 	 * 扩展BeanFactory接口,提供所有bean 实例的枚举,不再需要客户端通过一个个bean name查找.
 * 	 * BeanFactory实现类预加载bean定义(如通过实现xml的工厂)需要实现这个接口.
 *
 * If this is a {@link HierarchicalBeanFactory}, the return values will <i>not</i>
 * take any BeanFactory hierarchy into account, but will relate only to the beans
 * defined in the current factory. Use the {@link BeanFactoryUtils} helper class
 * to consider beans in ancestor factories too.
 *
 * 如果一样实现了HierarchicalBeanFactory,返回值不会考虑父类BeanFactory,
 * 只考虑当前factory定义的类.当然也可以使用BeanFactoryUtils辅助类来查找祖先工厂中的类.
 *
 * The methods in this interface will just respect bean definitions of this factory.
 * They will ignore any singleton beans that have been registered by other means like
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}'s
 * {@code registerSingleton} method, with the exception of
 * {@code getBeanNamesOfType} and {@code getBeansOfType} which will check
 * such manually registered singletons too. Of course, BeanFactory's {@code getBean}
 * does allow transparent access to such special beans as well. However, in typical
 * scenarios, all beans will be defined by external bean definitions anyway, so most
 * applications don't need to worry about this differentiation.
 *
 * 这个接口中的方法只会考虑本factory定义的bean.这些方法会忽略ConfigurableBeanFactory的registerSingleton注册的单例bean,
 * getBeanNamesOfType和getBeansOfType是例外,一样会考虑手动注册的单例.
 * 当然BeanFactory的getBean一样可以透明访问这些特殊bean.当然在典型情况下,所有的bean都是由external bean定义,所以应用不需要顾虑这些差别.
 *
 * <b>NOTE:</b> With the exception of {@code getBeanDefinitionCount}
 * and {@code containsBeanDefinition}, the methods in this interface
 * are not designed for frequent invocation. Implementations may be slow.
 * 注意:getBeanDefinitionCount和containsBeanDefinition的实现方法因为效率比较低,并不是供频繁调用的.
 * @since 16 April 2001
 * @see HierarchicalBeanFactory
 * @see BeanFactoryUtils
 *
 * 根据各种条件获取bean的配置清单。
 *
 * 扩展了BeanFactory接口,提供了对bean的枚举能力,即可以返回bean的实例集合,而不用像BeanFactory只能返回单个bean的实例
 * 注意:如果存在父容器的话该接口不会考虑父容器中的bean,只会返回当前容器中的bean
 *
 * containsBeanDefinition
 * findAnnotationOnBean
 * getBeanDefinitionCount
 * getBeanDefinitionNames
 * getBeanNamesForAnnotation
 * getBeanNamesForType
 * getBeansOfType
 * getBeansWithAnnotation
 */
public interface ListableBeanFactory extends BeanFactory {

	//-------------------------------------------------------------------------
	// 提供容器内bean实例的枚举功能.这边不会考虑父容器内的实例
	//-------------------------------------------------------------------------

	/**
	 * Check if this bean factory contains a bean definition with the given name.
	 * Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by other means than bean definitions.
	 * @param beanName the name of the bean to look for
	 * @return if this bean factory contains a bean definition with the given name
	 * @see #containsBean
	 */
	boolean containsBeanDefinition(String beanName);// 检查bean factory是否含有给定name的bean定义  忽略父factory和其他factory注册的单例bean

	/**
	 * 查看此BeanFactory中包含的Bean数量  一样不考虑父factory和其他factory注册的单例bean
	 * Return the number of beans defined in the factory.
	 * Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by other means than bean definitions.
	 * @return the number of beans defined in the factory
	 */
	int getBeanDefinitionCount();

	/**
	 * Return the names of all beans defined in this factory.
	 * Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * @return the names of all beans defined in this factory,
	 * or an empty array if none defined
	 */
	String[] getBeanDefinitionNames();  // 获取工厂中定义的所有bean 的name  一样不考虑父factory和其他factory注册的单例bean

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * <b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * This version of {@code getBeanNamesForType} matches all kinds of beans,
	 * be it singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for {@code getBeanNamesForType(type, true, true)}.
	 * Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * @param type the generically typed class or interface to match
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 *
	 * 获取给定类型的bean names(包括子类),通过bean 定义或者FactoryBean的getObjectType判断.
	 * 注意:这个方法仅检查顶级bean.它不会检查嵌套的bean.
	 * FactoryBean创建的bean会匹配为FactoryBean而不是原始类型.
	 * 一样不会考虑父factory中的bean,可以使用BeanFactoryUtils中的beanNamesForTypeIncludingAncestors.
	 * 其他方式注册的单例这边会纳入判断.
	 * 这个版本的getBeanNamesForType会匹配所有类型的bean,包括单例,原型,FactoryBean.在大多数实现中返回结果跟getBeanNamesOfType(type,true,true)一样.
	 * 返回的bean names会根据backend 配置的进行排序.
	 *
	 * @since 4.2
	 * @see #isTypeMatch(String, ResolvableType)
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, ResolvableType)
	 */
	String[] getBeanNamesForType(ResolvableType type);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * <b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * This version of {@code getBeanNamesForType} matches all kinds of beans,
	 * be it singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for {@code getBeanNamesForType(type, true, true)}.
	 * Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * @param type the class or interface to match, or {@code null} for all bean names
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType} in the case of FactoryBeans.
	 * <b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * @param type the class or interface to match, or {@code null} for all bean names
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * Return the bean instances that match the given object type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType} in the case of FactoryBeans.
	 * <b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i> check nested beans which might match the specified type as well.
	 * Does consider objects created by FactoryBeans, which means that FactoryBeans will get initialized.
	 * If the object created by the FactoryBean doesn't match, the raw FactoryBean itself will be matched against the type.
	 * Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors} to include beans in ancestor factories too.
	 * Note: Does <i>not</i> ignore singleton beans that have been registered by other means than bean definitions.
	 * This version of getBeansOfType matches all kinds of beans, be it singletons, prototypes, or FactoryBeans.
	 * In most implementations, the  result will be the same as for {@code getBeansOfType(type, true, true)}.
	 * The Map returned by this method should always return bean names and
	 * corresponding bean instances <i>in the order of definition</i> in the backend configuration, as far as possible.
	 * @param type the class or interface to match, or {@code null} for all concrete beans
	 * @return a Map with the matching beans, containing the bean names as keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 1.1.2
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException;

	/**
	 * Return the bean instances that match the given object type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType} in the case of FactoryBeans.
	 * <b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,which means that FactoryBeans will get initialized.
	 * If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * If "allowEagerInit" is not set, only raw FactoryBeans will be checked (which doesn't require initialization of each FactoryBean).
	 * Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors} to include beans in ancestor factories too.
	 * Note: Does <i>not</i> ignore singleton beans that have been registered by other means than bean definitions.
	 * The Map returned by this method should always return bean names and
	 * corresponding bean instances <i>in the order of definition</i> in the backend configuration, as far as possible.
	 * @param type the class or interface to match, or {@code null} for all concrete beans
	 * @param includeNonSingletons whether to include prototype or scoped beans too or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return a Map with the matching beans, containing the bean names as  keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException;

	/**
	 * Find all names of beans which are annotated with the supplied {@link Annotation} type, without creating corresponding bean instances yet.
	 * Note that this method considers objects created by FactoryBeans, which means
	 * that FactoryBeans will get initialized in order to determine their object type.
	 * @param annotationType the type of annotation to look for
	 * @return the names of all matching beans
	 * @since 4.0
	 * @see #findAnnotationOnBean
	 */
	String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType); // 找到所有带有指定注解类型的Bean

	/**
	 * Find all beans which are annotated with the supplied {@link Annotation} type,
	 * returning a Map of bean names with corresponding bean instances.
	 * Note that this method considers objects created by FactoryBeans, which means
	 * that FactoryBeans will get initialized in order to determine their object type.
	 * @param annotationType the type of annotation to look for
	 * @return a Map with the matching beans, containing the bean names as  keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 3.0
	 * @see #findAnnotationOnBean
	 */
	// 找到所有带有指定注解的Bean，返回一个以Bean的name为键，其对应的Bean实例为值的Map
	Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

	/**
	 * Find an {@link Annotation} of {@code annotationType} on the specified bean,
	 * traversing its interfaces and super classes if no annotation can be found on the given class itself.
	 * @param beanName the name of the bean to look for annotations on
	 * @param annotationType the type of annotation to look for
	 * @return the annotation of the given type if found, or {@code null} otherwise
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 3.0
	 * @see #getBeanNamesForAnnotation
	 * @see #getBeansWithAnnotation
	 */
	// 在指定name对应的Bean上找指定的注解，如果没有找到的话，去指定Bean的父类或者父接口上查找
	@Nullable
	<A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException;


}
