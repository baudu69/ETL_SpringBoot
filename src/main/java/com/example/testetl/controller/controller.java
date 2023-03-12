package com.example.testetl.controller;

import com.example.testetl.service.DbService;
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
@RequestMapping("/test")
public class controller {
	private final static Logger logger = LoggerFactory.getLogger(controller.class);
	private final DbService dbService;

	public controller(DbService dbService) {
		this.dbService = dbService;
	}


	@GetMapping
	@Transactional(rollbackOn = RuntimeException.class)
	public ResponseEntity<String> test(@RequestParam String table, @RequestParam String sql) {
		try {
			this.dbService.createTableFromRequest(table, sql);
			this.dbService.insertData(
					table,
					this.dbService.getRowFromRequest(sql)
			);
		} catch (RuntimeException e) {
			logger.warn(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}

		return ResponseEntity.noContent().build();
	}
}
