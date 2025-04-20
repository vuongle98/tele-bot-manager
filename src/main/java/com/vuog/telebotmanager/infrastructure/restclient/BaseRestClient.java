package com.vuog.telebotmanager.infrastructure.restclient;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public abstract class BaseRestClient {

    protected final RestTemplate restTemplate;

    protected <T> ResponseEntity<T> get(String url, HttpHeaders headers, ParameterizedTypeReference<T> responseType) {
        return exchange(url, HttpMethod.GET, null, headers, responseType);
    }

    protected <T, B> ResponseEntity<T> post(String url, B body, HttpHeaders headers, ParameterizedTypeReference<T> responseType) {
        return exchange(url, HttpMethod.POST, body, headers, responseType);
    }

    protected <T, B> ResponseEntity<T> put(String url, B body, HttpHeaders headers, ParameterizedTypeReference<T> responseType) {
        return exchange(url, HttpMethod.PUT, body, headers, responseType);
    }

    protected <T> ResponseEntity<T> delete(String url, HttpHeaders headers, ParameterizedTypeReference<T> responseType) {
        return exchange(url, HttpMethod.DELETE, null, headers, responseType);
    }

    private <T, B> ResponseEntity<T> exchange(
            String url,
            HttpMethod method,
            B body,
            HttpHeaders headers,
            ParameterizedTypeReference<T> responseType
    ) {
        HttpEntity<B> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, method, entity, responseType);
    }

    protected HttpHeaders defaultHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null && !token.isBlank()) {
            headers.setBearerAuth(token);
        }
        return headers;
    }
}
