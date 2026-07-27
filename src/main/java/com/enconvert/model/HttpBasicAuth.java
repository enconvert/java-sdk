package com.enconvert.model;

/** HTTP Basic Auth credentials for pages behind a login (plan-gated). */
public record HttpBasicAuth(String username, String password) {
}
