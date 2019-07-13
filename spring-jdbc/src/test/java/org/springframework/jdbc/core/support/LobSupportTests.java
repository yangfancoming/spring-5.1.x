

package org.springframework.jdbc.core.support;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.LobRetrievalFailureException;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * @author Alef Arendsen
 */
public class LobSupportTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testCreatingPreparedStatementCallback() throws SQLException {
		LobHandler handler = mock(LobHandler.class);
		LobCreator creator = mock(LobCreator.class);
		PreparedStatement ps = mock(PreparedStatement.class);

		given(handler.getLobCreator()).willReturn(creator);
		given(ps.executeUpdate()).willReturn(3);

		class SetValuesCalled {
			boolean b = false;
		}

		final SetValuesCalled svc = new SetValuesCalled();

		AbstractLobCreatingPreparedStatementCallback psc = new AbstractLobCreatingPreparedStatementCallback(
				handler) {
			@Override
			protected void setValues(PreparedStatement ps, LobCreator lobCreator)
					throws SQLException, DataAccessException {
				svc.b = true;
			}
		};

		assertEquals(Integer.valueOf(3), psc.doInPreparedStatement(ps));
		assertTrue(svc.b);
		verify(creator).close();
		verify(handler).getLobCreator();
		verify(ps).executeUpdate();
	}

	@Test
	public void testAbstractLobStreamingResultSetExtractorNoRows() throws SQLException {
		ResultSet rset = mock(ResultSet.class);
		AbstractLobStreamingResultSetExtractor<Void> lobRse = getResultSetExtractor(false);
		thrown.expect(IncorrectResultSizeDataAccessException.class);
		try {
			lobRse.extractData(rset);
		}
		finally {
			verify(rset).next();
		}
	}

	@Test
	public void testAbstractLobStreamingResultSetExtractorOneRow() throws SQLException {
		ResultSet rset = mock(ResultSet.class);
		given(rset.next()).willReturn(true, false);
		AbstractLobStreamingResultSetExtractor<Void> lobRse = getResultSetExtractor(false);
		lobRse.extractData(rset);
		verify(rset).clearWarnings();
	}

	@Test
	public void testAbstractLobStreamingResultSetExtractorMultipleRows()
			throws SQLException {
		ResultSet rset = mock(ResultSet.class);
		given(rset.next()).willReturn(true, true, false);
		AbstractLobStreamingResultSetExtractor<Void> lobRse = getResultSetExtractor(false);
		thrown.expect(IncorrectResultSizeDataAccessException.class);
		try {
			lobRse.extractData(rset);
		}
		finally {
			verify(rset).clearWarnings();
		}
	}

	@Test
	public void testAbstractLobStreamingResultSetExtractorCorrectException()
			throws SQLException {
		ResultSet rset = mock(ResultSet.class);
		given(rset.next()).willReturn(true);
		AbstractLobStreamingResultSetExtractor<Void> lobRse = getResultSetExtractor(true);
		thrown.expect(LobRetrievalFailureException.class);
		lobRse.extractData(rset);
	}

	private AbstractLobStreamingResultSetExtractor<Void> getResultSetExtractor(final boolean ex) {
		AbstractLobStreamingResultSetExtractor<Void> lobRse = new AbstractLobStreamingResultSetExtractor<Void>() {

			@Override
			protected void streamData(ResultSet rs) throws SQLException, IOException {
				if (ex) {
					throw new IOException();
				}
				else {
					rs.clearWarnings();
				}
			}
		};
		return lobRse;
	}
}
