# S01 Platform Modernization - Specification

## Legacy Behavior & API Contract

This story modernizes the Maven build platform and dependencies from Spring Boot to Quarkus. The legacy behavior centers on the `pom.xml` build configuration file, which defines the application's dependency tree, build plugins, and platform BOM selection.

### In-Scope Legacy Files

**`pom.xml`** - Maven project object model defining build configuration

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
</parent>

<properties>
    <java.version>11</java.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jersey</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### Behavioral Contract

The platform modernization must preserve existing behavioral expectations:

- **Build compatibility**: The application must continue to build successfully with Maven
- **Package namespace**: All Java packages must migrate from `com.redhat.coolstore` to `com.demo`
- **Environment configuration**: `CATALOG_ENDPOINT` environment variable must remain functional
- **Dependency functionality**: All existing Spring Boot dependencies must have functional Quarkus equivalents
- **Plugin functionality**: Maven build plugins must provide equivalent or enhanced functionality

### Preserved Configuration

- **CATALOG_ENDPOINT**: Environment variable for external catalog service integration (demo-env-integration-00001)
- **Java 11+ compatibility**: Must maintain Java 11+ build target (upgrading to Java 21)
- **Test framework**: Existing JUnit 4/5 test structure preserved

### Integration Surfaces

- **Maven build**: Platform BOM controls all dependency versions
- **Spring Cloud OpenFeign**: Feign client for catalog service integration 
- **Spring Boot Actuator**: Health and metrics endpoints
- **Testing framework**: Spring Boot test dependencies and configuration

### Contracts to Preserve

1. **CATALOG_ENDPOINT environment-driven configuration** (`demo-env-integration-00001`)
2. **Spring Boot Actuator health endpoints** (migrating to `/q/health`)
3. **Spring Boot metrics endpoints** (migrating to MicroProfile Metrics)
4. **OpenFeign catalog client** (migrating to Quarkus REST client)

### Out of Scope

This specification covers ONLY the `pom.xml` platform modernization. Source code changes, REST endpoints, service implementations, and domain models remain unchanged until subsequent stories (S02-S06).

**Legacy UI surface coverage**: The cart service does not expose a web UI - it is a pure REST API backend service. No legacy UI surface exists to waive.

All behavioral contracts from legacy tests remain unchanged - the platform modernization is purely a build-time transformation with no runtime behavior changes.

### Evidence Sources

- Legacy POM analysis from `/projects/legacy/pom.xml`
- MTA findings inventory rules for platform conversion
- Migration architecture profile decision on Quarkus platform BOM selection
- Spring Boot to Quarkus dependency mappings from MAPPINGS.md