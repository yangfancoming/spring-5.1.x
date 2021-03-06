

package org.springframework.core.io;

import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

/**
 * Strategy interface for loading resources (e.. class path or file system  resources).
 * An {@link org.springframework.context.ApplicationContext} is required to provide this functionality, plus extended {@link org.springframework.core.io.support.ResourcePatternResolver} support.
 * {@link DefaultResourceLoader} is a standalone implementation that is usable outside an ApplicationContext, also used by {@link ResourceEditor}.
 * Bean properties of type Resource and Resource array can be populated from Strings when running in an ApplicationContext, using the particular context's resource loading strategy.
 * @since 10.03.2004
 * @see Resource
 * @see org.springframework.core.io.support.ResourcePatternResolver
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 * 定义资源加载器，主要应用于根据给定的资源文件地址返回对应的Resource
 * ResourceLoader接口就是通过getresource方法来new各种resource对象
 */
public interface ResourceLoader {

	/** Pseudo URL prefix for loading from the class path: "classpath:".   用于从类路径加载的伪URL前缀：“classpath：” */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

	/**
	 * Return a Resource handle for the specified resource location.
	 * The handle should always be a reusable resource descriptor,allowing for multiple {@link Resource#getInputStream()} calls.
	 * <li>Must support fully qualified URLs, e.g. "file:C:/test.dat".
	 * <li>Must support classpath pseudo-URLs, e.g. "classpath:test.dat".
	 * <li>Should support relative file paths, e.g. "WEB-INF/test.dat".
	 * (This will be implementation-specific, typically provided by an ApplicationContext implementation.)
	 * Note that a Resource handle does not imply an existing resource; you need to invoke {@link Resource#exists} to check for existence.
	 * @param location the resource location
	 * @return a corresponding Resource handle (never {@code null})
	 * @see #CLASSPATH_URL_PREFIX
	 * @see Resource#exists()
	 * @see Resource#getInputStream()
	 * 返回指定资源位置的资源句柄
	 */
	Resource getResource(String location);

	/**
	 * Expose the ClassLoader used by this ResourceLoader.
	 * Clients which need to access the ClassLoader directly can do so in a uniform manner with the ResourceLoader, rather than relying on the thread context ClassLoader.
	 * @return the ClassLoader (only {@code null} if even the system ClassLoader isn't accessible)
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 * @see org.springframework.util.ClassUtils#forName(String, ClassLoader)
	 * 暴露此ResourceLoader使用的ClassLoader。
	 */
	@Nullable
	ClassLoader getClassLoader();
}
