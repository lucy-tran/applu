package applu.compiler;

import applu.ast.StatementNode;

/**
 * An error that occurred duration compilation of an Applu program _after_ parsing. This usually
 * indicates the the generated Java code was invalid.
 * 
 * @author Paul Cantrell
 */
public class CompilationException extends RuntimeException {
    public CompilationException(Exception e, StatementNode appluSource, String javaSource) {
        super(
            "Unable to compile Applu source code: " + e
                + "\n━━━━━━━━━━ APPLU AST ━━━━━━━━━━━\n"
                + appluSource.dump()
                + "\n━━━━━━ COMPILED JAVA CODE ━━━━━━\n"
                + javaSource
                + "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
