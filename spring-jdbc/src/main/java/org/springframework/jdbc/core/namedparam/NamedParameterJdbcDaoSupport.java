

package org.springframework.jdbc.core.namedparam;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.lang.Nullable;

/**
 * Extension of JdbcDaoSupport that exposes a NamedParameterJdbcTemplate as well.
 *
 * @author Thomas Risberg

 * @since 2.0
 * @see NamedParameterJdbcTemplate
 */
public class NamedParameterJdbcDaoSupport extends JdbcDaoSupport {

	@Nullable
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


	/**
	 * Create a NamedParameterJdbcTemplate based on the configured JdbcTemplate.
	 */
	@Override
	protected void initTemplateConfig() {
		JdbcTemplate jdbcTemplate = getJdbcTemplate();
		if (jdbcTemplate != null) {
			this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		}
	}

	/**
	 * Return a NamedParameterJdbcTemplate wrapping the configured JdbcTemplate.
	 */
	@Nullable
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return this.namedParameterJdbcTemplate;
	}

}
