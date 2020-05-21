

package org.springframework.core.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the {@link ResourceLoader} interface.
 * Used by {@link ResourceEditor}, and serves as base class for {@link org.springframework.context.support.AbstractApplicationContext}.Can also be used standalone.
 * Will return a {@link UrlResource} if the location value is a URL, and a {@link ClassPathResource} if it is a non-URL path or a "classpath:" pseudo-URL.
 * @since 10.03.2004
 * @see FileSystemResourceLoader
 * @see org.springframework.context.support.ClassPathXmlApplicationContext
 *
 * DefaultResourceLoader功能：
 * 1.增加基于protocolResolvers的拦截增强
 * 2.基于ClassPathResource 、FileUrlResource、UrlResource解析，优先级从左到右
 *
 * 通过ResourceEditor来使用，作为一个基础类来服务于 org.springframework.context.support.AbstractApplicationContext
 *  如果location 的值是一个 URL，将返回一个UrlResource资源；
 *  如果location 的值非URL路径或是一个"classpath:"相对URL，则返回一个ClassPathResource.
 */
public class DefaultResourceLoader implements ResourceLoader {

	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	private ClassLoader classLoader;

	// 为开发者提供了自定义扩展接口ProtocolResolver，开发者可实现该接口定制个性化资源表达式
	private final Set<ProtocolResolver> protocolResolvers = new LinkedHashSet<>(4);
	/** 用于资源缓存的Map的映射集 */
	private final Map<Class<?>, Map<Resource, ?>> resourceCaches = new ConcurrentHashMap<>(4);

	/**
	 * Create a new DefaultResourceLoader.
	 * ClassLoader access will happen using the thread context class loader at the time of this ResourceLoader's initialization.
	 * @see java.lang.Thread#getContextClassLoader()
	 * 底层实现是按以下递进获取，有则返回：当前线程的类加载、ClassUtils.class 的加载器、系统类加载
	 */
	public DefaultResourceLoader() {
		logger.warn("进入 【DefaultResourceLoader】 构造函数 1");
		classLoader = ClassUtils.getDefaultClassLoader();
	}

	/**
	 * Create a new DefaultResourceLoader.
	 * @param classLoader the ClassLoader to load class path resources with, or {@code null}
	 * for using the thread context class loader at the time of actual resource access
	 */
	public DefaultResourceLoader(@Nullable ClassLoader classLoader) {
		logger.warn("进入 【DefaultResourceLoader】 构造函数 2");
		this.classLoader = classLoader;
	}

