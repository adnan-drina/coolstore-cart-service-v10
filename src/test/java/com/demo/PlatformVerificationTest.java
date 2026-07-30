package com.demo;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Platform verification test class to validate the Quarkus migration
 * and ensure all required components are properly configured.
 */
@QuarkusTest
class PlatformVerificationTest {

    /**
     * Verifies that the health endpoint returns UP status.
     */
    @Test
    void healthEndpointReturnsUp() {
        given()
                .when().get("/q/health")
                .then()
                .statusCode(200)
                .body("status", is("UP"));
    }

    /**
     * Verifies that the Maven POM uses the Quarkus BOM as parent
     * and the Red Hat Quarkus platform group ID.
     */
    @Test
    void quarkusBomIsParent() throws java.io.IOException {
        Path pom = Paths.get("pom.xml");
        String content = Files.readString(pom);
        org.junit.jupiter.api.Assertions.assertTrue(
                content.contains("quarkus-bom"), 
                "pom.xml must reference quarkus-bom");
        org.junit.jupiter.api.Assertions.assertTrue(
                content.contains("com.redhat.quarkus.platform"),
                "pom.xml must use Red Hat Quarkus platform group ID");
        org.junit.jupiter.api.Assertions.assertFalse(
                content.contains("spring-boot-starter-parent"),
                "pom.xml must not reference Spring Boot parent");
    }

    /**
     * Verifies that all Spring Boot dependencies have been removed from the POM.
     */
    @Test
    void noSpringBootDependencies() throws java.io.IOException {
        Path pom = Paths.get("pom.xml");
        String content = Files.readString(pom);
        org.junit.jupiter.api.Assertions.assertFalse(
                content.contains("spring-boot-starter-web"),
                "spring-boot-starter-web must be removed");
        org.junit.jupiter.api.Assertions.assertFalse(
                content.contains("spring-boot-starter-jersey"),
                "spring-boot-starter-jersey must be removed");
        org.junit.jupiter.api.Assertions.assertFalse(
                content.contains("spring-boot-starter-actuator"),
                "spring-boot-starter-actuator must be removed");
        org.junit.jupiter.api.Assertions.assertFalse(
                content.contains("spring-boot-maven-plugin"),
                "spring-boot-maven-plugin must be removed");
        org.junit.jupiter.api.Assertions.assertFalse(
                content.contains("spring-cloud-starter-openfeign"),
                "spring-cloud-starter-openfeign must be removed");
    }

    /**
     * Verifies that the Quarkus extensions are present in the POM file.
     */
    @Test
    void quarkusExtensionsPresent() throws java.io.IOException {
        Path pom = Paths.get("pom.xml");
        String content = Files.readString(pom);
        org.junit.jupiter.api.Assertions.assertTrue(
                content.contains("quarkus-rest"), 
                "quarkus-rest must be present");
        org.junit.jupiter.api.Assertions.assertTrue(
                content.contains("quarkus-smallrye-health"),
                "quarkus-smallrye-health must be present");
        org.junit.jupiter.api.Assertions.assertTrue(
                content.contains("quarkus-rest-client"),
                "quarkus-rest-client must be present");
        org.junit.jupiter.api.Assertions.assertTrue(
                content.contains("quarkus-maven-plugin"),
                "quarkus-maven-plugin must be present");
    }

    /**
     * Verifies that the package namespace has been migrated to com.demo
     * and legacy packages have been removed.
     */
    @Test
    /**
     * Verifies that the package namespace has been migrated to com.demo
     * and legacy packages have been removed.
     */
    void packageNamespaceIsComDemo() {
        Path srcMain = Paths.get("src", "main", "java", "com", "demo");
        org.junit.jupiter.api.Assertions.assertTrue(
                srcMain.toFile().isDirectory(),
                "src/main/java/com/demo directory must exist");
        Path srcTest = Paths.get("src", "test", "java", "com", "demo");
        org.junit.jupiter.api.Assertions.assertTrue(
                srcTest.toFile().isDirectory(),
                "src/test/java/com/demo directory must exist");

        Path legacyMain = Paths.get("src", "main", "java", "com", "redhat", "coolstore");
        org.junit.jupiter.api.Assertions.assertFalse(
                legacyMain.toFile().exists(),
                "Legacy com.redhat.coolstore package must not exist");
    }

    /**
     * Verifies that the Java version is set to 21 in the POM file.
     */
    @Test
    void javaVersionIs21() throws java.io.IOException {
        Path pom = Paths.get("pom.xml");
        String content = Files.readString(pom);
        org.junit.jupiter.api.Assertions.assertTrue(
                content.contains("<maven.compiler.release>21</maven.compiler.release>")
                        || content.contains("<java.version>21</java.version>"),
                "Java version must be 21");
    }

    /**
     * Verifies that the application.properties file exists in the resources directory.
     */
    @Test
    void applicationPropertiesExists() {
        Path props = Paths.get("src", "main", "resources", "application.properties");
        org.junit.jupiter.api.Assertions.assertTrue(
                props.toFile().exists(), 
                "application.properties must exist");
    }

    @Test
    void catalogEndpointPreservedInApplicationProperties() throws java.io.IOException {
        Path props = Paths.get("src", "main", "resources", "application.properties");
        String content = Files.readString(props);
        org.junit.jupiter.api.Assertions.assertTrue(
                content.contains("CATALOG_ENDPOINT"),
                "application.properties must preserve CATALOG_ENDPOINT");
    }

}
