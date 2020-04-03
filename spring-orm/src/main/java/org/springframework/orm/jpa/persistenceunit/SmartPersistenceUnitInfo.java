

package org.springframework.orm.jpa.persistenceunit;

import java.util.List;
import javax.persistence.spi.PersistenceUnitInfo;

/**
 * Extension of the standard JPA PersistenceUnitInfo interface, for advanced collaboration
 * between Spring's {@link org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean}
 * and {@link PersistenceUnitManager} implementations.
 *

 * @since 3.0.1
 * @see PersistenceUnitManager
 * @see org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
 */
public interface SmartPersistenceUnitInfo extends PersistenceUnitInfo {

	/**
	 * Return a list of managed Java packages, to be introspected by the persistence provider.
	 * Typically found through scanning but not exposable through {@link #getManagedClassNames()}.
	 * @return a list of names of managed Java packages (potentially empty)
	 * @since 4.1
	 */
	List<String> getManagedPackages();

	/**
	 * Set the persistence provider's own package name, for exclusion from class transformation.
	 * @see #addTransformer(javax.persistence.spi.ClassTransformer)
	 * @see #getNewTempClassLoader()
	 */
	void setPersistenceProviderPackageName(String persistenceProviderPackageName);

}
