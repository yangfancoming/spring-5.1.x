

package org.springframework.core;

/**
 * Default implementation of the {@link ParameterNameDiscoverer} strategy interface,
 * using the Java 8 standard reflection mechanism (if available), and falling back
 * to the ASM-based {@link LocalVariableTableParameterNameDiscoverer} for checking
 * debug information in the class file.
 *
 * <p>If a Kotlin reflection implementation is present,
 * {@link KotlinReflectionParameterNameDiscoverer} is added first in the list and used
 * for Kotlin classes and interfaces. When compiling or running as a Graal native image,
 * no {@link ParameterNameDiscoverer} is used.
 *
 * <p>Further discoverers may be added through {@link #addDiscoverer(ParameterNameDiscoverer)}.
 *

 * @author Sebastien Deleuze
 * @since 4.0
 * @see StandardReflectionParameterNameDiscoverer
 * @see LocalVariableTableParameterNameDiscoverer
 * @see KotlinReflectionParameterNameDiscoverer
 */
public class DefaultParameterNameDiscoverer extends PrioritizedParameterNameDiscoverer {

	public DefaultParameterNameDiscoverer() {
		if (!GraalDetector.inImageCode()) {
			if (KotlinDetector.isKotlinReflectPresent()) {
				addDiscoverer(new KotlinReflectionParameterNameDiscoverer());
			}
			addDiscoverer(new StandardReflectionParameterNameDiscoverer());
			addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
		}
	}

}
