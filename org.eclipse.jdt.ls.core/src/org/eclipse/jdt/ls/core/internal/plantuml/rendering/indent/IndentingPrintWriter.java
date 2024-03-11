package org.eclipse.jdt.ls.core.internal.plantuml.rendering.indent;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * PrintWriter implementation that will indent each new line with a specified number of whitespace
 * characters. The writing itself can be delegated to any other {@link Writer} implementation.
 * <p>
 * Care was taken to ensure that not only lines ended by calls to {@link #println()} methods trigger indentation,
 * but any other newline characters as well.
 *
 * @author Sjoerd Talsma
 */
public class IndentingPrintWriter extends PrintWriter {

    protected IndentingPrintWriter(Appendable writer, Indentation indentation) {
        super(IndentingWriter.wrap(writer, indentation));
    }

    /**
     * Returns an indenting printwriter around the given {@code delegate}.
     * If the {@code delegate} printwriter is already an indenting printwriter, it will simply be returned as-is.
     * If the {@code delegate} printwriter is not yet an indending printwriter, a new indenting printwriter class
     * will be created to wrap the delegate using the specified <code>indentation</code>.
     *
     * @param delegate    The delegate to turn into an indenting printwriter.
     * @param indentation The indentation to use for the indenting printwriter
     *                    (optional, specify <code>null</code> to use the default indentation).
     * @return The indenting delegate writer.
     * @see Indentation#DEFAULT
     */
    public static IndentingPrintWriter wrap(Appendable delegate, Indentation indentation) {
        return delegate instanceof IndentingPrintWriter
                ? ((IndentingPrintWriter) delegate).withIndentation(indentation)
                : new IndentingPrintWriter(delegate, indentation);
    }

    protected IndentingWriter getDelegate() {
        return (IndentingWriter) super.out;
    }

    /**
     * The indentation; must be non-<code>null</code> in all practical instances of this object.
     *
     * @return The indentation (non-<code>null</code>).
     */
    private Indentation getIndentation() {
        return requireNonNull(out instanceof IndentingWriter ? ((IndentingWriter) out).getIndentation() : null,
                "No indentation detected in IndentingPrintWriter!");
    }

    private IndentingPrintWriter withIndentation(Indentation indentation) {
        return indentation == null || indentation.equals(getIndentation()) ? this
                : new IndentingPrintWriter(out, indentation);
    }

    public IndentingPrintWriter indent() {
        return withIndentation(getIndentation().increase());
    }

    public IndentingPrintWriter unindent() {
        return withIndentation(getIndentation().decrease());
    }

    public IndentingPrintWriter whitespace() {
        try {
            if (out instanceof IndentingWriter) ((IndentingWriter) out).whitespace();
            else out.append(' ');
            return this;
        } catch (IOException ioe) {
            throw new IllegalStateException("Error writing whitespace: " + ioe.getMessage(), ioe);
        }
    }

    public IndentingPrintWriter newline() {
        super.println();
        return this;
    }

    @Override
    public IndentingPrintWriter append(CharSequence csq) {
        return (IndentingPrintWriter) super.append(csq);
    }

    @Override
    public IndentingPrintWriter append(CharSequence csq, int start, int end) {
        return (IndentingPrintWriter) super.append(csq, start, end);
    }

    @Override
    public IndentingPrintWriter append(char c) {
        return (IndentingPrintWriter) super.append(c);
    }

    @Override
    public String toString() {
        return out.toString();
    }

}