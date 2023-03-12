package com.example.testetl.service.connectors;

import com.example.testetl.exception.TableAlreadyExistException;
import com.example.testetl.objs.Colonnes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class DBConnector {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected final JdbcTemplate jdbcTemplate;

	public DBConnector(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private static Class<?> convertTypesToSQLTypes(int type) {
		return switch (type) {
			case Types.VARCHAR, Types.LONGVARCHAR -> String.class;
			case Types.INTEGER, Types.TINYINT, Types.SMALLINT -> Integer.class;
			case Types.DOUBLE, Types.REAL, Types.NUMERIC, Types.DECIMAL -> Double.class;
			case Types.FLOAT -> Float.class;
			case Types.BOOLEAN -> Boolean.class;
			case Types.BIGINT -> Long.class;
			case Types.DATE -> LocalDate.class;
			case Types.TIME -> LocalTime.class;
			case Types.TIMESTAMP -> LocalDateTime.class;
			case Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY -> Byte[].class;
			case Types.CHAR -> Character.class;

			default -> throw new IllegalStateException("Unexpected value: " + type);
		};
	}

	public void createTable(String name, List<Colonnes> colonnes) {
		if (isTableExist(name)) {
			throw new TableAlreadyExistException(name);
		}

		final String sqlRequest = prepareSQLCreateTable(name, colonnes, true);
		logger.info("SQL request to create: {}", sqlRequest);
		this.jdbcTemplate.execute(sqlRequest);
	}

	protected String prepareSQLCreateTable(String name, List<Colonnes> colonnes, boolean semiColon) {
		StringBuilder sql = new StringBuilder("CREATE TABLE %s (".formatted(name));
		for (Colonnes colonne : colonnes) {
			sql.append(colonne.toSQL());
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
		if (semiColon) {
			sql.append(";");
		}
		return sql.toString();
	}

	public List<Colonnes> getColonnesOfRequest(String requestSQL) {
		List<Colonnes> colonnes = new ArrayList<>();
		String requestFirst = getOnlyFirstResultSQL(requestSQL);
		jdbcTemplate.query(requestFirst, (rs, rowNum) ->
		{
			ResultSetMetaData metadata = rs.getMetaData();
			colonnes.addAll(getColumnOfResult(metadata));
			return null;
		});
		return colonnes;
	}

	static boolean needComma(Object obj) {
		return
				obj instanceof String
						|| obj instanceof LocalDate
						|| obj instanceof Date;
	}

	boolean isTableExist(String tableName) {
		try {
			DatabaseMetaData dbm = Objects.requireNonNull(this.jdbcTemplate.getDataSource()).getConnection().getMetaData();
			ResultSet tables = dbm.getTables(null, null, tableName, null);
			return tables.next();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return false;
	}

	public abstract void insertData(String tableName, List<List<Object>> data);



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

	protected abstract String getOnlyFirstResultSQL(String request);
}
