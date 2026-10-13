package com.ikram.supervision;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests d'intégration : authentification JWT et contrôle des rôles.
 */
@SpringBootTest(properties = "app.simulation.enabled=false")
@AutoConfigureMockMvc
class ApiSecuriteTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private String token(String username, String password) throws Exception {
        String reponse = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode json = mapper.readTree(reponse);
        return json.get("token").asText();
    }

    @Test
    void accesRefuseSansToken() throws Exception {
        mvc.perform(get("/api/bornes")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginInvalideRetourne401() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"faux\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void technicienPeutConsulterLesBornes() throws Exception {
        mvc.perform(get("/api/bornes").header("Authorization", "Bearer " + token("technicien", "tech123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void technicienNePeutPasCreerDeBorne() throws Exception {
        mvc.perform(post("/api/bornes")
                        .header("Authorization", "Bearer " + token("technicien", "tech123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"X\",\"localisation\":\"Y\",\"nombrePrises\":2}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPeutGererLesUtilisateurs() throws Exception {
        mvc.perform(get("/api/utilisateurs").header("Authorization", "Bearer " + token("admin", "admin123")))
                .andExpect(status().isOk());
    }
}
