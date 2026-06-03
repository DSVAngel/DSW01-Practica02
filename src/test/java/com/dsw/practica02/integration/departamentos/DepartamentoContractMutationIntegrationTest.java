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
class DepartamentoContractMutationIntegrationTest extends BasePostgresDepartamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldHonorMutationContractStatuses() throws Exception {
        String createBody = objectMapper.writeValueAsString(Map.of(
            "clave", "DEPMUT001",
            "nombre", "Auditoria",
            "descripcion", "Control"
        ));

        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated());

        String putBody = objectMapper.writeValueAsString(Map.of(
            "nombre", "Auditoria Interna",
            "descripcion", "Control interno",
            "empleadosClaves", List.of()
        ));

        mockMvc.perform(put("/api/v1/departamentos/{clave}", "DEPMUT001")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(putBody))
            .andExpect(status().isOk());

        String patchBody = objectMapper.writeValueAsString(Map.of(
            "descripcion", "Compliance"
        ));

        mockMvc.perform(patch("/api/v1/departamentos/{clave}", "DEPMUT001")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/departamentos/{clave}", "DEPMUT001")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNoContent());
    }
}
