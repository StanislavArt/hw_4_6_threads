package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("student")
public class StudentController {
    private StudentService studentService;

    //public  StudentController() {}
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getAll() {
        Collection<Student> students = studentService.getAll();
        return ResponseEntity.ok(students);
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        Student student = studentService.get(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping("add")
    public ResponseEntity<Student> addStudent(@RequestParam String name, @RequestParam Integer age) {
        return ResponseEntity.ok(studentService.add(name, age));
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        if (id != student.getId()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (studentService.get(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(studentService.update(student));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Student> removeStudent(@PathVariable Long id) {
        studentService.remove(id);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * @param minAge  минимальный возраст (обязательный параметр)
     * @param maxAge  максимальный возраст (необязательный параметр)
     * @return коллекция студентов, попадающих по возрасту в заданный интервал.
     * Если параметр 'maxAge' не задан, то возвратится коллекция студентов,
     * возраст которых соответствует параметру 'minAge'.
     */
    @GetMapping("filter")
    public ResponseEntity<Collection<Student>> getStudentsByAge(@RequestParam int minAge, @RequestParam(required = false, defaultValue = "-1") int maxAge) {
        return ResponseEntity.ok(studentService.getStudentsByAge(minAge, maxAge));
    }

    @GetMapping("{id}/faculty")
    public ResponseEntity<Faculty> getFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getFacultyByStudent(id));
    }
}
