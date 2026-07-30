# Autonomous run report

## Executive summary

Autonomous migration of coolstore-cart-service-v10:
story gate passed (non-deploy story): pipeline + quality gate green. Findings delta and per-task detail: migration/run-log.md;
debt: migration/debt.md. Orchestrator custom:maas-m2/minimax-m2,
worker qwen27b/qwen3-6-27b, 4 model sessions.

- Outcome: story gate passed (non-deploy story): pipeline + quality gate green
- Supervisor version: 85526ffa; run base: aa797a872797e722f2a8048254d44b9b614b1e19
- Orchestrator: custom:maas-m2/minimax-m2; worker: qwen27b/qwen3-6-27b

## Sessions

| session | seconds | rc |
|---|---|---|
| m3-lint-a1p0 | 103 | rc=0 |
| m3-lint-a2p0 | 80 | rc=0 |
| m5-evaluate-a1p0 | 177 | rc=130 |
| m5-evaluate-a2p0 | 88 | rc=0 |

- Escalations (KPI, from supervisor events): 0 (untested: 0)

## Classified events

```
      2 success
      2 no_commit
      1 story_gate_pass
      1 pipeline_succeeded
      1 debt_retained
      1 already_complete
```
