# S04: REST API modernization plan

## Story scope
Modernize CartEndpoint REST controller from Spring @RestController to JAX-RS @Path with Quarkus session management. Remove JerseyConfig in favor of Quarkus auto-discovery.

## Migration strategy

### Core modernization
1. **JAX-RS migration**: Convert javax.ws.rs imports to jakarta.ws.rs equivalents for native JAX-RS support
2. **Session scope conversion**: Replace Spring WebApplicationContext.SCOPE_SESSION with Quarkus session management
3. **Resource registration elimination**: Remove JerseyConfig manual registration, leverage Quarkus auto-discovery
4. **Package transformation**: Full prefix replacement com.redhat.coolstore → com.demo

### Package mapping (com.redhat.coolstore → com.demo)
- **Affected imports**: All javax/jakarta imports updated
- **Service references**: ShoppingCartService injection updated to constructor injection pattern
- **Model references**: ShoppingCart, ShoppingCartItem, Product imports updated

### Session management transformation
- **Current**: Spring @Scope(WebApplicationContext.SCOPE_SESSION)
- **Target**: Quarkus session management preserving cart state persistence
- **Requirement**: Maintain existing behavioral contracts for cart operations

### Endpoint preservation
All legacy endpoint contracts maintained exactly:
- GET /cart/{cartId} → ShoppingCart JSON
- POST /cart/{cartId}/{itemId}/{quantity} → ShoppingCart JSON  
- POST /cart/{cartId}/{tmpId} → ShoppingCart JSON
- DELETE /cart/{cartId}/{itemId}/{quantity} → ShoppingCart JSON
- POST /cart/checkout/{cartId} → ShoppingCart JSON

## Task breakdown

### T-001: Package rename and imports
**Class**: rewrite  
**Scope**: Package prefix transformation com.redhat.coolstore → com.demo

**Actions**:
- Update package declarations in CartEndpoint.java and JerseyConfig.java
- Update all import statements for models and services
- Preserve import structure for javax/jakarta JAX-RS APIs
- Update pom.xml dependency coordinates

**Target design**: → `src/main/java/com/demo/rest/CartEndpoint.java`  
**Absorbs**: src/main/java/com/redhat/coolstore/rest/JerseyConfig.java

### T-002: JAX-RS dependency migration  
**Class**: rewrite  
**Scope**: Replace JAX-RS dependency with Quarkus-native equivalent

**Actions**:
- Add quarkus-rest dependency for JAX-RS support
- Update javax.ws.rs imports to jakarta.ws.rs where needed
- Ensure RESTEasy integration for Quarkus

**Target design**: → `pom.xml`  
**Owns**: JAX-RS dependency configuration

### T-003: Remove JerseyConfig configuration
**Class**: rewrite  
**Scope**: Eliminate manual resource registration

**Actions**:
- Remove JerseyConfig.java entirely
- Verify Quarkus auto-discovery mechanism
- Test resource discovery during startup

**Target design**: → Remove `src/main/java/com/demo/rest/JerseyConfig.java`  
**Owns**: JerseyConfig removal, JAX-RS resource discovery

### T-004: Convert CartEndpoint to native JAX-RS
**Class**: infer  
**Scope**: Transform Spring REST controller to JAX-RS resource with Quarkus session management

**Actions**:
- Replace @RestController with JAX-RS @Path annotation
- Convert Spring @Scope to Quarkus session management equivalent
- Implement constructor injection for ShoppingCartService (preparing for S05)
- Preserve exact endpoint signatures and behavioral contracts
- Maintain session-scoped cart state across requests
- Apply thread-safe design patterns per architecture profile §7

**Target design**: → `src/main/java/com/demo/rest/CartEndpoint.java`  
**Owns**: CartEndpoint JAX-RS conversion, session management

### T-005: Add REST endpoint characterization tests
**Class**: infer  
**Scope**: Verify endpoint behavior preservation through testing

**Actions**:
- Port CartServiceBoundaryTest to verify REST API contracts
- Test all five endpoints (GET, POST set, POST add, DELETE, POST checkout)
- Verify cart state persistence across requests within session
- Validate ShoppingCartService integration with proper boundary test oracles
- Ensure CATALOG_ENDPOINT environment variable functionality

**Target design**: → `src/test/java/com/demo/rest/CartEndpointTest.java`  
**Owns**: REST endpoint verification, session management testing

### T-006: Remove Spring Web and session dependencies
**Class**: rewrite  
**Scope**: Eliminate Spring Web dependencies no longer needed

**Actions**:
- Remove Spring Web dependencies from pom.xml
- Clean up any remaining Spring Boot annotations or imports
- Verify Quarkus RESTEasy integration works correctly

**Target design**: → `pom.xml`  
**Owns**: Spring Web dependency removal

## Migration order
Following dependency-order.md and extensions → models → resources → config → tests:

1. T-002: Add Quarkus REST dependency
2. T-001: Package rename and imports  
3. T-006: Remove Spring Web dependencies
4. T-003: Remove JerseyConfig
5. T-004: Convert CartEndpoint (resource layer conversion)
6. T-005: Add characterization tests

## Quality gates
- Session-scoped cart state maintained across HTTP requests
- All five REST endpoints functional with exact legacy behavior
- JerseyConfig removed, resources auto-discovered by Quarkus
- Package transformation com.redhat.coolstore → com.demo complete
- Boundary test oracles preserved (CartServiceBoundaryTest expectations)
- CATALOG_ENDPOINT environment variable functional
- Build passes with Quarkus-native dependencies

## Acceptance criteria
- Factory pipeline green with REST endpoints serving at /api/cart/*
- Session management functional preserving cart state persistence
- No JerseyConfig manual registration needed
- Tests verify exact legacy behavioral contracts maintained
