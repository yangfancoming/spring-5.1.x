

package org.springframework.core.io.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Holder that combines a {@link Resource} descriptor with a specific encoding or {@code Charset} to be used for reading from the resource.
 * Used as an argument for operations that support reading content with a specific encoding, typically via a {@code java.io.Reader}.
 * 目的是把资源封装为有编码的，就是把编码和资源绑定在一块
 * @since 1.2.6
 * @see Resource#getInputStream()
 * @see java.io.Reader
 * @see java.nio.charset.Charset
 */
public class EncodedResource implements InputStreamSource {

	// 文件资源
	private final Resource resource;

	// 指定编码字符集
	@Nullable
	private final String encoding;

	// 指定编码字符集   类似Class和ClassLoad一样  有优先权
	@Nullable
	private final Charset charset;

	/**
	 * Create a new {@code EncodedResource} for the given {@code Resource},not specifying an explicit encoding or {@code Charset}.
	 * @param resource the {@code Resource} to hold (never {@code null})
	 */
	public EncodedResource(Resource resource) {
		this(resource, null, null);
	}

	/**
	 * Create a new {@code EncodedResource} for the given {@code Resource},using the specified {@code encoding}.
	 * @param resource the {@code Resource} to hold (never {@code null})
	 * @param encoding the encoding to use for reading from the resource
	 */
	public EncodedResource(Resource resource, @Nullable String encoding) {
		this(resource, encoding, null);
	}

	/**
	 * Create a new {@code EncodedResource} for the given {@code Resource},using the specified {@code Charset}.
	 * @param resource the {@code Resource} to hold (never {@code null})
	 * @param charset the {@code Charset} to use for reading from the resource
	 */
	public EncodedResource(Resource resource, @Nullable Charset charset) {
		this(resource, null, charset);
	}

	private EncodedResource(Resource resource, @Nullable String encoding, @Nullable Charset charset) {
		super();
		Assert.notNull(resource, "Resource must not be null");
		this.resource = resource;
		this.encoding = encoding;
		this.charset = charset;
	}

	/**
	 * Return the {@code Resource} held by this {@code EncodedResource}.
	 */
	public final Resource getResource() {
		return resource;
	}

	/**
	 * Return the encoding to use for reading from the {@linkplain #getResource() resource}, or {@code null} if none specified.
	 */
	@Nullable
	public final String getEncoding() {
		return encoding;
	}

	/**
	 * Return the {@code Charset} to use for reading from the {@linkplain #getResource() resource},or {@code null} if none specified.
	 */
	@Nullable
	public final Charset getCharset() {
		return charset;
	}

	/**
	 * Determine whether a {@link Reader} is required as opposed to an {@link InputStream},
	 * i.e. whether an {@linkplain #getEncoding() encoding} or a {@link #getCharset() Charset} has been specified.
	 * @see #getReader()
	 * @see #getInputStream()
	 */
	public boolean requiresReader() {
		return (encoding != null || charset != null);
	}

	/**
	 * Open a {@code java.io.Reader} for the specified resource, using the specified {@link #getCharset() Charset} or {@linkplain #getEncoding() encoding} (if any).
	 * @throws IOException if opening the Reader failed
	 * @see #requiresReader()
	 * @see #getInputStream()
	 * 是否存在指定编码，优先选择charset   返回一个InputStreamReader
	 */
	public Reader getReader() throws IOException {
		if (charset != null) {
			return new InputStreamReader(resource.getInputStream(), charset);
		}else if (encoding != null) {
			return new InputStreamReader(resource.getInputStream(), encoding);
		}else {
			return new InputStreamReader(resource.getInputStream());
		}
	}

	/**
	 * Open an {@code InputStream} for the specified resource, ignoring any specified
	 * {@link #getCharset() Charset} or {@linkplain #getEncoding() encoding}.
	 * @throws IOException if opening the InputStream failed
	 * @see #requiresReader()
	 * @see #getReader()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return resource.getInputStream();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof EncodedResource)) {
			return false;
		}
		EncodedResource otherResource = (EncodedResource) other;
		return (resource.equals(otherResource.resource) && ObjectUtils.nullSafeEquals(charset, otherResource.charset) && ObjectUtils.nullSafeEquals(encoding, otherResource.encoding));
	}

	@Override
	public int hashCode() {
		return resource.hashCode();
	}

	@Override
	public String toString() {
		return resource.toString();
	}

}
