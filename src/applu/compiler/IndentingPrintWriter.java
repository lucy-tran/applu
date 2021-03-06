package applu.compiler;

import java.io.PrintWriter;

/**
 * A helper to produce better-formatted Java output. Prefixes each line of output with a given
 * indent string.
 * 
 * @author Paul Cantrell
 */
class IndentingPrintWriter extends PrintWriter {
    private final String linePrefix;
    private boolean indentPending = true;

    public IndentingPrintWriter(PrintWriter out, String linePrefix) {
        super(out);
        this.linePrefix = linePrefix;
    }

    @Override
    public void write(String s, int off, int len) {
        if (indentPending) {
            indentPending = false;
            super.write(linePrefix);
        }
        super.write(s, off, len);
    }

    @Override
    public void println(String s) {
        super.println(s);
        indentPending = true;
    }
}
