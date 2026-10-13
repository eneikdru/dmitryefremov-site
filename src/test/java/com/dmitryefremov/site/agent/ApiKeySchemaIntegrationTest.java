package com.dmitryefremov.site.agent;

import com.dmitryefremov.site.agent.domain.ApiKey;
import com.dmitryefremov.site.agent.repository.ApiKeyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ApiKeySchemaIntegrationTest {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedApiKeysTable() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'API_KEYS'",
                Integer.class
        );

        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    void testFlywayMigrationCreatedIndexes() {
        List<String> indexNames = jdbcTemplate.queryForList(
                "SELECT UPPER(index_name) FROM information_schema.indexes WHERE UPPER(table_name) = 'API_KEYS'",
                String.class
        );

        assertThat(indexNames).contains("IDX_API_KEYS_PREFIX", "IDX_API_KEYS_STATUS", "IDX_API_KEYS_CLIENT_NAME");
    }

    @Test
    void testSaveAndRetrieveApiKeyByPrefixAndClient() {
        OffsetDateTime createdAt = OffsetDateTime.of(2026, 10, 13, 20, 34, 25, 0, ZoneOffset.UTC);
        OffsetDateTime expiresAt = createdAt.plusYears(1);

        ApiKey apiKey = new ApiKey(
                "ak_live_1234",
                "$argon2id$v=19$m=65536,t=3,p=4$c2FsdHNhbHQ$hashhashhashhashhashhash",
                "ARGON2ID",
                "External AI Agent Client",
                "a2a:read,a2a:write",
                "ACTIVE",
                expiresAt,
                createdAt
        );

        ApiKey saved = apiKeyRepository.save(apiKey);
        apiKeyRepository.flush();

        assertThat(saved.getId()).isNotNull();

        Optional<ApiKey> fetchedOptional = apiKeyRepository.findByKeyPrefix("ak_live_1234");
        assertThat(fetchedOptional).isPresent();

        ApiKey fetched = fetchedOptional.get();
        assertThat(fetched.getKeyHash()).isEqualTo("$argon2id$v=19$m=65536,t=3,p=4$c2FsdHNhbHQ$hashhashhashhashhashhash");
        assertThat(fetched.getAlgorithm()).isEqualTo("ARGON2ID");
        assertThat(fetched.getClientName()).isEqualTo("External AI Agent Client");
        assertThat(fetched.getScopes()).isEqualTo("a2a:read,a2a:write");
        assertThat(fetched.getStatus()).isEqualTo("ACTIVE");
        assertThat(fetched.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(fetched.getCreatedAt()).isEqualTo(createdAt);
        assertThat(fetched.getUpdatedAt()).isNotNull();

        List<ApiKey> clientKeys = apiKeyRepository.findByClientName("External AI Agent Client");
        assertThat(clientKeys).hasSize(1);
        assertThat(clientKeys.get(0).getKeyPrefix()).isEqualTo("ak_live_1234");
    }
}
