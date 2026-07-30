# Autonomous run report

## Executive summary

Autonomous migration of coolstore-cart-service-v10:
story gate passed (non-deploy story): pipeline + quality gate green. Findings delta and per-task detail: migration/run-log.md;
debt: migration/debt.md. Orchestrator custom:maas-m2/minimax-m2,
worker qwen27b/qwen3-6-27b, 7 model sessions.

- Outcome: story gate passed (non-deploy story): pipeline + quality gate green
- Supervisor version: 85526ffa; run base: a7121c65af3bf5618261ad603dd220b5a74a0d0c
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

- Escalations (KPI, from supervisor events): 0 (untested: 0)

## Classified events

```
      3 success
      2 story_gate_pass
      2 pipeline_succeeded
      2 no_commit
      2 already_complete
      1 style_autofix
      1 sensor_red_post_commit
      1 debt_retained
```
