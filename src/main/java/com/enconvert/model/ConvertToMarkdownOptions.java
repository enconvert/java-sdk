package com.enconvert.model;

/** Options for {@code Enconvert#convertToMarkdown} (anything-to-markdown). */
public final class ConvertToMarkdownOptions {

    private final String saveTo;
    private final String outputFilename;

    private ConvertToMarkdownOptions(Builder b) {
        this.saveTo = b.saveTo;
        this.outputFilename = b.outputFilename;
    }

    public String saveTo() {
        return saveTo;
    }

    public String outputFilename() {
        return outputFilename;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String saveTo;
        private String outputFilename;

        private Builder() {
        }

        public Builder saveTo(String saveTo) {
            this.saveTo = saveTo;
            return this;
        }

        public Builder outputFilename(String outputFilename) {
            this.outputFilename = outputFilename;
            return this;
        }

        public ConvertToMarkdownOptions build() {
            return new ConvertToMarkdownOptions(this);
        }
    }
}
