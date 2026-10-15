package com.dmitryefremov.site.admin;

import com.dmitryefremov.site.admin.domain.AdminUser;
import com.dmitryefremov.site.admin.repository.AdminUserRepository;
import com.dmitryefremov.site.admin.repository.TelemetryLogRepository;
import com.dmitryefremov.site.admin.domain.TelemetryLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private TelemetryLogRepository telemetryLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        adminUserRepository.deleteAll();
        telemetryLogRepository.deleteAll();

        adminUserRepository.save(new AdminUser("dmitry", passwordEncoder.encode("secret"), "test@test.com", "ADMIN"));
        telemetryLogRepository.save(new TelemetryLog("GQM_EVENT", "{}", "test"));
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthorizedAccess() throws Exception {
        String authString = "dmitry:secret";
        String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
        String authHeader = "Basic " + encodedAuthString;

        mockMvc.perform(get("/api/admin")
                .header("Authorization", authHeader))
                .andExpect(status().isOk());
    }

    @Test
    public void testExportUserData() throws Exception {
        String authString = "dmitry:secret";
        String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
        String authHeader = "Basic " + encodedAuthString;

        mockMvc.perform(post("/api/admin/gdpr/export?userId=testUser")
                .header("Authorization", authHeader))
                .andExpect(status().isOk());
    }

    @Test
    public void testEraseUserData() throws Exception {
        String authString = "dmitry:secret";
        String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
        String authHeader = "Basic " + encodedAuthString;

        mockMvc.perform(delete("/api/admin/gdpr/erasure?userId=testUser")
                .header("Authorization", authHeader))
                .andExpect(status().isOk());
    }
}
