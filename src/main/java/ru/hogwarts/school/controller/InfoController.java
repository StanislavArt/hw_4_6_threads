package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {
	@Value("${server.port}")
	private String port;
	
	@GetMapping("/getPort")
	public ResponseEntity<String> getPort() {
		String result = "Приложение запущено на порту " + port;
		return ResponseEntity.ok(result);
	}
}