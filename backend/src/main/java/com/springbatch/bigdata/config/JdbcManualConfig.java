package com.springbatch.bigdata.config;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcManualConfig {
	
	@Bean
	public BasicDataSource getDataSource() {
		System.out.println("entering to database connection. ");
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

		dataSource.setInitialSize(2);
		System.out.println("datasource ::" + dataSource);
		
		return dataSource;		
	}
	
	@Bean
	public JdbcTemplate getSampleLiveLmsjdbcTemplate() {
		return new JdbcTemplate(getDataSource());
	}
}
