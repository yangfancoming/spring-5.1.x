

package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import org.springframework.instrument.InstrumentationSavingAgent;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * {@link LoadTimeWeaver} relying on VM {@link Instrumentation}.
 *
 * Start the JVM specifying the Java agent to be used, like as follows:
 *
 * <code class="code">-javaagent:path/to/org.springframework.instrument.jar</code>
 *
 * where {@code org.springframework.instrument.jar} is a JAR file containing
 * the {@link InstrumentationSavingAgent} class, as shipped with Spring.
 *
 * In Eclipse, for example, set the "Run configuration"'s JVM args to be of the form:
 *
 * <code class="code">-javaagent:${project_loc}/lib/org.springframework.instrument.jar</code>
 *
 * @author Rod Johnson

 * @since 2.0
 * @see InstrumentationSavingAgent
 */
public class InstrumentationLoadTimeWeaver implements LoadTimeWeaver {

	private static final boolean AGENT_CLASS_PRESENT = ClassUtils.isPresent(
			"org.springframework.instrument.InstrumentationSavingAgent",
			InstrumentationLoadTimeWeaver.class.getClassLoader());


	@Nullable
	private final ClassLoader classLoader;

	@Nullable
	private final Instrumentation instrumentation;

	private final List<ClassFileTransformer> transformers = new ArrayList<>(4);


	/**
	 * Create a new InstrumentationLoadTimeWeaver for the default ClassLoader.
	 */
	public InstrumentationLoadTimeWeaver() {
		this(ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Create a new InstrumentationLoadTimeWeaver for the given ClassLoader.
	 * @param classLoader the ClassLoader that registered transformers are supposed to apply to
	 */
	public InstrumentationLoadTimeWeaver(@Nullable ClassLoader classLoader) {
		this.classLoader = classLoader;
		this.instrumentation = getInstrumentation();
	}


	@Override
	public void addTransformer(ClassFileTransformer transformer) {
		Assert.notNull(transformer, "Transformer must not be null");
		FilteringClassFileTransformer actualTransformer =
				new FilteringClassFileTransformer(transformer, this.classLoader);
		synchronized (this.transformers) {
			Assert.state(this.instrumentation != null,
					"Must start with Java agent to use InstrumentationLoadTimeWeaver. See Spring documentation.");
			this.instrumentation.addTransformer(actualTransformer);
			this.transformers.add(actualTransformer);
		}
	}

	/**
	 * We have the ability to weave the current class loader when starting the
	 * JVM in this way, so the instrumentable class loader will always be the
	 * current loader.
	 */
	@Override
	public ClassLoader getInstrumentableClassLoader() {
		Assert.state(this.classLoader != null, "No ClassLoader available");
		return this.classLoader;
	}

	/**
	 * This implementation always returns a {@link SimpleThrowawayClassLoader}.
	 */
	@Override
	public ClassLoader getThrowawayClassLoader() {
		return new SimpleThrowawayClassLoader(getInstrumentableClassLoader());
	}

	/**
	 * Remove all registered transformers, in inverse order of registration.
	 */
	public void removeTransformers() {
		synchronized (this.transformers) {
			if (this.instrumentation != null && !this.transformers.isEmpty()) {
				for (int i = this.transformers.size() - 1; i >= 0; i--) {
					this.instrumentation.removeTransformer(this.transformers.get(i));
				}
				this.transformers.clear();
			}
		}
	}


	/**
	 * Check whether an Instrumentation instance is available for the current VM.
	 * @see #getInstrumentation()
	 */
	public static boolean isInstrumentationAvailable() {
		return (getInstrumentation() != null);
	}

	/**
	 * Obtain the Instrumentation instance for the current VM, if available.
	 * @return the Instrumentation instance, or {@code null} if none found
	 * @see #isInstrumentationAvailable()
	 */
	@Nullable
	private static Instrumentation getInstrumentation() {
		if (AGENT_CLASS_PRESENT) {
			return InstrumentationAccessor.getInstrumentation();
		}
		else {
			return null;
		}
	}


	/**
	 * Inner class to avoid InstrumentationSavingAgent dependency.
	 */
	private static class InstrumentationAccessor {

		public static Instrumentation getInstrumentation() {
			return InstrumentationSavingAgent.getInstrumentation();
		}
	}


	/**
	 * Decorator that only applies the given target transformer to a specific ClassLoader.
	 */
	private static class FilteringClassFileTransformer implements ClassFileTransformer {

		private final ClassFileTransformer targetTransformer;

		@Nullable
		private final ClassLoader targetClassLoader;

		public FilteringClassFileTransformer(
				ClassFileTransformer targetTransformer, @Nullable ClassLoader targetClassLoader) {

			this.targetTransformer = targetTransformer;
			this.targetClassLoader = targetClassLoader;
		}

		@Override
		@Nullable
		public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
				ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

			if (this.targetClassLoader != loader) {
				return null;
			}
			return this.targetTransformer.transform(
					loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
		}

		@Override
		public String toString() {
			return "FilteringClassFileTransformer for: " + this.targetTransformer.toString();
		}
	}

}
