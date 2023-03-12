package com.example.testetl.exception;

public class TableAlreadyExistException extends RuntimeException {
	public TableAlreadyExistException(String tableName) {
		super("Table %s already exist".formatted(tableName));
	}
}
