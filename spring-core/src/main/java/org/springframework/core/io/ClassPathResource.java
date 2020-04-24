

package org.springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * {@link Resource} implementation for class path resources. Uses either a given {@link ClassLoader} or a given {@link Class} for loading resources.
 * Supports resolution as {@code java.io.File} if the class path resource resides in the file system, but not for resources in a JAR.
 * Always supports resolution as URL.
 * @since 28.12.2003
 * @see ClassLoader#getResourceAsStream(String)
 * @see Class#getResourceAsStream(String)
 */
public class ClassPathResource extends AbstractFileResolvingResource {

	private final String path;

	@Nullable
	private ClassLoader classLoader;

	@Nullable
	private Class<?> clazz;

	/**
	 * Create a new {@code ClassPathResource} for {@code ClassLoader} usage.
	 * A leading slash will be removed, as the ClassLoader resource access  methods will not accept it.
	 * The thread context class loader will be used for loading the resource.
	 * @param path the absolute path within the class path
	 * @see java.lang.ClassLoader#getResourceAsStream(String)
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 */
	public ClassPathResource(String path) {
		this(path, (ClassLoader) null);
	}

	/**
	 * Create a new {@code ClassPathResource} for {@code ClassLoader} usage.
	 * A leading slash will be removed, as the ClassLoader resource access methods will not accept it.
	 * @param path the absolute path within the classpath
	 * @param classLoader the class loader to load the resource with, or {@code null} for the thread context class loader
	 * @see ClassLoader#getResourceAsStream(String)
	 */
	public ClassPathResource(String path, @Nullable ClassLoader classLoader) {
		Assert.notNull(path, "Path must not be null");
		// 转换路径
		String pathToUse = StringUtils.cleanPath(path);
		// 如果路径以"/"开头,则截取开头"/"以后字符做为路径
		if (pathToUse.startsWith("/")) pathToUse = pathToUse.substring(1);
		// 保存转换后的路径
		this.path = pathToUse;
		// 保存类加载器
		this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Create a new {@code ClassPathResource} for {@code Class} usage.
	 * The path can be relative to the given class, or absolute within the classpath via a leading slash.
	 * @param path relative or absolute path within the class path
	 * @param clazz the class to load resources with
	 * @see java.lang.Class#getResourceAsStream
	 */
	public ClassPathResource(String path, @Nullable Class<?> clazz) {
		Assert.notNull(path, "Path must not be null");
		this.path = StringUtils.cleanPath(path);
		this.clazz = clazz;
	}

	/**
	 * Create a new {@code ClassPathResource} with optional {@code ClassLoader}  and {@code Class}. Only for internal usage.
	 * @param path relative or absolute path within the classpath
	 * @param classLoader the class loader to load the resource with, if any
	 * @param clazz the class to load resources with, if any
	 * @deprecated as of 4.3.13, in favor of selective use of {@link #ClassPathResource(String, ClassLoader)} vs {@link #ClassPathResource(String, Class)}
	 */
	@Deprecated
	protected ClassPathResource(String path, @Nullable ClassLoader classLoader, @Nullable Class<?> clazz) {
		this.path = StringUtils.cleanPath(path);
		this.classLoader = classLoader;
		this.clazz = clazz;
	}

	// Return the path for this resource (as resource path within the class path).
	public final String getPath() {
		return path;
	}

	// Return the ClassLoader that this resource will be obtained from.
	@Nullable
	public final ClassLoader getClassLoader() {
		return (clazz != null ? clazz.getClassLoader() : classLoader);
	}

	/**
	 * This implementation checks for the resolution of a resource URL.
	 * @see java.lang.ClassLoader#getResource(String)
	 * @see java.lang.Class#getResource(String)
	 */
	@Override
	public boolean exists() {
		return (resolveURL() != null);
	}

	/**
	 * Resolves a URL for the underlying class path resource.
	 * @return the resolved URL, or {@code null} if not resolvable
	 */
	@Nullable
	protected URL resolveURL() {
		if (clazz != null) {
			return clazz.getResource(path);
		}else if (classLoader != null) {
			return classLoader.getResource(path);
		}else {
			return ClassLoader.getSystemResource(path);
		}
	}

	/**
	 * This implementation opens an InputStream for the given class path resource.
	 * @see java.lang.ClassLoader#getResourceAsStream(String)
	 * @see java.lang.Class#getResourceAsStream(String)
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		InputStream is;
		// ①如果类对象不为null,则使用类对象信息的getResourceAsStream获取输入流
		if (clazz != null) {
			is = clazz.getResourceAsStream(path);
		}else if (classLoader != null) {
		// ②如果类加载器不为null,则使用类加载器的getResourceAsStream获取输入流
			is = classLoader.getResourceAsStream(path);
		}else {
			// ③否则使用ClassLoader类的getSystemResourceAsStream方法获取输入流
			is = ClassLoader.getSystemResourceAsStream(path);
		}
		//以上三种方法都无法获取到输入流的话,那么说明文件不存在,抛出异常
		if (is == null) throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist or didn't find the right ClassLoader");// -modify
		return is;
	}

	/**
	 * This implementation returns a URL for the underlying class path resource,if available.
	 * @see java.lang.ClassLoader#getResource(String)
	 * @see java.lang.Class#getResource(String)
	 */
	@Override
	public URL getURL() throws IOException {
		URL url = resolveURL();
		if (url == null) throw new FileNotFoundException(getDescription() + " cannot be resolved to URL because it does not exist");
		return url;
	}

	/**
	 * This implementation creates a ClassPathResource, applying the given path relative to the path of the underlying resource of this descriptor.
	 * @see org.springframework.util.StringUtils#applyRelativePath(String, String)
	 */
	@Override
	public Resource createRelative(String relativePath) {
		String pathToUse = StringUtils.applyRelativePath(path, relativePath);
		return (clazz != null ? new ClassPathResource(pathToUse, clazz) : new ClassPathResource(pathToUse, classLoader));
	}

	/**
	 * This implementation returns the name of the file that this class path resource refers to.
	 * @see org.springframework.util.StringUtils#getFilename(String)
	 */
	@Override
	@Nullable
	public String getFilename() {
		return StringUtils.getFilename(path);
	}

	// This implementation returns a description that includes the class path location.
	@Override
	public String getDescription() {
		StringBuilder builder = new StringBuilder("class path resource [");
		String pathToUse = path;
		if (clazz != null && !pathToUse.startsWith("/")) {
			builder.append(ClassUtils.classPackageAsResourcePath(clazz));
			builder.append('/');
		}
		if (pathToUse.startsWith("/")) {
			pathToUse = pathToUse.substring(1);
		}
		builder.append(pathToUse);
		builder.append(']');
		return builder.toString();
	}

	// This implementation compares the underlying class path locations.
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof ClassPathResource)) {
			return false;
		}
		ClassPathResource otherRes = (ClassPathResource) other;
		return (path.equals(otherRes.path) &&
				ObjectUtils.nullSafeEquals(classLoader, otherRes.classLoader) &&
				ObjectUtils.nullSafeEquals(clazz, otherRes.clazz));
	}

	// This implementation returns the hash code of the underlying class path location.
	@Override
	public int hashCode() {
		return path.hashCode();
	}
}
