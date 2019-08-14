package com.goat.chapter589.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 设置/获取 cookie 后  在 浏览器F12 点击已发的请求  查看收发数据的详细信息 可以看到cookie信息
*/
@RestController
public class SampleController extends CookieLocaleResolver {

	/**
	 * 测试url：  http://localhost:8589/setCookieLocale?locale=zh_CN
	 * 通过Controller修改系统Locale信息
	 */
	@GetMapping(value = "/setCookieLocale" , produces = "text/html;charset=UTF-8")
	public String cookieLocaleResolver(HttpServletRequest request , HttpServletResponse response) {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		String locale = request.getParameter("locale");
		localeResolver.setLocale(request , response , parseLocaleValue(locale));
		return "设置Locale成功";
	}

	/**
	 * 测试url：  http://localhost:8589/getCookieLocale
	 * 查看Locale信息
	 */
	@GetMapping(value = "/getCookieLocale" , produces = "text/html;charset=UTF-8")
	public String cookieLocaleResolver2(HttpServletRequest request) {
		String clientLocale = "";
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for(Cookie cookie : cookies){
				clientLocale +=cookie.getName()+"="+cookie.getValue()+",";
			}
		}
		System.out.println(clientLocale);
		RequestContext requestContext = new RequestContext(request);
		String value = requestContext.getMessage("message.locale");
		return "客户端发送的Cookie有："+clientLocale+" </br>当前使用的Locale是：" + requestContext.getLocale() + " </br>使用的资源Locale文件是：" + value ;
	}
}
