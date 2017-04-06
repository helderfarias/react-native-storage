package br.com.helderfarias.storage.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Created by helder on 05/04/17.
 */

public class FileUtils {

    public static final int EOF = -1;

    private static final char[] DEFAULT_BUFFER_SIZE = new char[1024 * 4];

    public static String toString(final InputStream input) throws IOException {
        InputStreamReader in = new InputStreamReader(input, Charset.defaultCharset());
        StringBuilderWriter sw = new StringBuilderWriter();
        copy(in, sw);
        return sw.toString();
    }

    private static int copy(final Reader input, final Writer output) throws IOException {
        final long count = copyLarge(input, output, DEFAULT_BUFFER_SIZE);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    private static long copyLarge(final Reader input, final Writer output, final char[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}
