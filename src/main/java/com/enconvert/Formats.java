package com.enconvert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Format tables mirroring the gateway's CONVERTER_MAP (api/v1/convert.py).
 *
 * <p>{@link #IMPLEMENTED_CONVERSIONS} is the client-side gate: the gateway
 * returns 503 for any {@code {input}-to-{output}} endpoint not in its
 * CONVERTER_MAP, so unsupported pairs are rejected here with a useful
 * message instead of paying a network round-trip for a guaranteed failure.
 */
public final class Formats {

    private Formats() {
    }

    /** The 43 implemented {@code {input}-to-{output}} conversion endpoints. */
    public static final Set<String> IMPLEMENTED_CONVERSIONS = Collections.unmodifiableSet(new java.util.LinkedHashSet<>(List.of(
            // Structured text (13)
            "json-to-xml",
            "xml-to-json",
            "json-to-yaml",
            "yaml-to-json",
            "csv-to-json",
            "json-to-csv",
            "json-to-toml",
            "toml-to-json",
            "csv-to-xml",
            "xml-to-csv",
            "markdown-to-html",
            "markdown-to-pdf",
            "html-to-pdf",
            // Documents (9) — EPUB->PDF now flows through anything-to-pdf, not a dedicated pair.
            "doc-to-pdf",
            "excel-to-pdf",
            "ppt-to-pdf",
            "odt-to-pdf",
            "ods-to-pdf",
            "odp-to-pdf",
            "ots-to-pdf",
            "pages-to-pdf",
            "numbers-to-pdf",
            // Images (21)
            "jpeg-to-png",
            "png-to-jpeg",
            "jpeg-to-svg",
            "svg-to-jpeg",
            "jpeg-to-heic",
            "heic-to-jpeg",
            "jpeg-to-webp",
            "webp-to-jpeg",
            "png-to-svg",
            "svg-to-png",
            "png-to-heic",
            "heic-to-png",
            "png-to-webp",
            "webp-to-png",
            "svg-to-heic",
            "heic-to-svg",
            "svg-to-webp",
            "webp-to-svg",
            "heic-to-webp",
            "webp-to-heic",
            "pdf-to-jpeg"
    )));

    /** Extension -&gt; API format name (input side, images). */
    public static final Map<String, String> IMAGE_FORMATS = mapOf(
            ".jpg", "jpeg",
            ".jpeg", "jpeg",
            ".png", "png",
            ".svg", "svg",
            ".heic", "heic",
            ".webp", "webp",
            // PDF is an image input solely for pdf-to-jpeg (rasterization).
            ".pdf", "pdf"
    );

    /** Extension -&gt; API format name (input side, documents). */
    public static final Map<String, String> DOCUMENT_FORMATS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(".doc", "doc");
        m.put(".docx", "doc");
        m.put(".xls", "excel");
        m.put(".xlsx", "excel");
        m.put(".ppt", "ppt");
        m.put(".pptx", "ppt");
        m.put(".html", "html");
        m.put(".htm", "html");
        m.put(".odt", "odt");
        m.put(".ods", "ods");
        m.put(".odp", "odp");
        m.put(".ots", "ots");
        m.put(".pages", "pages");
        m.put(".numbers", "numbers");
        // .epub has no dedicated document pair — use convertToPdf / convertToMarkdown.
        m.put(".md", "markdown");
        m.put(".markdown", "markdown");
        m.put(".csv", "csv");
        m.put(".json", "json");
        m.put(".xml", "xml");
        m.put(".yaml", "yaml");
        m.put(".yml", "yaml");
        m.put(".toml", "toml");
        DOCUMENT_FORMATS = Collections.unmodifiableMap(m);
    }

    public static final Map<String, String> MIME_BY_EXT;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(".jpg", "image/jpeg");
        m.put(".jpeg", "image/jpeg");
        m.put(".png", "image/png");
        m.put(".svg", "image/svg+xml");
        m.put(".heic", "image/heic");
        m.put(".webp", "image/webp");
        m.put(".pdf", "application/pdf");
        m.put(".doc", "application/msword");
        m.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        m.put(".xls", "application/vnd.ms-excel");
        m.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        m.put(".ppt", "application/vnd.ms-powerpoint");
        m.put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        m.put(".html", "text/html");
        m.put(".htm", "text/html");
        m.put(".odt", "application/vnd.oasis.opendocument.text");
        m.put(".ods", "application/vnd.oasis.opendocument.spreadsheet");
        m.put(".odp", "application/vnd.oasis.opendocument.presentation");
        m.put(".epub", "application/epub+zip");
        m.put(".md", "text/markdown");
        m.put(".markdown", "text/markdown");
        m.put(".csv", "text/csv");
        m.put(".json", "application/json");
        m.put(".xml", "application/xml");
        m.put(".yaml", "application/x-yaml");
        m.put(".yml", "application/x-yaml");
        m.put(".toml", "application/toml");
        MIME_BY_EXT = Collections.unmodifiableMap(m);
    }

    // Common aliases users pass that differ from the API's canonical format names.
    private static final Map<String, String> OUTPUT_FORMAT_ALIASES = mapOf(
            "jpg", "jpeg",
            "yml", "yaml",
            "htm", "html",
            "md", "markdown"
    );

    public static String extOf(String name) {
        int i = name.lastIndexOf('.');
        return i == -1 ? "" : name.substring(i).toLowerCase();
    }

    public static String mimeFor(String name) {
        return MIME_BY_EXT.getOrDefault(extOf(name), "application/octet-stream");
    }

    /** Map a filename's extension to its API input format, or throw. */
    public static String resolveInputFormat(String name, Map<String, String> map) {
        String ext = extOf(name);
        String fmt = map.get(ext);
        if (fmt == null) {
            String supported = String.join(", ", new TreeSet<>(map.keySet()));
            throw new IllegalArgumentException(
                    "Unsupported file extension '" + ext + "'. Supported: " + supported);
        }
        return fmt;
    }

    /** Lowercase, strip a leading dot, and resolve aliases (jpg, yml, htm, md). */
    public static String normalizeOutputFormat(String fmt) {
        String f = fmt.toLowerCase();
        if (f.startsWith(".")) f = f.substring(1);
        return OUTPUT_FORMAT_ALIASES.getOrDefault(f, f);
    }

    /** List the output formats the API implements for a given input format. */
    public static List<String> validOutputsFor(String inputFormat) {
        String prefix = inputFormat + "-to-";
        List<String> outputs = new ArrayList<>();
        for (String name : IMPLEMENTED_CONVERSIONS) {
            if (name.startsWith(prefix)) {
                outputs.add(name.substring(prefix.length()));
            }
        }
        Collections.sort(outputs);
        return outputs;
    }

    /**
     * Assert {@code {input}-to-{output}} is an implemented endpoint and
     * return its name. Throws with the list of valid outputs for that input
     * otherwise.
     */
    public static String assertConversionImplemented(String inputFormat, String outputFormat) {
        String endpoint = inputFormat + "-to-" + outputFormat;
        if (!IMPLEMENTED_CONVERSIONS.contains(endpoint)) {
            List<String> outputs = validOutputsFor(inputFormat);
            String hint = outputs.isEmpty()
                    ? "No conversions are available for input format '" + inputFormat + "'"
                    : "Supported outputs for '" + inputFormat + "': " + String.join(", ", outputs);
            throw new IllegalArgumentException(
                    "Conversion '" + inputFormat + "' to '" + outputFormat + "' is not supported. " + hint + ".");
        }
        return endpoint;
    }

    private static Map<String, String> mapOf(String... kv) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put(kv[i], kv[i + 1]);
        }
        return Collections.unmodifiableMap(m);
    }
}