	/**
	 * Specify the ClassLoader to load class path resources with, or {@code null}
	 * for using the thread context class loader at the time of actual resource access.
	 * The default is that ClassLoader access will happen using the thread context class loader at the time of this ResourceLoader's initialization.
	 */
	public void setClassLoader(@Nullable ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Register the given resolver with this resource loader, allowing for  additional protocols to be handled.
	 * Any such resolver will be invoked ahead of this loader's standard  resolution rules. It may therefore also override any default rules.
	 * @since 4.3
	 * @see #getProtocolResolvers()
	 */
	public void addProtocolResolver(ProtocolResolver resolver) {
		Assert.notNull(resolver, "ProtocolResolver must not be null");
		protocolResolvers.add(resolver);
	}

	/**
	 * Return the collection of currently registered protocol resolvers,allowing for introspection as well as modification.
	 * @since 4.3
	 */
	public Collection<ProtocolResolver> getProtocolResolvers() {
		return protocolResolvers;
	}

	/**
	 * Obtain a cache for the given value type, keyed by {@link Resource}.
	 * @param valueType the value type, e.g. an ASM {@code MetadataReader}
	 * @return the cache {@link Map}, shared at the {@code ResourceLoader} level
	 * @since 5.0
	 */
	@SuppressWarnings("unchecked")
	public <T> Map<Resource, T> getResourceCache(Class<T> valueType) {
		return (Map<Resource, T>) resourceCaches.computeIfAbsent(valueType, key -> new ConcurrentHashMap<>());
	}

	/**
	 * Clear all resource caches in this resource loader.
	 * @since 5.0
	 * @see #getResourceCache
	 */
	public void clearResourceCaches() {
		resourceCaches.clear();
	}

	/**
	 * Return a Resource handle for the resource at the given path.
	 * The default implementation supports class path locations. This should be appropriate for standalone implementations but can be overridden,
	 * e.g. for implementations targeted at a Servlet container.
	 * @param path the path to the resource
	 * @return the corresponding Resource handle
	 * @see ClassPathResource
	 * @see org.springframework.context.support.FileSystemXmlApplicationContext#getResourceByPath
	 * @see org.springframework.web.context.support.XmlWebApplicationContext#getResourceByPath
	 */
	protected Resource getResourceByPath(String path) {
		return new ClassPathContextResource(path, getClassLoader());
	}

	/**
	 * ClassPathResource that explicitly expresses a context-relative path through implementing the ContextResource interface.
	 * 通过实现ContextResource接口显式表达上下文相关路径的ClassPathResource，此方法重点在于实现ContextResource
	 */
	protected static class ClassPathContextResource extends ClassPathResource implements ContextResource {
		public ClassPathContextResource(String path, @Nullable ClassLoader classLoader) {
			super(path, classLoader);// 基于父类构造
		}
		@Override
		public String getPathWithinContext() {
			return getPath();
		}
		// 基于当前路径的重新构建对象
		@Override
		public Resource createRelative(String relativePath) {
			String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
			return new ClassPathContextResource(pathToUse, getClassLoader());
		}
	}

	//---------------------------------------------------------------------
	// Implementation of 【ResourceLoader】 interface
	//---------------------------------------------------------------------

	/**
	 * 默认查找方式主要分为三种
	 * 1.匹配开头斜杠和异常： getResourceByPath   例如：/WEB-INF/classes/smart-context.xml
	 * 2.匹配classpath: 格式：ClassPathResource   classpath:前缀开头的表达式，例如: classpath:smart-context.xml
	 * 3.匹配 非“/”开头的表达：尝试用FileUrlResource或者UrlResource获取  ，例如：WEB-INF/classes/smart-context.xml
	 * 4.url协议，例如：file:/D:/ALANWANG-AIA/Horse-workspace/chapter3/target/classes/smart-context.xml
	 *
	 * 默认情况下，DefaultResourceLoader类中的protocolResolvers成员变量是一个空的Set，
	 * 即默认情况下是没有 ProtocolResolver 可以去解析的，只能走ClassPath和URL两种方式获得Resource
	 * 若想使用 ProtocolResolver 自定义方式解析，需要自己写实现类重写接口的resolve()方法。并通过addProtocolResolver()初始化
	*/
	@Override
	public Resource getResource(String location) {
		Assert.notNull(location, "Location must not be null");
		/**
		 * 【1】 步骤1，先用扩展协议解析器解析资源地址，如果解析成功则直接返回，无需再使用默认方式进行解析。
		 *  @see org.springframework.core.io.DefaultResourceLoaderTest#test()
		*/
		for (ProtocolResolver protocolResolver : protocolResolvers) {
			Resource resource = protocolResolver.resolve(location, this);
			if (resource != null) return resource;
		}
		// 程序能够运行到这里，说明用户没有配置自定义解析器，使用默认方式进行解析
		if (location.startsWith("/")) { // 【2】
			return getResourceByPath(location);
		}else if (location.startsWith(CLASSPATH_URL_PREFIX)) { // 【3】
			return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
		}else {
			try {
				// 【4】 Try to parse the location as a URL...
				URL url = new URL(location);
				return (ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url));
			}catch (MalformedURLException ex) {
				// 【5】 No URL -> resolve as resource path.
				return getResourceByPath(location);
			}
		}
	}

	/**
	 * 返回用于加载类资源的类加载器。 如果为null，则通过ClassUtils.getDefaultClassLoader()重新获取。
	 * Return the ClassLoader to load class path resources with.
	 * Will get passed to ClassPathResource's constructor for all ClassPathResource objects created by this resource loader.
	 * @see ClassPathResource
	 */
	@Override
	@Nullable
	public ClassLoader getClassLoader() {
		return (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
	}
}
