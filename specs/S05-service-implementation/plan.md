# S05 Service Implementation Plan

## Quarkus Migration Mapping

This story modernizes ShoppingCartServiceImpl from Spring @Service with field injection to Quarkus @ApplicationScoped CDI service with constructor injection and thread-safe state management.

### Target Architecture
- **Service Scope**: @ApplicationScoped CDI bean (not @Singleton for mockability)
- **Injection Pattern**: Constructor injection replacing @Autowired field injection
- **State Management**: ConcurrentHashMap replacing HashMap for thread safety
- **Package Migration**: com.redhat.coolstore → com.demo (full prefix replacement)
- **Thread Safety**: ConcurrentHashMap compute() methods for atomic updates
- **Product Cache**: Bounded refresh policy with no-clear-on-miss guard

## Task Breakdown

### Class: rewrite (mechanical transformation)

#### T-001: Package and import migration
**Target**: → `src/main/java/com/demo/service/ShoppingCartServiceImpl.java`
- Package rename: `com.redhat.coolstore.service` → `com.demo.service`
- Import migration: `javax.annotation.PostConstruct` → `jakarta.annotation.PostConstruct`
- Jakarta namespace migration for all javax.* imports
- **Findings**: removed-javaee-modules-00020 (ShoppingCartServiceImpl.java:11)

#### T-002: Spring to Quarkus stereotype conversion  
**Target**: → `src/main/java/com/demo/service/ShoppingCartServiceImpl.java`
- Replace `@Service` with `@ApplicationScoped`
- Remove Spring framework imports
- Add Jakarta annotations if needed
- **Findings**: removed-javaee-modules-00020

#### T-003: Field injection to constructor injection conversion
**Target**: → `src/main/java/com/demo/service/ShoppingCartServiceImpl.java`
- Convert `@Autowired ShippingService ss` to constructor parameter
- Convert `@Autowired CatalogService catalogServie` to constructor parameter  
- Convert `@Autowired PromoService ps` to constructor parameter
- Add final fields and constructor assignment
- Remove field declarations
- **Findings**: springboot-di-to-quarkus-00003 (implied service layer modernization)

#### T-004: HashMap to ConcurrentHashMap migration
**Target**: → `src/main/java/com/demo/service/ShoppingCartServiceImpl.java`
- Replace `Map<String, ShoppingCart> carts` with `ConcurrentHashMap<String, ShoppingCart>`
- Replace `Map<String, Product> productMap` with `ConcurrentHashMap<String, Product>`
- Update initialization: `carts = new ConcurrentHashMap<>()`
- Update initialization: `productMap = new ConcurrentHashMap<>()`
- **Findings**: thread-safety modernization for concurrent access

### Class: infer (design decisions and implementation)

#### T-005: Atomic cart operations with compute() methods
**Target design**: → `src/main/java/com/demo/service/ShoppingCartServiceImpl.java`
- Implement `getShoppingCart()` using `carts.compute()` for atomic updates
- Implement `addItem()` using `carts.compute()` to prevent lost updates
- Implement `deleteItem()` using `carts.compute()` for safe concurrent access
- Preserve existing cart creation and pricing logic
- **Contract**: All ShoppingCartService interface methods unchanged
- **Thread Safety**: No synchronized blocks, use ConcurrentHashMap atomic methods
- **Behavioral Pin**: Cart `add()` behavior remains additive (quantity 4 after two add operations)

