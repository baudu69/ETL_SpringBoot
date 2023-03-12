package com.example.testetl.service.connectors;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class MySQLConnector extends DBConnector {
	public MySQLConnector(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
	}

	@Override
	public void insertData(String tableName, List<List<Object>> data) {
		StringBuilder sql = new StringBuilder("INSERT INTO %s VALUES".formatted(tableName));
		for (List<Object> ligne : data) {
			StringBuilder ligneStr = new StringBuilder("(");
			for (Object obj : ligne) {
				if (needComma(obj)) {
					ligneStr
							.append("'")
							.append(obj)
							.append("'")
							.append(",");
				} else {
					ligneStr
							.append(obj)
							.append(",");
				}

			}
			ligneStr.deleteCharAt(ligneStr.length() - 1);
			ligneStr.append("),");
			sql.append(ligneStr);
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(";");
		final String sqlRequest = sql.toString();
		jdbcTemplate.execute(sqlRequest);
	}

	@Override
	protected String getOnlyFirstResultSQL(String request) {
		return request.replace(";", " LIMIT 1;");
	}
}
