package com.example.testetl.configuration;

import com.example.testetl.service.connectors.DBConnector;
import com.example.testetl.service.connectors.MySQLConnector;
import com.example.testetl.service.connectors.OracleConnector;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DBSelector {

	private final MySQLConnector mySQLConnector;
	private final MySQLConnector postGreConnector;
	private final OracleConnector oracleConnector;

	public DBSelector(
			@Qualifier("mySqlJdbcTemplate") JdbcTemplate mySqlJdbcTemplate,
			@Qualifier("postGreJdbcTemplate") JdbcTemplate postGreJdbcTemplate,
			@Qualifier("oracleJdbcTemplate") JdbcTemplate oracleJdbcTemplate
	) {
		this.mySQLConnector = new MySQLConnector(mySqlJdbcTemplate);
		this.postGreConnector = new MySQLConnector(postGreJdbcTemplate);
		this.oracleConnector = new OracleConnector(oracleJdbcTemplate);
	}

	public DBConnector getDBConnector(String db) {
		return switch (db) {
			case "mysql" -> this.mySQLConnector;
			case "postgre" -> this.postGreConnector;
			case "oracle" -> this.oracleConnector;
			default -> null;
		};
	}
}
