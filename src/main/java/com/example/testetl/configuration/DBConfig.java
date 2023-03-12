package com.example.testetl.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DBConfig {
	@Bean
	@Primary
	public JdbcTemplate mySqlJdbcTemplate(@Qualifier("mySqlDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}



	@Bean
	public JdbcTemplate postGreJdbcTemplate(@Qualifier("postGreDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public JdbcTemplate oracleJdbcTemplate(@Qualifier("oracleDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}
