

package org.springframework.jdbc.support;

import java.sql.SQLException;

import org.junit.Test;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.BadSqlGrammarException;

import static org.junit.Assert.*;

/**
 * Tests for custom {@link SQLExceptionTranslator}.
 *
 * @author Thomas Risberg
 */
public class CustomSQLExceptionTranslatorRegistrarTests {

	@Test
	@SuppressWarnings("resource")
	public void customErrorCodeTranslation() {
		new ClassPathXmlApplicationContext("test-custom-translators-context.xml",
				CustomSQLExceptionTranslatorRegistrarTests.class);

		SQLErrorCodes codes = SQLErrorCodesFactory.getInstance().getErrorCodes("H2");
		SQLErrorCodeSQLExceptionTranslator sext = new SQLErrorCodeSQLExceptionTranslator();
		sext.setSqlErrorCodes(codes);

		DataAccessException exFor4200 = sext.doTranslate("", "", new SQLException("Ouch", "42000", 42000));
		assertNotNull("Should have been translated", exFor4200);
		assertTrue("Should have been instance of BadSqlGrammarException",
			BadSqlGrammarException.class.isAssignableFrom(exFor4200.getClass()));

		DataAccessException exFor2 = sext.doTranslate("", "", new SQLException("Ouch", "42000", 2));
		assertNotNull("Should have been translated", exFor2);
		assertTrue("Should have been instance of TransientDataAccessResourceException",
			TransientDataAccessResourceException.class.isAssignableFrom(exFor2.getClass()));

		DataAccessException exFor3 = sext.doTranslate("", "", new SQLException("Ouch", "42000", 3));
		assertNull("Should not have been translated", exFor3);
	}

}
