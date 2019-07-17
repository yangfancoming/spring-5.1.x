

package org.springframework.jdbc.support.lob;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.SQLException;

import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;

/**
 * Simple JDBC {@link Clob} adapter that exposes a given String or character stream.
 * Optionally used by {@link DefaultLobHandler}.
 *

 * @since 2.5.3
 */
class PassThroughClob implements Clob {

	@Nullable
	private String content;

	@Nullable
	private Reader characterStream;

	@Nullable
	private InputStream asciiStream;

	private long contentLength;


	public PassThroughClob(String content) {
		this.content = content;
		this.contentLength = content.length();
	}

	public PassThroughClob(Reader characterStream, long contentLength) {
		this.characterStream = characterStream;
		this.contentLength = contentLength;
	}

	public PassThroughClob(InputStream asciiStream, long contentLength) {
		this.asciiStream = asciiStream;
		this.contentLength = contentLength;
	}


	@Override
	public long length() throws SQLException {
		return this.contentLength;
	}

	@Override
	public Reader getCharacterStream() throws SQLException {
		if (this.content != null) {
			return new StringReader(this.content);
		}
		else if (this.characterStream != null) {
			return this.characterStream;
		}
		else {
			return new InputStreamReader(
					(this.asciiStream != null ? this.asciiStream : StreamUtils.emptyInput()),
					StandardCharsets.US_ASCII);
		}
	}

	@Override
	public InputStream getAsciiStream() throws SQLException {
		try {
			if (this.content != null) {
				return new ByteArrayInputStream(this.content.getBytes(StandardCharsets.US_ASCII));
			}
			else if (this.characterStream != null) {
				String tempContent = FileCopyUtils.copyToString(this.characterStream);
				return new ByteArrayInputStream(tempContent.getBytes(StandardCharsets.US_ASCII));
			}
			else {
				return (this.asciiStream != null ? this.asciiStream : StreamUtils.emptyInput());
			}
		}
		catch (IOException ex) {
			throw new SQLException("Failed to read stream content: " + ex);
		}
	}


	@Override
	public Reader getCharacterStream(long pos, long length) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Writer setCharacterStream(long pos) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public OutputStream setAsciiStream(long pos) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSubString(long pos, int length) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int setString(long pos, String str) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int setString(long pos, String str, int offset, int len) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long position(String searchstr, long start) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long position(Clob searchstr, long start) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void truncate(long len) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void free() throws SQLException {
		// no-op
	}

}
