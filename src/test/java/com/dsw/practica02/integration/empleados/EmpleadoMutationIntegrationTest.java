package com.dsw.practica02.integration.empleados;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@AutoConfigureMockMvc
class EmpleadoMutationIntegrationTest extends BasePostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPutPatchAndDeleteWithOptimisticConcurrency() throws Exception {
        String createBody = objectMapper.writeValueAsString(Map.of(
            "clave", "EMPINT003",
            "nombre", "Luis",
            "direccion", "Calle 3",
            "telefono", "999"
        ));

        mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated());

        String putBody = objectMapper.writeValueAsString(Map.of(
            "nombre", "Luis P",
            "direccion", "Calle 4",
            "telefono", "111"
        ));
        String etag = mockMvc.perform(get("/api/v1/empleados/{clave}", "EMPINT003")
            .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(header().exists("ETag"))
            .andReturn()
            .getResponse()
            .getHeader("ETag");

        mockMvc.perform(put("/api/v1/empleados/{clave}", "EMPINT003")
                .with(httpBasic("admin", "admin123"))
            .header("If-Match", etag)
                .contentType(MediaType.APPLICATION_JSON)
                .content(putBody))
            .andExpect(status().isOk());

        String patchBody = objectMapper.writeValueAsString(Map.of(
            "direccion", "Calle 5"
        ));

        mockMvc.perform(patch("/api/v1/empleados/{clave}", "EMPINT003")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody))
            .andExpect(status().isPreconditionRequired());

        mockMvc.perform(patch("/api/v1/empleados/{clave}", "EMPINT003")
                .with(httpBasic("admin", "admin123"))
                .header("If-Match", etag)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody))
            .andExpect(status().isPreconditionFailed());

        String currentEtag = mockMvc.perform(get("/api/v1/empleados/{clave}", "EMPINT003")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(header().exists("ETag"))
            .andReturn()
            .getResponse()
            .getHeader("ETag");

        mockMvc.perform(patch("/api/v1/empleados/{clave}", "EMPINT003")
                .with(httpBasic("admin", "admin123"))
                .header("If-Match", currentEtag)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/empleados/{clave}", "EMPINT003")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNoContent());
    }
}
