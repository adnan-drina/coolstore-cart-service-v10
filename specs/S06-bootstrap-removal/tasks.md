# S06: Application Bootstrap Removal - Tasks

#### T-001: Remove CartServiceApplication bootstrap class
|**Class**: rewrite
|**Findings**: springboot-annotations-to-quarkus-00000 (1)
|**Goal**: Delete obsolete Spring Boot application bootstrap class
|**Target design** (infer tasks — REQUIRED, cite MAPPINGS.md):
- `src/main/java/com/redhat/coolstore/CartServiceApplication.java` → DELETE
- Quarkus provides native bootstrap and CDI initialization replacing Spring Boot main class
|**Acceptance**: CartServiceApplication.java removed; application builds and starts with Quarkus native bootstrap

**Out of scope**: Web UI surface — this is a backend service without legacy web UI; REST API serves as the user-facing interface via /api/cart/* endpoints.

**Preserved integrations**: CATALOG_ENDPOINT environment variable remains functional (already configured via Quarkus from previous stories).

**Acceptance path**: /api/cart/acceptance-check — endpoint already implemented in CartEndpoint from previous story S04; serves as deploy verification endpoint for factory pipeline.
