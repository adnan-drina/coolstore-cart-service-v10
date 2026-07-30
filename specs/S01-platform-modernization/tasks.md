# S01 Platform Modernization - Tasks

## Task List (ordered: rewrite → infer)

#### T-001: Create package directory structure with .gitkeep files
**Class: rewrite**  
**Target design**: → `src/main/java/com/demo/`, `src/test/java/com/demo/`, `src/main/resources/`

Create the new package directory structure for the target namespace `com.demo` and populate with `.gitkeep` files to ensure empty directories are committable.

**Finds**: springboot-parent-pom-to-quarkus-00000, springboot-di-to-quarkus-00000, springboot-web-to-quarkus-00000

**Owns**: Package directory structure (no legacy files, new directory creation)

---

#### T-002: Convert Maven parent, dependencies, and plugins to Quarkus platform
**Class: rewrite**  
**Target design**: → `pom.xml` comprehensive modernization

Replace Spring Boot parent POM with Quarkus platform BOM, convert all dependencies to Quarkus equivalents, and update all Maven plugins to Quarkus versions:

```xml
<parent>
    <groupId>com.redhat.quarkus.platform</groupId>
    <artifactId>quarkus-bom</artifactId>
    <version>3.27.3.SP1</version>
</parent>
```

Convert dependencies: `spring-boot-starter-web` → `quarkus-rest`, `spring-boot-starter-actuator` → `quarkus-smallrye-health`, `spring-cloud-starter-openfeign` → `quarkus-rest-client`, etc. Convert plugins: `spring-boot-maven-plugin` → `quarkus-maven-plugin`, add Compiler/Surefire/Failsafe, add native profile. Update Java version to 21.

**Finds**: springboot-parent-pom-to-quarkus-00000, javaee-pom-to-quarkus-00010, javaee-pom-to-quarkus-00020, javaee-pom-to-quarkus-00030, javaee-pom-to-quarkus-00040, javaee-pom-to-quarkus-00050, javaee-pom-to-quarkus-00060, javaee-pom-to-quarkus-00080, springboot-actuator-to-quarkus-0100, springboot-di-to-quarkus-00000, springboot-metrics-to-quarkus-0100, springboot-metrics-to-quarkus-0200, springboot-plugins-to-quarkus-0000, springboot-properties-to-quarkus-00000, springboot-web-to-quarkus-00000, spring-components-00001, spring-components-00002

**Owns**: projects/legacy/pom.xml (complete platform modernization)

---

#### T-003: Harvest and convert application.properties configuration
**Class: rewrite**  
**Target design**: → `src/main/resources/application.properties`

Migrate configuration from legacy `/projects/legacy/src/main/resources/application.properties` to new location with Quarkus-compatible keys. Preserve `CATALOG_ENDPOINT` environment variable configuration.

**Finds**: springboot-properties-to-quarkus-00000, demo-env-integration-00001

---

#### T-004: Convert package namespace from com.redhat.coolstore to com.demo
**Class: rewrite**  
**Target design**: → Full package tree migration

Update Maven coordinates and Java package imports from `com.redhat.coolstore` to `com.demo` throughout the project structure.

**Finds**: springboot-parent-pom-to-quarkus-00000 (package mapping from migration.yaml)

**Absorbs**: com.redhat.coolstore.* classes (will be harvested in subsequent stories)

---

#### T-005: Create platform modernization verification tests
**Class: infer**  
**Target design**: → `src/test/java/com/demo/PlatformVerificationTest.java`

Create verification tests to ensure platform modernization succeeded:
- Maven build completes successfully with Quarkus dependencies
- All Spring Boot dependencies successfully converted to Quarkus equivalents  
- Package namespace migrated successfully to `com.demo`
- Configuration preservation verified

**Finds**: springboot-parent-pom-to-quarkus-00000, javaee-pom-to-quarkus-00010, springboot-properties-to-quarkus-00000

**Package verification**: com.redhat.coolstore → com.demo (full prefix replacement as specified in migration.yaml)

---

## Task Ordering Justification

Tasks follow the conversion order from migration/dependency-order.md: extensions and BOM first, then dependencies, then configuration, then tests. Rewrite tasks establish the mechanical foundation before infer tasks make architectural decisions.

**All rewrite tasks (T-001 through T-004)** establish the platform foundation.  
**All infer tasks (T-005)** make architectural decisions and verification for subsequent stories.

**S-INFTEST compliance**: After the first infer task (T-005), remaining tasks are also classified as infer, ensuring consistent task type throughout the architectural decision phase.

**S-PKGDIR compliance**: Package directory creation (T-001) includes `.gitkeep` files for committable empty directories.

**UI surface coverage**: The cart service exposes only REST API endpoints with no web UI surface. No legacy UI exists to modernize - all user interactions occur through `/api/cart/*` endpoints via HTTP clients.