#### T-006: Product cache refresh guard implementation
**Target design**: → `src/main/java/com/demo/service/ShoppingCartServiceImpl.java`
- Add bounded refresh policy for productMap (avoid unbounded growth)
- Implement no-clear-on-miss guard (don't clear cache on product fetch failure)
- Add cache size monitoring and potential eviction policy
- Preserve existing catalogService.products() integration
- **Behavioral Pin**: Product retrieval and productMap caching behavior preserved
- **Contract**: CATALOG_ENDPOINT environment variable functional for catalog integration

#### T-007: ShoppingCartServiceTest migration to Quarkus
**Target**: → `src/test/java/com/demo/service/ShoppingCartServiceTest.java`
- Package migration: com.redhat.coolstore → com.demo
- Replace @SpringBootTest with @QuarkusTest
- Replace @MockBean with @InjectMock for CatalogService
- Update Spring imports to Jakarta equivalents
- Preserve all assertion logic and test method contracts
- **Unit Oracle**: Empty cart returns zero totals (cartItemPromoSavings=0.0, cartItemTotal=0.0, shippingPromoSavings=0.0, cartTotal=0.0)
- **Pricing Oracle**: Item total 2000.0, shipping promo savings -10.99, cart total 2000.0
- **Behavioral Pin**: All ShoppingCartService interface methods unchanged
- **Findings**: test framework modernization

#### T-008: CartServiceBoundaryTest migration to Quarkus  
**Target**: → `src/test/java/com/demo/CartServiceBoundaryTest.java`
- Package migration: com.redhat.coolstore → com.demo
- Replace @SpringBootTest with @QuarkusTest
- Replace TestRestTemplate with @Inject WebTarget or @RestClient
- Update HoverflyRule configuration for Quarkus REST client
- Preserve REST API contract testing
- **Boundary Oracle**: 2000.0 item total, -10.99 shipping savings (assert exact same values)
- **Integration**: End-to-end cart operations through CartEndpoint
- **Findings**: test framework modernization

## Service Method Contract Preservation

All interface methods must maintain existing behavioral contracts:

### getShoppingCart(String cartId)
- Creates new cart if not exists (legacy behavior)
- Automatic pricing on retrieval (existing logic)
- Returns fully initialized ShoppingCart object

### addItem(String cartId, String itemId, int quantity) 
- **CRITICAL**: Additive behavior preserved - two add(cartId, itemId, 2) → quantity 4 after dedupe
- Product validation with warning logging (existing behavior)
- Deduplication via dedupeCartItems() method
- Pricing recalculation after addition

### deleteItem(String cartId, String itemId, int quantity)
- Partial quantity handling (existing behavior)
- Complete item removal if quantity >= existing (existing behavior)
- Pricing recalculation after deletion

### checkout(String cartId)
- Cart emptying behavior (existing logic)
- Zero state pricing (existing logic)

### getProduct(String itemId)
- Catalog service integration (existing behavior)
- Product caching in productMap (existing behavior)
- Warning logging for invalid products (existing behavior)

### set(String cartId, String tmpId)
- Cart-to-cart item transfer (existing logic)
- Deduplication after transfer (existing behavior)
- Pricing recalculation on target cart

### priceShoppingCart(ShoppingCart sc)
- Promotion application (existing logic)
- Shipping calculation (existing logic) 
- Total derivation: cartTotal = cartItemTotal + shippingTotal (existing logic)

## External Service Integration Preservation
- **ShippingService**: ss.calculateShipping() with existing tier logic
- **CatalogService**: catalogServie.products() with CATALOG_ENDPOINT configuration
- **PromoService**: ps.applyCartItemPromotions() and ps.applyShippingPromotions()

## Thread Safety Requirements
- **Cart Storage**: ConcurrentHashMap eliminates HashMap race conditions
- **Atomic Updates**: Use compute() methods instead of get-then-put patterns
- **Product Cache**: ConcurrentHashMap prevents concurrent modification exceptions
- **No Synchronized Blocks**: Prefer ConcurrentHashMap atomic methods over manual synchronization

## Package Rename Specification
- **Source**: com.redhat.coolstore.service.ShoppingCartServiceImpl
- **Target**: com.demo.service.ShoppingCartServiceImpl
- **Pattern**: Full prefix replacement (never com.demo.coolstore.service)
- **Scope**: All source files, test files, and package declarations in story scope

## Behavioral Oracle Preservation
Must preserve exact numeric assertions from legacy tests:
- **Empty Cart**: cartItemPromoSavings=0.0, cartItemTotal=0.0, shippingPromoSavings=0.0, cartTotal=0.0
- **Priced Cart**: cartItemTotal=2000.0, shippingPromoSavings=-10.99, cartTotal=2000.0
- **Boundary Test**: 2000.0 item total, -10.99 shipping savings (CartServiceBoundaryTest lines 38-42)