package com.example.testetl.configuration;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class MySQLDatasourceConfiguration {
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.mysql")
	public DataSourceProperties mySqlDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	public DataSource mySqlDataSource() {
		return mySqlDataSourceProperties().initializeDataSourceBuilder().build();
	}
}
