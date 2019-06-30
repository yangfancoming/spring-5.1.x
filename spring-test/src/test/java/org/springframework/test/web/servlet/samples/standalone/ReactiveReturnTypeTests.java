
package org.springframework.test.web.servlet.samples.standalone;

import java.time.Duration;

import org.junit.Test;
import reactor.core.publisher.Flux;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

/**
 * Tests with reactive return value types.
 *
 * @author Rossen Stoyanchev
 */
public class ReactiveReturnTypeTests {


	@Test // SPR-16869
	public void sseWithFlux() throws Exception {

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(ReactiveController.class).build();

		MvcResult mvcResult = mockMvc.perform(get("/spr16869"))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(content().string("data:event0\n\ndata:event1\n\ndata:event2\n\n"));
	}



	@RestController
	static class ReactiveController {

		@GetMapping(path = "/spr16869", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
		Flux<String> sseFlux() {
			return Flux.interval(Duration.ofSeconds(1)).take(3)
					.map(aLong -> String.format("event%d", aLong));
		}
	}

}
