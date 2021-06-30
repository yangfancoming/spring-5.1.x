package com.goat.chapter201.autowire.item02;

import com.goat.chapter201.autowire.Dao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by 64274 on 2019/8/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/16---11:45
 */
public class Service {

	@Autowired
	private Dao mysqlDao;

	@Autowired
	private Dao mongoDao;

	public Dao getMysqlDao() {
		return mysqlDao;
	}

	public void setMysqlDao(Dao mysqlDao) {
		this.mysqlDao = mysqlDao;
	}

	public Dao getMongoDao() {
		return mongoDao;
	}

	public void setMongoDao(Dao mongoDao) {
		this.mongoDao = mongoDao;
	}

	@Override
	public String toString() {
		return "Service{" + "mysqlDao=" + mysqlDao + ", mongoDao=" + mongoDao + '}';
	}
}
