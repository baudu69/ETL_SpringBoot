package com.example.testetl.service.connectors;

import com.example.testetl.exception.TableAlreadyExistException;
import com.example.testetl.objs.Colonnes;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class OracleConnector extends DBConnector{
	public OracleConnector(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
	}

	@Override
	public void insertData(String tableName, List<List<Object>> data) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT ALL ");
		for (List<Object> ligne : data) {
			StringBuilder ligneStr = new StringBuilder("INTO %s VALUES(".formatted(tableName));
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
			ligneStr.append(") ");
			sql.append(ligneStr);
		}
		sql.append("SELECT 1 FROM DUAL");
		final String sqlRequest = sql.toString();
		logger.info(sqlRequest);
		jdbcTemplate.execute(sqlRequest);
	}

	@Override
	public void createTable(String name, List<Colonnes> colonnes) {
		//Convert BIGINT to NUMBER(19,0)
		List<Colonnes> newColonnes = colonnes
				.stream()
				.map(colonne -> {
					if (colonne.type().equals("BIGINT")) {
						return new Colonnes(colonne.name(), "NUMBER(19,0)", null, false, colonne.nullable());
					}
					return colonne;
				})
				.toList();
		super.createTable(name, newColonnes);
	}

	@Override
	protected String prepareSQLCreateTable(String name, List<Colonnes> colonnes, boolean semiColon) {
		//On ne veut pas de virgule à la fin en oracle
		return super.prepareSQLCreateTable(name, colonnes, false);
	}

	@Override
	protected String getOnlyFirstResultSQL(String request) {
		return request + " FETCH FIRST 1 ROWS ONLY";
	}
}
