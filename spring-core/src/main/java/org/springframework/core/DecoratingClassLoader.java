package org.springframework.core;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Base class for decorating ClassLoaders such as {@link OverridingClassLoader}
 * and {@link org.springframework.instrument.classloading.ShadowingClassLoader},
 * providing common handling of excluded packages and classes.
 * @since 2.5.2
 */
public abstract class DecoratingClassLoader extends ClassLoader {

	static {
		ClassLoader.registerAsParallelCapable();
	}

	private final Set<String> excludedPackages = Collections.newSetFromMap(new ConcurrentHashMap<>(8));

	private final Set<String> excludedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(8));


	/**
	 * Create a new DecoratingClassLoader with no parent ClassLoader.
	 */
	public DecoratingClassLoader() {
	}

	/**
	 * Create a new DecoratingClassLoader using the given parent ClassLoader for delegation.
	 */
	public DecoratingClassLoader(@Nullable ClassLoader parent) {
		super(parent);
	}


	/**
	 * Add a package name to exclude from decoration (e.g. overriding).
	 * Any class whose fully-qualified name starts with the name registered
	 * here will be handled by the parent ClassLoader in the usual fashion.
	 * @param packageName the package name to exclude
	 */
	public void excludePackage(String packageName) {
		Assert.notNull(packageName, "Package name must not be null");
		this.excludedPackages.add(packageName);
	}

	/**
	 * Add a class name to exclude from decoration (e.g. overriding).
	 * Any class name registered here will be handled by the parent ClassLoader in the usual fashion.
	 * @param className the class name to exclude
	 */
	public void excludeClass(String className) {
		Assert.notNull(className, "Class name must not be null");
		this.excludedClasses.add(className);
	}

	/**
	 * Determine whether the specified class is excluded from decoration by this class loader.
	 * The default implementation checks against excluded packages and classes.
	 * @param className the class name to check
	 * @return whether the specified class is eligible
	 * @see #excludePackage
	 * @see #excludeClass
	 */
	protected boolean isExcluded(String className) {
		if (this.excludedClasses.contains(className)) {
			return true;
		}
		for (String packageName : this.excludedPackages) {
			if (className.startsWith(packageName)) {
				return true;
			}
		}
		return false;
	}
}
