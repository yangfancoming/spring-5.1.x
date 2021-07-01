package com.goat.chapter201.model;

/**
 * Created by Administrator on 2021/7/1.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/7/1---17:01
 */
public class User {

	private Long id;
	private String addr;//地址
	private String idCard;//身份证号

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
}
