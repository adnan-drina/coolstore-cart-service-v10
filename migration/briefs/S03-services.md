# S03: Service interfaces and core services

<!-- The brief is the self-contained work order for one modernization
     story. Bar: a competent developer or a fresh session starts the
     story from THIS FILE ALONE. Fill every section; delete none. -->

## Goal & position

What this story achieves and why it is next: its place in the roadmap,
what it unblocks, which stories it depends on (cite
dependency-order.md / architecture-profile.md).

This story modernizes the service layer interfaces and core business services to Quarkus CDI. Following the dependency order (dependency-order.md lines 25-28), service interfaces provide contracts that services implement. The services (PromoService, ShippingService, CatalogService) are REDESIGN classes that must adopt thread-safe patterns, environment configuration preservation, and proper error handling as specified in architecture-profile §7.

## In scope

The exact legacy classes/files this story modernizes. For each, quote
the load-bearing legacy code (the lines being transformed — imports,
annotations, key methods), so the story never starts from a blank
read:

- `src/main/java/com/redhat/coolstore/service/ShoppingCartService.java` — Service interface contract
  ```java
  package com.redhat.coolstore.service;
  
  import com.redhat.coolstore.model.Product;
  import com.redhat.coolstore.model.ShoppingCart;
  
  public interface ShoppingCartService {
      ShoppingCart getShoppingCart(String cartId);
      Product getProduct(String itemId);
      ShoppingCart deleteItem(String cartId, String itemId, int quantity);
      ShoppingCart checkout(String cartId);
      ShoppingCart addItem(String cartId, String itemId, int quantity);
      ShoppingCart set(String cartId, String tmpId);
      void priceShoppingCart(ShoppingCart sc);
  ```

- `src/main/java/com/redhat/coolstore/service/PromoService.java` — Promotion calculation service
  ```java
  package com.redhat.coolstore.service;
  
  import java.io.Serializable;
  import java.util.HashMap;
  import java.util.HashSet;
  import java.util.Map;
  import java.util.Set;
  import org.springframework.stereotype.Component;
  
  import com.redhat.coolstore.model.Promotion;
  import com.redhat.coolstore.model.ShoppingCart;
  import com.redhat.coolstore.model.ShoppingCartItem;
  
  @Component
  public class PromoService implements Serializable {
      
      private static final long serialVersionUID = 2088590587856645568L;
      private String name = null;
      private Set<Promotion> promotionSet = null;
      
      public PromoService() {
          promotionSet = new HashSet<Promotion>();
          // Coolstore seed item also used by inventory/catalog demos
          promotionSet.add(new Promotion("329299", .25));
      }
  ```

- `src/main/java/com/redhat/coolstore/service/ShippingService.java` — Shipping calculation service
  ```java
  package com.redhat.coolstore.service;
  
  import org.springframework.stereotype.Component;
  
  import com.redhat.coolstore.model.ShoppingCart;
  
  @Component
  public class ShippingService {
      
      public void calculateShipping(ShoppingCart sc) {
          if (sc != null) {
              if (sc.getCartItemTotal() >= 0 && sc.getCartItemTotal() < 25) {
                  sc.setShippingTotal(2.99);
              } else if (sc.getCartItemTotal() >= 25 && sc.getCartItemTotal() < 50) {
                  sc.setShippingTotal(4.99);
              } else if (sc.getCartItemTotal() >= 50 && sc.getCartItemTotal() < 75) {
                  sc.setShippingTotal(6.99);
              } else if (sc.getCartItemTotal() >= 75 && sc.getCartItemTotal() < 100) {
                  sc.setShippingTotal(8.99);
              } else if (sc.getCartItemTotal() >= 100 && sc.getCartItemTotal() < 10000) {
                  sc.setShippingTotal(10.99);
              }
          }
      }
  ```

- `src/main/java/com/redhat/coolstore/service/CatalogService.java` — Feign client for product catalog
  ```java
  package com.redhat.coolstore.service;
  
  import java.util.List;
  
  import org.springframework.cloud.openfeign.FeignClient;
  import org.springframework.web.bind.annotation.GetMapping;
  
  import com.redhat.coolstore.model.Product;
  
  @FeignClient(name = "catalogService", url = "${CATALOG_ENDPOINT}")
  interface CatalogService {
      @GetMapping("/api/products")
      List<Product> products();
  ```

## Out of scope

