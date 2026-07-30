# S02 Domain Model Harvest - Plan

## Quarkus Migration Mapping

This plan maps legacy domain models to Quarkus-native equivalents following the HARVEST pattern. All model classes preserve exact legacy behavior, field names, types, and method signatures while migrating from Spring Boot to Quarkus platform.

### Package Renaming Strategy

**Full prefix replacement**: `com.redhat.coolstore` → `com.demo`
- Model package: `com.redhat.coolstore.model` → `com.demo.model`
- Package rename applies to ALL classes uniformly (never `com.demo.coolstore` when targetPackage is `com.demo`)

### Recipe-Executed Transformations

**javax-to-jakarta import migration** (javax-to-jakarta-import-00001)
- **Status**: ALREADY EXECUTED via OpenRecipe in M1
- **Evidence**: Listed in `migration/recipe-log.md` with model classes
- **Action**: NO TASKS REQUIRED - harvest from `migration/staging/src`
- **Scope**: All javax.* imports → jakarta.* imports (java.util unchanged)

### HARVEST Class Strategy

All four model classes are HARVEST classes (architecture-profile §7):
- **Behavior preservation**: Every field, method, and constructor preserved exactly
- **Serialization compatibility**: serialVersionUID values maintained
- **Test compatibility**: Legacy test assertions continue to work
- **API compatibility**: JSON serialization behavior preserved

## Task Breakdown

### Dependency Order Compliance

Following `migration/dependency-order.md` lines 18-24:
1. Product (god node) - characterization tests first
2. Promotion 
3. ShoppingCartItem (god node) - characterization tests first  
4. ShoppingCart (god node) - characterization tests first

### Characterization Test Strategy

**Test doubles policy**: Characterization tests for HARVEST classes use TEST DOUBLES for not-yet-converted REDESIGN types. When ShoppingCartServiceTest references services owned by later stories, stub with Mockito mocks in `src/test`, never create real classes in `src/main`.

**Target vs Legacy pinning**: HARVEST class tests pin LEGACY values (since we're preserving behavior), not target contract values (architecture-profile §7).

### Findings Resolution Mapping

- **javax-to-jakarta-import-00001**: Recipe-executed, no tasks required
- **Package mapping**: Mechanical package rename task
- **God node testing**: Characterization tests for behavioral validation
- **Field preservation**: Verified through test assertions

### Story Dependencies

- **Prerequisites**: S01 platform modernization completed (Quarkus BOM in place)
- **Unblocks**: S03 service layer modernization (ShoppingCartService requires these models)
- **Parallel**: Can proceed independent of service/endpoint stories

### Quality Gate Compliance

- **Test coverage**: Domain model tests ensure ≥80% line coverage
- **Build verification**: `mvn clean test` must pass at each commit
- **Sensor validation**: Model-specific sensors verify field/method preservation
- **No behavior changes**: HARVEST contract forbids any functional modifications