# Migration debt

Tasks the harness could not complete within its iteration budget, with
the failure evidence. Written by the Hermes orchestrator; resolved by a
follow-up run or a human steering-loop improvement (better spec, better
skill, better sensor) — never by weakening the sensors.

(none)

### RESOLVED archive — T-003 FALSE GREEN (O-AC-K8S / O-FGRETRO) — 2026-07-30T20:54Z

- Commit ed1514f claimed CATALOG_ENDPOINT already present; props lacked it until remount 9b7e7af.
- Probe hardened mid-run (O-AC-K8S); instance never re-queued (O-FGRETRO).
- HOLD: supervisor-pause + debt-freeze before M5 ship; remount harvest.
- RESOLVED 2026-07-30T21:10Z: remount 9b7e7af; freeze cleared; M5 resumed by implementing agent (O-NOWAIT).

### RESOLVED archive — T-005 milestone RED (O-SFIXSCOPE / O-SONARBLEED) — 2026-07-30T23:40Z

- O-SFIXSCOPE archived sfix  (Promo S1066/S2699 genuine; S5976 overclaim).
- Remount: re-applied Promo patch + parameterized ShippingServiceTest (S5976/S2699).
- Milestone sensor GREEN after remount commits.
- FREEZE cleared; outer-loop resume (O-NOWAIT).

T-002: Package rename com.redhat.coolstore → com.demo

COMPLETED:
- Harvested CartEndpoint.java from migration/staging with package rename
- Harvested JerseyConfig.java from migration/staging with package rename
- Harvested all referenced model and service classes:
  * ShoppingCart.java
  * ShoppingCartItem.java
  * Product.java
  * ShoppingCartService.java
  * ShoppingCartServiceImpl.java
  * CatalogService.java
  * PromoService.java
  * ShippingService.java

BLOCKED:
- Compilation errors due to Spring framework dependencies still present
- Files contain imports for:
  * org.springframework.* (removed from CartEndpoint/JerseyConfig)
  * org.glassfish.jersey.server (Jersey dependency not available)
  * org.springframework.cloud.openfeign (Spring Cloud dependency)

ROOT CAUSE:
T-002 depends on T-001 (dependency management) being completed first.
The staging files contain legacy Spring dependencies that need to be
replaced with Quarkus equivalents before compilation succeeds.

STATUS: Ready for T-001 completion, then re-run T-002 or continue with remaining tasks.
FILES MODIFIED: 10 Java files with package rename applied successfully.


## T-005 — task RED
- head: 347047a
- reason: sensor-fix did not clear task
