# Retro proposals (coolstore-cart-service-v10)

## Brief updates (auto-applicable)

None - all story briefs (S01-S06) are complete.

## Skill / harness proposals (human-only)

### (1) The three costliest failure patterns of this run

**Pattern 1: Coverage gate failure requiring extensive correction sessions (T-005)**
Evidence: run-log.md lines 158-162 show CartEndpoint.java (0.0% coverage, 38 uncovered lines) and ShoppingCartServiceImpl.java (0.0% coverage, 118 uncovered lines) failing the ≥80% gate. This triggered sensor-fix sessions totaling 1,318 seconds (574 + 633 + 111 seconds across three sfix sessions). The debt.md line 17-22 shows T-005 milestone RED requiring archived sfix with Promo S1066/S2699 genuine fixes plus parameterized ShippingServiceTest.

**Pattern 2: Sonar quality gate violations requiring per-rule correction (T-002)**
Evidence: run-log.md lines 164-172 list multiple Sonar violations: S1135 (missing TODOs), S1155 (redundant comparisons), S1192 (string duplication - 2 violations), S2737 (conditional logging), S2864 (unused import), S3824 (boolean comparisons), S6813 (CDI annotations - 3 violations). T-002 required 2 attempts and extensive correction. This represents the classic "write it right the first time" failure with significant remediation cost.

**Pattern 3: Story dependency blocking (T-002 dependency failure)**
Evidence: debt.md lines 24-51 show T-002 blocked by T-001 completion requirement due to "compilation errors due to Spring framework dependencies still present" with legacy imports (org.springframework.*, org.glassfish.jersey.server, org.springframework.cloud.openfeign). This represents planning/scope issues where dependent tasks were attempted before prerequisites completed.

### (2) Concrete proposed changes to skills/sensors

**A. Coverage sensor hardening (EXECUTION.md lines 156-173)**

Change: Strengthen the coverage gate enforcement in M4 execution loop
Current: "Code-producing tasks must ship unit tests with the code — coverage debt is a gate failure"
Proposed: Add explicit coverage validation BEFORE commit, not as post-hoc M5 check

File: `.hermes/skills/migration-harness/EXECUTION.md`
Section: "Sensors: run the task sensor BEFORE you commit — never commit red"
Change:
```
**Sensors after EVERY task (cheap → expensive):**
- Run `sensors.sh task` GREEN before commit (mandatory pre-commit check)
- For tasks creating new classes: require ≥80% coverage demonstrated in JaCoCo report BEFORE commit
- CartEndpoint.java and ShoppingCartServiceImpl.java patterns show 0% coverage is unacceptable
```

**B. Sonar rule prevention guidance (EXECUTION.md lines 118-143)**

Change: Expand the "Recurring Sonar rules to write correctly the first time" section with concrete code patterns
Current: Lists S5778, S2864, S5976, S2737, S2925, S1066 rules
Proposed: Add S1135 (missing TODO comments), S1155 (redundant comparisons), S1192 (string literals), S6813 (CDI annotations) with specific examples

File: `.hermes/skills/migration-harness/EXECUTION.md`
Section: "Recurring Sonar rules to write correctly the first time (O-SONARFIX)"
Change:
Add concrete prevention patterns for the violations observed in this run:
```
**S1135 (Missing TODO comments)** — Add meaningful // TODO comments for incomplete implementation areas
**S1155 (Redundant comparisons)** — Prefer direct boolean evaluation over == true/false comparisons  
**S1192 (String literals should not be duplicated)** — Extract string constants for repeated values
**S6813 (CDI annotations should be properly used)** — Use @Inject constructor injection, @ApplicationScoped for services
```

**C. Task dependency validation (PLANNING.md lines 61-78)**

Change: Add dependency-order enforcement in M3 planning
Current: dependency-order.md exists but tasks can violate the ordering
Proposed: Add plan-lint validation for dependency order compliance

File: `.hermes/skills/migration-harness/PLANNING.md`
Section: "Conversion order within a story: extensions → models → resources → config → tests"
Change:
```
**Dependency order validation**: Every task must cite its dependency-order.md line number. Tasks that modify dependent classes without prerequisite completion emit LINT:dependency-violation: and are rejected by plan-lint.

T-002 (Package rename com.redhat.coolstore → com.demo) cannot proceed until T-001 (dependency management) completes. Enforce this ordering via mandatory task prerequisites.
```

### (3) ARTIFACT review of this run's commits

**Harvest fidelity**: Acceptable
- CartEndpoint.java, JerseyConfig.java, ShoppingCartServiceImpl.java properly harvested from migration/staging with package rename
- Domain models (Product, Promotion, ShoppingCart, ShoppingCartItem) preserved legacy behavior correctly
- No fabricated classes detected in scope-sensor post-commit verification

**Story-scope compliance**: Issues detected
- Multiple "later_story_class" events in retro-events.csv (lines 30, 35) show ShoppingCartServiceImpl.java ownership conflicts
- Story-scope sensor reverted out-of-scope src/main edits appropriately
- Scope boundaries maintained despite tension points

**Fabrication**: Minimal issues
- No mock product fallbacks detected (forbidden pattern avoided)
- No spring-di/spring-web extensions used (native Quarkus path maintained)
- Service implementations correctly redesigned to @ApplicationScoped CDI

### (4) Harness waste analysis

**Session-level waste from retro-metrics.csv**:
- T-005-sfix: 111 seconds wasted on sfix_committed_still_red (commit made despite sensor red)
- T-001-a1p0: 531 seconds (rc=130) represents failed task that should have been prevented
- T-002-a1p0: 604 seconds (rc=137) indicates incomplete packet or incorrect task design
- Total sensor-fix overhead: 2,065 seconds (574+633+111+747) across three sfix sessions for T-005

**Red-commit waste**: T-005 sfix showed "sfix_committed_still_red" pattern where commits were made despite red sensors, requiring correction sessions
**Retry overhead**: T-002 required 2 attempts (209+350=559 seconds) due to dependency violations
**Sensor execution cost**: Multiple milestone sensor runs at 257-625 seconds each when cheaper task sensors would have caught issues earlier

**Root cause**: Pre-commit sensor enforcement gap. Current workflow allows commits with failing sensors, triggering expensive post-hoc correction sessions. The "never commit red" rule needs stronger enforcement in the M4 loop.
