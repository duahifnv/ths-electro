package org.envelope.helperservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        servers = {
                @Server(url = "/api/helper", description = "Сервер helper-service")
        }
)
public class HelperServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelperServiceApplication.class, args);
    }
}
