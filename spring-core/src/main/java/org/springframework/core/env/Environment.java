

package org.springframework.core.env;

/**
 * Interface representing the environment in which the current application is running.
 * Models two key aspects of the application environment: <em>profiles</em> and <em>properties</em>.
 * Methods related to property access are exposed via the {@link PropertyResolver} superinterface.
 * A <em>profile</em> is a named, logical group of bean definitions to be registered  with the container only if the given profile is <em>active</em>.
 * Beans may be assigned to a profile whether defined in XML or via annotations;
 * see the spring-beans 3.1 schema or the {@link org.springframework.context.annotation.Profile @Profile} annotation for syntax details.
 * The role of the {@code Environment} object with relation to profiles is in determining which profiles (if any) are currently {@linkplain #getActiveProfiles active},
 * and which profiles (if any) should be {@linkplain #getDefaultProfiles active by default}.
 * <em>Properties</em> play an important role in almost all applications, and may originate from a variety of sources:
 * properties files, JVM system properties, system environment variables, JNDI, servlet context parameters, ad-hoc Properties objects,Maps, and so on.
 * The role of the environment object with relation to properties is to provide the user with a convenient service interface for configuring property sources  and resolving properties from them.
 * Beans managed within an {@code ApplicationContext} may register to be {@link org.springframework.context.EnvironmentAware EnvironmentAware} or {@code @Inject}
 *  the {@code Environment} in order to query profile state or resolve properties directly.
 * In most cases, however, application-level beans should not need to interact with the
 * {@code Environment} directly but instead may have to have {@code ${...}} property  values replaced by a property placeholder configurer such as
 * {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer PropertySourcesPlaceholderConfigurer},
 * which itself is {@code EnvironmentAware} and as of Spring 3.1 is registered by default when using {@code <context:property-placeholder/>}.
 * Configuration of the environment object must be done through the {@code ConfigurableEnvironment} interface,
 * returned from all {@code AbstractApplicationContext} subclass {@code getEnvironment()} methods.
 * See {@link ConfigurableEnvironment} Javadoc for usage examples demonstrating manipulation of property sources prior to application context {@code refresh()}.
 * @since 3.1
 * @see PropertyResolver
 * @see EnvironmentCapable
 * @see ConfigurableEnvironment
 * @see AbstractEnvironment
 * @see StandardEnvironment
 * @see org.springframework.context.EnvironmentAware
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#setEnvironment
 * @see org.springframework.context.support.AbstractApplicationContext#createEnvironment
 * 这个接口代表了当前应用正在运行的环境，为应用的两个重要方面建立抽象模型 【profiles】和【properties】。
 * 关于属性访问的方法通过父接口PropertyResolver暴露给客户端使用，本接口主要是扩展出访问【profiles】相关的接口。
 */
public interface Environment extends PropertyResolver {

	/**
	 * Return the set of profiles explicitly made active for this environment.
	 * Profiles are used for creating logical groupings of bean definitions to be registered conditionally,
	 * for example based on deployment environment.
	 * Profiles can be activated by setting {@linkplain AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME "spring.profiles.active"} as a system property or by calling
	 * {@link ConfigurableEnvironment#setActiveProfiles(String...)}.
	 * If no profiles have explicitly been specified as active, then any {@linkplain #getDefaultProfiles() default profiles} will automatically be activated.
	 * @see #getDefaultProfiles
	 * @see ConfigurableEnvironment#setActiveProfiles
	 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 * 就算被激活  也是支持同时激活多个profiles的~，设置的key是：spring.profiles.active
	 */
	String[] getActiveProfiles();

	/**
	 * Return the set of profiles to be active by default when no active profiles have been set explicitly.
	 * @see #getActiveProfiles
	 * @see ConfigurableEnvironment#setDefaultProfiles
	 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
	 * 默认的也可以有多个  key为：spring.profiles.default
	 */
	String[] getDefaultProfiles();

	/**
	 * Return whether one or more of the given profiles is active or, in the case of no explicit active profiles,
	 * whether one or more of the given profiles is included in the set of default profiles.
	 * If a profile begins with '!' the logic is inverted,i.e. the method will return {@code true} if the given profile is <em>not</em> active.
	 * For example, {@code env.acceptsProfiles("p1", "!p2")} will return {@code true} if  profile 'p1' is active or 'p2' is not active.
	 * @throws IllegalArgumentException if called with zero arguments or if any profile is {@code null}, empty, or whitespace only
	 * @see #getActiveProfiles
	 * @see #getDefaultProfiles
	 * @see #acceptsProfiles(Profiles)
	 * @deprecated as of 5.1 in favor of {@link #acceptsProfiles(Profiles)}
	 * 看看传入的profiles是否是激活的~~~~  支持!表示不激活
	 */
	@Deprecated
	boolean acceptsProfiles(String... profiles);

	/**
	 * Spring5.1后提供的  用于替代上面方法   Profiles是Spring5.1才有的一个函数式接口~
	 * Return whether the {@linkplain #getActiveProfiles() active profiles} match the given {@link Profiles} predicate.
	 */
	boolean acceptsProfiles(Profiles profiles);
}
