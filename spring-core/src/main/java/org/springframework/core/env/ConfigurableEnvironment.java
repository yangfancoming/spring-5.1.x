

package org.springframework.core.env;

import java.util.Map;

/**
 * Configuration interface to be implemented by most if not all {@link Environment} types.
 * Provides facilities for setting active and default profiles and manipulating underlying property sources.
 * Allows clients to set and validate required properties, customize the conversion service and more through the {@link ConfigurablePropertyResolver} superinterface.
 *
 * <h2>Manipulating property sources</h2>
 * Property sources may be removed, reordered, or replaced; and additional
 * property sources may be added using the {@link MutablePropertySources}
 * instance returned from {@link #getPropertySources()}.
 * The following examples are against the {@link StandardEnvironment} implementation of {@code ConfigurableEnvironment},
 * but are generally applicable to any implementation,though particular default property sources may differ.
 *
 * <h4>Example: adding a new property source with highest search priority</h4>
 * ConfigurableEnvironment environment = new StandardEnvironment();
 * MutablePropertySources propertySources = environment.getPropertySources();
 * Map&lt;String, String&gt; myMap = new HashMap&lt;&gt;();
 * myMap.put("xyz", "myValue");
 * propertySources.addFirst(new MapPropertySource("MY_MAP", myMap));
 *
 * <h4>Example: removing the default system properties property source</h4>
 * MutablePropertySources propertySources = environment.getPropertySources();
 * propertySources.remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)
 *
 * <h4>Example: mocking the system environment for testing purposes</h4>
 * MutablePropertySources propertySources = environment.getPropertySources();
 * MockPropertySource mockEnvVars = new MockPropertySource().withProperty("xyz", "myValue");
 * propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars);
 *
 * When an {@link Environment} is being used by an {@code ApplicationContext}, it is important that any such {@code PropertySource} manipulations be performed
 * <em>before</em> the context's {@link org.springframework.context.support.AbstractApplicationContext#refresh() refresh()}
 * method is called. This ensures that all property sources are available during the container bootstrap process, including use by
 * {@linkplain org.springframework.context.support.PropertySourcesPlaceholderConfigurer property placeholder configurers}.
 * @since 3.1
 * @see StandardEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment
 * 扩展出了修改和配置profiles的一系列方法，包括用户自定义的和系统相关的属性。所有的环境实现类也都是它的实现~
 */
public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {

	/**
	 * Specify the set of profiles active for this {@code Environment}.
	 * Profiles are evaluated during container bootstrap to determine whether bean definitions should be registered with the container.
	 * Any existing active profiles will be replaced with the given arguments; call with zero arguments to clear the current set of active profiles.
	 * Use {@link #addActiveProfile} to add a profile while preserving the existing set.
	 * @throws IllegalArgumentException if any profile is null, empty or whitespace-only
	 * @see #addActiveProfile
	 * @see #setDefaultProfiles
	 * @see org.springframework.context.annotation.Profile
	 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 */
	void setActiveProfiles(String... profiles);

	/**
	 * Add a profile to the current set of active profiles.
	 * @throws IllegalArgumentException if the profile is null, empty or whitespace-only
	 * @see #setActiveProfiles
	 */
	void addActiveProfile(String profile);

	/**
	 * Specify the set of profiles to be made active by default if no other profiles  are explicitly made active through {@link #setActiveProfiles}.
	 * @throws IllegalArgumentException if any profile is null, empty or whitespace-only
	 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
	 */
	void setDefaultProfiles(String... profiles);

	/**
	 * Return the {@link PropertySources} for this {@code Environment} in mutable form,
	 * allowing for manipulation of the set of {@link PropertySource} objects that should be searched when resolving properties against this {@code Environment} object.
	 * The various {@link MutablePropertySources} methods such as
	 * {@link MutablePropertySources#addFirst addFirst},
	 * {@link MutablePropertySources#addLast addLast},
	 * {@link MutablePropertySources#addBefore addBefore} and
	 * {@link MutablePropertySources#addAfter addAfter} allow for fine-grained control over property source ordering.
	 * This is useful, for example, in ensuring that  certain user-defined property sources have search precedence over default property
	 * sources such as the set of system properties or the set of system environment variables.
	 * @see AbstractEnvironment#customizePropertySources
	 * 获取到所有的属性源~  MutablePropertySources表示可变的属性源们~~~ 它是一个聚合的  持有List<PropertySource<?>>
	 * 这样获取出来后，我们可以add或者remove我们自己自定义的属性源了~
	 */
	MutablePropertySources getPropertySources();

	/**
	 * Return the value of {@link System#getProperties()} if allowed by the current {@link SecurityManager},
	 * otherwise return a map implementation that will attempt to access individual keys using calls to {@link System#getProperty(String)}.
	 * Note that most {@code Environment} implementations will include this system properties map as a default {@link PropertySource} to be searched.
	 * Therefore, it is recommended that this method not be used directly unless bypassing other property sources is expressly intended.
	 * Calls to {@link Map#get(Object)} on the Map returned will never throw {@link IllegalAccessException}; in cases where the SecurityManager forbids access
	 * to a property, {@code null} will be returned and an INFO-level log message will be issued noting the exception.
	 */
	Map<String, Object> getSystemProperties();

	/**
	 * Return the value of {@link System#getenv()} if allowed by the current {@link SecurityManager},
	 * otherwise return a map implementation that will attempt to access individual keys using calls to {@link System#getenv(String)}.
	 * Note that most {@link Environment} implementations will include this system environment map as a default {@link PropertySource} to be searched.
	 * Therefore, it is recommended that this method not be used directly unless bypassing other property sources is expressly intended.
	 * Calls to {@link Map#get(Object)} on the Map returned will never throw {@link IllegalAccessException}; in cases where the SecurityManager forbids access
	 * to a property, {@code null} will be returned and an INFO-level log message will be issued noting the exception.
	 */
	Map<String, Object> getSystemEnvironment();

	/**
	 * Append the given parent environment's active profiles, default profiles and property sources to this (child) environment's respective collections of each.
	 * For any identically-named {@code PropertySource} instance existing in both parent and child, the child instance is to be preserved and the parent instance discarded.
	 * This has the effect of allowing overriding of property sources by the child as well as avoiding redundant searches through common property source types,
	 * e.g. system environment and system properties.
	 * Active and default profile names are also filtered for duplicates, to avoid confusion and redundant storage.
	 * The parent environment remains unmodified in any case.
	 * Note that any changes to the parent environment occurring after the call to {@code merge} will not be reflected in the child.
	 * Therefore, care should be taken to configure parent property sources and profile information prior to calling {@code merge}.
	 * @param parent the environment to merge with
	 * @since 3.1.2
	 * @see org.springframework.context.support.AbstractApplicationContext#setParent
	 * 合并两个环境配置信息~  此方法唯一实现在AbstractEnvironment上
	 */
	void merge(ConfigurableEnvironment parent);
}
