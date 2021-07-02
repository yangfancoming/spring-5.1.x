package com.goat.chapter201.bean.item03;


public class Hello {

	private String content ;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Hello() {
		System.out.println("Hello 构造函数 执行！");
	}
}
