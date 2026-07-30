# S05: Service implementation and integration

<!-- The brief is the self-contained work order for one modernization
     story. Bar: a competent developer or a fresh session starts the
     story from THIS FILE ALONE. Fill every section; delete none. -->

## Goal & position

What this story achieves and why it is next: its place in the roadmap,
what it unblocks, which stories it depends on (cite
dependency-order.md / architecture-profile.md).

This story modernizes the core service implementation with thread-safe state management and catalog integration. Following the dependency order (dependency-order.md line 30), ShoppingCartServiceImpl is the final integration layer that connects all modernized components. It requires thread-safe ConcurrentHashMap for cart storage, proper error handling for catalog failures, and concurrent access patterns as specified in architecture-profile §7.

## In scope

The exact legacy classes/files this story modernizes. For each, quote
the load-bearing legacy code (the lines being transformed — imports,
annotations, key methods), so the story never starts from a blank
read:

- `src/main/java/com/redhat/coolstore/service/ShoppingCartServiceImpl.java` — Core business service implementation
  ```java
  package com.redhat.coolstore.service;
  
  import com.redhat.coolstore.model.Product;
  import com.redhat.coolstore.model.ShoppingCart;
  import com.redhat.coolstore.model.ShoppingCartItem;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Service;
  
  import javax.annotation.PostConstruct;
  import java.util.ArrayList;
  import java.util.HashMap;
  import java.util.List;
  import java.util.Map;
  import java.util.function.Function;
  import java.util.stream.Collectors;
  
  @Service
  public class ShoppingCartServiceImpl implements ShoppingCartService {
      
      private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartServiceImpl.class);
      
      @Autowired
      ShippingService ss;
      
      @Autowired
      CatalogService catalogServie;
      
      @Autowired
      PromoService ps;
      
      Map<String, ShoppingCart> carts;
      
      Map<String, Product> productMap = new HashMap<>();
      
      @PostConstruct
      public void init() {
          LOG.info("Using local in-memory cache for cart data");
          carts = new HashMap<>();
      }
  ```

## Out of scope

What neighboring code this story must NOT touch, and which story owns
it. (The tree must stay buildable: name any temporary seams — e.g. a
dependent class that keeps compiling against the old shape until its
own story.)

CartEndpoint (S04) remains JAX-RS with session management. Service interfaces and core services (S03) are already modernized to CDI. Domain models (S02) remain unchanged. CartServiceApplication remains Spring Boot bootstrap until S06. All REST endpoints continue using the modernized ShoppingCartService interface.

## Class roles & target contract (from architecture-profile §7)

For each in-scope class, its role and — for REDESIGN classes — the target
contract carried forward from profile §7, so M3 writes tasks and tests to
the target (not the legacy):

- `ShoppingCartServiceImpl` — REDESIGN
  - Target: @ApplicationScoped CDI service with constructor injection, thread-safe ConcurrentHashMap for cart storage (replacing HashMap in line 42), compute() methods for atomic updates, no-clear-on-miss refresh guard for product cache, normalize-before-derive pricing ensuring cart totals agree with item totals
  - Implementation pattern from S03 experience: use `map.compute(cartId, (key, existingCart) -> { ... })` for atomic cart operations, avoid synchronized blocks in favor of ConcurrentHashMap atomic methods
  - Must preserve existing numeric oracles from tests (2000.0 item total, -10.99 shipping savings) while adding thread safety for concurrent cart access
  - Must preserve cart `add()` behavior as additive — two `add(cartId, itemId, 2)` → quantity **4** after dedupe

## Decided target shapes

The MAPPINGS.md rows that apply (quote the decided target, don't
re-decide). Recipe-executed rules already handled: reference
`migration/recipe-log.md` and `migration/staging/` where applicable.

**Story ordering:** extensions and BOM first, then models, then resources,
then config keys, then tests (`extensions → models → resources → config →
tests`).

Service implementation modernization implements:
- removed-javaee-modules-00020 → JEE modules removed from JDK, now provided by Quarkus platform dependencies
- All service injection converted to CDI constructor injection
- Cart storage converted from HashMap to ConcurrentHashMap for thread safety
- Product cache refresh guard with bounded refresh policies

## Contracts owned by this story

- **Findings**: the mandatory rule ids this story resolves (from the
  roadmap entry).
  - removed-javaee-modules-00020

- **Preserve**: the `preserve:` items whose surfaces live in scope —
  spell out the env var names/values mechanism to keep.
  - CATALOG_ENDPOINT environment variable for catalog service integration (already preserved in S03)

- **Behavioral pins**: the assertion values that must hold after this
  story (quote numbers/strings and their test source). Harvest classes
  and behavior-preserving redesign pin LEGACY values; behavior-changing
  redesign pins the §7 TARGET (e.g. 404, not create-on-GET). Name the
  contract GAPS this story closes with characterization tests.
  - **Service methods**: All ShoppingCartService interface methods unchanged (getShoppingCart, getProduct, deleteItem, checkout, addItem, set, priceShoppingCart)
  - **Cart initialization**: Empty cart returns zero totals (ShoppingCartServiceTest.java:32-35)
  - **Cart pricing**: Item total 2000.0, shipping promo savings -10.99, cart total 2000.0 (ShoppingCartServiceTest.java:49-53)
  - **Cart `add()` behavior**: ADDITIVE - two add(cartId, itemId, 2) → quantity 4 after dedupe (dedupeCartItems method)
  - **Product retrieval**: catalogService.products() call and productMap caching behavior
  - **Promotion integration**: 25% off product "329299" and free shipping over $75
  - **Shipping calculation**: All shipping tiers preserved (2.99 to 10.99 based on cart total)
  - **Thread safety**: Concurrent access to cart storage and product cache
  - **Boundary test oracles**: CartServiceBoundaryTest.java:38-42 assert exact same values

- **Forbidden**: the fabrication tripwires relevant here.
  - No service method signature changes
  - No cart behavior changes (must remain additive)
  - No promotion/shipping logic changes
  - No product cache behavior changes (refresh guard only, not caching strategy)
  - No mock product fallbacks

## Done-criteria

Checkable, story-scoped:
- builds + `sensors.sh task` green at every commit; milestone green at
  story end
- Service implementation converted to @ApplicationScoped CDI with constructor injection
- ConcurrentHashMap replaces HashMap for thread-safe cart storage
- All service method behaviors preserved exactly as legacy
- Cart `add()` behavior remains additive (quantity 4 after two add operations)
- ShoppingCartServiceTest assertions continue to pass (zero totals, pricing oracles)
- CartServiceBoundaryTest assertions continue to pass (2000.0 item total, -10.99 shipping savings)
- CATALOG_ENDPOINT environment variable functional for catalog integration
- Thread-safe concurrent access to cart operations
- Package rename com.redhat.coolstore → com.demo applied correctly
- **DEPLOY MILESTONE**: Factory pipeline green, deployed, acceptance path serving
