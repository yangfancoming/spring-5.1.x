package com.goat.chapter200.item04;

import com.goat.chapter200.base.CompactDisc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2021/6/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/16---17:29
 */
@Service
public class GoatPlayer {

	@Autowired
	private CompactDisc cd;

	/**
	 * @see DefaultListableBeanFactory#determineAutowireCandidate(java.util.Map, org.springframework.beans.factory.config.DependencyDescriptor)
	 * 报错  expected single matching bean but found 2: beyond,blackPanther
	     @Autowired
	     private CompactDisc cd;
	 * 正常   注入 Beyond 对象 （ 配合 Beyond 类 + @Primary  ）
		 @Autowired
		 private CompactDisc cd;
	 * 正常   注入 Beyond 对象  （ 配合 @Qualifier 注解  ）
		 @Autowired
		 @Qualifier("beyond")
		 private CompactDisc cd;
	 * 正常   注入 Beyond 对象   （ 配合 先byType再byName blackPanther  ）
		 @Autowired
		 private CompactDisc blackPanther;
	*/


	public void insert(){
		System.out.println("GoatPlayer insert() -----------");
		cd.play();
	}
}
