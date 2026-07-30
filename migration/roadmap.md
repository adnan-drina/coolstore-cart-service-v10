# Modernization roadmap

## S01: Platform and dependency modernization
- scope: pom.xml
- findings: springboot-parent-pom-to-quarkus-00000, javaee-pom-to-quarkus-00010, javaee-pom-to-quarkus-00020, javaee-pom-to-quarkus-00030, javaee-pom-to-quarkus-00040, javaee-pom-to-quarkus-00050, javaee-pom-to-quarkus-00060, javaee-pom-to-quarkus-00080, springboot-actuator-to-quarkus-0100, springboot-di-to-quarkus-00000, springboot-metrics-to-quarkus-0100, springboot-metrics-to-quarkus-0200, springboot-plugins-to-quarkus-0000, springboot-properties-to-quarkus-00000, springboot-web-to-quarkus-00000, spring-components-00001, spring-components-00002
- depends: -
- deploy: false
- done: All Maven dependencies converted to Quarkus BOM with proper plugin configuration
- rationale: Foundation modernization must precede any code changes; Quarkus platform provides all Jakarta EE and cloud-native capabilities

## S02: Domain model harvest
- scope: src/main/java/com/redhat/coolstore/model/Product.java, src/main/java/com/redhat/coolstore/model/Promotion.java, src/main/java/com/redhat/coolstore/model/ShoppingCartItem.java, src/main/java/com/redhat/coolstore/model/ShoppingCart.java
- findings: -
- depends: S01
- deploy: false
- done: All model classes harvested with preserved behavior and Jackson JSON annotations
- rationale: God nodes Product, ShoppingCartItem, and ShoppingCart require characterization tests first; models must be stable before dependent services

## S03: Service interfaces and core services
- scope: src/main/java/com/redhat/coolstore/service/ShoppingCartService.java, src/main/java/com/redhat/coolstore/service/PromoService.java, src/main/java/com/redhat/coolstore/service/ShippingService.java, src/main/java/com/redhat/coolstore/service/CatalogService.java
- findings: springboot-di-to-quarkus-00003, demo-env-integration-00001, localhost-http-00001
- depends: S02
- deploy: false
- done: All service classes converted to @ApplicationScoped CDI with constructor injection, environment configuration preserved
- rationale: Service interfaces provide contracts; core services implement business logic without state management complexity

## S04: REST API modernization
- scope: src/main/java/com/redhat/coolstore/rest/CartEndpoint.java, src/main/java/com/redhat/coolstore/rest/JerseyConfig.java
- findings: jakarta-jaxrs-to-quarkus-00010, springboot-annotations-to-quarkus-00000
- depends: S03
- deploy: true
- done: REST endpoint converted to JAX-RS with proper error handling and session management
- rationale: API surface modernization exposes the migrated service; JerseyConfig removal is automated in Quarkus

## S05: Service implementation and integration
- scope: src/main/java/com/redhat/coolstore/service/ShoppingCartServiceImpl.java
- findings: removed-javaee-modules-00020
- depends: S04
- deploy: true
- done: Core service implementation modernized with thread-safe state management and catalog integration
- rationale: Final integration layer connects all components; thread safety and concurrent access patterns applied

## S06: Application bootstrap removal
- scope: src/main/java/com/redhat/coolstore/CartServiceApplication.java
- findings: -
- depends: S05
- deploy: true
- done: Spring Boot application class removed, Quarkus bootstrap configured
- rationale: Cleanup story removes obsolete bootstrap code after all components are modernized
