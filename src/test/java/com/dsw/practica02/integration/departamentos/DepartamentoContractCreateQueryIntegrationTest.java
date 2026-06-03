package com.dsw.practica02.integration.departamentos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DepartamentoContractCreateQueryIntegrationTest extends BasePostgresDepartamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExposeCreateAndQueryContractShape() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
            "clave", "DEPCTR001",
            "nombre", "Ventas",
            "descripcion", "Comercial"
        ));

        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clave").value("DEPCTR001"))
            .andExpect(jsonPath("$.nombre").value("Ventas"))
            .andExpect(jsonPath("$.descripcion").value("Comercial"))
            .andExpect(jsonPath("$.empleados").isArray());

        mockMvc.perform(get("/api/v1/departamentos/{clave}", "DEPCTR001")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clave").value("DEPCTR001"));
    }

    @Test
    void shouldRequirePagingParametersOnList() throws Exception {
        mockMvc.perform(get("/api/v1/departamentos")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isBadRequest());
    }
}
