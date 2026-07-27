package com.enconvert.model;

/** Header or footer block rendered on each PDF page. */
public record PdfHeaderFooter(
        /** Text content, max 2000 characters. */
        String content,
        /** Block height. */
        Double height) {
}
