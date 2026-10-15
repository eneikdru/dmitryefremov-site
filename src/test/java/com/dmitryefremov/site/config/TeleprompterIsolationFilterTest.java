package com.dmitryefremov.site.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class TeleprompterIsolationFilterTest {

    private TeleprompterIsolationFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new TeleprompterIsolationFilter();
        filterChain = mock(FilterChain.class);
    }

    @Test
    void shouldAllowNormalRequest() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/essays/some-essay");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }

    @Test
    void shouldBlockTeleprompterScriptPath() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/articles/teleprompter-raw-script-path");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verifyNoInteractions(filterChain);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_NOT_FOUND);
        assertThat(response.getContentAsString()).isEqualTo("{\"status\":404,\"message\":\"Not Found\"}");
    }

    @Test
    void shouldBlockInternalMountPath() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/mnt/HC_Volume_106932004/podcast/episodes/raw.txt");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verifyNoInteractions(filterChain);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_NOT_FOUND);
        assertThat(response.getContentAsString()).isEqualTo("{\"status\":404,\"message\":\"Not Found\"}");
    }
}
