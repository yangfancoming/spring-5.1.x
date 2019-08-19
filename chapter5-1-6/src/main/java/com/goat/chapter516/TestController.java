package com.goat.chapter516;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by 64274 on 2019/8/19.
 *
 * @ Description:    http://localhost:8516/sayHello?age=12&name=Jack  doit 为啥是404？？
 * @ author  山羊来了
 * @ date 2019/8/19---17:40
 */
@Controller
public class TestController {

	@RequestMapping("sayHello")
	@ResponseBody
	public ModelAndView sayHello(int age, String name) {
		ModelAndView mav = new ModelAndView();
		MyBean bean = new MyBean(age, name);
		mav.addObject("myBean", bean);
		mav.setViewName("home");
		System.out.println(bean.toString());
		return mav;
	}
}