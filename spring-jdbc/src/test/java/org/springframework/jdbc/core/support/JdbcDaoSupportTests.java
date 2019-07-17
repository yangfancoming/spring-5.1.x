

package org.springframework.jdbc.core.support;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.junit.Test;

import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**

 * @since 30.07.2003
 */
public class JdbcDaoSupportTests {

	@Test
	public void testJdbcDaoSupportWithDataSource() throws Exception {
		DataSource ds = mock(DataSource.class);
		final List<String> test = new ArrayList<>();
		JdbcDaoSupport dao = new JdbcDaoSupport() {
			@Override
			protected void initDao() {
				test.add("test");
			}
		};
		dao.setDataSource(ds);
		dao.afterPropertiesSet();
		assertEquals("Correct DataSource", ds, dao.getDataSource());
		assertEquals("Correct JdbcTemplate", ds, dao.getJdbcTemplate().getDataSource());
		assertEquals("initDao called", 1, test.size());
	}

	@Test
	public void testJdbcDaoSupportWithJdbcTemplate() throws Exception {
		JdbcTemplate template = new JdbcTemplate();
		final List<String> test = new ArrayList<>();
		JdbcDaoSupport dao = new JdbcDaoSupport() {
			@Override
			protected void initDao() {
				test.add("test");
			}
		};
		dao.setJdbcTemplate(template);
		dao.afterPropertiesSet();
		assertEquals("Correct JdbcTemplate", dao.getJdbcTemplate(), template);
		assertEquals("initDao called", 1, test.size());
	}

}
