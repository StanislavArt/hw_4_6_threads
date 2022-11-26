package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> getAll() {
        return ResponseEntity.ok(facultyService.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable Long id) {
        Faculty faculty = facultyService.get(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PostMapping("add")
    public ResponseEntity<Faculty> addFaculty(@RequestParam String name, @RequestParam String color) {
        return ResponseEntity.ok(facultyService.add(name, color));
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable Long id, @RequestBody Faculty faculty) {
        if (id != faculty.getId()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (facultyService.get(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facultyService.update(faculty));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Faculty> removeFaculty(@PathVariable Long id) {
        facultyService.remove(id);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * @param color
     * @param name
     * @return коллекция факультетов.
     * Если не заданы оба параметра, то будет возвращена коллекция, содержащая все факультеты.
     */
   @GetMapping("filter")
    public ResponseEntity<Collection<Faculty>> getFacultiesByColorOrNameCaseIgnored(@RequestParam(required = false) String color, @RequestParam(required = false) String name) {
        return ResponseEntity.ok(facultyService.getFacultiesByColorOrNameCaseIgnored(color, name));
    }

    @GetMapping("{id}/students")
    public ResponseEntity<Collection<Student>> getStudentsByFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getStudentsByFaculty(id));
    }
}
