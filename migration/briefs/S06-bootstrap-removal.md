# S06: Application bootstrap removal

<!-- The brief is the self-contained work order for one modernization
     story. Bar: a competent developer or a fresh session starts the
     story from THIS FILE ALONE. Fill every section; delete none. -->

## Goal & position

What this story achieves and why it is next: its place in the roadmap,
what it unblocks, which stories it depends on (cite
dependency-order.md / architecture-profile.md).

This story removes the obsolete Spring Boot application bootstrap and configuration class. Following the dependency order (dependency-order.md line 31), CartServiceApplication is the final cleanup task after all components have been modernized. Quarkus provides native bootstrap and CDI initialization, eliminating the need for Spring Boot's @SpringBootApplication and main class. This completes the migration with a fully Quarkus-native application.

## In scope

The exact legacy classes/files this story modernizes. For each, quote
the load-bearing legacy code (the lines being transformed — imports,
annotations, key methods), so the story never starts from a blank
read:

- `src/main/java/com/redhat/coolstore/CartServiceApplication.java` — Spring Boot bootstrap
  ```java
  package com.redhat.coolstore;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.openfeign.EnableFeignClients;
  
  @SpringBootApplication
  @EnableFeignClients
  public class CartServiceApplication {
  
      public static void main(String[] args) {
          SpringApplication.run(CartServiceApplication.class, args);
      }
  ```

## Out of scope

What neighboring code this story must NOT touch, and which story owns
it. (The tree must stay buildable: name any temporary seams — e.g. a
dependent class that keeps compiling against the old shape until its
own story.)

All application components are already modernized: domain models (S02), services (S03-S05), and REST endpoints (S04). This story only removes the bootstrap class and relies on Quarkus's native application model. No source code functionality changes.

Service implementations and REST endpoints remain in their S04-S05 converted state. Do NOT modify ShoppingCartServiceImpl, CartEndpoint, or other already-modernized components.

## Class roles & target contract (from architecture-profile §7)

For each in-scope class, its role and — for REDESIGN classes — the target
contract carried forward from profile §7, so M3 writes tasks and tests to
the target (not the legacy):

- `CartServiceApplication` — REDESIGN
  - Target: removed — Quarkus bootstrap and CDI replace Spring Boot application model, main class subsumed by Quarkus startup

## Decided target shapes

The MAPPINGS.md rows that apply (quote the decided target, don't
re-decide). Recipe-executed rules already handled: reference
`migration/recipe-log.md` and `migration/staging/` where applicable.

**Story ordering:** extensions and BOM first, then models, then resources,
then config keys, then tests (`extensions → models → resources → config →
tests`).

Bootstrap modernization implements:
- springboot-annotations-to-quarkus-00000 → delete `@SpringBootApplication` + main class (already partially applied to JerseyConfig in S04)

## Contracts owned by this story

- **Findings**: the mandatory rule ids this story resolves (from the
  roadmap entry).
  - (none - this is a cleanup story with no new findings to resolve)

- **Preserve**: the `preserve:` items whose surfaces live in scope —
  spell out the env var names/values mechanism to keep.
  - CATALOG_ENDPOINT environment variable preserved at application level (already functional from previous stories)

- **Behavioral pins**: the assertion values that must hold after this
  story (quote numbers/strings and their test source). Harvest classes
  and behavior-preserving redesign pin LEGACY values; behavior-changing
  redesign pins the §7 TARGET (e.g. 404, not create-on-GET). Name the
  contract GAPS this story closes with characterization tests.
  - **Application startup**: Quarkus native bootstrap replaces SpringApplication.run()
  - **CDI initialization**: All @ApplicationScoped services initialized by Quarkus CDI container
  - **REST endpoint discovery**: JAX-RS resources auto-discovered by Quarkus (no JerseyConfig needed)
  - **Configuration**: application.properties and environment variables processed by Quarkus
  - **All legacy behaviors**: Cart operations, pricing, promotions, shipping calculations remain exactly as previous stories established
  - **Health endpoints**: /q/health available from quarkus-smallrye-health

- **Forbidden**: the fabrication tripwires relevant here.
  - No application functionality changes
  - No service behavior changes
  - No REST endpoint changes
  - No configuration loss (CATALOG_ENDPOINT must remain functional)

## Done-criteria

Checkable, story-scoped:
- builds + `sensors.sh task` green at every commit; milestone green at
  story end
- CartServiceApplication.java removed from source tree
- Quarkus native application startup functional
- All @ApplicationScoped services initialized correctly
- REST endpoints accessible and functional at /api/cart/*
- All existing test assertions continue to pass (ShoppingCartServiceTest, CartServiceBoundaryTest)
- CATALOG_ENDPOINT environment variable functional
- Application starts with Quarkus bootstrap (no SpringApplication.run())
- Health endpoint /q/health accessible
- Package rename com.redhat.coolstore → com.demo completed throughout
- **DEPLOY MILESTONE**: Factory pipeline green, deployed, acceptance path serving
