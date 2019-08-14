package com.goat.chapter587.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * 设置/获取 cookie 后  在 浏览器F12 点击已发的请求  查看收发数据的详细信息 可以看到cookie信息
*/
@RestController
public class SampleController {

	/**
	 * 测试url：   http://localhost:8587/getSessionLocale?locale=en_US
	 * 变更参数locale的值  http://localhost:8587/getSessionLocale?locale=zh_CN
	 *
	 */
	@GetMapping(value = "/getSessionLocale", produces = "text/html;charset=UTF-8")
	public String sessionLocaleResolver(HttpServletRequest request) {
		RequestContext requestContext = new RequestContext(request);
		String value = requestContext.getMessage("message.locale");
		HttpSession session = request.getSession();
		return "Session中设置的Locale是："+session.getAttribute("locale")+" </br>当前使用的Locale是：" + requestContext.getLocale() + " </br>使用的资源Locale文件是：messages_" + value+".properties";
	}
}
