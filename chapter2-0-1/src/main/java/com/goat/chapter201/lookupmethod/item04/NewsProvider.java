package com.goat.chapter201.lookupmethod.item04;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

/**
 * Created by 64274 on 2019/8/16.
 *
 * @ Description: @Lookup 使用方式一
 * @ author  山羊来了
 * @ date 2019/8/16---12:32
 */
@Component
public abstract class NewsProvider {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Lookup
	public abstract News getNews();
}