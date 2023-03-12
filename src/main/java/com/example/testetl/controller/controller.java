package com.example.testetl.controller;

import com.example.testetl.exception.TableAlreadyExistException;
import com.example.testetl.service.DBService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class controller {
	private final static Logger logger = LoggerFactory.getLogger(controller.class);
	private final DBService dbService;

	public controller(DBService dbService) {
		this.dbService = dbService;
	}

	@GetMapping("/createTable")
	@Transactional
	public ResponseEntity<String> createTable(@RequestParam String table, @RequestParam String sql, @RequestParam String fromDb, @RequestParam String toDb) {
		try {
			this.dbService.createSchema(table, sql, fromDb, toDb);
		} catch (TableAlreadyExistException e) {
			logger.warn(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/extractData")
	@Transactional
	public ResponseEntity<String> extractData(@RequestParam String table, @RequestParam String sql, @RequestParam String fromDb, @RequestParam String toDb) {
		try {
			this.dbService.extractData(table, sql, fromDb, toDb);
		} catch (TableAlreadyExistException e) {
			logger.warn(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/extractAndCreate")
	@Transactional
	public ResponseEntity<String> extractAndCreate(@RequestParam String table, @RequestParam String sql, @RequestParam String fromDb, @RequestParam String toDb) {
		try {
			this.dbService.createSchema(table, sql, fromDb, toDb);
			this.dbService.extractData(table, sql, fromDb, toDb);
		} catch (TableAlreadyExistException e) {
			logger.warn(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return ResponseEntity.noContent().build();
	}


}
