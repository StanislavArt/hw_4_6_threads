package ru.hogwarts.school;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SchoolApplicationStudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void getAllStudents() {
        String url = "http://localhost:" + port + "/student";
        Collection<Student> students = testRestTemplate.getForObject(url, Collection.class);
        Assertions.assertTrue(students.size() > 0);
    }

    @Test
    public void getStudent() {
        String url = "http://localhost:" + port + "/student";
        ResponseEntity<Collection<Student>> response = testRestTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Student>>() {
        });
        Collection<Student> students = response.getBody();

        Assertions.assertTrue(students.size() > 0);

        Student studentExpected = students.stream().findFirst().orElseGet(null);
        Assertions.assertNotNull(studentExpected);

        long studentId = studentExpected.getId();

        url = "http://localhost:" + port + "/student/" + studentId;
        Student studentActual = testRestTemplate.getForObject(url, Student.class);
        Assertions.assertEquals(studentExpected, studentActual);
    }

    @Test
    public void getStudentsByAge() {
        String url = "http://localhost:" + port + "/student";
        ResponseEntity<Collection<Student>> response = testRestTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Student>>() {
        });
        Collection<Student> students = response.getBody();
        Assertions.assertTrue(students.size() > 0);

        int testMinAge = 23;
        int testMaxAge = 50;
        Collection<Student> studentsExpected = students.stream()
                .filter(student -> student.getAge() >= testMinAge && student.getAge() <= testMaxAge)
                .collect(Collectors.toList());

        url = "http://localhost:" + port + "/student/filter?minAge=" + testMinAge + "&maxAge=" + testMaxAge;
        response = testRestTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Student>>() {
        });
        Collection<Student> studentsActual = response.getBody();

        org.assertj.core.api.Assertions.assertThat(studentsActual).containsExactlyInAnyOrderElementsOf(studentsExpected);
    }

    @Test
    public void addStudent() {
        String testName = "testStudent";
        int testAge = 33;

        String url = "http://localhost:" + port + "/student/add?name=" + testName + "&age=" + testAge;
        Student actual = testRestTemplate.postForObject(url, null, Student.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(testName, actual.getName());
        Assertions.assertEquals(testAge, actual.getAge());
    }

    @Test
    public void updateStudent() {
        String testName = "testStudent";
        int testAge = 33;

        String url = "http://localhost:" + port + "/student/add?name=" + testName + "&age=" + testAge;
        Student student = testRestTemplate.postForObject(url, null, Student.class);
        Assertions.assertNotNull(student);
        Assertions.assertEquals(testName, student.getName());
        Assertions.assertEquals(testAge, student.getAge());

        long studentId = student.getId();
        student.setName("testNewStudent");
        student.setAge(25);

        url = "http://localhost:" + port + "/student/update/" + studentId;

        HttpEntity<Student> request = new HttpEntity<>(student);
        ResponseEntity<Student> response = testRestTemplate.exchange(url, HttpMethod.PUT, request, Student.class);
        Student actual = response.getBody();

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(student, actual);
    }

    @Test
    public void deleteStudent() {
        String testName = "testStudent";
        int testAge = 33;

        String url = "http://localhost:" + port + "/student/add?name=" + testName + "&age=" + testAge;
        Student student = testRestTemplate.postForObject(url, null, Student.class);
        Assertions.assertNotNull(student);
        Assertions.assertEquals(testName, student.getName());
        Assertions.assertEquals(testAge, student.getAge());

        long studentId = student.getId();
        url = "http://localhost:" + port + "/student/delete/" + studentId;
        testRestTemplate.delete(url);

        url = "http://localhost:" + port + "/student/" + studentId;
        Student studentActual = testRestTemplate.getForObject(url, Student.class);
        Assertions.assertNull(studentActual);
    }
}
