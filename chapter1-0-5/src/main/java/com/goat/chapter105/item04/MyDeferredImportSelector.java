package com.goat.chapter105.item04;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Created by Administrator on 2021/6/21.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/21---16:22
 */

public class MyDeferredImportSelector implements DeferredImportSelector {
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[0];
	}
}
