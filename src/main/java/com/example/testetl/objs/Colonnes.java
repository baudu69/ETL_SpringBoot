package com.example.testetl.objs;

public record Colonnes(String name, String type, Integer taille, boolean primaryKey, boolean nullable) {
	public String toSQL() {
		String sql = name;
		sql += " ";
		sql += type;
		if (this.taille != null && !this.type.equals("DATE")) {
			sql += String.format("(%s)", taille);
		}
		sql += " ";
		if (primaryKey) {
			sql += "primary key ";
		}
		if (nullable) {
			sql += "null ";
		}
		sql += ",";
		return sql;
	}
}

