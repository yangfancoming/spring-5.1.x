package com.goat.chapter201.lookupmethod.item04;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

/**
 * Created by 64274 on 2019/8/16.
 *
 * @ Description:  @Lookup 使用方式二
 * @ author  山羊来了
 * @ date 2019/8/16---12:32
 */
@Component
public class NewsProvider2 {

	@Lookup
	public News getNews(){
		return null;
	}
}