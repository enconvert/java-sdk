package com.enconvert.model.v2;

/**
 * String constants for every V2 enumeration. Fields that accept one of these
 * values are typed as plain {@code String} (not a Java {@code enum}) so a
 * newer API value never breaks deserialization in an older SDK version —
 * these classes exist purely as a discoverable, typo-proof reference.
 */
public final class V2Enums {

    private V2Enums() {
    }

    /** Values for {@code PerceiveOptions#outputs}. */
    public static final class PerceiveOutputs {
        private PerceiveOutputs() {
        }

        public static final String MARKDOWN = "markdown";
        public static final String MARKDOWN_FIT = "markdown_fit";
        public static final String HTML_CLEANED = "html_cleaned";
        public static final String HTML_RAW = "html_raw";
        public static final String SCREENSHOT = "screenshot";
        public static final String SCREENSHOT_FULL_PAGE = "screenshot_full_page";
        public static final String PDF = "pdf";
        public static final String LINKS = "links";
        public static final String IMAGES = "images";
        public static final String STRUCTURED = "structured";
    }

    /** Values for {@code PerceiveOptions#extract}. */
    public static final class PerceiveExtract {
        private PerceiveExtract() {
        }

        public static final String TABLES = "tables";
        public static final String PRICES = "prices";
        public static final String CONTACTS = "contacts";
        public static final String METADATA = "metadata";
        public static final String MAIN_CONTENT = "main_content";
        public static final String HEADINGS = "headings";
        public static final String STRUCTURED_DATA = "structured_data";
        public static final String TECHNOLOGIES = "technologies";
        public static final String ALL = "all";
    }

    /** Values for {@code PerceiveOptions#blockResources}. */
    public static final class PerceiveResourceType {
        private PerceiveResourceType() {
        }

        public static final String IMAGE = "image";
        public static final String MEDIA = "media";
        public static final String FONT = "font";
        public static final String STYLESHEET = "stylesheet";
        public static final String SCRIPT = "script";
        public static final String XHR = "xhr";
        public static final String FETCH = "fetch";
        public static final String WEBSOCKET = "websocket";
        public static final String MANIFEST = "manifest";
        public static final String OTHER = "other";
    }

    /** Values for {@code PerceiveOptions#cacheMode}. */
    public static final class PerceiveCacheMode {
        private PerceiveCacheMode() {
        }

        public static final String ENABLED = "enabled";
        public static final String BYPASS = "bypass";
        public static final String REFRESH = "refresh";
    }

    /** Values for {@code PerceiveResult#status}. */
    public static final class PerceiveStatus {
        private PerceiveStatus() {
        }

        public static final String QUEUED = "queued";
        public static final String PROCESSING = "processing";
        public static final String COMPLETED = "completed";
        public static final String FAILED = "failed";
    }

    /** Values for {@code PerceiveResult#extractionTier}. */
    public static final class PerceiveExtractionTier {
        private PerceiveExtractionTier() {
        }

        public static final String HEURISTIC = "heuristic";
        public static final String CSS = "css";
        public static final String LLM = "llm";
    }

    /** Values for {@code PerceiveBatchOptions#outputMode}. */
    public static final class PerceiveBatchOutputMode {
        private PerceiveBatchOutputMode() {
        }

        public static final String MANIFEST = "manifest";
        public static final String ZIP = "zip";
    }

    /** Values for {@code PerceiveBatchResult#status}. */
    public static final class PerceiveBatchStatus {
        private PerceiveBatchStatus() {
        }

        public static final String QUEUED = "queued";
        public static final String PROCESSING = "processing";
        public static final String COMPLETED = "completed";
        public static final String FAILED = "failed";
        public static final String PARTIAL = "partial";
    }

    /** Values for {@code DiscoverOptions#mode} / {@code DistillDiscoverFrom#mode}. */
    public static final class DiscoverMode {
        private DiscoverMode() {
        }

        public static final String SITEMAP = "sitemap";
        public static final String CRAWL = "crawl";
        public static final String HYBRID = "hybrid";
    }

    /** Values for {@code LookupOptions#category}. */
    public static final class LookupCategory {
        private LookupCategory() {
        }

        public static final String WEB = "web";
        public static final String NEWS = "news";
        public static final String IMAGES = "images";
        public static final String SCHOLAR = "scholar";
        public static final String PATENTS = "patents";
        public static final String MAPS = "maps";
    }

    /** Values for {@code LookupOptions#timeFilter}. */
    public static final class LookupTimeFilter {
        private LookupTimeFilter() {
        }

        public static final String HOUR = "hour";
        public static final String DAY = "day";
        public static final String WEEK = "week";
        public static final String MONTH = "month";
        public static final String YEAR = "year";
    }

    /** Values for {@code CssField#type}. */
    public static final class CssFieldType {
        private CssFieldType() {
        }

        public static final String TEXT = "text";
        public static final String ATTRIBUTE = "attribute";
        public static final String HTML = "html";
        public static final String REGEX = "regex";
        public static final String NESTED = "nested";
        public static final String LIST = "list";
        public static final String NESTED_LIST = "nested_list";
    }

    /** Values for {@code DistillItem#extractionTier}. */
    public static final class DistillExtractionTier {
        private DistillExtractionTier() {
        }

        public static final String CSS = "css";
        public static final String LLM = "llm";
        public static final String MIXED = "mixed";
        public static final String NONE = "none";
    }

    /** Values for {@code IngestOptions#mode} / {@code IngestJob#mode}. */
    public static final class IngestMode {
        private IngestMode() {
        }

        public static final String URLS = "urls";
        public static final String SITEMAP = "sitemap";
        public static final String CRAWL = "crawl";
        public static final String FILES = "files";
    }

    /** Values for {@code IngestJob#status}. */
    public static final class IngestStatus {
        private IngestStatus() {
        }

        public static final String QUEUED = "queued";
        public static final String DISCOVERING = "discovering";
        public static final String PROCESSING = "processing";
        public static final String COMPLETED = "completed";
        public static final String FAILED = "failed";
        public static final String CANCELED = "canceled";
    }

    /** Values for {@code WatchCreateOptions#diffMode} / {@code Watcher#diffMode}. */
    public static final class WatchDiffMode {
        private WatchDiffMode() {
        }

        public static final String AUTO = "auto";
        public static final String TEXT = "text";
        public static final String STRUCTURED = "structured";
        public static final String TABLES = "tables";
        public static final String METADATA = "metadata";
    }

    /** Values for {@code Watcher#status}. */
    public static final class WatcherStatus {
        private WatcherStatus() {
        }

        public static final String ACTIVE = "active";
        public static final String PAUSED = "paused";
        public static final String DELETED = "deleted";
    }
}
