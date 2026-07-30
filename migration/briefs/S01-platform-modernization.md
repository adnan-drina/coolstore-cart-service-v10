# S01: Platform and dependency modernization

<!-- The brief is the self-contained work order for one modernization
     story. Bar: a competent developer or a fresh session starts the
     story from THIS FILE ALONE. Fill every section; delete none. -->

## Goal & position

What this story achieves and why it is next: its place in the roadmap,
what it unblocks, which stories it depends on (cite
dependency-order.md / architecture-profile.md).

This story modernizes the Maven platform dependencies and build configuration to Quarkus BOM. It is the foundation that enables all subsequent code transformations, providing Jakarta EE 9+, Quarkus extensions, and cloud-native capabilities. This story has no dependencies (S01) and must complete before any source code changes in S02-S06.

## In scope

The exact legacy classes/files this story modernizes. For each, quote
the load-bearing legacy code (the lines being transformed — imports,
annotations, key methods), so the story never starts from a blank
read:

- `pom.xml` — Maven build configuration and dependencies
  ```xml
  <parent>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-parent</artifactId>
      <version>2.3.0.RELEASE</version>
  </parent>
  ```

## Out of scope

What neighboring code this story must NOT touch, and which story owns
it. (The tree must stay buildable: name any temporary seams — e.g. a
dependent class that keeps compiling against the old shape until its
own story.)

This story modifies only pom.xml. All source code in src/main/java remains unchanged until S02. The legacy Spring Boot dependencies remain functional during this story, providing a buildable intermediate state.

## Class roles & target contract (from architecture-profile §7)

For each in-scope class, its role and — for REDESIGN classes — the target
contract carried forward from profile §7, so M3 writes tasks and tests to
the target (not the legacy):

- `pom.xml` — BUILD CONFIGURATION
  - Platform modernization: Spring Boot parent → Quarkus BOM (com.redhat.quarkus.platform 3.27.3.SP1)
  - Dependency conversion: Spring dependencies → Quarkus extensions (quarkus-rest, quarkus-smallrye-health, etc.)
  - Plugin conversion: spring-boot-maven-plugin → quarkus-maven-plugin
  - Compiler configuration: Java 21 target, proper Maven plugin versions

## Decided target shapes

The MAPPINGS.md rows that apply (quote the decided target, don't
re-decide). Recipe-executed rules already handled: reference
`migration/recipe-log.md` and `migration/staging/` where applicable.

**Story ordering:** extensions and BOM first, then models, then resources,
then config keys, then tests (`extensions → models → resources → config →
tests`).

This story implements the umbrella POM conversion rules:
- springboot-parent-pom-to-quarkus-00000 → Quarkus platform BOM replaces Spring parent
- javaee-pom-to-quarkus-00010/20/30/40/50/60/80 → Maven plugins and dependencies converted to Quarkus equivalents
- springboot-actuator-to-quarkus-0100 → quarkus-smallrye-health for health endpoints
- springboot-metrics-to-quarkus-0100/0200 → quarkus-smallrye-metrics for metrics
- springboot-di-to-quarkus-00000 → removed (native CDI used instead)
- springboot-plugins-to-quarkus-0000 → quarkus-maven-plugin
- springboot-properties-to-quarkus-00000 → plain property support (no extension)

## Contracts owned by this story

- **Findings**: the mandatory rule ids this story resolves (from the
  roadmap entry).
  - springboot-parent-pom-to-quarkus-00000, javaee-pom-to-quarkus-00010, javaee-pom-to-quarkus-00020, javaee-pom-to-quarkus-00030, javaee-pom-to-quarkus-00040, javaee-pom-to-quarkus-00050, javaee-pom-to-quarkus-00060, javaee-pom-to-quarkus-00080, springboot-actuator-to-quarkus-0100, springboot-di-to-quarkus-00000, springboot-metrics-to-quarkus-0100, springboot-metrics-to-quarkus-0200, springboot-plugins-to-quarkus-0000, springboot-properties-to-quarkus-00000, springboot-web-to-quarkus-00000

- **Preserve**: the `preserve:` items whose surfaces live in scope —
  spell out the env var names/values mechanism to keep.
  - CATALOG_ENDPOINT environment variable preserved in application.properties for catalog service integration

- **Behavioral pins**: the assertion values that must hold after this
  story (quote numbers/strings and their test source). Harvest classes
  and behavior-preserving redesign pin LEGACY values; behavior-changing
  redesign pins the §7 TARGET (e.g. 404, not create-on-GET). Name the
  contract GAPS this story closes with characterization tests.
  - **Package namespace**: com.redhat.coolstore → com.demo (migration.yaml legacyPackage → targetPackage)
  - **CATALOG_ENDPOINT**: environment variable preserved for Feign/Quarkus REST client configuration
  - **Health endpoint**: /q/health (Quarkus SmallRye Health) replaces Spring Boot Actuator

- **Forbidden**: the fabrication tripwires relevant here.
  - No spring-di extension (native CDI only)
  - No spring-web extension (native JAX-RS only)
  - No mock product fallbacks (getMockProducts, "Fallback to mock")

## Done-criteria

Checkable, story-scoped:
- builds + `sensors.sh task` green at every commit; milestone green at
  story end
- Maven build completes successfully with Quarkus BOM dependencies
- All Spring Boot dependencies converted to Quarkus equivalents
- No functional changes to source code behavior
