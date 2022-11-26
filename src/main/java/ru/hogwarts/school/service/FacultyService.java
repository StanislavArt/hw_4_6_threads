package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Set;

@Service
public class FacultyService {
    private Logger logger = LoggerFactory.getLogger(FacultyService.class);
	private FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty add(String name, String color) {
        logger.info("Method 'add()' was invoked");
		Faculty faculty = new Faculty(name, color);
        return facultyRepository.save(faculty);
    }

    public Faculty get(Long id) {
		logger.info("Method 'get()' was invoked");
        return facultyRepository.findById(id).get();
    }

    public Faculty update(Faculty faculty) {
		logger.info("Method 'update()' was invoked");
        return facultyRepository.save(faculty);
    }

    public void remove(Long id) {
		logger.info("Method 'remove()' was invoked");
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAll() {
		logger.info("Method 'getAll()' was invoked");
        return facultyRepository.findAll();
    }

    public Collection<Faculty> getFacultiesByColorOrNameCaseIgnored(String color, String name) {
        logger.info("Method 'getFacultiesByColorOrNameCaseIgnored()' was invoked");
		if (color == null && name == null) {
            return facultyRepository.findAllBy();
        }
        return facultyRepository.findByColorIgnoreCaseOrNameIgnoreCase(color, name);
    }

    public Set<Student> getStudentsByFaculty(Long id) {
		logger.info("Method 'getStudentsByFaculty()' was invoked");
        return get(id).getStudents();
    }
}
