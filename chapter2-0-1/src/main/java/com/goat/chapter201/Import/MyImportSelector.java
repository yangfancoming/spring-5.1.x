package com.goat.chapter201.Import;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/17---16:57
 */
public class MyImportSelector implements ImportSelector {

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[]{"com.goat.chapter201.model.Blue","com.goat.chapter201.model.Red"};
	}
}
