# S01 Platform Modernization Tasks

Ordered checklist for platform and dependency modernization to Quarkus.
Brief scope: `pom.xml` and preserve pins in `application.properties`.
No Java sources under `src/main/java` in this story.

**UI surface: waived (API-only).** Legacy cart is REST-only (no
JSP/Thymeleaf/HTML web UI to migrate in S01).

**Deferred acceptance (migration.yaml):** `/api/cart/acceptance-check` is
owned by the deploy story (S04) on the real `CartEndpoint` with
catalog/products proof — not a status-map placeholder here (S-AC1 / G-OK).

#### T-001: Platform Modernization - Complete POM Conversion
**Class**: rewrite
**Target design**: → `pom.xml`
**Findings**: springboot-parent-pom-to-quarkus-00000, javaee-pom-to-quarkus-00010, javaee-pom-to-quarkus-00020, javaee-pom-to-quarkus-00030, javaee-pom-to-quarkus-00040, javaee-pom-to-quarkus-00050, javaee-pom-to-quarkus-00060, javaee-pom-to-quarkus-00080, springboot-actuator-to-quarkus-0100, springboot-di-to-quarkus-00000, springboot-metrics-to-quarkus-0100, springboot-metrics-to-quarkus-0200, springboot-plugins-to-quarkus-0000, springboot-properties-to-quarkus-00000, springboot-web-to-quarkus-00000, spring-components-00001, spring-components-00002
**Owns**: pom.xml

Complete Maven platform conversion in `pom.xml`:
1. Replace Spring Boot parent with Quarkus platform BOM (3.27.3.SP1)
2. Convert spring-boot-maven-plugin to quarkus-maven-plugin
3. Replace Spring Boot dependencies with Quarkus extensions:
   - spring-boot-starter-jersey → quarkus-rest
   - spring-boot-starter-actuator → quarkus-smallrye-health
   - spring-boot-starter-test → quarkus-junit5 + quarkus-rest-jackson (test)
   - Add quarkus-smallrye-metrics
4. Remove spring-boot-starter-web (native JAX-RS only); no spring-di / spring-web extensions
5. spring-components / web starter findings close via POM dependency removal (no UI code in this service)

#### T-002: Environment Configuration Preservation
**Class**: infer
**Target design**: → `src/main/resources/application.properties`
**Findings**: (preserve pin — CATALOG_ENDPOINT)
**Owns**: application.properties

Preserve `CATALOG_ENDPOINT` in `src/main/resources/application.properties`
for catalog service integration (brief behavioral pin). Do not invent mock
product fallbacks.
