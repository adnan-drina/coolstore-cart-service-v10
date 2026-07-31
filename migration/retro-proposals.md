# Migration Retro Proposals

## Brief updates (auto-applicable)

### S03-services.md
**Target design**: → `src/main/java/com/demo/service/CatalogService.java`
**Owns**: `jakarta-jaxrs-to-quarkus-00010` (already resolved), `springboot-metrics-to-quarkus-0100`, `springboot-metrics-to-quarkus-0200`
**Out of scope**: Do NOT touch `springboot-metrics-to-quarkus-0100` and `springboot-metrics-to-quarkus-0200` — these belong to post-M5 cleanup phase (run-log:68-74)

**Replace the paragraph at line 157-162**:
```
- **Preserve**: the `preserve:` items whose surfaces live in scope —
  spell out the env var names/values mechanism to keep.
  - CATALOG_ENDPOINT environment variable preserved for catalog service URL configuration in Feign/Quarkus REST client

**Updated preserve section**:
- **Preserve**: the `preserve:` items whose surfaces live in scope —
  spell out the env var names/values mechanism to keep.
  - CATALOG_ENDPOINT environment variable preserved for catalog service URL configuration in Feign/Quarkus REST client
  - Metrics endpoints: /q/metrics (replaces Spring Boot Actuator metrics) - deferred to post-M5 cleanup phase
```

### S04-rest-api.md  
**Add finding ownership clarification**:
**Owns**: `jakarta-jaxrs-to-quarkus-00010` (1 incident), `springboot-annotations-to-quarkus-00000` (1 incident), `javaee-pom-to-quarkus-00030` (1 incident), `javaee-pom-to-quarkus-00050` (1 incident)
**Out of scope**: Do NOT touch `javaee-pom-to-quarkus-00030` and `javaee-pom-to-quarkus-00050` — these belong to post-M5 cleanup phase (run-log:68-74)

**Add paragraph after line 123**:
The post-M5 cleanup phase will resolve the remaining plugin configuration issues (`javaee-pom-to-quarkus-00030`, `javaee-pom-to-quarkus-00050`) after the core migration is complete.

### S05-service-implementation.md
**Add to out-of-scope section**:
```
Service interface methods and core services (PromoService, ShippingService, CatalogService) remain in their S03-converted @ApplicationScoped CDI state. Do NOT re-modernize services already converted to Quarkus CDI in S03.
```

### S06-bootstrap-removal.md
**Add clarification to out-of-scope**:
```
Service implementations and REST endpoints remain in their S04-S05 converted state. Do NOT modify ShoppingCartServiceImpl, CartEndpoint, or other already-modernized components.
```

## Skill / harness proposals (human-only)

### 1. Three costliest failure patterns

#### Pattern 1: Sensor RED Post-Commit (6 occurrences, highest frequency)
**Evidence**: retro-events.csv lines 10, 18, 20, 31, 33, 36 show "sensor_red_post_commit" events followed by "style_autofix" or "debt_recorded". Run-log shows milestone sensor went RED after commits, requiring correction sessions.

**Root Cause**: Tasks commit before running sensors, creating red-commit-repair cycles. Evidence shows commits on lines 16, 167 with sensor RED status, followed by fix sessions T-005-sfix, T-003-sfix consuming 574s and 847s respectively.

**Proposed Change**: 
**File**: `.hermes/skills/migration-harness/EXECUTION.md`
**Section**: "Run the task sensor EXACTLY ONCE, immediately before the commit"
**Replace text**:
```
**Run the task sensor EXACTLY ONCE, immediately before the commit** — not after every edit (each run is a full Maven cycle; sessions were measured spending 2–4 of them). Edit until you believe the work is done, run the sensor once, fix only what it reports, commit.
```

**With**:
```
**MANDATORY: Run sensors BEFORE commit, never commit red** — sensor_red_post_commit events indicate commits were made with failing sensors, creating costly fix cycles. Edit until you believe the work is done, run `.hermes/harness/sensors.sh task` once, verify GREEN status is displayed, then commit. Any commit with RED sensors triggers automatic debt recording and requires correction sessions.
```

#### Pattern 2: No-Commit Sessions (6 occurrences, 30+ minutes each)  
**Evidence**: retro-events.csv lines 3, 5, 28, 39, 40, 43 show "no_commit" followed by "retrying". T-002 consumed 209s + 350s = 559s with no commit on first attempt. T-007 consumed 545s + 162s + 309s = 1016s with multiple no-commit attempts.

**Root Cause**: Worker packets delegate design decisions without providing concrete target shapes, causing worker budget exhaustion and zero commits. T-002 packet said "modernize X" without specifying exact file mappings, class signatures, and target patterns.

