package com.enconvert.model;

/** Options for {@code Enconvert#convertDocument}. */
public final class ConvertDocumentOptions {

    private final String outputFormat;
    private final String saveTo;
    private final String outputFilename;
    private final PdfOptions pdfOptions;

    private ConvertDocumentOptions(Builder b) {
        this.outputFormat = b.outputFormat;
        this.saveTo = b.saveTo;
        this.outputFilename = b.outputFilename;
        this.pdfOptions = b.pdfOptions;
    }

    /** Target format. Defaults to "pdf" when unset. */
    public String outputFormat() {
        return outputFormat;
    }

    public String saveTo() {
        return saveTo;
    }

    public String outputFilename() {
        return outputFilename;
    }

    public PdfOptions pdfOptions() {
        return pdfOptions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String outputFormat;
        private String saveTo;
        private String outputFilename;
        private PdfOptions pdfOptions;

        private Builder() {
        }

        public Builder outputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        public Builder saveTo(String saveTo) {
            this.saveTo = saveTo;
            return this;
        }

        public Builder outputFilename(String outputFilename) {
            this.outputFilename = outputFilename;
            return this;
        }

        public Builder pdfOptions(PdfOptions pdfOptions) {
            this.pdfOptions = pdfOptions;
            return this;
        }

        public ConvertDocumentOptions build() {
            return new ConvertDocumentOptions(this);
        }
    }
}
