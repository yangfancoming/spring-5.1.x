package org.springframework.core.annotation.beans;

import org.springframework.core.annotation.Order;

/**
 * Created by 64274 on 2019/6/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/30---15:36
 */
public  class Leaf extends Root {

	@Order(25)
	public void annotatedOnLeaf() {
	}

	@Meta1
	public void metaAnnotatedOnLeaf() {
	}

	@MetaMeta
	public void metaMetaAnnotatedOnLeaf() {
	}

	@Override
	@Order(1)
	public void overrideToAnnotate() {
	}

	@Override
	public void overrideWithoutNewAnnotation() {
	}
}