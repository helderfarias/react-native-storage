package br.com.helderfarias.storage.util;

import java.io.Writer;

public class StringBuilderWriter extends Writer {

    private final StringBuilder builder;

    public StringBuilderWriter() {
        this.builder = new StringBuilder();
    }

    public StringBuilderWriter(final int capacity) {
        this.builder = new StringBuilder(capacity);
    }

    public StringBuilderWriter(final StringBuilder builder) {
        this.builder = builder != null ? builder : new StringBuilder();
    }

    @Override
    public Writer append(final char value) {
        builder.append(value);
        return this;
    }

    @Override
    public Writer append(final CharSequence value) {
        builder.append(value);
        return this;
    }

    @Override
    public Writer append(final CharSequence value, final int start, final int end) {
        builder.append(value, start, end);
        return this;
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public void flush() {
        // no-op
    }


    @Override
    public void write(final String value) {
        if (value != null) {
            builder.append(value);
        }
    }

    @Override
    public void write(final char[] value, final int offset, final int length) {
        if (value != null) {
            builder.append(value, offset, length);
        }
    }

    public StringBuilder getBuilder() {
        return builder;
    }

    public String toString() {
        return builder.toString();
    }
}
