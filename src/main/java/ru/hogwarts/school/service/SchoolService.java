package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

@Service
public class SchoolService {
	private Logger logger = LoggerFactory.getLogger(SchoolService.class);
	private final StudentRepository studentRepository;
	private final FacultyRepository facultyRepository;
	
	public SchoolService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
		this.studentRepository = studentRepository;
		this.facultyRepository = facultyRepository;
	}
	
	public Integer getTotalStudents() {
		logger.info("Method 'getTotalStudents()' was invoked");
		return studentRepository.getTotalStudents();
	}
	
	public Double getAverageAgeOfStudents() {
		logger.info("Method 'getAverageAgeOfStudents()' was invoked");
		Double averageAgeOfStudents = studentRepository.getAverageAgeOfStudents();
		return averageAgeOfStudents;
	}
	
	public List<Student> getLastStudents(int studentsNumber) {
		logger.info("Method 'getLastStudents()' was invoked");
		return studentRepository.getLastStudents(studentsNumber);
	}
	
	public List<Student> getStudentsSortedByFirstLetter(Character letter) {
		logger.info("Method 'getStudentsSortedByFirstLetter()' was invoked");
		List<Student> students = studentRepository.findAllBy();
		
		char letterLowerCase = Character.toLowerCase(letter.charValue());
		return students.stream()
					.filter( s -> Character.toLowerCase(s.getName().charAt(0)) == letterLowerCase)
					.sorted((s1,s2) -> s1.getName().toLowerCase().compareTo(s2.getName().toLowerCase()))
					.toList();
	}
	
	public Double getAverageAgeOfStudentsByStream() {
		logger.info("Method 'getAverageAgeOfStudentsByStream()' was invoked");
		List<Student> students = studentRepository.findAllBy();
		
		if (students == null || students.size() == 0) {
			return null;
		}

		return students.stream()
					.mapToInt(s -> s.getAge())
					.average().getAsDouble();
	}
	
	public String getLongestFacultyName() {
		logger.info("Method 'getLongestFacultyName()' was invoked");
		Collection<Faculty> faculties = facultyRepository.findAllBy();
		
		if (faculties == null || faculties.size() == 0) {
			return null;
		}
		
		return faculties.stream()
					.map(f -> f.getName())
					.reduce((f1,f2) -> f1.length() > f2.length() ? f1 : f2).get();
	}
	
}