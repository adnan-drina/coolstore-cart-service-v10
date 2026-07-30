package com.demo.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;

/**
 * Characterization: CATALOG_ENDPOINT property resolution and Quarkus REST client
 * URL wiring (S03 T-007 Environment Configuration Validation).
 */
class CatalogEndpointConfigTest {

    @Test
    void applicationPropertiesDefinesCatalogEndpoint() throws IOException {
        Properties props = loadMainProperties();
        assertTrue(props.containsKey("CATALOG_ENDPOINT"), "CATALOG_ENDPOINT key present");
        assertFalse(props.getProperty("CATALOG_ENDPOINT", "").isBlank(), "CATALOG_ENDPOINT non-blank");
    }

    @Test
    void restClientUrlUsesCatalogEndpointPlaceholder() throws IOException {
        Properties props = loadMainProperties();
        String url = props.getProperty("quarkus.rest-client.catalog-service.url");
        assertTrue(url != null && url.contains("${CATALOG_ENDPOINT}"),
            "REST client url must reference ${CATALOG_ENDPOINT}, got: " + url);
    }

    @Test
    void catalogEndpointDefaultIsLocalhostHttp() throws IOException {
        Properties props = loadMainProperties();
        String endpoint = props.getProperty("CATALOG_ENDPOINT");
        assertEquals("http://localhost:8081", endpoint);
    }

    private static Properties loadMainProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("application.properties")) {
            // Ensure application.properties is available on test classpath
            if (in == null) {
                throw new AssertionError("application.properties not found on test classpath");
            }
            props.load(in);
        }
        return props;
    }
}
