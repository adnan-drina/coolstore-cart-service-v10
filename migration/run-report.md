# Autonomous run report

## Executive summary

Autonomous migration of coolstore-cart-service-v10:
success: shipped, route 200, 4 products. Findings delta and per-task detail: migration/run-log.md;
debt: migration/debt.md. Orchestrator custom:maas-m2/minimax-m2,
worker qwen27b/qwen3-6-27b, 26 model sessions.

- Outcome: success: shipped, route 200, 4 products
- Supervisor version: ba7c145a; run base: de319e7806fb20ba39743e578715c46a0b0a949f
- Orchestrator: custom:maas-m2/minimax-m2; worker: qwen27b/qwen3-6-27b

## Sessions

| session | seconds | rc |
|---|---|---|
| m3-lint-a1p0 | 103 | rc=0 |
| m3-lint-a2p0 | 80 | rc=0 |
| m5-evaluate-a1p0 | 177 | rc=130 |
| m5-evaluate-a2p0 | 88 | rc=0 |
| retro | 59 | rc=0 |
| T-005-sfix | 574 | rc=0 |
| m5-evaluate-a1p0 | 74 | rc=0 |
| retro | 44 | rc=0 |
| T-003-a1p0 | 252 | rc=0 |
| T-003-sfix | 847 | rc=0 |
| T-005-sfix | 633 | rc=0 |
| m5-evaluate-a1p0 | 78 | rc=0 |
| preflightfix-r1-a1p0 | 161 | rc=0 |
| retro | 50 | rc=0 |
| T-002-a1p0 | 209 | rc=0 |
| T-002-a2p0 | 350 | rc=0 |
| T-003-sfix | 753 | rc=0 |
| T-005-a1p0 | 600 | rc=0 |
| T-005-sfix | 111 | rc=130 |
| T-007-a1p0 | 545 | rc=0 |
| T-007-a1p1 | 162 | rc=0 |
| T-007-a2p1 | 309 | rc=0 |
| m5-evaluate-a1p0 | 74 | rc=0 |
| preflightfix-r1-a1p0 | 257 | rc=0 |
| preflightfix-r1-a2p0 | 625 | rc=0 |
| deployfix-r1-a1p0 | 279 | rc=0 |

- Escalations (KPI, from supervisor events): 0 (untested: 0)

## Classified events

```
     10 success
      6 sensor_red_post_commit
      6 no_commit
      5 pipeline_succeeded
      4 already_complete
      3 story_gate_pass
      2 style_autofix
      2 preflight_red
      2 later_story_class
      2 debt_recorded
      1 sfix_committed_still_red
      1 quota
      1 orphan_worker
      1 debt_retained
      1 acceptance_pass
```
