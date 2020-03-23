

package org.springframework.web.reactive.result.view;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.ui.Model;

/**
 * Default implementation of {@link Rendering}.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
class DefaultRendering implements Rendering {

	private static final HttpHeaders EMPTY_HEADERS = HttpHeaders.readOnlyHttpHeaders(new HttpHeaders());


	private final Object view;

	private final Map<String, Object> model;

	@Nullable
	private final HttpStatus status;

	private final HttpHeaders headers;


	DefaultRendering(Object view, @Nullable Model model, @Nullable HttpStatus status, @Nullable HttpHeaders headers) {
		this.view = view;
		this.model = (model != null ? model.asMap() : Collections.emptyMap());
		this.status = status;
		this.headers = (headers != null ? headers : EMPTY_HEADERS);
	}


	@Override
	@Nullable
	public Object view() {
		return this.view;
	}

	@Override
	public Map<String, Object> modelAttributes() {
		return this.model;
	}

	@Override
	@Nullable
	public HttpStatus status() {
		return this.status;
	}

	@Override
	public HttpHeaders headers() {
		return this.headers;
	}


	@Override
	public String toString() {
		return "Rendering[view=" + this.view + ", modelAttributes=" + this.model +
				", status=" + this.status + ", headers=" + this.headers + "]";
	}
}
