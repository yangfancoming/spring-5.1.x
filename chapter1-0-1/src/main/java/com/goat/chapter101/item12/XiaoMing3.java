package com.goat.chapter101.item12;

/**
 * Created by Administrator on 2020/4/24.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/24---10:08
 */
public class XiaoMing3 {

	private IBook _book;

	// 3.通过Setter方法传递依赖对象
	public void setBook(IBook _book){
		this._book = _book;
	}

	public void Study(){
		this._book.ShowMsg();
	}
}
