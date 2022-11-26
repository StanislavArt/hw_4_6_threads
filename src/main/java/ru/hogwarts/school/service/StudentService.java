package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {
	private Logger logger = LoggerFactory.getLogger(StudentService.class);
    private StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student add(String name, int age) {
        logger.info("Method 'add()' was invoked");
		Student student = new Student(name, age);
		return studentRepository.save(student);
    }

    public Student get(Long id) {
		logger.info("Method 'get()' was invoked");
        return studentRepository.findById(id).orElse(null);
    }

    public Student update(Student student) {
        logger.info("Method 'update()' was invoked");
		return studentRepository.save(student);
    }

    public void remove(Long id) {
        logger.info("Method 'remove()' was invoked");
		studentRepository.deleteById(id);
    }

    public Collection<Student> getAll() {
        logger.info("Method 'getAll()' was invoked");
		return studentRepository.findAll();
    }

    public Collection<Student> getStudentsByAge(int minAge, int maxAge) {
        logger.info("Method 'getStudentsByAge()' was invoked");
		if (minAge <= 0) {
            logger.error("Неверный возраст: {} . Возраст должен быть положительным", minAge);
			throw new RuntimeException("Возраст должен быть положительным");
        }
        if (maxAge == -1 || minAge == maxAge) {
            return studentRepository.findByAge(minAge);
        }
        if (minAge >= maxAge) {
            logger.error("Диапазон возрастов задан неверно [{}, {}]", minAge, maxAge);
			throw new RuntimeException("Диапазон возрастов задан неверно");
        }
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public Faculty getFacultyByStudent(Long studentId) {
        logger.info("Method 'getFacultyByStudent()' was invoked");
		return get(studentId).getFaculty();
    }
}
