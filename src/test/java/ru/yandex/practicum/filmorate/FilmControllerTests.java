package ru.yandex.practicum.filmorate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@WebMvcTest(FilmController.class)
public class FilmControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Film testFilm;


    @BeforeEach
    public void setUp() {
        testFilm = new Film();
        testFilm.setName("test film");
        testFilm.setDescription("this is description test film");
        testFilm.setDuration(60);
        testFilm.setReleaseDate(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void createFilm_validData_filmCreated() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFilm)));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(testFilm.getName()))
                .andExpect(jsonPath("$.description").value(testFilm.getDescription()))
                .andExpect(jsonPath("$.duration").value(testFilm.getDuration()))
                .andExpect(jsonPath("$.releaseDate").value(testFilm.getReleaseDate().toString()));
    }

    @Test
    public void createFilm_durationIs1_filmCreated() throws Exception {
        testFilm.setDuration(1);;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isOk());
    }

    @Test
    public void createFilm_durationIsLessThan1_filmCreated() throws Exception {
        testFilm.setDuration(0);;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFilm_validData_filmUpdated() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFilm)));

        MvcResult result = resultActions.andReturn();
        String responseBody = result.getResponse().getContentAsString();

        Long filmId = JsonPath.parse(responseBody).read("$.id", Long.class);

        Film updatedFilm = new Film();
        updatedFilm.setId(filmId);
        updatedFilm.setName("updated film");
        updatedFilm.setDescription("updated description");
        updatedFilm.setReleaseDate(LocalDate.of(2022, 1, 1));
        updatedFilm.setDuration(34);

        resultActions = mockMvc.perform(put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFilm)));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(updatedFilm.getName()))
                .andExpect(jsonPath("$.description").value(updatedFilm.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(updatedFilm.getReleaseDate().toString()))
                .andExpect(jsonPath("$.description").value(updatedFilm.getDescription()));
    }

    @Test
    public void getAllFilms_responseStatusOK() throws Exception {
        mockMvc.perform(get("/films")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void createFilm_emptyName_validationFails() throws Exception {
        testFilm.setName(null);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFilm_blankName_validationFails() throws Exception {
        testFilm.setName("  ");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFilm_descriptionHasMoreThan200Chars_validationFails() throws Exception {
        testFilm.setDescription("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
                + "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, "
                + "sed diam voluptua. At vero eos et accusam et justo duo dolores e");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isBadRequest());
    }

}

