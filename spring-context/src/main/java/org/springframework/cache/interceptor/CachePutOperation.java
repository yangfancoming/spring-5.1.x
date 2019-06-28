

package org.springframework.cache.interceptor;

import org.springframework.lang.Nullable;

/**
 * Class describing a cache 'put' operation.
 *
 * @author Costin Leau
 * @author Phillip Webb
 * @author Marcin Kamionowski
 * @since 3.1
 */
public class CachePutOperation extends CacheOperation {

	@Nullable
	private final String unless;


	/**
	 * Create a new {@link CachePutOperation} instance from the given builder.
	 * @since 4.3
	 */
	public CachePutOperation(CachePutOperation.Builder b) {
		super(b);
		this.unless = b.unless;
	}


	@Nullable
	public String getUnless() {
		return this.unless;
	}


	/**
	 * A builder that can be used to create a {@link CachePutOperation}.
	 * @since 4.3
	 */
	public static class Builder extends CacheOperation.Builder {

		@Nullable
		private String unless;

		public void setUnless(String unless) {
			this.unless = unless;
		}

		@Override
		protected StringBuilder getOperationDescription() {
			StringBuilder sb = super.getOperationDescription();
			sb.append(" | unless='");
			sb.append(this.unless);
			sb.append("'");
			return sb;
		}

		public CachePutOperation build() {
			return new CachePutOperation(this);
		}
	}

}
