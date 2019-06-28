

package org.springframework.instrument.classloading;

import org.springframework.core.OverridingClassLoader;
import org.springframework.lang.Nullable;

/**
 * ClassLoader that can be used to load classes without bringing them
 * into the parent loader. Intended to support JPA "temp class loader"
 * requirement, but not JPA-specific.
 *
 * @author Rod Johnson
 * @since 2.0
 */
public class SimpleThrowawayClassLoader extends OverridingClassLoader {

	static {
		ClassLoader.registerAsParallelCapable();
	}


	/**
	 * Create a new SimpleThrowawayClassLoader for the given ClassLoader.
	 * @param parent the ClassLoader to build a throwaway ClassLoader for
	 */
	public SimpleThrowawayClassLoader(@Nullable ClassLoader parent) {
		super(parent);
	}

}
