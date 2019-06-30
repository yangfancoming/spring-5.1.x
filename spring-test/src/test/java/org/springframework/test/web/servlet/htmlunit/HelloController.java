

package org.springframework.test.web.servlet.htmlunit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rob Winch
 * @since 4.2
 */
@RestController
public class HelloController {

	@RequestMapping
	public String header(HttpServletRequest request) {
		return "hello";
	}

}
