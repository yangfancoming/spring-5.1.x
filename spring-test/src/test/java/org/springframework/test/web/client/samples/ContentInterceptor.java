package org.springframework.test.web.client.samples;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by 64274 on 2019/6/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/30---20:05
 */
public class ContentInterceptor implements ClientHttpRequestInterceptor {

	private final Resource resource;

	public ContentInterceptor(Resource resource) {
		this.resource = resource;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		ClientHttpResponse response = execution.execute(request, body);
		byte[] expected = FileCopyUtils.copyToByteArray(this.resource.getInputStream());
		byte[] actual = FileCopyUtils.copyToByteArray(response.getBody());
		assertEquals(new String(expected), new String(actual));
		return response;
	}
}