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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DepartamentoSecurityIntegrationTest extends BasePostgresDepartamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRequireAuthOnDepartmentAssociationEndpoints() throws Exception {
        String empleadoBody = objectMapper.writeValueAsString(Map.of(
            "clave", "EMPSEC001",
            "nombre", "Pilar",
            "direccion", "Calle C",
            "telefono", "333"
        ));

        mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(empleadoBody))
            .andExpect(status().isCreated());

        String createBody = objectMapper.writeValueAsString(Map.of(
            "clave", "DEPSEC001",
            "nombre", "Legal",
            "descripcion", "Normativo"
        ));

        mockMvc.perform(post("/api/v1/departamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated());

        String patchBody = objectMapper.writeValueAsString(Map.of(
            "empleadosClaves", List.of("EMPSEC001")
        ));

        mockMvc.perform(patch("/api/v1/departamentos/{clave}", "DEPSEC001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(patch("/api/v1/departamentos/{clave}", "DEPSEC001")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody))
            .andExpect(status().isOk());
    }
}
