package com.enconvert.model.v2;

import java.util.List;

/** One field of a CSS extraction schema (recursive for nested types). */
public final class CssField {

    private final String name;
    private final String type;
    private final String selector;
    private final String attribute;
    private final String pattern;
    private final Object defaultValue;
    private final String transform;
    private final List<CssField> fields;

    private CssField(Builder b) {
        this.name = b.name;
        this.type = b.type;
        this.selector = b.selector;
        this.attribute = b.attribute;
        this.pattern = b.pattern;
        this.defaultValue = b.defaultValue;
        this.transform = b.transform;
        this.fields = b.fields;
    }

    public String name() {
        return name;
    }

    /** One of "text", "attribute", "html", "regex", "nested", "list", "nested_list". See {@link V2Enums.CssFieldType}. */
    public String type() {
        return type;
    }

    public String selector() {
        return selector;
    }

    /** Required when type is "attribute". */
    public String attribute() {
        return attribute;
    }

    /** Required when type is "regex". Compiled server-side; ReDoS-screened. */
    public String pattern() {
        return pattern;
    }

    public Object defaultValue() {
        return defaultValue;
    }

    /** One of "lowercase", "uppercase", "strip". */
    public String transform() {
        return transform;
    }

    /** Required (non-empty) for "nested" / "list" / "nested_list". Max depth 5. */
    public List<CssField> fields() {
        return fields;
    }

    /** {@code name} and {@code type} are required. */
    public static Builder builder(String name, String type) {
        return new Builder(name, type);
    }

    public static final class Builder {
        private final String name;
        private final String type;
        private String selector;
        private String attribute;
        private String pattern;
        private Object defaultValue;
        private String transform;
        private List<CssField> fields;

        private Builder(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public Builder selector(String selector) {
            this.selector = selector;
            return this;
        }

        public Builder attribute(String attribute) {
            this.attribute = attribute;
            return this;
        }

        public Builder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder defaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder transform(String transform) {
            this.transform = transform;
            return this;
        }

        public Builder fields(List<CssField> fields) {
            this.fields = fields;
            return this;
        }

        public CssField build() {
            return new CssField(this);
        }
    }
}
