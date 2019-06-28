

package org.springframework.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static org.mockito.Mockito.spy;

/**
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class MockHttpOutputMessage implements HttpOutputMessage {

	private final HttpHeaders headers = new HttpHeaders();

	private final ByteArrayOutputStream body = spy(new ByteArrayOutputStream());

	private boolean headersWritten = false;

	private final HttpHeaders writtenHeaders = new HttpHeaders();


	@Override
	public HttpHeaders getHeaders() {
		return (this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers);
	}

	/**
	 * Return a copy of the actual headers written at the time of the call to
	 * getResponseBody, i.e. ignoring any further changes that may have been made to
	 * the underlying headers, e.g. via a previously obtained instance.
	 */
	public HttpHeaders getWrittenHeaders() {
		return writtenHeaders;
	}

	@Override
	public OutputStream getBody() throws IOException {
		writeHeaders();
		return body;
	}

	public byte[] getBodyAsBytes() {
		writeHeaders();
		return body.toByteArray();
	}

	public String getBodyAsString(Charset charset) {
		byte[] bytes = getBodyAsBytes();
		return new String(bytes, charset);
	}

	private void writeHeaders() {
		if (this.headersWritten) {
			return;
		}
		this.headersWritten = true;
		this.writtenHeaders.putAll(this.headers);
	}

}
