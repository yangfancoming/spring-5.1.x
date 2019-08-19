package com.goat.chapter606;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by 64274 on 2019/8/19.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/19---19:57
 */
public class AccountServiceImpl implements AccountService {

	private JdbcTemplate jdbcTemplate;

	private static String insert_sql = "insert into account(balance) values (100)";

	@Override
	public void save() throws RuntimeException {
		System.out.println("==开始执行sql");
		jdbcTemplate.update(insert_sql);
		System.out.println("==结束执行sql");

		System.out.println("==准备抛出异常");
		throw new RuntimeException("==手动抛出一个异常");
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}