

package org.springframework.web.servlet.mvc.method.annotation

import org.junit.Assert.*
import org.junit.Test
import org.springframework.mock.web.test.MockHttpServletRequest
import org.springframework.mock.web.test.MockHttpServletResponse
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


class ServletAnnotationControllerHandlerMethodKotlinTests : AbstractServletHandlerMethodTests() {

	@Test
	fun dataClassBinding() {
		initServletWithControllers(DataClassController::class.java)

		val request = MockHttpServletRequest("GET", "/bind")
		request.addParameter("param1", "value1")
		request.addParameter("param2", "2")
		val response = MockHttpServletResponse()
		servlet.service(request, response)
		assertEquals("value1-2", response.contentAsString)
	}

	@Test
	fun dataClassBindingWithOptionalParameterAndAllParameters() {
		initServletWithControllers(DataClassController::class.java)

		val request = MockHttpServletRequest("GET", "/bind-optional-parameter")
		request.addParameter("param1", "value1")
		request.addParameter("param2", "2")
		val response = MockHttpServletResponse()
		servlet.service(request, response)
		assertEquals("value1-2", response.contentAsString)
	}

	@Test
	fun dataClassBindingWithOptionalParameterAndOnlyMissingParameters() {
		initServletWithControllers(DataClassController::class.java)

		val request = MockHttpServletRequest("GET", "/bind-optional-parameter")
		request.addParameter("param1", "value1")
		val response = MockHttpServletResponse()
		servlet.service(request, response)
		assertEquals("value1-12", response.contentAsString)
	}


	data class DataClass(val param1: String, val param2: Int)

	data class DataClassWithOptionalParameter(val param1: String, val param2: Int = 12)

	@RestController
	class DataClassController {

		@RequestMapping("/bind")
		fun handle(data: DataClass) = "${data.param1}-${data.param2}"

		@RequestMapping("/bind-optional-parameter")
		fun handle(data: DataClassWithOptionalParameter) = "${data.param1}-${data.param2}"
	}

}
