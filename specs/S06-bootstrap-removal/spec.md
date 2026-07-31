# S06: Application Bootstrap Removal - Specification

## Legacy Behavior Contract

The legacy application (`src/main/java/com/redhat/coolstore/CartServiceApplication.java:7`) implements Spring Boot's application bootstrap pattern:

```java
@SpringBootApplication
@EnableFeignClients
public class CartServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
    }
}
```

This class serves as the application's main entry point and Spring configuration root, providing:
- **Spring Boot Bootstrap**: `@SpringBootApplication` enables component scanning, auto-configuration, and configuration properties
- **Feign Client Integration**: `@EnableFeignClients` activates declarative REST client support for `CatalogService`
- **Application Startup**: `SpringApplication.run()` initializes the Spring context and starts embedded Tomcat server

## API Contract & Integration Surfaces

**Application Startup**: The legacy bootstrap manages application lifecycle initialization and server startup through Spring Boot's opinionated configuration model.

**CDI Initialization**: Under the hood, Spring Boot's `@SpringBootApplication` triggers:
- Component scanning for `@RestController`, `@Service`, `@Component` annotated classes
- Auto-configuration of Feign clients via `@EnableFeignClients`
- Embedded servlet container (Tomcat) initialization and port binding

**Configuration**: The bootstrap inherits Spring Boot's property injection model, enabling environment-driven configuration through:
- `application.properties` file processing
- Environment variable overrides (e.g., `CATALOG_ENDPOINT`)
- Externalized configuration via Spring's PropertySource abstraction

**Discovery**: JAX-RS resources require explicit `JerseyConfig` registration due to Spring Boot's integration with Jersey for REST endpoint discovery.

## Evidence Files

- **Bootstrap Class**: `/projects/legacy/src/main/java/com/redhat/coolstore/CartServiceApplication.java:7-13` — Spring Boot application bootstrap with Feign client integration
- **Legacy Configuration**: `/projects/legacy/src/main/java/com/redhat/coolstore/rest/JerseyConfig.java:6` — JAX-RS configuration required for endpoint discovery under Spring Boot
- **Environment Configuration**: `/projects/legacy/src/main/resources/application.properties` — CATALOG_ENDPOINT property configuration

## Legacy Component Dependencies

All application components depend on the bootstrap for:
- **Component Scanning**: `CartEndpoint` discovered via Spring's component scanning
- **Service Initialization**: `ShoppingCartServiceImpl`, `PromoService`, `ShippingService` created as Spring beans
- **Feign Client**: `CatalogService` initialized via `@EnableFeignClients` configuration
- **REST Framework**: JAX-RS endpoints registered through `JerseyConfig`

## Behavioral Preservation Requirements

- **Environment Variables**: `CATALOG_ENDPOINT` must remain functional for catalog service configuration
- **REST Endpoints**: `/api/cart/*` endpoints must continue serving cart operations
- **Service Initialization**: All `@ApplicationScoped` services must initialize correctly
- **Application Startup**: Quarkus startup must replace `SpringApplication.run()` lifecycle
- **CDI Container**: Quarkus CDI must replace Spring's bean management
- **Health Endpoints**: `/q/health` must be available through Quarkus SmallRye Health

## Story Integration

This cleanup story completes the migration by removing the final Spring Boot artifact after all functional components have been modernized:
- Domain models modernized in S02 (HARVEST classes)
- Services modernized in S03-S05 (REDESIGN classes) 
- REST endpoints modernized in S04 (REDESIGN classes)
- JerseyConfig removed in S04 (REDESIGN - removed)
- CartServiceApplication removed in S06 (REDESIGN - removed)

The result is a fully Quarkus-native application with no Spring Boot bootstrap dependencies.
