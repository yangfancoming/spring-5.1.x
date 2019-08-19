package com.goat.chapter602;

/**
 * Created by 64274 on 2019/8/19.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/19---19:29
 */

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

/**
 * Spring编程式事物
 */
public class MyTransaction {

	private JdbcTemplate jdbcTemplate;
	private DataSourceTransactionManager txManager;
	private DefaultTransactionDefinition txDefinition;
	private String insert_sql = "insert into account (balance) values ('100')";

	public void save() {

		// 1、初始化jdbcTemplate
		DataSource dataSource = getDataSource();
		jdbcTemplate = new JdbcTemplate(dataSource);

		// 2、创建物管理器
		txManager = new DataSourceTransactionManager();
		txManager.setDataSource(dataSource);

		// 3、定义事物属性
		txDefinition = new DefaultTransactionDefinition();
		txDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		// 3、开启事物
		TransactionStatus txStatus = txManager.getTransaction(txDefinition);

		// 4、执行业务逻辑
		try {
			jdbcTemplate.execute(insert_sql);
			int i = 1/0;
			jdbcTemplate.execute(insert_sql);
			txManager.commit(txStatus);
		} catch (DataAccessException e) {
			txManager.rollback(txStatus);
			e.printStackTrace();
		}

	}

	public DataSource getDataSource() {

		DruidDataSource ds = new DruidDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://172.16.163.135:3306/spring?Unicode=true&characterEncoding=utf8");
		ds.setUsername("root");
		ds.setPassword("12345");

		ds.setInitialSize(5);
		ds.setMinIdle(5);
		ds.setMaxActive(10);
		ds.setMaxWait(6000);
		ds.setValidationQuery("SELECT 1 FROM DUAL");
		return ds;
	}

}