

package org.springframework.instrument.classloading.websphere;

import java.lang.instrument.ClassFileTransformer;

import org.springframework.core.OverridingClassLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * {@link LoadTimeWeaver} implementation for WebSphere's instrumentable ClassLoader.
 * Compatible with WebSphere 7 as well as 8 and 9.
 *
 * @author Costin Leau
 * @since 3.1
 */
public class WebSphereLoadTimeWeaver implements LoadTimeWeaver {

	private final WebSphereClassLoaderAdapter classLoader;


	/**
	 * Create a new instance of the {@link WebSphereLoadTimeWeaver} class using
	 * the default {@link ClassLoader class loader}.
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 */
	public WebSphereLoadTimeWeaver() {
		this(ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Create a new instance of the {@link WebSphereLoadTimeWeaver} class using
	 * the supplied {@link ClassLoader}.
	 * @param classLoader the {@code ClassLoader} to delegate to for weaving
	 */
	public WebSphereLoadTimeWeaver(@Nullable ClassLoader classLoader) {
		Assert.notNull(classLoader, "ClassLoader must not be null");
		this.classLoader = new WebSphereClassLoaderAdapter(classLoader);
	}


	@Override
	public void addTransformer(ClassFileTransformer transformer) {
		this.classLoader.addTransformer(transformer);
	}

	@Override
	public ClassLoader getInstrumentableClassLoader() {
		return this.classLoader.getClassLoader();
	}

	@Override
	public ClassLoader getThrowawayClassLoader() {
		return new OverridingClassLoader(this.classLoader.getClassLoader(),
				this.classLoader.getThrowawayClassLoader());
	}

}
