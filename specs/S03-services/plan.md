# S03 Services Migration Plan

## M3 Plan: Service Layer Modernization to Quarkus CDI

### Findings Mapping

**Mandatory Findings Resolution**

1. **springboot-di-to-quarkus-00003** → **@ApplicationScoped CDI Constructor Injection**
   - **Rewrite**: Convert @Component to @ApplicationScoped across service implementations
   - **Rewrite**: Replace @Autowired field injection with constructor injection patterns  
   - **Infer**: Thread-safe state management improvements for concurrent access

2. **demo-env-integration-00001** → **Environment Configuration Preservation**
   - **Infer**: CATALOG_ENDPOINT environment variable integration for REST client
   - **Infer**: Property-driven configuration migration from Spring Boot to Quarkus

3. **localhost-http-00001** → **Cloud-Ready External Integration**
   - **Infer**: Environment-driven configuration ${CATALOG_ENDPOINT:default} for cloud deployment
   - **Infer**: REST client error handling and exception mapping strategy

### Class Modernization Strategy

**HARVEST Classes** (Behavioral Preservation)
- **ShoppingCartService** - Interface preserved unchanged, implementation migrated to Quarkus CDI
  - Method signatures maintained exactly (getShoppingCart, getProduct, deleteItem, checkout, addItem, set, priceShoppingCart)
  - Implementation converted from Spring @Service to @ApplicationScoped with constructor injection

**REDESIGN Classes** (Architecture Evolution)
- **PromoService** - @ApplicationScoped CDI with thread-safe promotion management
  - Thread-safe promotionSet access using ConcurrentHashMap or synchronized access
  - Constructor injection for dependencies, bounded refresh for promotion data
  - ExceptionMapper integration for downstream failures mapped to 503
  
- **ShippingService** - @ApplicationScoped CDI with thread-safe calculation methods  
  - Thread-safe shipping tier calculation logic
  - Constructor injection replacing @Component stereotype
  - Bounded shipping tier validation and error handling
  
- **CatalogService** - Quarkus REST client replacement for Feign
  - @RegisterRestClient annotation with environment-driven URL configuration
  - ${CATALOG_ENDPOINT:default} property resolution for cloud readiness
  - ExceptionMapper integration for service failures mapped to 503
  - Thread-safe client usage patterns

### Dependency Order Compliance

Service layer conversion follows `migration/dependency-order.md` lines 25-28:

1. **ShoppingCartService** (service interface) - Line 7 dependency order
2. **PromoService** (promotion calculation) - Line 8 dependency order  
3. **ShippingService** (shipping calculation) - Line 9 dependency order
4. **CatalogService** (REST client) - Line 5 dependency order (external integration first)

### Behavioral Contract Preservation

**Service Interface Compatibility**
- All ShoppingCartService methods preserve exact signatures and return types
- Exception handling contracts maintained for backward compatibility
- Null-handling behavior preserved from legacy implementation

**Business Logic Preservation**  
- **Promotion Logic**: 25% discount on product "329299" exact value (PromoService.java:27)
- **Shipping Tiers**: Five-tier structure with exact amounts ($2.99, $4.99, $6.99, $8.99, $10.99)
- **Free Shipping**: Threshold at $75.00 cart total preserved
- **Catalog Integration**: GET /api/products endpoint contract maintained

### Thread Safety Improvements

**Current State** (Legacy):
- HashSet<Promotion> not thread-safe for concurrent modification
- No synchronization in calculation methods
- Potential race conditions in promotion data updates

**Target State** (Quarkus):
- ConcurrentHashMap or synchronized access patterns for shared state
- Thread-safe calculation methods preventing concurrent modification
- Bounded refresh strategies for cached promotion data

### Configuration Integration

**Environment Variable Preservation**
- CATALOG_ENDPOINT maintained for catalog service URL configuration
- Property resolution via ${CATALOG_ENDPOINT:default} for cloud deployment
- Environment-driven configuration preserved per migration.yaml preserve contract

**Quarkus Integration Patterns**
- @ApplicationScoped for all service implementations
- Constructor injection replacing field injection
- ExceptionMapper for global error handling strategy

### Story Scope Boundaries

**In Scope**:
- Service layer interfaces and implementations modernized to Quarkus CDI
- Environment configuration preservation and cloud readiness
- Thread-safe service patterns and exception handling

**Out of Scope** (deferred to later stories):
- ShoppingCartServiceImpl field injection patterns (deferred to S05)
- CartEndpoint REST controller modernization (deferred to S04) 
- JerseyConfig configuration class (deferred to S04)
- CartServiceApplication bootstrap (deferred to S06)

### Acceptance Criteria (Story-Level)

- **Build Compatibility**: All service classes compile in Quarkus CDI context
- **Behavioral Fidelity**: Promotion and shipping calculations match legacy exactly
- **Environment Integration**: CATALOG_ENDPOINT configuration functional
- **Thread Safety**: Concurrent access patterns improved without behavior changes
- **Package Structure**: com.redhat.coolstore → com.demo migration applied correctly
- **Interface Compatibility**: ShoppingCartService methods unchanged and backward compatible
