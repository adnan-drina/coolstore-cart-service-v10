package com.demo;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class PlatformVerificationTest {

    @Test
    void healthEndpointReturnsUp() {
        given()
                .when().get("/q/health")
                .then()
                .statusCode(200)
                .body("status", is("UP"));
    }

    @Test
    void quarkusBomIsParent() throws Exception {
        Path pom = Paths.get("pom.xml");
        String content = Files.readString(pom);
        assert content.contains("quarkus-bom") : "pom.xml must reference quarkus-bom";
        assert content.contains("com.redhat.quarkus.platform")
                : "pom.xml must use Red Hat Quarkus platform group ID";
        assert !content.contains("spring-boot-starter-parent")
                : "pom.xml must not reference Spring Boot parent";
    }

    @Test
    void noSpringBootDependencies() throws Exception {
        Path pom = Paths.get("pom.xml");
        String content = Files.readString(pom);
        assert !content.contains("spring-boot-starter-web")
                : "spring-boot-starter-web must be removed";
        assert !content.contains("spring-boot-starter-jersey")
                : "spring-boot-starter-jersey must be removed";
        assert !content.contains("spring-boot-starter-actuator")
                : "spring-boot-starter-actuator must be removed";
        assert !content.contains("spring-boot-maven-plugin")
                : "spring-boot-maven-plugin must be removed";
        assert !content.contains("spring-cloud-starter-openfeign")
                : "spring-cloud-starter-openfeign must be removed";
    }

    @Test
    void quarkusExtensionsPresent() throws Exception {
        Path pom = Paths.get("pom.xml");
        String content = Files.readString(pom);
        assert content.contains("quarkus-rest") : "quarkus-rest must be present";
        assert content.contains("quarkus-smallrye-health")
                : "quarkus-smallrye-health must be present";
        assert content.contains("quarkus-rest-client")
                : "quarkus-rest-client must be present";
        assert content.contains("quarkus-maven-plugin")
                : "quarkus-maven-plugin must be present";
    }

    @Test
    void packageNamespaceIsComDemo() throws Exception {
        Path srcMain = Paths.get("src", "main", "java", "com", "demo");
        assert srcMain.toFile().isDirectory()
                : "src/main/java/com/demo directory must exist";
        Path srcTest = Paths.get("src", "test", "java", "com", "demo");
        assert srcTest.toFile().isDirectory()
                : "src/test/java/com/demo directory must exist";

        Path legacyMain = Paths.get("src", "main", "java", "com", "redhat", "coolstore");
        assert !legacyMain.toFile().exists()
                : "Legacy com.redhat.coolstore package must not exist";
    }

    @Test
    void javaVersionIs21() throws Exception {
        Path pom = Paths.get("pom.xml");
        String content = Files.readString(pom);
        assert content.contains("<maven.compiler.release>21</maven.compiler.release>")
                || content.contains("<java.version>21</java.version>")
                : "Java version must be 21";
    }

    @Test
    void applicationPropertiesExists() throws Exception {
        Path props = Paths.get("src", "main", "resources", "application.properties");
        assert props.toFile().exists() : "application.properties must exist";
    }
}
