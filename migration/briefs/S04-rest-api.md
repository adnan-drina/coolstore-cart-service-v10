# S04: REST API modernization

<!-- The brief is the self-contained work order for one modernization
     story. Bar: a competent developer or a fresh session starts the
     story from THIS FILE ALONE. Fill every section; delete none. -->

## Goal & position

What this story achieves and why it is next: its place in the roadmap,
what it unblocks, which stories it depends on (cite
dependency-order.md / architecture-profile.md).

This story modernizes the REST API layer to native JAX-RS with Quarkus session management. Following the dependency order (dependency-order.md line 29), CartEndpoint is converted from Spring @RestController to JAX-RS @Path resources. JerseyConfig is removed as Quarkus auto-discovers JAX-RS resources. This exposes the migrated service API surface and requires deploy milestone verification, making it the first story marked for deployment.

## In scope

The exact legacy classes/files this story modernizes. For each, quote
the load-bearing legacy code (the lines being transformed — imports,
annotations, key methods), so the story never starts from a blank
read:

- `src/main/java/com/redhat/coolstore/rest/CartEndpoint.java` — JAX-RS REST controller
  ```java
  package com.redhat.coolstore.rest;
  
  import java.io.Serializable;
  import javax.ws.rs.DELETE;
  import javax.ws.rs.GET;
  import javax.ws.rs.POST;
  import javax.ws.rs.Path;
  import javax.ws.rs.PathParam;
  import javax.ws.rs.Produces;
  import javax.ws.rs.core.MediaType;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.context.annotation.Scope;
  import org.springframework.web.bind.annotation.RestController;
  import org.springframework.web.context.WebApplicationContext;
  import com.redhat.coolstore.model.ShoppingCart;
  import com.redhat.coolstore.service.ShoppingCartService;
  
  @RestController
  @Scope(scopeName = WebApplicationContext.SCOPE_SESSION)
  @Path("/cart")
  public class CartEndpoint implements Serializable {
      
      private static final long serialVersionUID = -7227732980791688773L;
      
      @Autowired
      private ShoppingCartService shoppingCartService;
      
      @GET
      @Path("/{cartId}")
      @Produces(MediaType.APPLICATION_JSON)
      public ShoppingCart getCart(@PathParam("cartId") String cartId) {
          return shoppingCartService.getShoppingCart(cartId);
      }
      
      @POST
      @Path("/{cartId}/{itemId}/{quantity}")
      @Produces(MediaType.APPLICATION_JSON)
      public ShoppingCart add(@PathParam("cartId") String cartId,
                              @PathParam("itemId") String itemId,
                              @PathParam("quantity") int quantity) throws Exception {
          return shoppingCartService.addItem(cartId, itemId, quantity);
      }
  ```

- `src/main/java/com/redhat/coolstore/rest/JerseyConfig.java` — JAX-RS configuration
  ```java
  package com.redhat.coolstore.rest;
  
  import org.glassfish.jersey.server.ResourceConfig;
  import org.springframework.stereotype.Component;
  
  @Component
  public class JerseyConfig extends ResourceConfig {
      public JerseyConfig() {
          register(CartEndpoint.class);
      }
  ```

## Out of scope

What neighboring code this story must NOT touch, and which story owns
it. (The tree must stay buildable: name any temporary seams — e.g. a
dependent class that keeps compiling against the old shape until its
own story.)

ShoppingCartServiceImpl remains Spring @Service with @Autowired field injection until S05. Service interfaces and core services (S03) are already modernized to CDI. CartServiceApplication remains Spring Boot bootstrap until S06. All domain models (S02) and their package rename remain unchanged.

## Class roles & target contract (from architecture-profile §7)

For each in-scope class, its role and — for REDESIGN classes — the target
contract carried forward from profile §7, so M3 writes tasks and tests to
the target (not the legacy):

- `CartEndpoint` — REDESIGN
  - Target: thread-safe singleton with ConcurrentHashMap for cart storage, GET returns 404 on missing cart IDs, POST validates input quantities >0 with 400 problem-detail on validation failure, POST operations are additive→quantity 4, 503 via ExceptionMapper on downstream failures
  - Must maintain session-scoped cart state while migrating from Spring session scope to Quarkus session management
  - Must preserve existing behavioral contracts from boundary tests (CartServiceBoundaryTest.java:35-46)

- `JerseyConfig` — REDESIGN
  - Target: removed — Quarkus auto-discovers JAX-RS resources, configuration class subsumed by Quarkus bootstrap

## Decided target shapes

The MAPPINGS.md rows that apply (quote the decided target, don't
re-decide). Recipe-executed rules already handled: reference
`migration/recipe-log.md` and `migration/staging/` where applicable.

**Story ordering:** extensions and BOM first, then models, then resources,
then config keys, then tests (`extensions → models → resources → config →
tests`).

REST API modernization implements:
- jakarta-jaxrs-to-quarkus-00010 → quarkus-rest dependency for JAX-RS support
- springboot-annotations-to-quarkus-00000 → remove @SpringBootApplication bootstrap model

## Contracts owned by this story

- **Findings**: the mandatory rule ids this story resolves (from the
  roadmap entry).
  - jakarta-jaxrs-to-quarkus-00010, springboot-annotations-to-quarkus-00000

- **Preserve**: the `preserve:` items whose surfaces live in scope —
  spell out the env var names/values mechanism to keep.
  - CATALOG_ENDPOINT environment variable for catalog service integration (inherited from service layer)

- **Behavioral pins**: the assertion values that must hold after this
  story (quote numbers/strings and their test source). Harvest classes
  and behavior-preserving redesign pin LEGACY values; behavior-changing
  redesign pins the §7 TARGET (e.g. 404, not create-on-GET). Name the
  contract GAPS this story closes with characterization tests.
  - **GET endpoint**: GET /cart/{cartId} returns ShoppingCart JSON (CartEndpoint.java:31-36)
  - **POST endpoint**: POST /cart/{cartId}/{itemId}/{quantity} adds items (CartEndpoint.java:38-45)
  - **DELETE endpoint**: DELETE /cart/{cartId}/{itemId}/{quantity} removes items (CartEndpoint.java:55-62)
  - **Checkout endpoint**: POST /cart/checkout/{cartId} processes checkout (CartEndpoint.java:64-69)
  - **Session management**: cart state maintained across requests (WebApplicationContext.SCOPE_SESSION → Quarkus session)
  - **Service integration**: ShoppingCartService methods called with exact legacy signatures
  - **Boundary test oracles**: CartServiceBoundaryTest.java:38-42 assert cart totals of 2000.0 for item total, -10.99 for shipping promo savings, 2000.0 for cart total

- **Forbidden**: the fabrication tripwires relevant here.
  - No endpoint signature changes (@GET, @POST, @DELETE, @Path, @Produces unchanged)
  - No session scope loss (cart state must persist across requests)
  - No service method signature changes
  - No JerseyConfig registration loss (Quarkus auto-discovery replaces manual registration)

## Done-criteria

Checkable, story-scoped:
- builds + `sensors.sh task` green at every commit; milestone green at
  story end
- REST endpoints accessible at /api/cart/* (mapped to /cart/* in legacy)
- JerseyConfig removed, JAX-RS resources auto-discovered by Quarkus
- Session-scoped cart state maintained across requests
- CartServiceBoundaryTest passes with boundary oracles preserved
- CATALOG_ENDPOINT environment variable functional for catalog service
- Package rename com.redhat.coolstore → com.demo applied correctly
- **DEPLOY MILESTONE**: Factory pipeline green, deployed, acceptance path serving
