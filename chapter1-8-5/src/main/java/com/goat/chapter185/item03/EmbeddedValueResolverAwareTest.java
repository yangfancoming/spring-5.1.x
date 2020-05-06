package com.goat.chapter185.item03;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;

/**
 * Created by Administrator on 2020/4/21.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/21---13:45
 */
public class EmbeddedValueResolverAwareTest implements EmbeddedValueResolverAware {

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		System.out.println("setEmbeddedValueResolver..." + resolver);
	}
}
