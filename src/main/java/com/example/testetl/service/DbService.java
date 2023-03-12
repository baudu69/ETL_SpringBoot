package com.example.testetl.service;

import com.example.testetl.exception.TableAlreadyExistException;
import com.example.testetl.objs.Colonnes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DbService {
	private static final Logger logger = LoggerFactory.getLogger(DbService.class);
	private final JdbcTemplate jdbcTemplate;

	public DbService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public void createTable(String name, List<Colonnes> colonnes) {
		if (isTableExist(name)) {
			throw new TableAlreadyExistException(name);
		}
		StringBuilder sql = new StringBuilder("CREATE TABLE %s (".formatted(name));
		for (Colonnes colonne : colonnes) {
			sql.append(colonne.toSQL());
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(");");

		final String sqlRequest = sql.toString();
		logger.info(sqlRequest);
		this.jdbcTemplate.execute(sqlRequest);
	}

	private boolean isTableExist(String tableName) {
		try {
			DatabaseMetaData dbm = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection().getMetaData();
			ResultSet tables = dbm.getTables(null, null, tableName, null);
			return tables.next();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return false;
	}

	private static boolean needComma(Object obj) {
		return
				obj instanceof String
						|| obj instanceof LocalDate
						|| obj instanceof Date;
	}

	public void insertData(String tableName, List<List<Object>> data) {
		StringBuilder sql = new StringBuilder("INSERT INTO %s VALUES".formatted(tableName));
		for (List<Object> ligne : data) {
			StringBuilder ligneStr = new StringBuilder("(");
			for (Object obj : ligne) {
				if (needComma(obj)) {
					ligneStr
							.append("\"")
							.append(obj)
							.append("\"")
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
		logger.info(sqlRequest);
		this.jdbcTemplate.execute(sqlRequest);
	}

	public void createTableFromRequest(String tableName, String requestSQL) {
		List<Colonnes> colonnes = new ArrayList<>();
		String requestFirst = getOnlyFirstResultSQL(requestSQL);
		jdbcTemplate.query(requestFirst, (rs, rowNum) ->
		{
			ResultSetMetaData metadata = rs.getMetaData();
			colonnes.addAll(getColumnOfResult(metadata));
			return null;
		});
		this.createTable(tableName, colonnes);
	}

	private static List<Colonnes> getColumnOfResult(ResultSetMetaData metadata) throws SQLException {
		List<Colonnes> colonnes = new ArrayList<>();
		int nbColonnes = metadata.getColumnCount();
		for (int i = 1; i <= nbColonnes; i++) {
			String nameColumn = JdbcUtils.lookupColumnName(metadata, i);
			String type = JDBCType.valueOf(metadata.getColumnType(i)).getName();
			int taille = metadata.getPrecision(i);
			boolean nullable = metadata.isNullable(i) == ResultSetMetaData.columnNullable;
			colonnes.add(new Colonnes(nameColumn, type, taille, false, nullable));
		}
		return colonnes;
	}

	private static String getOnlyFirstResultSQL(String request) {
		return request.replace(";", " LIMIT 1;");
	}

	public List<List<Object>> getRowFromRequest(String sql) {
		return jdbcTemplate.query(sql, (rs, rowNum) ->
		{
			ResultSetMetaData metadata = rs.getMetaData();
			int nbColonnes = metadata.getColumnCount();
			List<Object> line = new ArrayList<>();
			for (int i = 1; i <= nbColonnes; i++) {
				line.add(rs.getObject(i, convertTypesToSQLTypes(metadata.getColumnType(i))));
			}
			return line;
		});
	}

	private static Class<?> convertTypesToSQLTypes(int type) {
		return switch (type) {
			case Types.VARCHAR -> String.class;
			case Types.INTEGER, Types.BIGINT -> Integer.class;
			case Types.DATE -> Date.class;
			default -> throw new IllegalStateException("Unexpected value: " + type);
		};
	}
}
