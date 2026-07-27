package com.enconvert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Hand-built {@code multipart/form-data} body (java.net.http.HttpClient has no built-in support). */
final class Multipart {

    private final String boundary = "----EnconvertBoundary" + UUID.randomUUID().toString().replace("-", "");
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private boolean closed = false;

    void addField(String name, String value) {
        writeBoundary();
        write("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
        write(value);
        write("\r\n");
    }

    void addFile(String name, String filename, String contentType, byte[] bytes) {
        writeBoundary();
        write("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"\r\n");
        write("Content-Type: " + (contentType != null ? contentType : "application/octet-stream") + "\r\n\r\n");
        try {
            out.write(bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        write("\r\n");
    }

    byte[] build() {
        if (!closed) {
            write("--" + boundary + "--\r\n");
            closed = true;
        }
        return out.toByteArray();
    }

    String contentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    private void writeBoundary() {
        write("--" + boundary + "\r\n");
    }

    private void write(String s) {
        try {
            out.write(s.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
