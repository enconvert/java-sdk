package com.enconvert.model;

/** PDF page margins. Any field left {@code null} is omitted from the request. */
public record PdfMargins(Double top, Double bottom, Double left, Double right) {
}
