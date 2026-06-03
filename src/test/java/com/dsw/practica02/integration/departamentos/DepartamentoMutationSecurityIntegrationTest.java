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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DepartamentoMutationSecurityIntegrationTest extends BasePostgresDepartamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRequireAuthOnPutPatchDeleteDepartamentos() throws Exception {
        String createBody = objectMapper.writeValueAsString(Map.of(
            "clave", "DEPSEC900",
            "nombre", "Riesgos",
            "descripcion", "Risk"
        ));

        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated());

        String putBody = objectMapper.writeValueAsString(Map.of(
            "nombre", "Riesgos Corp",
            "descripcion", "Risk Mgmt",
            "empleadosClaves", List.of()
        ));

        mockMvc.perform(put("/api/v1/departamentos/{clave}", "DEPSEC900")
                .contentType(MediaType.APPLICATION_JSON)
                .content(putBody))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(patch("/api/v1/departamentos/{clave}", "DEPSEC900")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("descripcion", "Riesgo"))))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/v1/departamentos/{clave}", "DEPSEC900"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/v1/departamentos/{clave}", "DEPSEC900")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(putBody))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/departamentos/{clave}", "DEPSEC900")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNoContent());
    }
}
