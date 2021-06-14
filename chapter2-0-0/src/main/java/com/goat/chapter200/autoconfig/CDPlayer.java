package com.goat.chapter200.autoconfig;
import com.goat.chapter200.base.CompactDisc;
import com.goat.chapter200.base.MediaPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class CDPlayer implements MediaPlayer {

	private CompactDisc cd;

	/**
	 *  注入方式一：构造函数注入
	 * 	这表明当Spring创建CDPlayer bean的时候， 会通过这个构造器来进行实例化并且会传入一个可设置给CompactDisc类型的bean
	 * 	注意： CompactDisc 唱片接口有2个实现类 beyond和黑豹乐队 这里注入就必须要通过 @Qualifier 来显示指定要注入哪个实现类
	 * 	否则spring会报错：No qualifying bean of type 'com.goat.chapter200.base.CompactDisc' available: expected single matching bean but found 2: beyond,bp
	 * @Date:   2019/8/7
	*/
	@Autowired(required = false)
	public CDPlayer(@Qualifier("bp") CompactDisc cd) {
		this.cd = cd;
	}

	@Override
	public void insert() {
		cd.play();
	}
}
