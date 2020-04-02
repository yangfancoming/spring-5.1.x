

package org.springframework.instrument.classloading.weblogic;

import java.lang.instrument.ClassFileTransformer;

import org.springframework.core.OverridingClassLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * {@link LoadTimeWeaver} implementation for WebLogic's instrumentable
 * ClassLoader.
 *
 * <b>NOTE:</b> Requires BEA WebLogic version 10 or higher.
 *
 * @author Costin Leau

 * @since 2.5
 */
public class WebLogicLoadTimeWeaver implements LoadTimeWeaver {

	private final WebLogicClassLoaderAdapter classLoader;


	/**
	 * Creates a new instance of the {@link WebLogicLoadTimeWeaver} class using
	 * the default {@link ClassLoader class loader}.
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 */
	public WebLogicLoadTimeWeaver() {
		this(ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Creates a new instance of the {@link WebLogicLoadTimeWeaver} class using
	 * the supplied {@link ClassLoader}.
	 * @param classLoader the {@code ClassLoader} to delegate to for weaving
	 */
	public WebLogicLoadTimeWeaver(@Nullable ClassLoader classLoader) {
		Assert.notNull(classLoader, "ClassLoader must not be null");
		this.classLoader = new WebLogicClassLoaderAdapter(classLoader);
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
