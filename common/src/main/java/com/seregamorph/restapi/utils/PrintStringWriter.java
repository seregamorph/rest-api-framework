package com.seregamorph.restapi.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Shortcut for PrintWriter to StringWriter.
 */
public class PrintStringWriter extends PrintWriter {

    public PrintStringWriter() {
        super(new StringWriter());
    }

    @Override
    public String toString() {
        return out.toString();
    }

}
