package org.envelope.identityservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class CorsIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCorsAllowsCrossOriginRequests() {
        // Заголовки с указанием Origin
        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", "http://example.com");
        headers.set(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET");

        // Отправляем OPTIONS-запрос (предварительный CORS-запрос)
        ResponseEntity<String> response = restTemplate.exchange(
                "/account/cors",
                HttpMethod.OPTIONS,
                new HttpEntity<>(headers),
                String.class
        );

        // Проверяем, что CORS разрешён
        assertNotNull(response.getHeaders().getAccessControlAllowOrigin());
        assertEquals("*", response.getHeaders().getAccessControlAllowOrigin()); // Должен разрешать все домены
    }

    @Test
    public void testActualRequestWorksWithCors() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", "http://example.com");

        ResponseEntity<String> response = restTemplate.exchange(
                "/account/cors",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals("Hello, CORS!", response.getBody());
    }
}