package ru.hogwarts.school.controller;

import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryBasedSynchronizationStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.SchoolService;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("school")
public class SchoolController {
	
	private final SchoolService schoolService;
	
	public SchoolController(SchoolService schoolService) {
		this.schoolService = schoolService;
	}
	
	@GetMapping("total")
	public ResponseEntity<String> getTotalStudents() {
		Integer total = schoolService.getTotalStudents();
		String result = String.format("В школе учатся %d студентов", total);
		return ResponseEntity.ok().body(result);
	}
	
	@GetMapping("average")
	public ResponseEntity<String> getAverageAgeOfStudents() {
		Double averageAge = schoolService.getAverageAgeOfStudents();
		String result = String.format("Средний возраст студентов равен %.2f", averageAge);
		return ResponseEntity.ok().body(result);
	}

	@GetMapping("last-students")
	public ResponseEntity<Collection<Student>> getLastStudents(@RequestParam("number") Integer studentsNumber) {
		Collection<Student> students = schoolService.getLastStudents(studentsNumber);
		return ResponseEntity.ok().body(students);
	}
	
	@GetMapping("first-letter")
	public ResponseEntity<List<Student>> getStudentsSortedByFirstLetter (@RequestParam("letter") Character letter) {
		List<Student> students = schoolService.getStudentsSortedByFirstLetter(letter);
		return ResponseEntity.ok().body(students);
	}
	
	@GetMapping("average-age")
	public ResponseEntity<String> getAverageAgeOfStudentsByStream() {
		Double averageAge = schoolService.getAverageAgeOfStudents();
		String result;
		if (averageAge == null) {
			result = "Студентов в школе не обнаружено";
		} else { 
			result = String.format("Средний возраст студентов равен %.2f", averageAge);
		}
		return ResponseEntity.ok().body(result);
	}
	
	@GetMapping("longest-name-faculty")
	public ResponseEntity<String> getLongestFacultyName() {
		String name = schoolService.getLongestFacultyName();
		String result;
		if (name == null) {
			result = "Факультетов в школе не обнаружено";
		} else {
			result = String.format("'%s' - самое длинное название факультета", name);
		}
		return ResponseEntity.ok().body(result);
	}
	
	@GetMapping("stream-test")
	public ResponseEntity<String> getIntValueByStreamTest() {
		long startTime = System.currentTimeMillis();
		int sum = Stream.iterate(1, a -> a +1)
						.limit(5_000_000)
						.reduce(0, (a, b) -> a + b );
		long finishTime = System.currentTimeMillis();
		long duration = finishTime - startTime;

		String result = String.format("Return value: %d\nTest execution time: %d millisecund", sum, duration);
		return ResponseEntity.ok().body(result);
	}

	@GetMapping("stream-test-modify")
	public ResponseEntity<String> getIntValueByStreamTestModify() {
		long startTime = System.currentTimeMillis();

		int sum = IntStream.range(1, 5_000_001)
				.reduce(0, Integer::sum);

		long finishTime = System.currentTimeMillis();
		long duration = finishTime - startTime;

		String result = String.format("Return value: %d\nTest execution time: %d millisecund", sum, duration);
		return ResponseEntity.ok().body(result);
	}
}