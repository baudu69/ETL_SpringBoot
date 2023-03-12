package com.example.testetl.configuration;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class PostGreDatasourceConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.postgre")
	public DataSourceProperties postGreDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource postGreDataSource() {
		return postGreDataSourceProperties().initializeDataSourceBuilder().build();
	}
}
