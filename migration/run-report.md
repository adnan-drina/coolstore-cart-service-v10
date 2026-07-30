# Autonomous run report

## Executive summary

Autonomous migration of coolstore-cart-service-v10:
story gate passed (non-deploy story): pipeline + quality gate green. Findings delta and per-task detail: migration/run-log.md;
debt: migration/debt.md. Orchestrator custom:maas-m2/minimax-m2,
worker qwen27b/qwen3-6-27b, 13 model sessions.

- Outcome: story gate passed (non-deploy story): pipeline + quality gate green
- Supervisor version: 85526ffa; run base: a2dede582daa3dcbfc89d10b8d4e7e949c2dda44
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

- Escalations (KPI, from supervisor events): 0 (untested: 0)

## Classified events

```
      6 success
      3 story_gate_pass
      3 sensor_red_post_commit
      3 pipeline_succeeded
      3 already_complete
      2 style_autofix
      2 no_commit
      1 sfix_committed_still_red
      1 preflight_red
      1 debt_retained
      1 debt_recorded
```
