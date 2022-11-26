package ru.hogwarts.school.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FacultyServiceTest {
    @Autowired
    private FacultyRepository facultyRepository;

    private FacultyService out;

    @BeforeAll
    public void init() {
        out = new FacultyService(facultyRepository);
    }

    @Test
    public void addFaculty() {
        String name = "room";
        String color = "red";

        long expectedCount = facultyRepository.count() + 1;

        Faculty expected = new Faculty(name, color);
        Faculty actual = out.add(name, color);

        Assertions.assertNotNull(actual);
        expected.setId(actual.getId());
        long actualCount = facultyRepository.count();

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    public void getFaculty() {
        Faculty expected = out.add("room5", "yellow");
        Long id = expected.getId();

        Faculty actual = out.get(id);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void updateFaculty() {
        Faculty expected = out.add("room7", "red");
        expected.setColor("blue");
        expected.setName("room10");

        long expectedCount = facultyRepository.count();
        Faculty actual = out.update(expected);
        long actualCount = facultyRepository.count();

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    public void removeFaculty() {
        long expectedCount = facultyRepository.count();
        Faculty faculty = out.add("room15", "red");
        Long id = faculty.getId();

        long actualCount = facultyRepository.count();
        Assertions.assertEquals(expectedCount+1, actualCount);

        out.remove(id);
        actualCount = facultyRepository.count();
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    public void getAll() {
        Collection<Faculty> expected = new ArrayList<>(List.of(
                new Faculty("room_1", "color1"),
                new Faculty("room_2", "color5"),
                new Faculty("room_3", "color3"),
                new Faculty("room_4", "color1"),
                new Faculty("room_5", "color1")
        ));

        for (Faculty faculty : expected) {
            Faculty tmp = out.add(faculty.getName(), faculty.getColor());
            faculty.setId(tmp.getId());
        }

        Collection<Faculty> actual = out.getAll();

        Assertions.assertTrue(actual.containsAll(expected));

        for (Faculty faculty : expected) {
            out.remove(faculty.getId());
        }
    }

    @Test
    public void getFacultiesByColorOrNameCaseIgnored() {
        ArrayList<Faculty> faculties = new ArrayList<>();

        faculties.add(new Faculty("room_1", "color1"));
        faculties.add(new Faculty("room_2", "color5"));
        faculties.add(new Faculty("room_3", "color3"));
        faculties.add(new Faculty("room_4", "color1"));
        faculties.add(new Faculty("room_5", "color1"));

        for (Faculty faculty : faculties) {
            Faculty tmp = out.add(faculty.getName(), faculty.getColor());
            faculty.setId(tmp.getId());
        }

        Collection<Faculty> actual = out.getFacultiesByColorOrNameCaseIgnored(null, null);
        Assertions.assertTrue(actual.containsAll(faculties));


        Collection<Faculty> expected = new ArrayList<>();
        expected.add(faculties.get(0));
        expected.add(faculties.get(2));
        expected.add(faculties.get(3));
        expected.add(faculties.get(4));

        actual = out.getFacultiesByColorOrNameCaseIgnored("COLOR1", "ROom_3");
        Assertions.assertTrue(actual.containsAll(expected));
        Assertions.assertFalse(actual.containsAll(faculties));

        for (Faculty faculty : faculties) {
            out.remove(faculty.getId());
        }
    }

}
