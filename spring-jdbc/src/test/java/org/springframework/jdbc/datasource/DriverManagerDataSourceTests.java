

package org.springframework.jdbc.datasource;

import org.junit.Test;

import java.sql.*;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.mock;


public class DriverManagerDataSourceTests {

	private Connection connection = mock(Connection.class);
	final String jdbcUrl = "jdbc:mysql://172.16.163.135:3306/mybatis?Unicode=true&amp;characterEncoding=utf8&amp;useSSL=false";
	final String uname = "root";
	final String pwd = "12345";
	Statement statement = null;
	ResultSet resultSet = null;

	@Test
	public void test() throws SQLException {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(jdbcUrl, "root","12345");
			statement = connection.createStatement();
			System.out.println(connection); // com.mysql.jdbc.JDBC4Connection@387a8303

			resultSet = statement.executeQuery("select * from foo");
			while (resultSet.next()){
				System.out.println( resultSet.getObject(1) + "---" + resultSet.getObject(2) );
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet!=null){
				resultSet.close();
			}
			statement.close();
			connection.close();
		}

	}

	@Test
	public void testStandardUsage() throws Exception {

		class TestDriverManagerDataSource extends DriverManagerDataSource {
			@Override
			protected Connection getConnectionFromDriverManager(String url, Properties props) throws SQLException {
				Connection connection = DriverManager.getConnection(url, props.getProperty("user"),props.getProperty("password"));
				return connection;
			}
		}
		DriverManagerDataSource ds = new TestDriverManagerDataSource();
		try {
			ds.setDriverClassName("com.mysql.jdbc.Driver");// 根据 字符串  反射创建数据库类型驱动
			ds.setUrl(jdbcUrl);
			ds.setUsername(uname);
			ds.setPassword(pwd);

			Connection actualCon = ds.getConnection();
			statement = actualCon.createStatement();
			resultSet = statement.executeQuery("select * from foo");
			while (resultSet.next()){
				System.out.println( resultSet.getObject(1) + "---" + resultSet.getObject(2) );
			}
		} finally {
			if (resultSet!=null){
				resultSet.close();
			}
			statement.close();
			connection.close();
		}

	}

	@Test
	public void testUsageWithConnectionProperties() throws Exception {
		final String jdbcUrl = "url";

		final Properties connProps = new Properties();
		connProps.setProperty("myProp", "myValue");
		connProps.setProperty("yourProp", "yourValue");
		connProps.setProperty("user", "uname");
		connProps.setProperty("password", "pwd");

		class TestDriverManagerDataSource extends DriverManagerDataSource {
			@Override
			protected Connection getConnectionFromDriverManager(String url, Properties props) {
				assertEquals(jdbcUrl, url);
				assertEquals("uname", props.getProperty("user"));
				assertEquals("pwd", props.getProperty("password"));
				assertEquals("myValue", props.getProperty("myProp"));
				assertEquals("yourValue", props.getProperty("yourProp"));
				return connection;
			}
		}

		DriverManagerDataSource ds = new TestDriverManagerDataSource();
		//ds.setDriverClassName("foobar");
		ds.setUrl(jdbcUrl);
		ds.setConnectionProperties(connProps);

		Connection actualCon = ds.getConnection();
		assertTrue(actualCon == connection);

		assertTrue(ds.getUrl().equals(jdbcUrl));
	}

	@Test
	public void testUsageWithConnectionPropertiesAndUserCredentials() throws Exception {
		final String jdbcUrl = "url";
		final String uname = "uname";
		final String pwd = "pwd";

		final Properties connProps = new Properties();
		connProps.setProperty("myProp", "myValue");
		connProps.setProperty("yourProp", "yourValue");
		connProps.setProperty("user", "uname2");
		connProps.setProperty("password", "pwd2");

		class TestDriverManagerDataSource extends DriverManagerDataSource {
			@Override
			protected Connection getConnectionFromDriverManager(String url, Properties props) {
				assertEquals(jdbcUrl, url);
				assertEquals(uname, props.getProperty("user"));
				assertEquals(pwd, props.getProperty("password"));
				assertEquals("myValue", props.getProperty("myProp"));
				assertEquals("yourValue", props.getProperty("yourProp"));
				return connection;
			}
		}

		DriverManagerDataSource ds = new TestDriverManagerDataSource();
		//ds.setDriverClassName("foobar");
		ds.setUrl(jdbcUrl);
		ds.setUsername(uname);
		ds.setPassword(pwd);
		ds.setConnectionProperties(connProps);

		Connection actualCon = ds.getConnection();
		assertTrue(actualCon == connection);

		assertTrue(ds.getUrl().equals(jdbcUrl));
		assertTrue(ds.getPassword().equals(pwd));
		assertTrue(ds.getUsername().equals(uname));
	}

	@Test
	public void testInvalidClassName()  {
		String bogusClassName = "foobar";
		DriverManagerDataSource ds = new DriverManagerDataSource();
		try {
			ds.setDriverClassName(bogusClassName);
			fail("Should have thrown IllegalStateException");
		}
		catch (IllegalStateException ex) {
			System.out.println(ex.getCause());
			assertTrue(ex.getCause() instanceof ClassNotFoundException);
		}
	}

}
