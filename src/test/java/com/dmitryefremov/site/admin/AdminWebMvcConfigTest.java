package com.dmitryefremov.site.admin;

import com.dmitryefremov.site.admin.config.AdminWebMvcConfig;
import com.dmitryefremov.site.admin.interceptor.AdminAuthInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AdminWebMvcConfig.class)
public class AdminWebMvcConfigTest {
    @MockBean
    private AdminAuthInterceptor adminAuthInterceptor;

    @Autowired
    private AdminWebMvcConfig adminWebMvcConfig;

    @Test
    public void contextLoads() {
        assertThat(adminWebMvcConfig).isNotNull();
    }
}
