package com.example.testetl.service;

import com.example.testetl.configuration.DBSelector;
import com.example.testetl.objs.Colonnes;
import com.example.testetl.service.connectors.DBConnector;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DBService {
	private final DBSelector dbSelector;

	public DBService(DBSelector dbSelector) {
		this.dbSelector = dbSelector;
	}

	public void createSchema(String tableName, String sqlRequest, String fromDB, String toDB) {
		final DBConnector fromConnector = dbSelector.getDBConnector(fromDB);
		final DBConnector toConnector = dbSelector.getDBConnector(toDB);

		final List<Colonnes> colonnes = fromConnector.getColonnesOfRequest(sqlRequest);

		toConnector.createTable(tableName, colonnes);
	}

	public void extractData(String tableName, String sqlRequest, String fromDB, String toDB) {
		final DBConnector fromConnector = dbSelector.getDBConnector(fromDB);
		final DBConnector toConnector = dbSelector.getDBConnector(toDB);

		final List<List<Object>> lignes = fromConnector.getRowFromRequest(sqlRequest);

		toConnector.insertData(tableName, lignes);
	}
}
