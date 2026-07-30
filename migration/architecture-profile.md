# M1 Architecture Profile

## 1. Purpose & domain

The Coolstore cart service manages customer shopping carts for an e-commerce application, providing cart management, product catalog integration, promotional pricing, and shipping calculations (pom.xml:lines 11-16). The application serves as the cart management backend for the Coolstore microservices architecture, handling cart state for web and mobile clients. The core domain centers around maintaining persistent cart state across user sessions, integrating with a separate product catalog service for pricing data, and applying promotional and shipping logic to calculate final order totals.

The service implements an in-memory cart storage model using HashMap (/projects/legacy/src/main/java/com/redhat/coolstore/service/ShoppingCartServiceImpl.java:42), supporting cart creation, item addition/removal, cart checkout, and promotional/shipping calculations. Business logic includes cart item promotions (25% off specific items like product "329299" from /projects/legacy/src/main/java/com/redhat/coolstore/service/PromoService.java:27), shipping tier-based calculations with free shipping over $75 from /projects/legacy/src/main/java/com/redhat/coolstore/service/PromoService.java:50-54, and total price derivation from item prices, quantities, and promotional discounts. Behavioral contracts for cart operations validated through ShoppingCartServiceTest and CartServiceBoundaryTest ensure expected functionality.

## 2. Components & relationships

The application architecture follows a layered service-oriented design with clear separation between REST endpoints, business services, and domain models (/projects/legacy/src/main/java/com/redhat/coolstore/rest/CartEndpoint.java:21, /projects/legacy/src/main/java/com/redhat/coolstore/service/ShoppingCartServiceImpl.java:28). The primary architectural components are: REST API layer (CartEndpoint), business service layer (ShoppingCartServiceImpl with supporting PromoService and ShippingService), Feign client layer (CatalogService), and domain model layer (ShoppingCart, Product, ShoppingCartItem, Promotion).

The dependency graph shows ShoppingCart as the central god node with fan-in from 5 classes (migration/dependency-order.md:10), followed by Product with fan-in from 4 classes and ShoppingCartItem with fan-in from 3 classes. ShoppingCartService acts as a central business orchestrator with 2 fan-in, 2 fan-out edges. The architectural pattern is classical layered architecture where CartEndpoint depends on ShoppingCartService, which coordinates PromoService, ShippingService, and CatalogService for business logic execution, with all services operating on shared domain model objects.

The conversion order specified in migration/dependency-order.md lines 16-29 establishes the safe migration sequence: model classes first (Product, Promotion, ShoppingCartItem, ShoppingCart), followed by service interfaces and implementations, then REST endpoints and configuration, ensuring the dependency tree compiles at each commit.

## 3. Integration surfaces

