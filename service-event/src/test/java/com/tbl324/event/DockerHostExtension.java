package com.tbl324.event;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DockerHostExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        System.setProperty("DOCKER_HOST", "tcp://localhost:2375");
        System.setProperty("DOCKER_API_VERSION", "1.41");
    }
}
