package org.springframework.core.annotation.beans;

import org.springframework.core.annotation.Order;

/**
 * Created by 64274 on 2019/6/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/30---15:30
 */
public interface AnnotatedInterface {
	@Order(0)
	void fromInterfaceImplementedByRoot();
}