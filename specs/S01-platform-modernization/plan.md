# S01 Platform Modernization - Plan

## Target Quarkus Platform Design

This plan maps the Spring Boot platform dependencies and build configuration to Quarkus equivalents, establishing the foundation for subsequent code transformations.

### Quarkus Platform BOM

**Target design**: Replace Spring Boot parent POM with Quarkus platform BOM
- Spring Boot parent (`org.springframework.boot:spring-boot-starter-parent:2.7.18`) → Quarkus platform BOM (`com.redhat.quarkus.platform:quarkus-bom:3.27.3.SP1`)
- Update Java version from 11 to 21 for Quarkus 3.27 compatibility
- Preserve Red Hat GA repository for production dependencies

### Maven Plugin Conversion

**Target design**: Adopt Quarkus-aligned Maven plugins
- `spring-boot-maven-plugin` → `quarkus-maven-plugin` with platform group ID
- Add Maven Compiler plugin with Java 21 configuration
- Add Maven Surefire plugin for unit testing
- Add Maven Failsafe plugin for integration testing
- Add native build profile for Quarkus native compilation

### Dependency Conversion Strategy

**Core Platform Dependencies** (rewrite):
- `spring-boot-starter-web` → `quarkus-rest` (jakarta-jaxrs-to-quarkus-00010)
- `spring-boot-starter-jersey` → removed (native JAX-RS replaces Jersey)
- `spring-boot-starter-actuator` → `quarkus-smallrye-health` (springboot-actuator-to-quarkus-0100)

**Cloud & Integration Dependencies** (rewrite):
- `spring-cloud-starter-openfeign` → `quarkus-rest-client` (CatalogService integration)
- Spring Cloud dependency management → removed (Quarkus BOM provides versions)

**Testing Dependencies** (rewrite):
- `spring-boot-starter-test` → `quarkus-junit5` (javaee-pom-to-quarkus-00080)
- `junit-vintage-engine` → removed (Quarkus supports JUnit 5 natively)
- Additional test dependencies preserved (assertj, hoverfly-java)

**Metrics & Monitoring** (rewrite):
- Spring Boot Actuator metrics → `quarkus-smallrye-metrics` (springboot-metrics-to-quarkus-0100)
- Micrometer → MicroProfile Metrics (springboot-metrics-to-quarkus-0200)

### Properties & Configuration

**Target design**: Plain application.properties support (springboot-properties-to-quarkus-00000)
- `CATALOG_ENDPOINT` environment variable configuration preserved
- Spring Boot-specific property keys → Quarkus-compatible alternatives
- Actuator endpoints → `/q/health` and `/q/metrics`

### Package Renaming Strategy

**Target design**: Full prefix replacement `com.redhat.coolstore` → `com.demo`
- Maven coordinates: `com.redhat.coolstore:cart` → `com.demo:cart`
- Java package structure completely migrated
- No hybrid naming (`com.demo.coolstore` is incorrect when targetPackage is `com.demo`)

### Dependency Injection Decision

**Target design**: Native CDI (NOT spring-di extension) (springboot-di-to-quarkus-00000, springboot-di-to-quarkus-00003)
- No `quarkus-spring-di` extension dependency
- CDI will be configured in subsequent service conversion tasks
- Spring DI patterns → native CDI constructor injection

### Web Framework Decision

**Target design**: Native JAX-RS (NOT spring-web extension) (springboot-web-to-quarkus-00000)
- No `quarkus-spring-web` extension dependency
- JAX-RS annotations will be configured in REST endpoint conversion tasks
- Spring Web patterns → native JAX-RS

### Configuration Preservation

**Environment-driven configuration**:
- `CATALOG_ENDPOINT` environment variable maintained
- Default catalog service URL preserved in application.properties
- External service integration pattern preserved

**Health and metrics endpoints**:
- Spring Boot Actuator `/actuator/health` → Quarkus SmallRye Health `/q/health`
- Micrometer metrics → MicroProfile Metrics `/q/metrics`

### Task Classification

**Class: rewrite** - Mechanical transformations:
- POM parent and dependency management updates
- Dependency artifact ID and version changes
- Maven plugin replacements
- Test dependency conversions

**Class: infer** - Design decisions requiring judgment:
- Quarkus extension selection strategy (native CDI/JAX-RS vs spring compatibility)
- Native build profile configuration
- Property key migration strategy
- CATALOG_ENDPOINT configuration preservation

### Non-Deploy Story Scope

As this is a non-deploy story, REST endpoint implementation is deferred to the service-focused stories. This platform modernization provides the foundation but defers endpoint implementation to the deploy story.

### Package Structure Requirements

Package directory creation:
- Source tree: `src/main/java/com/demo/`
- Test tree: `src/test/java/com/demo/`
- Resources: `src/main/resources/`
- Each directory requires `.gitkeep` files for empty directory commits

### Quality Gates

- All Maven dependencies successfully resolve
- Clean Maven build with no compilation errors
- Test execution passes with Quarkus test framework
- No Spring Boot-specific dependencies remain (except for test utilities that will be converted later)
- Package namespace successfully migrated to `com.demo`