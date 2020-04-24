package com.goat.chapter101.item12;

/**
 * Created by Administrator on 2020/4/24.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/24---10:06
 */
public class XiaoMing2 {

	private IBook _book;

	// 1.通过构造函数传递依赖对象
	public XiaoMing2(IBook book){
		this._book = book;
	}

	public void Study(){
		this._book.ShowMsg();
	}
}
