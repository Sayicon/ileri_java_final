package com.tbl324.desktop.client;

public class ApiException extends Exception {

    public ApiException(int statusCode) {
        super("HTTP " + statusCode);
    }

    public ApiException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
