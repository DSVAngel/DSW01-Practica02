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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DepartamentoAssignmentIntegrationTest extends BasePostgresDepartamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReassignEmpleadoBetweenDepartamentos() throws Exception {
        String empleadoBody = objectMapper.writeValueAsString(Map.of(
            "clave", "EMPREA001",
            "nombre", "Luis",
            "direccion", "Calle B",
            "telefono", "222"
        ));

        mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(empleadoBody))
            .andExpect(status().isCreated());

        String depA = objectMapper.writeValueAsString(Map.of(
            "clave", "DEPREA001",
            "nombre", "Soporte",
            "descripcion", "Mesa"
        ));
        String depB = objectMapper.writeValueAsString(Map.of(
            "clave", "DEPREA002",
            "nombre", "Desarrollo",
            "descripcion", "Software"
        ));

        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(depA))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(depB))
            .andExpect(status().isCreated());

        String patchA = objectMapper.writeValueAsString(Map.of(
            "empleadosClaves", List.of("EMPREA001")
        ));
        mockMvc.perform(patch("/api/v1/departamentos/{clave}", "DEPREA001")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchA))
            .andExpect(status().isOk());

        String patchB = objectMapper.writeValueAsString(Map.of(
            "empleadosClaves", List.of("EMPREA001")
        ));
        mockMvc.perform(patch("/api/v1/departamentos/{clave}", "DEPREA002")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchB))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/empleados/{clave}", "EMPREA001")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.departamento.clave").value("DEPREA002"));
    }
}
