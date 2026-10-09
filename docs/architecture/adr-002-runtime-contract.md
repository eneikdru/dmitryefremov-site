# ADR-002 - Runtime contract

Generated deterministically at project bootstrap. This file is the single source of truth
for which services this product runs against. `docker-compose.yml`, the build manifest and
the application configuration are CONSEQUENCES of what is declared here - never independent
decisions taken in one artifact.

## Declared services

```yaml
datastore: postgresql:16-alpine
```

The runtime datastore engine decision is owned by ARCHITECTURE (BARCAN-TAG-01).

## Consequences of the declaration

Once the line above names an engine, all four of these must follow from it, and a check in
the factory reports it when they do not:

1. `docker-compose.yml` provides that engine (`postgres:16-alpine`).
2. The build manifest declares that engine's driver (`org.postgresql:postgresql` in `pom.xml`).
3. The application configuration points at that engine (`jdbc:postgresql://...` in `src/main/resources/application.properties`).
4. **The test suite runs against that engine.**

## Architecture Decision & Ownership
- **Decision**: Formally establish PostgreSQL 16 (`postgresql:16-alpine`) as the single source of truth datastore engine for all system runtime environments.
- **Status**: Accepted
- **Role Owner**: BARCAN-TAG-01 (Architecture)
- **Next Implementation Owner**: BARCAN-TAG-09 (Data & Backend Infrastructure)
