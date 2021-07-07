package com.goat.chapter201.bean.lazy.item01;

/**
 * Created by 64274 on 2019/6/28.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/28---10:39
 */
public class LazyServiceImpl implements LazyService {

	public LazyServiceImpl() {
		System.out.println("LazyServiceImpl 构造函数执行");
	}

	public String getMessage() {
		return "懒加载实现类bean";
	}
}
