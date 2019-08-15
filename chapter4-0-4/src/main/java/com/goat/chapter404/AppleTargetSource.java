package com.goat.chapter404;

import com.goat.chapter404.bean.Apple;
import org.springframework.aop.TargetSource;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by 64274 on 2019/8/15.
 *
 * 实现自定义TargetSource主要有两个点要注意，
 * 一个是getTarget()方法，该方法中需要实现获取目标对象的逻辑，
 * 另一个是isStatic()方法，这个方法告知Spring是否需要缓存目标对象，在非单例的情况下一般是返回false。
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/15---16:45
 */
public class AppleTargetSource implements TargetSource {
	private Apple apple1;
	private Apple apple2;

	public AppleTargetSource() {
		this.apple1 = new Apple(1);
		this.apple2 = new Apple(2);
	}

	@Override
	public Class<?> getTargetClass() {
		return Apple.class;
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public Object getTarget() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int index = random.nextInt(2);
		return index % 2 == 0 ? apple1 : apple2;
	}

	@Override
	public void releaseTarget(Object target)  {}
}