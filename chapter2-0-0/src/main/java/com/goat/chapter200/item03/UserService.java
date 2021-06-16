package com.goat.chapter200.item03;

import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2021/6/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/16---16:18
 */
@Service
public class UserService {

	UserDao userDao;

	public void query(){
		System.out.println("UserService query() -----------");
		userDao.query();
	}

	public void setUserDao(UserDao userDao){
		this.userDao = userDao;
	}
}