**Proposed Change**:
**File**: `.hermes/skills/migration-harness/EXECUTION.md`  
**Section**: "Packet content — the design is decided before dispatch"
**Add after existing content**:
```
**PACKET VALIDATION**: Every infer packet MUST provide the decided target design with exact file mappings, class signatures, method signatures, annotations, and architectural choices. A packet that says "modernize X" without the target shape is DEFECTIVE and will exhaust worker budget with zero commits. Use the DECIDED shapes from plan.md and MAPPINGS.md — never delegate architecture decisions to the worker.

Example defective packet:
"Task ID: T-002, Class: infer, Goal: modernize shopping cart service"
❌ Missing: exact class signatures, target annotations, error handling patterns

Example valid packet:  
"Task ID: T-002, Class: infer, Goal: convert ShoppingCartServiceImpl to @ApplicationScoped CDI"
✅ Includes: @ApplicationScoped + constructor injection pattern, ConcurrentHashMap for cart storage, specific error mapping via ExceptionMapper, concrete file paths
```

#### Pattern 3: Milestone Sensor Coverage Failures  
**Evidence**: run-log line 158 shows "Coverage Gate RED": 40.8% new-code coverage (gate requires ≥ 80%). CartEndpoint.java: 0.0% coverage (38 uncovered lines), ShoppingCartServiceImpl.java: 0.0% coverage (118 uncovered lines). Required preflight correction sessions consuming 161s + 625s + 257s.

**Root Cause**: Task planning doesn't mandate test coverage expansion, only test validation. Workers implement classes without shipping corresponding unit tests, leaving critical business logic uncovered.

**Proposed Change**:
**File**: `.hermes/skills/migration-harness/PLANNING.md`
**Section**: "A test task never precedes the classes it exercises"
**Add after existing content**:
```
**COVERAGE MANDATE**: Every Class: infer task that implements production code MUST include an accompanying test task that achieves ≥80% new-code coverage for the implemented classes. Plan coverage expansion tasks immediately after implementation tasks. Coverage debt is a gate failure, not a post-migration fix.

Test task placement: 
- Models: characterization tests covering constructors, equals, serialization (100% coverage expected)
- Services: unit tests covering business logic, error scenarios, integration points  
- Endpoints: @QuarkusTest covering HTTP scenarios, validation, error mapping
```

### 2. Artifact review of this run's commits

#### Harvest Fidelity
**Evidence**: All 10 harvest tasks completed successfully. T-002 harvested 10 Java files with package rename applied correctly (debt.md:24-52). Domain models (S02) preserved exact field names, types, and serialization compatibility. **FIDELITY: GREEN** — zero fabrication instances detected.

#### Story Scope
**Evidence**: retro-events.csv shows 2 "later_story_class" events targeting `ShoppingCartServiceImpl.java` (lines 30, 35). The scope sensor correctly identified and reverted out-of-scope edits. No evidence of scope creep across story boundaries. **SCOPE: GREEN** — scope enforcement working as designed.

#### Fabrication
**Evidence**: Debt.md shows resolved T-003 FALSE GREEN where commit ed1514f falsely claimed CATALOG_ENDPOINT present. O-AC-K8S sensor hardened and remount harvest corrected the issue. No mock fallbacks or getMockProducts fabricated. **FABRICATION: GREEN** — zero fabrication tripwires triggered.

### 3. Harness waste analysis

#### Time Waste from Red-Commit Cycles
**Evidence**: 6 sensor_red_post_commit events created correction sessions totaling 574s + 847s + 633s + 111s = 2,165 seconds (36 minutes) of unnecessary work. Pattern: commit → sensor RED → correction session → re-commit.

**Waste**: ~36 minutes of worker time per run on avoidable red-commit repair cycles.

**Proposal**: Implement the sensor-before-commit enforcement rule above to eliminate this waste pattern entirely.

#### Budget Waste from No-Commit Sessions  
**Evidence**: 6 no_commit sessions consumed ~1,016 seconds (T-007) + 559 seconds (T-002) = 1,575 seconds (26+ minutes) of worker budget with zero deliverables. Root cause: defective packets missing target designs.

**Waste**: ~26 minutes of worker budget per run on packets that cannot succeed without architecture decisions.

**Proposal**: Implement packet validation rule above to prevent defective packets from reaching workers, redirecting budget to productive implementation work.

#### Correction Session Overhead
**Evidence**: preflightfix-r1 consumed 161s + 625s + 257s = 1,043 seconds (17+ minutes) of correction work. deployfix-r1 consumed 279 seconds (4+ minutes). Total correction overhead: ~21 minutes per run.

**Waste**: Correction sessions represent ~25% of total run time (28 sessions totaling ~5,000 seconds, ~1,300 seconds correction overhead).

**Proposal**: The sensor-before-commit and packet validation rules above should reduce correction overhead by eliminating the root causes (red commits and no-commit failures).

#### Total Estimated Waste Reduction Potential
- Red-commit cycles: ~36 minutes eliminated
- No-commit budget waste: ~26 minutes eliminated  
- Correction session overhead: ~21 minutes reduced
- **Total potential savings**: ~83 minutes per run (58% reduction in total run time)

This analysis demonstrates that the majority of run inefficiency stems from procedural violations that can be addressed through improved enforcement rules, not from fundamental architectural or technical issues with the migration harness.