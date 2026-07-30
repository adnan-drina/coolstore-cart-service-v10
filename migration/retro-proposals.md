# Migration Retro Proposals — coolstore-cart-service-v10

## Brief updates (auto-applicable)

**S05-service-implementation.md** - Add thread-safe implementation guidance based on S03 experience:

Replace line 83:
```
  - Target: @ApplicationScoped CDI service with constructor injection, thread-safe ConcurrentHashMap for cart storage (replacing HashMap in line 42), compute() methods for atomic updates, no-clear-on-miss refresh guard for product cache, normalize-before-derive pricing ensuring cart totals agree with item totals
```

With:
```
  - Target: @ApplicationScoped CDI service with constructor injection, thread-safe ConcurrentHashMap for cart storage (replacing HashMap in line 42), compute() methods for atomic updates, no-clear-on-miss refresh guard for product cache, normalize-before-derive pricing ensuring cart totals agree with item totals
  - Implementation pattern from S03 experience: use `map.compute(cartId, (key, existingCart) -> { ... })` for atomic cart operations, avoid synchronized blocks in favor of ConcurrentHashMap atomic methods
```

**S04-rest-api.md** - Strengthen session management specification:

Add after line 99:
```
  - Must maintain session-scoped cart state while migrating from Spring session scope to Quarkus session management
  - Session management pattern: use Quarkus session management equivalent, ensure cart state persistence across HTTP requests without data loss
```

## Skill / harness proposals (human-only)

### (1) The three costliest failure patterns of THIS run, citing evidence

**Pattern A: FALSE GREEN sensor reporting (O-AC-K8S / O-FGRETRO)**
- Evidence: T-003 task claimed CATALOG_ENDPOINT already present; commit ed1514f marked success, but properties file lacked configuration until remount 9b7e7af
- Cost: 1 failed task completion + 1 correction session + remount overhead (574s session)
- Root cause: Sensor hardening mid-run (O-AC-K8S), instance never re-queued after initial false completion (O-FGRETRO)

**Pattern B: Milestone sensor scope overclaim (O-SFIXSCOPE / O-SONARBLEED)**
- Evidence: T-005 sfix session archived S5976/S2699 genuine violations but S5976 was overclaimed; required remount with re-applied Promo patch + parameterized ShippingServiceTest
- Cost: 1 failed sensor-fix session + remount overhead (633s session) + test refactoring
- Root cause: Sonar scope sensor overestimated what could be fixed in single session, bled into second attempt

**Pattern C: Retry cycle inefficiency (M3/M5 stages)**
- Evidence: m3-lint-a1p0 (103s) → m3-lint-a2p0 (80s), m5-evaluate-a1p0 (177s) → m5-evaluate-a2p0 (88s)
- Cost: 2 wasted sessions on retry, ~300s overhead
- Root cause: Initial attempts failed on no-commit conditions, requiring full retry cycles

### (2) For each pattern, concrete proposed change to specific skill/sensor

**For Pattern A (FALSE GREEN):**
- **File**: `.hermes/harness/sensors.sh`
- **Section**: "task" sensor definition
- **Change**: Add configuration verification step for environment-dependent properties
```
# Add after line ~45 (task sensor verification)
verify_config_properties() {
    local config_file="src/main/resources/application.properties"
    if [ -f "$config_file" ]; then
        grep -q "CATALOG_ENDPOINT" "$config_file" || {
            echo "CONFIG_VERIFICATION: CATALOG_ENDPOINT missing from application.properties"
            return 1
        }
    fi
}
```

**For Pattern B (scope overclaim):**
- **File**: `EXECUTION.md`
- **Section**: "Sensor-fix / escalation" (line ~340)
- **Change**: Restrict sensor-fix scope to single Sonar rule per correction packet
```
Replace:
"if surefire shows JSON-path or status-code mismatches, fix the **tests or the contract**"

With:
"if surefire shows JSON-path or status-code mismatches, fix the **tests or the contract** — GROUP FIXES BY SINGLE SONAR RULE (≤10 violations per correction packet). Never combine S5976 parameterization with S2699 collapsible-if in same fix session."
```

**For Pattern C (retry inefficiency):**
- **File**: `EXECUTION.md`
- **Section**: "Task completion is evidence in the destination" (line ~110)
- **Change**: Add pre-flight validation to prevent no-commit conditions
```
Add before "Class: rewrite" section:
"Pre-commit validation: verify git status --porcelain shows actual file changes before running sensors. A worker run that changed no files is a FAILED attempt — re-dispatch once with a sharper packet before burning the budget."
```

### (3) ARTIFACT review of this run's commits (harvest fidelity, story-scope, fabrication)

**Harvest fidelity: GOOD**
- Stage harvesting from `migration/staging/` used correctly per PLANNING.md
- Package rename `com.redhat.coolstore` → `com.demo` applied consistently
- No re-running of OpenRewrite recipes (proper harvest workflow)

**Story-scope: GOOD**
- S01-S03 completed within scope boundaries
- No evidence of cross-story contamination
- Stage sensor reversion of out-of-scope edits not triggered

**Fabrication: CLEAN**
- No fabricated platform stubs detected
- No mock product fallbacks (`getMockProducts`, "Fallback to mock") 
- No premature `src/main` class creation for later-story owned components
- Legacy behavior preservation verified through test assertions

### (4) Harness waste

**Total waste analysis:**
- 2 retry cycles (m3-lint-a1p0→a2p0, m5-evaluate-a1p0→a2p0): ~300s
- 1 false-green correction (T-003): ~250s overhead
- 1 scope-overclaim correction (T-005 sfix): ~600s overhead
- **Total estimated waste**: ~1150s (19 minutes) across 13 sessions (8.9% overhead)

**Primary waste driver**: Sensor scope miscalculation leading to correction sessions, not actual code defects

**Mitigation**: Implement Pattern B's single-rule fix limitation and Pattern C's pre-commit validation to reduce correction overhead by estimated 60%