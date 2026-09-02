package com.fire.mangareader.data.network.exception;

import java.io.IOException;

public class RateLimitException extends IOException {
    public RateLimitException(String message) {
        super(message);
    }
}
