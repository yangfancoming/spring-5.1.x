

package org.springframework.jdbc.datasource.embedded;

import org.springframework.util.Assert;

/**
 * Maps well-known {@linkplain EmbeddedDatabaseType embedded database types}
 * to {@link EmbeddedDatabaseConfigurer} strategies.
 *
 * @author Keith Donald
 * @author Oliver Gierke
 * @author Sam Brannen
 * @since 3.0
 */
final class EmbeddedDatabaseConfigurerFactory {

	private EmbeddedDatabaseConfigurerFactory() {
	}


	/**
	 * Return a configurer instance for the given embedded database type.
	 * @param type the embedded database type (HSQL, H2 or Derby)
	 * @return the configurer instance
	 * @throws IllegalStateException if the driver for the specified database type is not available
	 */
	public static EmbeddedDatabaseConfigurer getConfigurer(EmbeddedDatabaseType type) throws IllegalStateException {
		Assert.notNull(type, "EmbeddedDatabaseType is required");
		try {
			switch (type) {
				case HSQL:
					return HsqlEmbeddedDatabaseConfigurer.getInstance();
				case H2:
					return H2EmbeddedDatabaseConfigurer.getInstance();
				case DERBY:
					return DerbyEmbeddedDatabaseConfigurer.getInstance();
				default:
					throw new UnsupportedOperationException("Embedded database type [" + type + "] is not supported");
			}
		}
		catch (ClassNotFoundException | NoClassDefFoundError ex) {
			throw new IllegalStateException("Driver for test database type [" + type + "] is not available", ex);
		}
	}

}
