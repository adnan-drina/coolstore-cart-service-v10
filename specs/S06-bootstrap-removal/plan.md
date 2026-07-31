# S06: Application Bootstrap Removal - Migration Plan

## Migration Strategy: Spring Boot Bootstrap to Quarkus Native

This story completes the migration by removing the obsolete Spring Boot application bootstrap. Quarkus provides native application startup and CDI initialization, eliminating the need for `@SpringBootApplication` and the main class.

## Package Mapping: com.redhat.coolstore → com.demo

**Full prefix replacement**: `com.redhat.coolstore` → `com.demo` 
- All package declarations updated throughout the codebase
- Never `com.demo.coolstore` when `targetPackage` is `com.demo`

## Task Breakdown

### Class: rewrite
**T-001: Remove CartServiceApplication bootstrap class**
- **Target design**: → `src/main/java/com/demo/CartServiceApplication.java` (DELETE)
- **Absorbs**: `src/main/java/com/redhat/coolstore/CartServiceApplication.java` (legacy package)
- **Changes**: Delete the entire CartServiceApplication.java file
- **Rationale**: Quarkus provides native bootstrap and CDI initialization; Spring Boot main class is obsolete
- **Rule coverage**: `springboot-annotations-to-quarkus-00000`

### Class: infer
**T-002: Verify Quarkus application startup**
- **Owns**: Application startup verification
- **Changes**: 
  - Ensure `application.properties` contains Quarkus configuration for CATALOG_ENDPOINT
  - Verify no Spring Boot artifacts remain in dependency tree
  - Test application startup completes without SpringApplication.run()
- **Rationale**: Confirm Quarkus native startup replaces Spring Boot bootstrap lifecycle
- **Rule coverage**: `springboot-annotations-to-quarkus-00000`, `demo-env-integration-00001`

**T-003: Verify CDI service initialization**
- **Owns**: Service initialization verification
- **Changes**:
  - Test all @ApplicationScoped services (ShoppingCartServiceImpl, PromoService, ShippingService) initialize correctly
  - Verify CATALOG_ENDPOINT environment variable remains functional
  - Confirm REST endpoints /api/cart/* serve correctly
- **Rationale**: Ensure Quarkus CDI replaces Spring bean management for service initialization
- **Rule coverage**: `demo-env-integration-00001`, `springboot-di-to-quarkus-00003`

**T-004: Verify deployment acceptance**
- **Owns**: Factory deployment verification
- **Changes**:
  - Test health endpoint /q/health serves correctly
  - Verify application starts with Quarkus bootstrap
  - Confirm acceptance path serves (health check endpoint)
- **Rationale**: Complete deploy milestone verification for S06
- **Rule coverage**: N/A (deploy verification)

## Legacy Component Impact

### Removed Components
- `CartServiceApplication` — Spring Boot bootstrap and main class (T-001)

### Preserved Components  
- `ShoppingCartServiceImpl` — @ApplicationScoped service with constructor injection (from S05)
- `PromoService` — @ApplicationScoped service with constructor injection (from S03)
- `ShippingService` — @ApplicationScoped service with constructor injection (from S03)
- `CartEndpoint` — JAX-RS @Path REST controller at /api/cart/* (from S04)
- `CatalogService` — Quarkus REST client with @RegisterRestClient (from S03)

### Integration Preservation
- **Environment Variables**: CATALOG_ENDPOINT functionality maintained via Quarkus configuration
- **REST API**: /api/cart/* endpoints continue serving cart operations
- **Service Orchestration**: All cart operations, pricing, promotions work exactly as previous stories established
- **Health Monitoring**: /q/health endpoint available via quarkus-smallrye-health

## Migration Verification

### Functional Verification
- Application starts successfully with Quarkus native bootstrap
- All @ApplicationScoped services initialize via Quarkus CDI
- REST endpoints /api/cart/* serve cart operations correctly
- CATALOG_ENDPOINT environment variable controls catalog service URL

### Quality Gates
- Build compiles without Spring Boot bootstrap dependencies
- All tests pass (ShoppingCartServiceTest, CartServiceBoundaryTest)
- Health endpoint /q/health returns healthy status
- Factory pipeline green with deployed application

## Deployment Story Integration

This story (S06) includes deploy=true verification. The acceptance path is the health endpoint `/q/health` which must serve correctly after deployment to complete the deploy milestone.
