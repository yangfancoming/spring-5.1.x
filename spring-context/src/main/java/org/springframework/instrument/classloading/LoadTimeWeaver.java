

package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;

/**
 * Defines the contract for adding one or more {@link ClassFileTransformer ClassFileTransformers} to a {@link ClassLoader}.
 * Implementations may operate on the current context {@code ClassLoader} or expose their own instrumentable {@code ClassLoader}.
 * @since 2.0
 * @see java.lang.instrument.ClassFileTransformer
 */
public interface LoadTimeWeaver {

	/**
	 * Add a {@code ClassFileTransformer} to be applied by this {@code LoadTimeWeaver}.
	 * @param transformer the {@code ClassFileTransformer} to add
	 */
	void addTransformer(ClassFileTransformer transformer);

	/**
	 * Return a {@code ClassLoader} that supports instrumentation
	 * through AspectJ-style load-time weaving based on user-defined  {@link ClassFileTransformer ClassFileTransformers}.
	 * May be the current {@code ClassLoader}, or a {@code ClassLoader} created by this {@link LoadTimeWeaver} instance.
	 * @return the {@code ClassLoader} which will expose instrumented classes according to the registered transformers
	 */
	ClassLoader getInstrumentableClassLoader();

	/**
	 * Return a throwaway {@code ClassLoader}, enabling classes to be
	 * loaded and inspected without affecting the parent {@code ClassLoader}.
	 * Should <i>not</i> return the same instance of the {@link ClassLoader}
	 * returned from an invocation of {@link #getInstrumentableClassLoader()}.
	 * @return a temporary throwaway {@code ClassLoader}; should return a new instance for each call, with no existing state
	 */
	ClassLoader getThrowawayClassLoader();

}