The cart service exposes REST API endpoints through the CartEndpoint at /api/cart/* (application.properties:line 2), providing HTTP methods for cart operations including GET cart retrieval, POST cart modification (add/set/checkout), and DELETE item removal. The API consumes and produces JSON media types, with session-scoped cart state managed via @Scope(WebApplicationContext.SCOPE_SESSION) annotation on CartEndpoint (CartEndpoint.java:line 22).

The service integrates externally with a product catalog service via Feign client CatalogService using OpenFeign dependency (pom.xml:line 71), with catalog endpoint URL configured through environment variable CATALOG_ENDPOINT (application.properties:line 6). The Feign client targets catalog endpoint for product data retrieval with the URL resolved from environment config.

External configuration relies on Spring Boot properties with environment variable overrides, specifically CATALOG_ENDPOINT for external service URLs. The application does not implement persistence, messaging, or database integrations — cart state is maintained in-memory using HashMap, requiring migration to persistent storage or session management strategy.

Preserve coverage analysis identifies demo-env-integration-00001 as the environment-driven configuration surface requiring preservation in migration.yaml preserve: contract, ensuring environment variable CATALOG_ENDPOINT and property-driven configuration mechanisms remain functional post-migration.

## 4. Behavioral contract sources

The application's behavioral contracts are validated through comprehensive test suites establishing expected functionality and numeric oracles for cart operations. The primary test classes are ShoppingCartServiceTest and CartServiceBoundaryTest, providing unit and integration test coverage respectively.

Key behavioral contracts include: cart initialization returns zero-valued totals (ShoppingCartServiceTest.java:lines 32-35), cart pricing calculates item totals and applies shipping tiers with specific numeric assertions (lines 49-53), and product retrieval through catalog service with mock-based validation (lines 58-63). The boundary test validates end-to-end cart operations through REST API calls, asserting specific cart totals of 2000.0 for item total, -10.99 for shipping promo savings, and 2000.0 for cart total (CartServiceBoundaryTest.java:lines 38-42).

Contract gaps exist in areas not covered by existing tests: cart expiration and cleanup mechanisms, error handling for invalid product IDs beyond current warning logs (ShoppingCartServiceImpl.java:line 156), concurrent cart access scenarios, and cart data persistence across application restarts. These gaps require characterization testing during migration to preserve behavioral expectations while migrating from in-memory storage to Quarkus-compatible state management.

## 5. Modernization surface

Per-component modernization requirements follow the findings inventory classifications, mapping mandatory and optional changes to each architectural component. The REST API layer requires javax-to-jakarta import migration (javax-to-jakarta-import-00001), Spring Boot to Quarkus dependency replacement (springboot-actuator-to-quarkus-0100), and annotation conversion from @RestController to JAX-RS @Path with native Quarkus support.

The business service layer mandates native CDI constructor injection replacing @Autowired field injection (springboot-di-to-quarkus-00003), converting ShoppingCartServiceImpl, PromoService, and ShippingService from Spring @Service/@Component stereotypes to Quarkus @ApplicationScoped services. The service implementation requires thread-safe concurrent hash map usage for cart storage with proper state management.

The configuration layer requires Spring Boot parent POM replacement with Quarkus BOM (springboot-parent-pom-to-quarkus-00000), Maven plugin conversion from spring-boot-maven-plugin to quarkus-maven-plugin (springboot-plugins-to-quarkus-0000), and property key migration for actuator endpoints to Quarkus health/metrics endpoints.

Integration surfaces need OpenFeign client replacement with Quarkus REST client for catalog service communication, maintaining environment-driven configuration through CATALOG_ENDPOINT while adapting the client implementation for Quarkus compatibility.

## 6. Domain boundaries

The cart service constitutes a single bounded context with cohesive domain functionality (/projects/legacy/src/main/java/com/redhat/coolstore/service/ShoppingCartServiceImpl.java:28), as evidenced by the tight coupling through ShoppingCart god node and the centralized business logic execution through ShoppingCartServiceImpl. All classes serve the unified purpose of cart management, promotional pricing, and shipping calculation within a single domain sphere (migration/dependency-order.md:16-29).

Domain seams for potential modernization cutting exist along technical boundaries rather than domain boundaries: the cart storage mechanism (HashMap → persistent storage in /projects/legacy/src/main/java/com/redhat/coolstore/service/ShoppingCartServiceImpl.java:42), the catalog service integration (Feign → Quarkus REST client from /projects/legacy/src/main/java/com/redhat/coolstore/service/CatalogService.java:10), and the promotion/shipping calculation rules (in-memory configuration → externalized configuration from /projects/legacy/src/main/java/com/redhat/coolstore/service/PromoService.java:24-28). These seams represent infrastructure modernization opportunities rather than domain decomposition.

The service demonstrates single responsibility principle adherence with clear separation between cart management, pricing logic, and external integration (/projects/legacy/src/main/java/com/redhat/coolstore/service/ShoppingCartServiceImpl.java:66-85), suggesting the entire service should migrate as one cohesive unit rather than splitting domain responsibilities across multiple bounded contexts during modernization.

## 7. Class roles & target contract

### REDESIGN

CartEndpoint — JAX-RS REST controller managing cart operations at /api/cart/* (/projects/legacy/src/main/java/com/redhat/coolstore/rest/CartEndpoint.java:21-23). Target: thread-safe singleton with ConcurrentHashMap for cart storage, GET returns 404 on missing cart IDs, POST validates input quantities >0 with 400 problem-detail on validation failure, POST operations are additive→quantity 4, 503 via ExceptionMapper on downstream failures. The endpoint must maintain session-scoped cart state while migrating from Spring session scope to Quarkus session management (/projects/legacy/src/main/java/com/redhat/coolstore/rest/CartEndpoint.java:22), ensuring cart operations preserve existing behavioral contracts from boundary tests (CartServiceBoundaryTest.java:35-46).

ShoppingCartServiceImpl — Core business service orchestrating cart operations, pricing calculations, and external service integration (/projects/legacy/src/main/java/com/redhat/coolstore/service/ShoppingCartServiceImpl.java:28-29). Target: @ApplicationScoped CDI service with constructor injection, thread-safe ConcurrentHashMap for cart storage (replacing HashMap in /projects/legacy/src/main/java/com/redhat/coolstore/service/ShoppingCartServiceImpl.java:42), compute() methods for atomic updates, no-clear-on-miss refresh guard for product cache, normalize-before-derive pricing ensuring cart totals agree with item totals (ShoppingCartServiceTest.java:49-53). Must preserve existing numeric oracles from tests (2000.0 item total, -10.99 shipping savings) while adding thread safety for concurrent cart access.

PromoService — Promotion calculation service applying item-level and shipping promotions (/projects/legacy/src/main/java/com/redhat/coolstore/service/PromoService.java:15). Target: @ApplicationScoped CDI service with constructor injection, thread-safe promotion set access, bounded refresh for promotion data, map catalog integration failures to 503 via ExceptionMapper. The service must preserve existing promotion logic (25% off product "329299" from /projects/legacy/src/main/java/com/redhat/coolstore/service/PromoService.java:27, free shipping over $75 from /projects/legacy/src/main/java/com/redhat/coolstore/service/PromoService.java:50-54) while ensuring thread-safe concurrent access and proper error handling.

ShippingService — Shipping calculation service determining shipping costs based on cart totals (/projects/legacy/src/main/java/com/redhat/coolstore/service/ShippingService.java:7). Target: @ApplicationScoped CDI service with constructor injection, thread-safe calculation methods, bounded shipping tier logic (/projects/legacy/src/main/java/com/redhat/coolstore/service/ShippingService.java:10-24). Must preserve existing shipping calculation tiers (free under $25, incremental increases to $10.99 over $100) while ensuring thread-safe concurrent execution.

CatalogService — Feign client interface for product catalog integration (/projects/legacy/src/main/java/com/redhat/coolstore/service/CatalogService.java:10). Target: Quarkus REST client replacement with @RegisterRestClient annotation, environment-driven URL configuration through ${CATALOG_ENDPOINT:default} (/projects/legacy/src/main/resources/application.properties:6), thread-safe client usage, map catalog service failures to 503 via ExceptionMapper. Must preserve existing product retrieval contracts while migrating from OpenFeign to native Quarkus REST client.

JerseyConfig — JAX-RS configuration class registering CartEndpoint (/projects/legacy/src/main/java/com/redhat/coolstore/rest/JerseyConfig.java:6). Target: removed — Quarkus auto-discovers JAX-RS resources, configuration class subsumed by Quarkus bootstrap.

CartServiceApplication — Spring Boot bootstrap and configuration class (/projects/legacy/src/main/java/com/redhat/coolstore/CartServiceApplication.java:7). Target: removed — Quarkus bootstrap and CDI replace Spring Boot application model, main class subsumed by Quarkus startup.

### HARVEST

ShoppingCart — Domain model representing cart state with totals and item collections (/projects/legacy/src/main/java/com/redhat/coolstore/model/ShoppingCart.java:7). Preserve existing behavior: cart ID management, item list manipulation, total calculation fields, serialization compatibility. Target: POJO with Jackson annotations for JSON serialization, preserved field names and types for test compatibility.

Product — Domain model for product information including pricing (/projects/legacy/src/main/java/com/redhat/coolstore/model/Product.java:5). Preserve existing behavior: item ID, name, description, price fields, constructors, serialization compatibility. Target: POJO with Jackson annotations, preserved field names and types for integration with catalog service.

ShoppingCartItem — Domain model for individual cart items with quantity and pricing (/projects/legacy/src/main/java/com/redhat/coolstore/model/ShoppingCartItem.java:5). Preserve existing behavior: product reference, quantity management, price calculations, promo savings tracking. Target: POJO with Jackson annotations, preserved field names and types for cart operations.

Promotion — Domain model for promotional pricing rules (/projects/legacy/src/main/java/com/redhat/coolstore/model/Promotion.java:3). Preserve existing behavior: item ID and percent off fields, constructors, promotion rule data. Target: POJO with Jackson annotations, preserved field structure for promotion service compatibility.

ShoppingCartService — Service interface defining cart operations contract (/projects/legacy/src/main/java/com/redhat/coolstore/service/ShoppingCartService.java:6). Preserve existing behavior: method signatures, return types, exception handling contracts. Target: interface preserved unchanged, implementation migrated to Quarkus CDI.

ProductsObjectMother — Test utility providing product data for testing (/projects/legacy/src/test/java/com/redhat/coolstore/ProductsObjectMother.java:7). Preserve existing behavior: static product creation methods, test data consistency. Target: test utility class preserved unchanged for test compatibility.