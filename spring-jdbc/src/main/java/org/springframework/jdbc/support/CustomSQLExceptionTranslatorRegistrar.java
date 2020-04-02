

package org.springframework.jdbc.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

/**
 * Registry for custom {@link SQLExceptionTranslator} instances for specific databases.
 * @since 3.1.1
 */
public class CustomSQLExceptionTranslatorRegistrar implements InitializingBean {

	/**
	 * Map registry to hold custom translators specific databases.
	 * Key is the database product name as defined in the
	 * {@link org.springframework.jdbc.support.SQLErrorCodesFactory}.
	 */
	private final Map<String, SQLExceptionTranslator> translators = new HashMap<>();


	/**
	 * Setter for a Map of {@link SQLExceptionTranslator} references where the key must
	 * be the database name as defined in the {@code sql-error-codes.xml} file.
	 * Note that any existing translators will remain unless there is a match in the
	 * database name, at which point the new translator will replace the existing one.
	 */
	public void setTranslators(Map<String, SQLExceptionTranslator> translators) {
		this.translators.putAll(translators);
	}

	@Override
	public void afterPropertiesSet() {
		this.translators.forEach((dbName, translator) -> CustomSQLExceptionTranslatorRegistry.getInstance().registerTranslator(dbName, translator));
	}

}
