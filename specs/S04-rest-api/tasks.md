# S04: REST API modernization tasks

## Task sequence for story S04: REST API modernization

#### T-001: Add Quarkus REST dependency and remove Spring Boot dependencies
**Class**: rewrite  
**Finding rules**: jakarta-jaxrs-to-quarkus-00010,springboot-annotations-to-quarkus-00000

**Actions**:
- Add `io.quarkus:quarkus-resteasy-reactive` dependency to pom.xml
- Replace `jakarta.ws.rs:jakarta.ws.rs-api` with Quarkus-native REST dependency
- Remove Spring Boot dependencies (spring-boot-starter-web, spring-boot-starter-actuator)
- Remove spring-boot-maven-plugin
- Clean up pom.xml Spring-specific configuration
- Ensure RESTEasy integration for JAX-RS support
- Update dependency management with Quarkus BOM

**Target design**: → `pom.xml`  
**Owns**: pom.xml dependency management and Spring Boot removal

#### T-002: Package rename com.redhat.coolstore → com.demo
**Class**: rewrite

**Actions**:
- Update package declarations in CartEndpoint.java from `com.redhat.coolstore.rest` to `com.demo.rest`
- Update package declarations in JerseyConfig.java from `com.redhat.coolstore.rest` to `com.demo.rest`
- Update all import statements for models and services
- Create package-info.java file for new package structure
- Update any Spring @Component annotations to CDI equivalents

**Target design**: → `src/main/java/com/demo/rest/`  
**Owns**: com.redhat.coolstore.rest.CartEndpoint, com.redhat.coolstore.rest.JerseyConfig

#### T-003: Remove JerseyConfig configuration
**Class**: rewrite

**Actions**:
- Remove JerseyConfig.java entirely from src/main/java
- Verify Quarkus auto-discovery mechanism works for JAX-RS resources
- Test resource discovery during application startup
- Ensure no manual resource registration required

**Target design**: → Remove `src/main/java/com/demo/rest/JerseyConfig.java`  
**Owns**: JerseyConfig removal, JAX-RS auto-discovery

#### T-004: Remove CartServiceApplication Spring Boot bootstrap
**Class**: rewrite

**Actions**:
- Remove CartServiceApplication.java entirely (Spring Boot bootstrap model eliminated)
- Verify Quarkus CDI and bootstrap replace Spring Boot application model
- Clean up Spring @SpringBootApplication and main method
- Ensure no Spring Boot initialization remains

**Target design**: → Remove `src/main/java/com/demo/CartServiceApplication.java`  
**Absorbs**: src/main/java/com/redhat/coolstore/CartServiceApplication.java

#### T-005: Convert CartEndpoint to JAX-RS with Quarkus session management
**Class**: infer

**Actions**:
- Replace `@RestController` with JAX-RS `@Path` annotation
- Replace `@Scope(WebApplicationContext.SCOPE_SESSION)` with Quarkus session management
- Convert `@Autowired` field injection to constructor injection (preparing for S05 CDI)
- Convert javax.ws.rs imports to jakarta.ws.rs equivalents
- Preserve exact endpoint signatures and behavioral contracts
- Implement thread-safe design patterns per architecture profile §7
- Add proper exception mapping for downstream service failures
- Ensure cart state persistence across HTTP requests within user sessions

**Target design**: → `src/main/java/com/demo/rest/CartEndpoint.java`  
**Owns**: CartEndpoint JAX-RS conversion, session management, behavioral preservation

#### T-006: Add REST endpoint acceptance endpoint with real @Path substance
**Class**: infer

**Actions**:
- Add acceptance-check endpoint with path `/api/cart/acceptance-check`
- Implement actual @Path("/acceptance-check") GET method returning 200 status
- Add real @Path implementation that validates basic cart functionality
- Include proper JAX-RS annotations (@GET, @Produces(MediaType.APPLICATION_JSON))
- Return JSON response with acceptance status and basic cart service health
- Ensure endpoint is accessible at the exact path specified in migration.yaml:17

**Target design**: → `src/main/java/com/demo/rest/AcceptanceEndpoint.java`  
**Owns**: /api/cart/acceptance-check endpoint, deployment verification

#### T-007: Port characterization tests for REST endpoints
**Class**: infer

**Actions**:
- Create CartEndpointTest.java with @QuarkusTest
- Test all five legacy endpoints (GET cart, POST add, POST set, DELETE, POST checkout)
- Test session management preservation across requests
- Validate ShoppingCartService integration with boundary test oracles
- Test CATALOG_ENDPOINT environment variable functionality
- Ensure exact numeric contracts preserved (2000.0 item total, -10.99 shipping promo, 2000.0 cart total)
- Verify proper JSON serialization/deserialization

**Target design**: → `src/test/java/com/demo/rest/CartEndpointTest.java`  
**Owns**: REST endpoint verification, session management testing, behavioral validation

## UI Surface Coverage
**Out of scope**: This story modernizes REST API endpoints only. The legacy cart service has no traditional web UI — it's a backend microservice exposing REST API endpoints. The API surface is the user interface for this service. No web UI modernization required.

## Task ordering rationale
Following dependency-order.md: extensions → models → resources → config → tests

1. T-001: Add Quarkus dependencies and remove Spring Boot dependencies
2. T-002: Package rename and imports
3. T-003: Remove JerseyConfig
4. T-004: Remove CartServiceApplication
5. T-005: Core resource layer conversion
6. T-006: Add acceptance endpoint (deployment verification)
7. T-007: Characterization tests

## Quality gates
- All tasks must maintain buildability at each commit
- Session-scoped cart state preserved across HTTP requests
- JerseyConfig removed, resources auto-discovered
- Package transformation complete (com.redhat.coolstore → com.demo)
- Acceptance endpoint functional at /api/cart/acceptance-check
- Tests verify exact legacy behavioral contracts maintained
- CATALOG_ENDPOINT environment variable functional

## DEPLOY milestone
This story requires factory pipeline deployment and verification at /api/cart/acceptance-check endpoint. The acceptance endpoint (T-007) provides deployment validation with real @Path substance per migration.yaml:17.