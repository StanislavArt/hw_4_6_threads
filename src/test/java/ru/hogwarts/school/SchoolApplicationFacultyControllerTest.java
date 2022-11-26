package ru.hogwarts.school;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class SchoolApplicationFacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private FacultyService facultyService;

    @InjectMocks
    private FacultyController facultyController;

    @Test
    public void getFaculty() throws Exception {
        long id = 5;
        String name = "magic";
        String color = "red";

        Faculty faculty = new Faculty(name, color);
        faculty.setId(id);

        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/faculty/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void getAllFaculties() throws Exception {
        List<Faculty> faculties = new ArrayList<>(List.of(
                new Faculty("magic", "red"),
                new Faculty("math", "blue")
        ));

        Long id = 100L;
        for (Faculty faculty : faculties) {
            faculty.setId(id++);
        }

        when(facultyRepository.findAll()).thenReturn(faculties);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("*").isArray());
    }

    @Test
    public void addFaculty() throws Exception {
        long id = 5;
        String name = "magic";
        String color = "red";

        Faculty faculty = new Faculty(name, color);
        faculty.setId(id);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty/add?name="+name+"&color="+color)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(id))
                        .andExpect(jsonPath("$.name").value(name))
                        .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void updateFaculty() throws Exception {
        long id = 5;
        String name = "magic";
        String color = "red";
        Faculty faculty = new Faculty(name, color);
        faculty.setId(id);

        String newName = "magic_high";
        String newColor = "blue";
        Faculty updateFaculty = new Faculty(newName, newColor);
        updateFaculty.setId(id);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(updateFaculty);
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("name", newName);
        jsonObject.put("color", newColor);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/faculty/update/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.color").value(newColor));
    }

    @Test
    public void deleteFaculty() throws Exception {
        long id = 5;
        String name = "magic";
        String color = "red";

        Faculty faculty = new Faculty(name, color);
        faculty.setId(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/delete/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        verify(facultyRepository).deleteById(any(Long.class));
    }

    @Test
    public void getFacultiesByColorOrNameCaseIgnored() throws Exception {
        String color = "red";
        List<Faculty> faculties = new ArrayList<>(List.of(
                new Faculty("magic", color),
                new Faculty("math", color)
        ));

        Long id = 100L;
        for (Faculty faculty : faculties) {
            faculty.setId(id++);
        }

        when(facultyRepository.findByColorIgnoreCaseOrNameIgnoreCase(any(), any())).thenReturn(faculties);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filter?color="+color)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("*").isArray());

    }
}
