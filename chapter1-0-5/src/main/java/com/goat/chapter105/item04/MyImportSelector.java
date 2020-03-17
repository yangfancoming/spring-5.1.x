package com.goat.chapter105.item04;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Created by Administrator on 2020/3/17.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/17---16:57
 */
public class MyImportSelector implements ImportSelector {

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[]{"com.goat.chapter105.model.Blue","com.goat.chapter105.model.Red"};
	}
}
