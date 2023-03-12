package com.example.testetl.service.connectors;

import com.example.testetl.objs.Colonnes;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

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
		List<Colonnes> newColonnes = colonnes
				.stream()
				.map(OracleConnector::checkColonne)
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

	/**
	 * Convertit les types de colonnes pour Oracle
	 * @param colonne colonne à tenter convertir
	 * @return colonne convertie ou non
	 */
	private static Colonnes checkColonne(Colonnes colonne) {
		if (conversion.containsKey(colonne.type())) {
			return new Colonnes(colonne.name(), conversion.get(colonne.type()), null, false, colonne.nullable());
		}
		return colonne;
	}

	private final static Map<String, String> conversion = Map.of(
			"BIGINT", "NUMBER(19,0)"
	);
}
