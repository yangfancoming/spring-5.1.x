

package org.springframework.web.reactive.function.server;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

import static org.junit.Assert.*;


public class InvalidHttpMethodIntegrationTests extends AbstractRouterFunctionIntegrationTests {

	@Override
	protected RouterFunction<?> routerFunction() {
		return RouterFunctions.route(RequestPredicates.GET("/"),
				request -> ServerResponse.ok().syncBody("FOO"))
				.andRoute(RequestPredicates.all(), request -> ServerResponse.ok().syncBody("BAR"));
	}

	@Test
	public void invalidHttpMethod() throws IOException {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
				.method("BAZ", null)
				.url("http://localhost:" + port + "/")
				.build();

		try (Response response = client.newCall(request).execute()) {
			assertEquals("BAR", response.body().string());
		}
	}

}
