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
class DepartamentoMutationIntegrationTest extends BasePostgresDepartamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldBlockDeleteWhenDepartamentoHasEmployees() throws Exception {
        String empleadoBody = objectMapper.writeValueAsString(Map.of(
            "clave", "EMPDEL001",
            "nombre", "Mario",
            "direccion", "Calle D",
            "telefono", "444"
        ));

        mockMvc.perform(post("/api/empleados")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(empleadoBody))
            .andExpect(status().isCreated());

        String createBody = objectMapper.writeValueAsString(Map.of(
            "clave", "DEPDEL001",
            "nombre", "RRHH",
            "descripcion", "People",
            "empleadosClaves", List.of("EMPDEL001")
        ));

        mockMvc.perform(post("/api/departamentos")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/departamentos/{clave}", "DEPDEL001")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectEmptyPatchAndSupportPutDelete() throws Exception {
        String createBody = objectMapper.writeValueAsString(Map.of(
            "clave", "DEPDEL002",
            "nombre", "Calidad",
            "descripcion", "QA"
        ));

        mockMvc.perform(post("/api/departamentos")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated());

        String putBody = objectMapper.writeValueAsString(Map.of(
            "nombre", "Calidad Total",
            "descripcion", "Quality",
            "empleadosClaves", List.of()
        ));

        mockMvc.perform(put("/api/departamentos/{clave}", "DEPDEL002")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(putBody))
            .andExpect(status().isOk());

        String emptyPatch = objectMapper.writeValueAsString(Map.of());
        mockMvc.perform(patch("/api/departamentos/{clave}", "DEPDEL002")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyPatch))
            .andExpect(status().isBadRequest());

        mockMvc.perform(delete("/api/departamentos/{clave}", "DEPDEL002")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNoContent());
    }
}
