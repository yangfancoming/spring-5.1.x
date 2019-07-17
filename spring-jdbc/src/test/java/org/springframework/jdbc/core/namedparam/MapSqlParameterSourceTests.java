

package org.springframework.jdbc.core.namedparam;

import org.junit.Test;

import org.springframework.jdbc.core.SqlParameterValue;

import static org.junit.Assert.*;


public class MapSqlParameterSourceTests {

	@Test
	public void nullParameterValuesPassedToCtorIsOk() {
		new MapSqlParameterSource(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getValueChokesIfParameterIsNotPresent() {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.getValue("pechorin was right!");
	}

	@Test
	public void sqlParameterValueRegistersSqlType() {
		MapSqlParameterSource msps = new MapSqlParameterSource("FOO", new SqlParameterValue(2, "Foo"));
		assertEquals("Correct SQL Type not registered", 2, msps.getSqlType("FOO"));
		MapSqlParameterSource msps2 = new MapSqlParameterSource();
		msps2.addValues(msps.getValues());
		assertEquals("Correct SQL Type not registered", 2, msps2.getSqlType("FOO"));
	}

}
