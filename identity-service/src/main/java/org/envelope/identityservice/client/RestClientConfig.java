package org.envelope.identityservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.IOException;

@Configuration
public class RestClientConfig {
//    private final ObservationRegistry observationRegistry;
    @Value("${client.image-service.url}")
    private String imageServiceUrl;
    @Bean
    public ImageClient imageClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(imageServiceUrl)
                .requestInterceptor(new LoggingInterceptor())
                .build();
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(ImageClient.class);
    }
    public static class LoggingInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            System.out.println("Request: " + request.getMethod() + " " + request.getURI());
            request.getHeaders().forEach((k, v) -> System.out.println(k + ": " + v));
            return execution.execute(request, body);
        }
    }
}
