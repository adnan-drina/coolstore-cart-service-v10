# S05 Service Implementation Tasks

**Story deployment requirement**: `story-deploy: true` (per migration.yaml acceptance.path)

## Class: rewrite (mechanical transformation)

#### T-001: Test migration to Quarkus
**Target design**: → `src/test/java/com/demo/service/ShoppingCartServiceTest.java`, `src/test/java/com/demo/CartServiceBoundaryTest.java`
**Class**: rewrite
**Absorbs**: src/test/java/com/redhat/coolstore/service/ShoppingCartServiceTest.java
**Absorbs**: src/test/java/com/redhat/coolstore/CartServiceBoundaryTest.java

Migrate unit and boundary tests from Spring Boot to Quarkus:
- Package migration: com.redhat.coolstore → com.demo
- Replace `@SpringBootTest` with `@QuarkusTest`
- Replace `@MockBean CatalogService catalogService` with `@InjectMock CatalogService catalogService`
- Replace Spring test imports with Jakarta / Quarkus equivalents
- Preserve all assertion logic and test method contracts
- **Behavioral Pin**: Empty cart returns zero totals (cartItemPromoSavings=0.0, cartItemTotal=0.0, shippingPromoSavings=0.0, cartTotal=0.0)
- **Pricing Oracle**: Item total 2000.0, shipping promo savings -10.99, cart total 2000.0
- **Boundary Oracle**: 2000.0 item total, -10.99 shipping savings (exact same values as legacy lines 38-42)

## Class: infer (design decisions and implementation)

#### T-002: ShoppingCartServiceImpl CDI + concurrency modernization
**Target design**: → `src/main/java/com/demo/service/ShoppingCartServiceImpl.java`
**Class**: infer
**Findings**: removed-javaee-modules-00020
**Owns**: src/main/java/com/demo/service/ShoppingCartServiceImpl.java

Decided shape (do not re-delegate):
```java
@ApplicationScoped
public class ShoppingCartServiceImpl implements ShoppingCartService {
  private final ShippingService shippingService;
  private final PromoService promoService;
  private final CatalogService catalogService;
  private final ConcurrentHashMap<String, ShoppingCart> carts = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Product> productMap = new ConcurrentHashMap<>();

  public ShoppingCartServiceImpl(ShippingService shippingService,
                                 PromoService promoService,
                                 @RestClient CatalogService catalogService) { ... }

  public ShoppingCart getShoppingCart(String cartId) {
    return carts.compute(cartId, (key, existing) -> { ... });
  }
  // addItem/deleteItem/checkout/set likewise via carts.compute(...)
}
```
Also: package `com.demo.service`; jakarta `@PostConstruct`; no Spring `@Service`/`@Autowired`;
no-clear-on-miss on catalog fetch failure; bounded productMap refresh; preserve
`catalogService.getProducts()` + `CATALOG_ENDPOINT`; additive add() after dedupe
(two add(..., 2) → quantity 4).

#### T-003: Verify existing catalog-backed acceptance (S04)
**Target design**: → `src/main/java/com/demo/rest/AcceptanceEndpoint.java`
**Class**: infer

Do **NOT** add a status/ok DTO or a second acceptance method on CartEndpoint (G-OK / G-CAT / O-M3ACCEPT).
S04 already serves `GET /api/cart/acceptance-check` returning `List<Product>` via
`@RestClient CatalogService.getProducts()`:

```java
@Path("/api/cart")
@ApplicationScoped
public class AcceptanceEndpoint {
  public AcceptanceEndpoint(@RestClient CatalogService catalogService) { ... }
  @GET @Path("acceptance-check") @Produces(MediaType.APPLICATION_JSON)
  public List<Product> acceptanceCheck() { return catalogService.getProducts(); }
}
```

Confirm products[] (not `{"status":"ok",...}`), keep
`CATALOG_ENDPOINT=http://catalog-service:8080` in `k8s/app.yaml`. If satisfied,
ALREADY COMPLETE — do not rewrite CartEndpoint.

## Story Acceptance Verification

### Story deploy=true verification requirements:
- Factory pipeline builds successfully
- All unit/boundary tests pass with preserved oracles
- **Acceptance** `/api/cart/acceptance-check` returns 200 with non-empty JSON product array (G-CAT)
- Thread-safe concurrent cart ops; no-clear-on-miss catalog cache
- CATALOG_ENDPOINT functional; package com.demo

### Package ownership:
- **Owns**: src/main/java/com/demo/service/ShoppingCartServiceImpl.java (T-002)
- **Absorbs**: src/test/java/com/redhat/coolstore/service/ShoppingCartServiceTest.java (T-001)
- **Absorbs**: src/test/java/com/redhat/coolstore/CartServiceBoundaryTest.java (T-001)

### Legacy UI Surface Coverage:
**Out of scope**: CartEndpoint REST API was S04. This story is service implementation layer only.
