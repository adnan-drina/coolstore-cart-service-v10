# Retro proposals — coolstore-cart-service-v10 migration run

## Brief updates (auto-applicable)

Concrete edits for REMAINING story briefs only (not the story just finished). For each change: name the brief file, quote the paragraph to add or replace. Empty list is fine if nothing should change.

**No brief updates needed** — the completed S06 story had no remaining issues, and the other story briefs (S01-S05) already contain sufficient detail about CATALOG_ENDPOINT preservation. The FALSE GREEN issue in T-003 was a detection gap, not a brief inadequacy.

## Skill / harness proposals (human-only)

### (1) Three costliest failure patterns

**Pattern 1: FALSE GREEN with missing essential configuration**
- **Evidence**: Debt entry shows T-003 "Commit ed1514f claimed CATALOG_ENDPOINT already present; props lacked it until remount 9b7e7af"
- **Impact**: Hard-to-detect missing functionality that passes surface-level validation but fails in production scenarios
- **Cost**: Required remount harvest (9b7e7af) + supervisor pause + debt-freeze before M5 ship

**Pattern 2: Hardened probes without re-queueing**  
- **Evidence**: "Probe hardened mid-run (O-AC-K8S); instance never re-queued (O-FGRETRO)" from debt.md
- **Impact**: Detection improvements don't apply to already-failing instances, creating testing blind spots
- **Cost**: Requires manual intervention (remount) + supervisor coordination to clear freeze

**Pattern 3: High-RC evaluation failures consuming budget**
- **Evidence**: m5-evaluate-a1p0 failed with rc=130, consuming 177 seconds before successful retry (m5-evaluate-a2p0: 88 seconds)
- **Impact**: Evaluation phase failures force expensive retries, burning iteration budget unnecessarily
- **Cost**: 177 seconds wasted on failed evaluation + subsequent retry overhead

### (2) Concrete skill/harness changes

**Change 1: FALSE GREEN detection**
- **File**: `EXECUTION.md` section "Task completion is evidence in the destination"
- **Current text**: "A task is complete when its FINDINGS are resolved IN `/projects/modernized`. If a finding is inherently resolved by the scaffold already (e.g. the pom is jakarta-native), verify that with concrete evidence and record it as `resolved-by-scaffold` in the run-log row — do not invent work. A worker run that changed no files is a FAILED attempt — re-dispatch once with a sharper packet before burning the budget."
- **Proposed addition**: After "A worker run that changed no files is a FAILED attempt" add: "For `preserve:` contract claims (e.g., environment variables), verify the configuration is functional AND present in the deployed configuration surface (application.properties, k8s manifests, etc.) — surface-level checks alone create false green where configuration lacks essential functionality until remount."

**Change 2: Probe hardening re-queueing**
- **File**: `SHIPPING.md` section "M5 ship — the factory gate loop"  
- **Current text**: "The supervisor classifies WHICH pipeline stage failed and starts the matching correction session:"
- **Proposed addition**: When probe configuration is hardened during a run, automatically re-queue any failing instances for retest. Add explicit note: "Probe hardening must trigger re-queue of affected instances to verify fixes apply to actual test failures, not just improve future detection."

**Change 3: Evaluation phase pre-validation**
- **File**: `SHIPPING.md` section "M5 evaluate — final sensors + ship"  
- **Current text**: "2. Factory pre-flight: run `.hermes/harness/sensors.sh preflight` (isolated clean verify, new-code sonar/coverage gate, prod-profile boot where applicable). **L-M5e:** the evaluate commit message must state preflight GREEN or RED honestly — never claim "factory/preflight green" unless that command exited 0."
- **Proposed addition**: Before full M5 evaluation, add lightweight pre-check focused on high-rc failure patterns. Insert after line 2: "Pre-validate evaluation prerequisites to prevent high-RC failures: verify baseline findings analysis can complete, confirm all `preserve:` surfaces are functional, and run isolated sonar/coverage checks that mirror full evaluation criteria."

### (3) Artifact review of this run's commits

**Harvest fidelity**: HIGH — Recipe-executed transforms (javax→jakarta) properly harvested from `migration/staging`, package rename applied consistently (com.redhat.coolstore → com.demo), all legacy behavior preserved in HARVEST classes.

**Story-scope adherence**: HIGH — All commits focused on S06 scope (bootstrap removal), no scope creep detected. Each story brief clearly defined boundaries that were respected.

**Fabrication detection**: MIXED — FALSE GREEN gap in T-003 where CATALOG_ENDPOINT was claimed present in configuration but actually missing until remount. This represents a detection failure rather than active fabrication.

### (4) Harness waste analysis

**Budget waste**: 
- 177 seconds on failed m5-evaluate-a1p0 (rc=130) 
- Supervisor pause + debt-freeze overhead for FALSE GREEN resolution
- Remount harvest coordination (9b7e7af) consuming supervisor time

**Process waste**:
- FALSE GREEN required manual debt recording and supervisor intervention instead of automated detection
- Probe hardening without re-queueing created testing blind spots that required manual remediation
- High-RC evaluation failures suggest inadequate pre-evaluation validation

**Recommended improvements focus on detection and prevention rather than post-hoc correction, targeting the three identified failure patterns to reduce both budget consumption and manual intervention requirements.**