What neighboring code this story must NOT touch, and which story owns
it. (The tree must stay buildable: name any temporary seams — e.g. a
dependent class that keeps compiling against the old shape until its
own story.)

ShoppingCartServiceImpl remains Spring @Service with @Autowired field injection until S05. CartEndpoint remains Spring @RestController with @Autowired field injection until S04. JerseyConfig remains Spring @Component until S04. CartServiceApplication bootstrap remains Spring Boot until S06.

## Class roles & target contract (from architecture-profile §7)

For each in-scope class, its role and — for REDESIGN classes — the target
contract carried forward from profile §7, so M3 writes tasks and tests to
the target (not the legacy):

- `ShoppingCartService` — HARVEST
  - Preserve existing behavior: method signatures, return types, exception handling contracts
  - Target: interface preserved unchanged, implementation migrated to Quarkus CDI

- `PromoService` — REDESIGN
  - Target: @ApplicationScoped CDI service with constructor injection, thread-safe promotion set access, bounded refresh for promotion data, map catalog integration failures to 503 via ExceptionMapper
  - Must preserve existing promotion logic (25% off product "329299", free shipping over $75) while ensuring thread-safe concurrent access

- `ShippingService` — REDESIGN  
  - Target: @ApplicationScoped CDI service with constructor injection, thread-safe calculation methods, bounded shipping tier logic
  - Must preserve existing shipping calculation tiers (free under $25, incremental increases to $10.99 over $100) while ensuring thread-safe concurrent execution

- `CatalogService` — REDESIGN
  - Target: Quarkus REST client replacement with @RegisterRestClient annotation, environment-driven URL configuration through ${CATALOG_ENDPOINT:default}, thread-safe client usage, map catalog service failures to 503 via ExceptionMapper

## Decided target shapes

The MAPPINGS.md rows that apply (quote the decided target, don't
re-decide). Recipe-executed rules already handled: reference
`migration/recipe-log.md` and `migration/staging/` where applicable.

**Story ordering:** extensions and BOM first, then models, then resources,
then config keys, then tests (`extensions → models → resources → config →
tests`).

Service layer modernization implements:
- springboot-di-to-quarkus-00003 → native CDI constructor injection for all @Component services
- demo-env-integration-00001 → preserve CATALOG_ENDPOINT environment variable for REST client configuration
- localhost-http-00001 → cloud-readiness with env-driven config ${CATALOG_ENDPOINT:default}

## Contracts owned by this story

- **Findings**: the mandatory rule ids this story resolves (from the
  roadmap entry).
  - springboot-di-to-quarkus-00003, demo-env-integration-00001, localhost-http-00001

- **Preserve**: the `preserve:` items whose surfaces live in scope —
  spell out the env var names/values mechanism to keep.
  - CATALOG_ENDPOINT environment variable preserved for catalog service URL configuration in Feign/Quarkus REST client

- **Behavioral pins**: the assertion values that must hold after this
  story (quote numbers/strings and their test source). Harvest classes
  and behavior-preserving redesign pin LEGACY values; behavior-changing
  redesign pins the §7 TARGET (e.g. 404, not create-on-GET). Name the
  contract GAPS this story closes with characterization tests.
  - **Service interface**: ShoppingCartService methods unchanged from legacy (getShoppingCart, getProduct, deleteItem, checkout, addItem, set, priceShoppingCart)
  - **Promotion behavior**: 25% discount on product "329299" (PromoService.java:27)
  - **Shipping tiers**: Free shipping when cart total >= $75 (PromoService.java:50-54), shipping costs $2.99 to $10.99 based on tiers (ShippingService.java:12-22)
  - **Catalog endpoint**: products() returns List<Product> from /api/products endpoint
  - **Thread safety**: ConcurrentHashMap for cart storage and thread-safe service methods

- **Forbidden**: the fabrication tripwires relevant here.
  - No method signature changes on service interfaces
  - No behavior changes to promotion logic or shipping calculations
  - No hardcoded localhost URLs (must use ${CATALOG_ENDPOINT:default})
  - No mock product fallbacks

## Done-criteria

Checkable, story-scoped:
- builds + `sensors.sh task` green at every commit; milestone green at
  story end
- All service classes converted to @ApplicationScoped CDI with constructor injection
- Service interface methods unchanged and backward compatible
- CATALOG_ENDPOINT environment variable preserved and functional
- Existing promotion logic (25% off product "329299") preserved
- Shipping tier calculations preserved exactly as legacy
- Package rename com.redhat.coolstore → com.demo applied correctly
