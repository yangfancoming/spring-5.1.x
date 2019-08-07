package com.goat.chapter200.autoconfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


/**
 *  不管是构造器、 Setter方法还是其他的方法， Spring都会尝试满足方法参数上所声明的依赖。
 *  假如有且只有一个bean匹配依赖需求的话， 那么这个bean将会被装配进来。
 *
 *  如果没有匹配的bean， 那么在应用上下文创建的时候， Spring会抛出一个异常。
 *  为了避免异常的出现， 你可以将@Autowired的required属性设置为false （默认为true）
 *
 *  如果有多个bean都能满足依赖关系的话， Spring将会抛出一个异常，表明没有明确指定要选择哪个bean进行自动装配。
 *  在第3章中， 我们 会进一步讨论自动装配中的歧义性
 * @Date:   2018/7/25
 */

@Component // doit 默认name 居然是  CDplayer  为啥又不是类名首字母小写了？  需要源码跟踪下
public class CDplayer implements MediaPlayer {

	private CompactDisc cd;

	/**
	 *  注入方式一：构造函数注入
	 * 	这表明当Spring创建CDPlayer bean的时候， 会通过这个构造器来进行实例化并且会传入一个可设置给CompactDisc类型的bean
	 * 	注意： CompactDisc 唱片接口有2个实现类 beyond和黑豹乐队 这里注入就必须要通过 @Qualifier 来显示指定要注入哪个实现类
	 * @Date:   2019/8/7
	*/
	@Autowired(required = false)
	public CDplayer(@Qualifier("bp") CompactDisc cd) {
		this.cd = cd;
	}

	@Override
	public void insert() {
		cd.play();
	}

}
