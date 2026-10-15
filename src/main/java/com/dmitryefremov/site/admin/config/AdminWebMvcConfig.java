package com.dmitryefremov.site.admin.config;

import com.dmitryefremov.site.admin.interceptor.AdminAuthInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdminWebMvcConfig implements WebMvcConfigurer {

    private final ObjectProvider<AdminAuthInterceptor> adminAuthInterceptorProvider;

    public AdminWebMvcConfig(ObjectProvider<AdminAuthInterceptor> adminAuthInterceptorProvider) {
        this.adminAuthInterceptorProvider = adminAuthInterceptorProvider;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        AdminAuthInterceptor adminAuthInterceptor = adminAuthInterceptorProvider.getIfAvailable();
        if (adminAuthInterceptor != null) {
            registry.addInterceptor(adminAuthInterceptor)
                    .addPathPatterns("/api/admin", "/api/admin/**");
        }
    }
}
