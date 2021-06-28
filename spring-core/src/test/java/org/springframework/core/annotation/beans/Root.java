package org.springframework.core.annotation.beans;

import org.springframework.core.annotation.Order;

/**
 * Created by 64274 on 2019/6/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/30---15:30
 */
public class Root implements AnnotatedInterface {

	@Order(27)
	public void annotatedOnRoot() {
	}

	@Meta1
	public void metaAnnotatedOnRoot() {
	}

	public void overrideToAnnotate() {
	}

	@Order(27)
	public void overrideWithoutNewAnnotation() {
	}

	public void notAnnotated() {
	}

	@Override
	public void fromInterfaceImplementedByRoot() {
	}


	@Order(99)
	public void goatTest() {
	}
}