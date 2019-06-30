

package org.springframework.test.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.jdbc.core.JdbcTemplate;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for {@link JdbcTestUtils}.
 *
 * @author Phillip Webb
 * @since 2.5.4
 * @see JdbcTestUtilsIntegrationTests
 */
@RunWith(MockitoJUnitRunner.class)
public class JdbcTestUtilsTests {

	@Mock
	private JdbcTemplate jdbcTemplate;


	@Test
	public void deleteWithoutWhereClause() throws Exception {
		given(jdbcTemplate.update("DELETE FROM person")).willReturn(10);
		int deleted = JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "person", null);
		assertThat(deleted, equalTo(10));
	}

	@Test
	public void deleteWithWhereClause() throws Exception {
		given(jdbcTemplate.update("DELETE FROM person WHERE name = 'Bob' and age > 25")).willReturn(10);
		int deleted = JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "person", "name = 'Bob' and age > 25");
		assertThat(deleted, equalTo(10));
	}

	@Test
	public void deleteWithWhereClauseAndArguments() throws Exception {
		given(jdbcTemplate.update("DELETE FROM person WHERE name = ? and age > ?", "Bob", 25)).willReturn(10);
		int deleted = JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "person", "name = ? and age > ?", "Bob", 25);
		assertThat(deleted, equalTo(10));
	}

}
