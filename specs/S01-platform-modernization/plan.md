# S01 Platform Modernization Plan

## Maven Platform Conversion Strategy

### POM Conversion Overview

Convert Spring Boot 2.7.18 parent to Quarkus platform BOM with equivalent dependencies and build configuration.

**Conversion Target** (per MAPPINGS.md):
- Spring Boot parent → Quarkus platform BOM (com.redhat.quarkus.platform 3.27.3.SP1)
- Spring dependencies → Quarkus extensions
- Spring plugins → Quarkus plugins

## Task Classification (Rewrite vs Infer)

### Rewrite Tasks (Mechanical Transformation)

Tasks covered by OpenRewrite recipes - mechanical import/annotation/dependency swaps:

1. **springboot-parent-pom-to-quarkus-00000** → Replace Spring parent with Quarkus BOM
2. **javaee-pom-to-quarkus-00010/20/30/40/50/60** → Maven plugins and dependencies conversion
3. **javaee-pom-to-quarkus-00080** → Use Quarkus junit artifact
4. **springboot-actuator-to-quarkus-0100** → Replace Spring Actuator with quarkus-smallrye-health
5. **springboot-metrics-to-quarkus-0100** → Replace Micrometer with quarkus-smallrye-metrics
6. **springboot-plugins-to-quarkus-0000** → Replace spring-boot-maven-plugin
7. **springboot-properties-to-quarkus-00000** → Plain property support

### Infer Tasks (Design Decisions)

Tasks requiring judgment - design decisions based on brief and architecture profile:

1. **springboot-di-to-quarkus-00000** → Native CDI (NOT spring-di extension)
2. **springboot-web-to-quarkus-00000** → Native JAX-RS (NOT spring-web extension)
3. **demo-env-integration-00001** → Preserve CATALOG_ENDPOINT environment configuration

## Conversion Tasks Breakdown

### Extensions and BOM First (Per Migration Order)

**T-001: Convert Parent POM and Platform BOM**
- **Rule**: springboot-parent-pom-to-quarkus-00000, javaee-pom-to-quarkus-00010
- **Class**: rewrite
- **Target**: Quarkus platform BOM replaces Spring Boot parent
- **Evidence**: `/projects/legacy/pom.xml:17-26`

**T-002: Convert Maven Plugins**
- **Rule**: springboot-plugins-to-quarkus-0000, javaee-pom-to-quarkus-00020/30/40/50/60
- **Class**: rewrite
- **Target**: quarkus-maven-plugin replaces spring-boot-maven-plugin
- **Evidence**: `/projects/legacy/pom.xml:103-106`

**T-003: Convert Web Dependencies to Quarkus Extensions**
- **Rule**: jakarta-jaxrs-to-quarkus-00010
- **Class**: rewrite
- **Target**: quarkus-rest replaces spring-boot-starter-jersey
- **Evidence**: `/projects/legacy/pom.xml:60`

**T-004: Convert Spring Boot Actuator to Quarkus Health**
- **Rule**: springboot-actuator-to-quarkus-0100
- **Class**: rewrite
- **Target**: quarkus-smallrye-health replaces spring-boot-starter-actuator
- **Evidence**: `/projects/legacy/pom.xml:65`

**T-005: Convert Metrics to Quarkus MicroProfile Metrics**
- **Rule**: springboot-metrics-to-quarkus-0100
- **Class**: rewrite
- **Target**: quarkus-smallrye-metrics replaces Micrometer dependency
- **Evidence**: `/projects/legacy/pom.xml:65`

**T-006: Remove Spring Boot Test Dependencies**
- **Rule**: javaee-pom-to-quarkus-00080
- **Class**: rewrite
- **Target**: Use quarkus-junit instead of spring-boot-starter-test
- **Evidence**: `/projects/legacy/pom.xml:75-85`

**T-007: Package Namespace Conversion**
- **Rule**: springboot-di-to-quarkus-00000 (inferred from migration.yaml)
- **Class**: rewrite
- **Target**: com.redhat.coolstore → com.demo
- **Evidence**: `migration.yaml:legacyPackage → targetPackage`

**T-008: Remove Spring Web Dependency (Design Decision)**
- **Rule**: springboot-web-to-quarkus-00000
- **Class**: infer
- **Target**: Native JAX-RS instead of spring-web extension
- **Reasoning**: Brief mandates native CDI and native JAX-RS - no Spring compatibility layers
- **Evidence**: `/projects/legacy/pom.xml:55`

**T-009: Environment Configuration Preservation**
- **Rule**: demo-env-integration-00001
- **Class**: infer
- **Target**: CATALOG_ENDPOINT preserved in application.properties
- **Reasoning**: Brief explicitly preserves CATALOG_ENDPOINT for catalog service integration
- **Evidence**: `/projects/legacy/src/main/resources/application.properties:6`

## Test Coverage Strategy

**T-010: Verify Build Configuration**
- **Class**: infer
- **Target**: Maven build completes successfully with Quarkus BOM
- **Verification**: mvn clean compile succeeds
- **Scope**: Build system only - no source code changes in S01

## Dependency Order Compliance

This story follows the conversion order mandated by migration/dependency-order.md:
1. Platform/BOM changes first
2. No source code modifications (models, services, endpoints handled in S02-S06)
3. Build-time dependencies only

## Preserve Contract Implementation

**CATALOG_ENDPOINT**: Preserved environment variable for catalog service integration
- Maintained in application.properties for Feign/Quarkus REST client configuration
- Test configuration patterns preserved (CATALOG_ENDPOINT=http://localhost)

**Forbidden Contract**:
- No spring-di extension used (native CDI only)
- No spring-web extension used (native JAX-RS only)

## Package Rename Implementation

**Full Prefix Replace**: `com.redhat.coolstore.*` → `com.demo.*`
- Never creates `com.demo.coolstore` when targetPackage is `com.demo`
- Only modifies package declarations, not code structure
