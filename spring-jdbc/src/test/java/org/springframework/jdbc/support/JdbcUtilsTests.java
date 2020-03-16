

package org.springframework.jdbc.support;

import org.junit.Test;

import static org.junit.Assert.*;


public class JdbcUtilsTests {

	@Test
	public void commonDatabaseName() {
		assertEquals("Oracle", JdbcUtils.commonDatabaseName("Oracle"));
		assertEquals("DB2", JdbcUtils.commonDatabaseName("DB2-for-Spring"));
		assertEquals("Sybase", JdbcUtils.commonDatabaseName("Sybase SQL Server"));
		assertEquals("Sybase", JdbcUtils.commonDatabaseName("Adaptive Server Enterprise"));
		assertEquals("MySQL", JdbcUtils.commonDatabaseName("MySQL"));
	}

	@Test
	public void convertUnderscoreNameToPropertyName() {
		assertEquals("myName", JdbcUtils.convertUnderscoreNameToPropertyName("MY_NAME"));
		assertEquals("yourName", JdbcUtils.convertUnderscoreNameToPropertyName("yOUR_nAME"));
		assertEquals("AName", JdbcUtils.convertUnderscoreNameToPropertyName("a_name"));
		assertEquals("someoneElsesName", JdbcUtils.convertUnderscoreNameToPropertyName("someone_elses_name"));
	}

}
