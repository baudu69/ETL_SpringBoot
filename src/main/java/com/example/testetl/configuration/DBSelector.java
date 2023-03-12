package com.example.testetl.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DBSelector {
	private final JdbcTemplate mySqlJdbcTemplate;
	private final JdbcTemplate postGreJdbcTemplate;

	public DBSelector(
			@Qualifier("mySqlJdbcTemplate") JdbcTemplate mySqlJdbcTemplate,
			@Qualifier("postGreJdbcTemplate") JdbcTemplate postGreJdbcTemplate
	) {
		this.mySqlJdbcTemplate = mySqlJdbcTemplate;
		this.postGreJdbcTemplate = postGreJdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplate(String db) {
		return switch (db) {
			case "mysql" -> mySqlJdbcTemplate;
			case "postgre" -> postGreJdbcTemplate;
			default -> null;
		};
	}
}
