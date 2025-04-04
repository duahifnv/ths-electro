package org.envelope.apigateway.dto;

public record ClientErrorResponse(String timestamp, int status, String error, String message, String path) {
}
