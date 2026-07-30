# Harness run log

Appended by the Hermes orchestrator after every task (see
`.hermes/skills/migration-harness/`). One line per task.

| Task | Class | Attempts | Result | Files |
|---|---|---|---|---|
| M5 evaluate: Findings Delta Analysis | M5-evaluate | 1 | COMPLETED | migration/mta-findings-{before,after}.json |

## M5 EVALUATE FINDINGS DELTA

**Migration Progress:**
- BEFORE: 24 violations, 47 incidents  
- AFTER: 7 violations, 11 incidents
- **RESOLVED: 17 violations, 36 incidents (70.8% reduction)**

### RESOLVED FINDINGS (17 violations resolved):
1. **javax-to-jakarta-import-00001** (8 incidents) → **RESOLVED**: javax packages migrated to jakarta
2. **springboot-di-to-quarkus-00003** (8 incidents) → **RESOLVED**: Spring DI converted to Quarkus CDI
3. **spring-components-00001** (5 incidents) → **RESOLVED**: Spring Boot version compatibility issues fixed
4. **spring-components-00002** (5 incidents) → **RESOLVED**: Spring version compatibility issues fixed
5. **removed-javaee-modules-00020** (1 incident) → **RESOLVED**: Java EE module removal handled
6. **javaee-pom-to-quarkus-00010** (1 incident) → **RESOLVED**: Quarkus BOM adopted
7. **javaee-pom-to-quarkus-00020** (1 incident) → **RESOLVED**: Quarkus Maven plugin adopted
8. **javaee-pom-to-quarkus-00040** (1 incident) → **RESOLVED**: Maven Surefire plugin configured
9. **javaee-pom-to-quarkus-00060** (1 incident) → **RESOLVED**: Native build profile added
10. **javaee-pom-to-quarkus-00080** (1 incident) → **RESOLVED**: Quarkus JUnit artifact configured
11. **springboot-actuator-to-quarkus-0100** (1 incident) → **RESOLVED**: Actuator replaced with Quarkus health/metrics
12. **springboot-annotations-to-quarkus-00000** (1 incident) → **RESOLVED**: SpringBootApplication bootstrap migrated
13. **springboot-di-to-quarkus-00000** (1 incident) → **RESOLVED**: Spring DI artifact replaced
14. **springboot-parent-pom-to-quarkus-00000** (1 incident) → **RESOLVED**: Spring parent POM replaced
15. **springboot-plugins-to-quarkus-0000** (1 incident) → **RESOLVED**: spring-boot-maven-plugin replaced
16. **springboot-properties-to-quarkus-00000** (1 incident) → **RESOLVED**: Spring Boot properties artifact replaced
17. **springboot-web-to-quarkus-00000** (1 incident) → **RESOLVED**: Spring Web artifact replaced

### REMAINING FINDINGS (7 violations, 11 incidents):

1. **demo-env-integration-00001** (4 incidents) → **GENUINE DEBT**: Environment-driven external configuration must be preserved
   - *Explanation*: This is environment-specific configuration that needs proper Quarkus configuration management
   - *Status*: Needs follow-up migration work for proper external configuration handling

2. **localhost-http-00001** (2 incidents) → **GENUINE DEBT**: Local HTTP Calls detected
   - *Explanation*: Hardcoded localhost URLs need to be externalized for proper deployment
   - *Status*: Needs configuration refactoring

3. **jakarta-jaxrs-to-quarkus-00010** (1 incident) → **OWNED BY LATER STORY**: Replace jakarta JAX-RS dependency
   - *Explanation*: JAX-RS dependency configuration needs finalization
   - *Status*: Scheduled for post-M5 cleanup phase

4. **javaee-pom-to-quarkus-00030** (1 incident) → **OWNED BY LATER STORY**: Adopt Maven Compiler plugin
   - *Explanation*: Maven compiler plugin configuration incomplete
   - *Status*: Scheduled for post-M5 cleanup phase

5. **javaee-pom-to-quarkus-00050** (1 incident) → **OWNED BY LATER STORY**: Adopt Maven Failsafe plugin
   - *Explanation*: Integration test plugin configuration pending
   - *Status*: Scheduled for post-M5 cleanup phase

6. **springboot-metrics-to-quarkus-0100** (1 incident) → **OWNED BY LATER STORY**: Replace Micrometer with Microprofile metrics
   - *Explanation*: Metrics library migration pending finalization
   - *Status*: Scheduled for post-M5 cleanup phase

7. **springboot-metrics-to-quarkus-0200** (1 incident) → **OWNED BY LATER STORY**: Replace Micrometer code with Microprofile Metrics
   - *Explanation*: Metrics code migration needs completion
   - *Status*: Scheduled for post-M5 cleanup phase

**Analysis Complete** - Ready for preflight sensor verification.

## M5 EVALUATION COMPLETE (Updated Findings Delta)

**Final Migration Status:**
- BEFORE: 24 violations, 47 incidents  
- AFTER: 7 violations, 13 incidents
- **RESOLVED: 17 violations, 34 incidents (70.8% reduction)**

### FINAL REMAINING FINDINGS (7 violations, 13 incidents):

1. **demo-env-integration-00001** (5 incidents) → **GENUINE DEBT**: Environment-driven external configuration must be preserved
   - *Explanation*: External configuration patterns need proper Quarkus configuration management
   - *Status*: Requires follow-up migration work for configuration refactoring

2. **localhost-http-00001** (3 incidents) → **GENUINE DEBT**: Local HTTP Calls detected
   - *Explanation*: Hardcoded localhost URLs need externalization for proper deployment
   - *Status*: Requires configuration management refactoring

3. **jakarta-jaxrs-to-quarkus-00010** (1 incident) → **OWNED BY LATER STORY**: Replace jakarta JAX-RS dependency
   - *Explanation*: JAX-RS dependency configuration pending finalization
   - *Status*: Scheduled for post-M5 cleanup phase

4. **javaee-pom-to-quarkus-00030** (1 incident) → **OWNED BY LATER STORY**: Adopt Maven Compiler plugin
   - *Explanation*: Maven compiler plugin configuration incomplete
   - *Status*: Scheduled for post-M5 cleanup phase

5. **javaee-pom-to-quarkus-00050** (1 incident) → **OWNED BY LATER STORY**: Adopt Maven Failsafe plugin
   - *Explanation*: Integration test plugin configuration pending
   - *Status*: Scheduled for post-M5 cleanup phase

6. **springboot-metrics-to-quarkus-0100** (1 incident) → **OWNED BY LATER STORY**: Replace Micrometer dependency with Quarkus Microprofile metrics
   - *Explanation*: Metrics library migration pending finalization
   - *Status*: Scheduled for post-M5 cleanup phase

7. **springboot-metrics-to-quarkus-0200** (1 incident) → **OWNED BY LATER STORY**: Replace Micrometer code with Microprofile Metrics code
   - *Explanation*: Metrics code migration needs completion
   - *Status*: Scheduled for post-M5 cleanup phase

**Preflight Status:** ✅ GREEN (sensors.sh exited 0)
- Harvest fidelity: GREEN
- Sonar check: GREEN (new-code gate)
- Milestone sensor: GREEN (clean verify + sonar[full], isolated repo)
- Boot check: GREEN (Flyway + schema validation against dev DB)

**M5 EVALUATION COMPLETE** - Ready for supervised factory ship.
