# Migration debt

Tasks the harness could not complete within its iteration budget, with
the failure evidence. Written by the Hermes orchestrator; resolved by a
follow-up run or a human steering-loop improvement (better spec, better
skill, better sensor) — never by weakening the sensors.

(none)

## T-003 — FALSE GREEN (O-AC-K8S / O-FGRETRO) — 2026-07-30T20:54Z

- Commit ed1514f claimed CATALOG_ENDPOINT already present; props lacked it until remount 9b7e7af.
- Probe hardened mid-run (O-AC-K8S); instance never re-queued (O-FGRETRO).
- HOLD: supervisor-pause + debt-freeze before M5 ship; remount harvest.
- RESOLVED 2026-07-30T21:10Z: remount 9b7e7af; freeze cleared; M5 resumed by implementing agent (O-NOWAIT).
