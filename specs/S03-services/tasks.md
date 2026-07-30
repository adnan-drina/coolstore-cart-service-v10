# S03 Services Migration Tasks

## Ordered Migration Checklist

### Rewrite Tasks (Mechanical Transformations)

#### T-001: Convert PromoService to Quarkus @ApplicationScoped CDI
**Class: rewrite**  
**Findings**: springboot-di-to-quarkus-00003  
**Target design**: → `src/main/java/com/demo/service/PromoService.java`

Converts PromoService from Spring @Component stereotype to Quarkus @ApplicationScoped CDI service with constructor injection. Implements thread-safe promotion storage using ConcurrentHashMap, preserves all existing promotion logic including 25% discount on product "329299" and free shipping threshold logic.

**Target contract**: @ApplicationScoped CDI service with constructor injection, thread-safe promotion set access, bounded refresh for promotion data.

**Owns**: src/main/java/com/redhat/coolstore/service/PromoService.java

#### T-002: Convert ShippingService to Quarkus @ApplicationScoped CDI  
**Class: rewrite**  
**Findings**: springboot-di-to-quarkus-00003  
**Target design**: → `src/main/java/com/demo/service/ShippingService.java`

Converts ShippingService from Spring @Component stereotype to Quarkus @ApplicationScoped CDI service with constructor injection. Implements thread-safe calculation methods, preserves all shipping tier calculations and business logic.

**Target contract**: @ApplicationScoped CDI service with constructor injection, thread-safe calculation methods, bounded shipping tier logic.

**Owns**: src/main/java/com/redhat/coolstore/service/ShippingService.java

#### T-003: Convert CatalogService from Feign to Quarkus REST Client
**Class: rewrite**  
**Findings**: springboot-di-to-quarkus-00003, demo-env-integration-00001, localhost-http-00001  
**Target design**: → `src/main/java/com/demo/service/CatalogService.java`

Converts CatalogService from OpenFeign @FeignClient to Quarkus @RegisterRestClient with environment-driven configuration through ${CATALOG_ENDPOINT:default} property resolution. Replaces Feign dependencies with Quarkus REST client, preserves CATALOG_ENDPOINT environment variable configuration.

**Target contract**: Quarkus REST client replacement with @RegisterRestClient annotation, environment-driven URL configuration through ${CATALOG_ENDPOINT:default}, thread-safe client usage, map catalog service failures to 503 via ExceptionMapper.

**Owns**: src/main/java/com/redhat/coolstore/service/CatalogService.java

#### T-004: Convert ShoppingCartService Interface Package Structure
**Class: rewrite**  
**Findings**: springboot-di-to-quarkus-00003  
**Target design**: → `src/main/java/com/demo/service/ShoppingCartService.java`

Migrates ShoppingCartService interface to new package structure. Interface remains unchanged but package name updated from com.redhat.coolstore.service to com.demo.service to support downstream implementation migration.

**Package structure**: Creates target package directory structure with .gitkeep marker.

**Owns**: src/main/java/com/redhat/coolstore/service/ShoppingCartService.java

### Infer Tasks (Design & Architecture)

#### T-005: Create Promotion Logic Characterization Tests
**Class: infer**  
**Findings**: springboot-di-to-quarkus-00003  
**Target design**: → `src/test/java/com/demo/service/PromoServiceTest.java`

Creates comprehensive characterization tests for PromoService promotion logic. Tests validate exact promotion behavior including 25% discount on product "329299" and free shipping threshold logic. Uses Mockito mocks for CatalogService dependencies not yet migrated.

**Test Coverage**: Item-level promotions, shipping promotions, promotion data management, edge case handling.

**Test Doubles**: Use Mockito mocks for ShoppingCartService and CatalogService dependencies.

#### T-006: Create Shipping Calculation Characterization Tests
**Class: infer**  
**Findings**: springboot-di-to-quarkus-00003  
**Target design**: → `src/test/java/com/demo/service/ShippingServiceTest.java`

Creates comprehensive characterization tests for ShippingService shipping tier calculations. Tests validate all five shipping tiers with exact dollar amounts and boundary conditions.

**Test Coverage**: All shipping tiers, boundary conditions, null input handling, cart total calculations.

**Test Doubles**: Use Mockito mocks for ShoppingCart dependencies.

#### T-007: Environment Configuration Validation
**Class: infer**  
**Findings**: demo-env-integration-00001, localhost-http-00001  
**Target design**: → `src/main/resources/application.properties`

Validates CATALOG_ENDPOINT environment variable integration works correctly in Quarkus. Creates test configuration files that demonstrate property resolution and environment variable fallback functionality.

**Validation Points**: 
- CATALOG_ENDPOINT property resolution from environment
- Default value substitution ${CATALOG_ENDPOINT:default}
- Cloud-readiness configuration validation

**Out of scope**: The following legacy files are owned by other stories:
- src/test/java/com/redhat/coolstore/service/ShoppingCartServiceTest.java - deferred to S05 service implementation story
- src/main/java/com/redhat/coolstore/rest/CartEndpoint.java - deferred to S04 REST endpoints story
- src/main/java/com/redhat/coolstore/rest/JerseyConfig.java - deferred to S04 REST endpoints story  
- src/main/java/com/redhat/coolstore/service/ShoppingCartServiceImpl.java - deferred to S05 service implementation story

**Legacy UI Surface**: This story modernizes service layer APIs. The legacy user interface is served by CartEndpoint REST controller, which is explicitly out of scope (deferred to S04) per the brief. No UI surface modernization or acceptance testing is performed in this service-layer story.

## Story Scope & Deployment

**Deployment Strategy**: This story is deploy=false per brief. Service layer modernization enables downstream stories S04 (REST endpoints) and S05 (service implementation integration). No acceptance endpoint implementation is required in this service story.

**Package Mapping**: Full prefix rename applied - com.redhat.coolstore → com.demo across all service classes and interfaces.

**Incidents Covered**:
- springboot-di-to-quarkus-00003: All @Component to @ApplicationScoped conversions covered in T-001, T-002, T-003, T-004
- demo-env-integration-00001: Environment configuration preserved in T-003, T-007  
- localhost-http-00001: Cloud-readiness configuration in T-003, T-007, T-010

**REDESIGN Classes Coverage**:
- ShoppingCartServiceImpl: Explicitly deferred to S05 per brief (S05 service implementation story) - §7 target shape: @ApplicationScoped CDI service with constructor injection, thread-safe ConcurrentHashMap for cart storage, compute() methods for atomic updates, no-clear-on-miss refresh guard for product cache, normalize-before-derive pricing ensuring cart totals agree with item totals
- CartEndpoint: Explicitly deferred to S04 per brief (S04 REST endpoints story) - §7 target shape: thread-safe singleton with ConcurrentHashMap for cart storage, GET returns 404 on missing cart IDs, POST validates input quantities >0 with 400 problem-detail on validation failure, POST operations are additive→quantity 4, 503 via ExceptionMapper on downstream failures
- JerseyConfig: Explicitly deferred to S04 per brief (S04 REST endpoints story) - §7 target shape: removed — Quarkus auto-discovers JAX-RS resources

**Out of Scope Files**: 
- ShoppingCartServiceImpl field injection patterns (deferred to S05)
- CartEndpoint REST controller (deferred to S04) 
- JerseyConfig configuration (deferred to S04)
- CartServiceApplication bootstrap (deferred to S06)
