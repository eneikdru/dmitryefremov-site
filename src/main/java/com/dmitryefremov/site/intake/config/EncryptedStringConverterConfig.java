package com.dmitryefremov.site.intake.config;

import com.dmitryefremov.site.intake.converter.EncryptedStringConverter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptedStringConverterConfig {

    @Value("${app.encryption.key:0123456789abcdef0123456789abcdef}")
    private String encryptionKey;

    @PostConstruct
    public void initConverterKey() {
        EncryptedStringConverter.setSecretKey(encryptionKey);
    }
}
