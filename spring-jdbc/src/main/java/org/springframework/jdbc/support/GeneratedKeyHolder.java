

package org.springframework.jdbc.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.lang.Nullable;

/**
 * The standard implementation of the {@link KeyHolder} interface, to be used for
 * holding auto-generated keys (as potentially returned by JDBC insert statements).
 *
 * Create an instance of this class for each insert operation, and pass it
 * to the corresponding {@link org.springframework.jdbc.core.JdbcTemplate} or
 * {@link org.springframework.jdbc.object.SqlUpdate} methods.
 * @since 1.1
 */
public class GeneratedKeyHolder implements KeyHolder {

	private final List<Map<String, Object>> keyList;

	/**
	 * Create a new GeneratedKeyHolder with a default list.
	 */
	public GeneratedKeyHolder() {
		this.keyList = new LinkedList<>();
	}

	/**
	 * Create a new GeneratedKeyHolder with a given list.
	 * @param keyList a list to hold maps of keys
	 */
	public GeneratedKeyHolder(List<Map<String, Object>> keyList) {
		this.keyList = keyList;
	}


	@Override
	@Nullable
	public Number getKey() throws InvalidDataAccessApiUsageException, DataRetrievalFailureException {
		if (this.keyList.isEmpty()) {
			return null;
		}
		if (this.keyList.size() > 1 || this.keyList.get(0).size() > 1) {
			throw new InvalidDataAccessApiUsageException(
					"The getKey method should only be used when a single key is returned. The current key entry contains multiple keys: " + this.keyList);

		}
		Iterator<Object> keyIter = this.keyList.get(0).values().iterator();
		if (keyIter.hasNext()) {
			Object key = keyIter.next();
			if (!(key instanceof Number)) {
				throw new DataRetrievalFailureException("The generated key is not of a supported numeric type. " +
						"Unable to cast [" + (key != null ? key.getClass().getName() : null) + "] to [" + Number.class.getName() + "]");
			}
			return (Number) key;
		}else {
			throw new DataRetrievalFailureException("Unable to retrieve the generated key.Check that the table has an identity column enabled.");
		}
	}

	@Override
	@Nullable
	public Map<String, Object> getKeys() throws InvalidDataAccessApiUsageException {
		if (this.keyList.isEmpty()) {
			return null;
		}
		if (this.keyList.size() > 1) {
			throw new InvalidDataAccessApiUsageException("The getKeys method should only be used when keys for a single row are returned.  The current key list contains keys for multiple rows: " + this.keyList);
		}
		return this.keyList.get(0);
	}

	@Override
	public List<Map<String, Object>> getKeyList() {
		return this.keyList;
	}

}
