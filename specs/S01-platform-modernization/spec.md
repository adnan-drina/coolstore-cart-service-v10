# S01 Platform Modernization Specification

## Observed Legacy Behavior & API Contract

### Maven Build Configuration (/projects/legacy/pom.xml)

The legacy application uses Spring Boot 2.7.18 as its parent POM:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
</parent>
```

Key dependency management:
- Spring Cloud Dependencies 2021.0.9 (lines 35-45)
- Java version 11 (line 50)

### Dependency Stack Analysis

**Spring Boot Starter Dependencies** (lines 54-67):
1. `spring-boot-starter-web` - Web framework support
2. `spring-boot-starter-jersey` - JAX-RS implementation via Jersey
3. `spring-boot-starter-actuator` - Health, metrics, management endpoints

**Spring Cloud Dependencies** (lines 69-71):
- `spring-cloud-starter-openfeign` - Declarative REST client for catalog service integration

**Build Plugins** (lines 103-106):
- `spring-boot-maven-plugin` - Spring Boot packaging and run plugin

### Environment Configuration (/projects/legacy/src/main/resources/application.properties)

Environment-driven configuration surface:
```properties
# Catalog products endpoint used by the Feign CatalogService.
# Override with env CATALOG_ENDPOINT or -DCATALOG_ENDPOINT=...
CATALOG_ENDPOINT=http://localhost:8081
```

**Preservation requirement**: CATALOG_ENDPOINT must be preserved in migrated application for catalog service integration.

### Test Configuration (/projects/legacy/src/test/java/com/redhat/coolstore/service/ShoppingCartServiceTest.java:18)

Test overrides catalog endpoint:
```java
@SpringBootTest(properties = "CATALOG_ENDPOINT=http://localhost")
```

**Preservation requirement**: Environment-driven configuration pattern must continue working post-migration.

## Legacy Build Contract

1. **Build Tool**: Maven with Spring Boot parent
2. **Java Version**: 11
3. **Packaging**: JAR with spring-boot-maven-plugin
4. **Health Endpoints**: Spring Boot Actuator at /actuator/*
5. **REST Framework**: Jersey (JAX-RS) with Spring Boot integration
6. **External Integration**: Feign client for catalog service with CATALOG_ENDPOINT configuration
7. **Dependencies**: All Spring Boot starters, Spring Cloud OpenFeign
8. **Test Framework**: Spring Boot Test with JUnit 4 and AssertJ

## Source Code Dependencies (No Changes in S01)

The following source files remain unchanged during S01 (modified in subsequent stories):
- `/projects/legacy/src/main/java/com/redhat/coolstore/rest/CartEndpoint.java` - JAX-RS endpoints (S02)
- `/projects/legacy/src/main/java/com/redhat/coolstore/service/ShoppingCartServiceImpl.java` - Business logic (S03)
- `/projects/legacy/src/main/java/com/redhat/coolstore/service/CatalogService.java` - Feign client (S04)
- `/projects/legacy/src/main/java/com/redhat/coolstore/model/*.java` - Domain models (S05)
- `/projects/legacy/src/main/java/com/redhat/coolstore/CartServiceApplication.java` - Bootstrap (S06)

**Contract Requirement**: S01 must maintain buildability with these legacy dependencies intact until their respective stories convert them.
