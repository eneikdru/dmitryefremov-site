package com.dmitryefremov.site.admin.interceptor;

import com.dmitryefremov.site.admin.domain.AdminUser;
import com.dmitryefremov.site.admin.repository.AdminUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final ObjectProvider<AdminUserRepository> adminUserRepositoryProvider;
    private final ObjectProvider<PasswordEncoder> passwordEncoderProvider;

    public AdminAuthInterceptor(
            ObjectProvider<AdminUserRepository> adminUserRepositoryProvider,
            ObjectProvider<PasswordEncoder> passwordEncoderProvider) {
        this.adminUserRepositoryProvider = adminUserRepositoryProvider;
        this.passwordEncoderProvider = passwordEncoderProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AdminUserRepository adminUserRepository = adminUserRepositoryProvider.getIfAvailable();
        PasswordEncoder passwordEncoder = passwordEncoderProvider.getIfAvailable();

        if (adminUserRepository == null || passwordEncoder == null) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.toLowerCase().startsWith("basic ")) {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);

            final String[] values = credentials.split(":", 2);
            if (values.length == 2) {
                String username = values[0];
                String password = values[1];

                Optional<AdminUser> adminUserOpt = adminUserRepository.findByUsername(username);
                if (adminUserOpt.isPresent() && passwordEncoder.matches(password, adminUserOpt.get().getPasswordHash())) {
                    return true;
                }
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
