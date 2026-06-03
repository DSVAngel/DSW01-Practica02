package com.dsw.practica02.integration.departamentos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DepartamentoContractAssignmentIntegrationTest extends BasePostgresDepartamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExposeAssignmentContractOnCreate() throws Exception {
        String empleadoBody = objectMapper.writeValueAsString(Map.of(
            "clave", "EMPASG001",
            "nombre", "Ana",
            "direccion", "Calle A",
            "telefono", "111"
        ));

        mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(empleadoBody))
            .andExpect(status().isCreated());

        String departamentoBody = objectMapper.writeValueAsString(Map.of(
            "clave", "DEPASG001",
            "nombre", "Compras",
            "descripcion", "Procurement",
            "empleadosClaves", List.of("EMPASG001")
        ));

        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(departamentoBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.empleados[0].clave").value("EMPASG001"));
    }

    @Test
    void shouldReturnNotFoundWhenAssigningMissingEmpleado() throws Exception {
        String departamentoBody = objectMapper.writeValueAsString(Map.of(
            "clave", "DEPASG002",
            "nombre", "Logistica",
            "descripcion", "Ops"
        ));

        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(departamentoBody))
            .andExpect(status().isCreated());

        String patchBody = objectMapper.writeValueAsString(Map.of(
            "empleadosClaves", List.of("EMP_NO_EXISTE")
        ));

        mockMvc.perform(patch("/api/v1/departamentos/{clave}", "DEPASG002")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody))
            .andExpect(status().isNotFound());
    }
}
