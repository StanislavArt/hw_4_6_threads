package ru.hogwarts.school.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentServiceTest {

    @Autowired
    private StudentRepository studentRepository;

    private StudentService out;

    @BeforeAll
    public void init() {
        out = new StudentService(studentRepository);
    }

    @Test
    public void addStudent() {
        String name = "Max";
        int age = 20;

        long expectedCount = studentRepository.count() + 1;

        Student expected = new Student(name, age);
        Student actual = out.add(name, age);

        Assertions.assertNotNull(actual);
        expected.setId(actual.getId());

        long actualCount = studentRepository.count();

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    public void getStudent() {
        Student expected = out.add("Иван", 23);
        Long id = expected.getId();

        Student actual = out.get(id);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void updateStudent() {
        Student expected = out.add("Иван", 23);
        expected.setAge(25);
        expected.setName("Николай");

        long expectedCount = studentRepository.count();
        Student actual = out.update(expected);
        long actualCount = studentRepository.count();

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    public void removeStudent() {
        long expectedCount = studentRepository.count();
        Student student = out.add("Иван", 23);
        Long id = student.getId();

        long actualCount = studentRepository.count();
        Assertions.assertEquals(expectedCount+1, actualCount);

        out.remove(id);
        actualCount = studentRepository.count();
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    public void getAll() {
        Collection<Student> expected = new ArrayList<>(List.of(
                new Student("student_1", 18),
                new Student("student_2", 19),
                new Student("student_3", 20),
                new Student("student_4", 18),
                new Student("student_5", 19)
        ));

        for (Student student : expected) {
            Student tmp = out.add(student.getName(), student.getAge());
            student.setId(tmp.getId());
        }

        Collection<Student> actual = out.getAll();

        Assertions.assertTrue(actual.containsAll(expected));

        for (Student student : expected) {
            out.remove(student.getId());
        }
    }

    @Test
    public void getStudentsByAge() {
        ArrayList<Student> students = new ArrayList<>();

        students.add(new Student("student_1", 18));
        students.add(new Student("student_2", 19));
        students.add(new Student("student_3", 20));
        students.add(new Student("student_4", 18));
        students.add(new Student("student_5", 25));

        for (Student student : students) {
            Student tmp = out.add(student.getName(), student.getAge());
            student.setId(tmp.getId());
        }

        Collection<Student> expected = new ArrayList<>();
        expected.add(students.get(0));  // student_1
        expected.add(students.get(3));  // student_4

        Collection<Student> actual = out.getStudentsByAge(18, -1);

        Assertions.assertTrue(actual.containsAll(expected));
        expected.add(students.get(1));  // student_2
        Assertions.assertFalse(actual.containsAll(expected));

        expected.clear();

        expected.add(students.get(2));  // student_3
        expected.add(students.get(4));  // student_5

        actual = out.getStudentsByAge(20, 30);

        Assertions.assertTrue(actual.containsAll(expected));
        expected.add(students.get(1));  // student_2
        Assertions.assertFalse(actual.containsAll(expected));

        for (Student student : students) {
            out.remove(student.getId());
        }

    }

    @Test
    public void getStudentsByAgeException() {
        Assertions.assertThrows(RuntimeException.class, () -> out.getStudentsByAge(-3, 20));
        Assertions.assertThrows(RuntimeException.class, () -> out.getStudentsByAge(30, 20));
    }

}
