package com.example.testetl.configuration;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class OracleDatasourceConfiguration {
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.oracle")
	public DataSourceProperties oracleDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource oracleDataSource() {
		return oracleDataSourceProperties().initializeDataSourceBuilder().build();
	}
}
