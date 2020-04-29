

package org.springframework.core.io;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

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
 */
public class DefaultResourceLoader implements ResourceLoader {

	@Nullable
	private ClassLoader classLoader;

	// 为开发者提供了自定义扩展接口ProtocolResolver，开发者可实现该接口定制个性化资源表达式
	private final Set<ProtocolResolver> protocolResolvers = new LinkedHashSet<>(4);

	private final Map<Class<?>, Map<Resource, ?>> resourceCaches = new ConcurrentHashMap<>(4);

	/**
	 * Create a new DefaultResourceLoader.
	 * ClassLoader access will happen using the thread context class loader at the time of this ResourceLoader's initialization.
	 * @see java.lang.Thread#getContextClassLoader()
	 */
	public DefaultResourceLoader() {
		classLoader = ClassUtils.getDefaultClassLoader();
	}

	/**
	 * Create a new DefaultResourceLoader.
	 * @param classLoader the ClassLoader to load class path resources with, or {@code null}
	 * for using the thread context class loader at the time of actual resource access
	 */
	public DefaultResourceLoader(@Nullable ClassLoader classLoader) {
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
			// 基于父类构造
			super(path, classLoader);
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
	 * 1.匹配开头斜杠和异常。getResourceByPath   例如：/WEB-INF/classes/smart-context.xml
	 * 2.匹配格式classpath:。ClassPathResource   classpath:前缀开头的表达式，例如: classpath:smart-context.xml
	 * 3.尝试用FileUrlResource或者UrlResource获取   非“/”开头的表达，例如：WEB-INF/classes/smart-context.xml
	 * 4.url协议，例如：file:/D:/ALANWANG-AIA/Horse-workspace/chapter3/target/classes/smart-context.xml
	*/
	@Override
	public Resource getResource(String location) {
		Assert.notNull(location, "Location must not be null");
		/**
		 * 【1】 步骤1，先用扩展协议解析器解析资源地址并返回
		 *  @see org.springframework.core.io.DefaultResourceLoaderTest#test()
		*/
		for (ProtocolResolver protocolResolver : protocolResolvers) {
			Resource resource = protocolResolver.resolve(location, this);
			if (resource != null) return resource;
		}
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
