package com.tbl324.desktop.client;

public class ApiException extends Exception {

    public ApiException(int statusCode) {
        super("HTTP " + statusCode);
    }

    public ApiException(int statusCode, String body) {
        super(extractDetail(statusCode, body));
    }

    public ApiException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    private static String extractDetail(int statusCode, String body) {
        if (body == null || body.isBlank()) return "HTTP " + statusCode;
        try {
            com.fasterxml.jackson.databind.JsonNode node =
                    new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);
            if (node.has("detail") && !node.get("detail").isNull())
                return node.get("detail").asText();
        } catch (Exception ignored) {}
        return "HTTP " + statusCode + ": " + body;
    }
}
