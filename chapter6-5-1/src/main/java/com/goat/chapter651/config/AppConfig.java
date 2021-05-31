package com.goat.chapter651.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


/**
 *  不加 @ComponentScan 会报错 No qualifying bean of type 'com.goat.chapter651.service.BookService' available
 *  因为不是 springboot 项目 需要手动自定组件扫描路径
 */
@Configuration
@MapperScan("com.goat.chapter651.dao") // 生成代理对象 交给spring的IOC容器管理
/**
 *  @MapperScan("com.goat.chapter651.dao")  等价于下面的xml配置
 * 	<bean id="mapperScan" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
 * 		<property name="basePackage" value="com.goat.chapter650.dao" />
 * 	</bean>
*/
@ComponentScan("com.goat.chapter651")
public class AppConfig {

	/**
	 * 等同于
	 * <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
	 *   <property name="dataSource" ref="dataSource" />
	 * </bean>
	*/
	@Bean
	public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource){
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		return sqlSessionFactoryBean;
	}

	// 使用 spring 内置 数据库连接池
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://47.98.148.84:3306/test2?Unicode=true&characterEncoding=utf8");
		ds.setUsername("root");
		ds.setPassword("12345");
		return ds;
	}

/**   使用 阿里巴巴 druid  数据库连接池
 *  @Bean
 * 	public DataSource dataSource() {
 * 		DruidDataSource ds = new DruidDataSource();
 * 		ds.setDriverClassName("com.mysql.jdbc.Driver");
 * 		ds.setUrl("jdbc:mysql://172.16.163.135:3306/test2?Unicode=true&characterEncoding=utf8");
 * 		ds.setUsername("root");
 * 		ds.setPassword("12345");
 * 		ds.setMaxActive(10);
 * 		ds.setMaxWait(6000);
 * 		ds.setValidationQuery("SELECT 1 FROM DUAL");
 * 		return ds;
 * 	}
*/

}
