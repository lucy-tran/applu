package applu.compiler;

import org.junit.jupiter.api.Test;

import applu.ast.ASTNode;
import applu.ast.ConstantNode;
import applu.ast.VariableNode;
// import applu.compiler.AppluCompiler;
// import applu.compiler.AppluExecutable;

import java.io.PrintWriter;
import java.io.StringWriter;

import static applu.parser.AppluParser.parseExpression;
import static applu.parser.AppluParser.parseProgram;
import static applu.parser.AppluParser.parseStatement;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompilerTest {
    @Test
    void compileConstant() {
        assertCompilationEquals("-34.13", new ConstantNode(-34.13));
    }

    @Test
    void compileVariable() {
        assertCompilationEquals("context.foo", new VariableNode("foo"));
    }

    @Test
    void compileBinaryExpression() {
        assertExpressionCompilesTo(
            "(context.x + 1.0)",
            "x plus 1");
        assertExpressionCompilesTo(
            "(2.0 - (context.x + 3.0))",
            "2 minus (x plus 3)");
        assertExpressionCompilesTo(
            "((2.0 - context.x) + 3.0)",
            "2 minus x plus 3");
        assertExpressionCompilesTo(
            "((1.0 * 2.0) / 3.0)",
            "1 times 2 divided by 3");
        assertExpressionCompilesTo(
            "(Math.pow(context.x, 3.0) + Math.pow(context.y, 2.0))",
            "x to the power of 3 plus y squared");
    }

    @Test
    void compileAssignment() {
        assertStatementCompilesTo(
            "context.foo = 10.0;",
            "set foo to 10");
        assertStatementCompilesTo(
            "context.foo = (context.bar - 10.0);",
            "set foo to bar minus 10");
    }

    @Test
    void compileBlock() {
        assertCompilationEquals(
            "{ context.x = 17.0; context.y = Math.pow(context.x, 2.0); }",
            parseProgram("set x to 17. set y to x squared."));
    }

    @Test
    void compileConditional() {
        assertStatementCompilesTo(
            "if(context.x < 12.0) context.a = context.x; else context.b = 0.0;",
            "if x is less than 12 then set a to x else set b to 0");
        assertStatementCompilesTo(
            "if(context.x < 12.0) context.a = context.x; else {}",
            "if x is less than 12 then set a to x");
        assertStatementCompilesTo(
            "if(context.x < 12.0) {"
                + " context.a = context.x; context.b = 1.0; }"
                + " else { context.a = 1.0; context.b = context.x; }",
            "if x is less than 12 then:"
                + " set a to x. set b to 1."
                + " else: set a to 1. set b to x."
                + " end of conditional");
    }

    @Test
    void compileLoopExit() {
        assertStatementCompilesTo(
            "break;",
            "exit loop");
    }

    @Test
    void compileLoop() {
        assertStatementCompilesTo(
            "while(true) { context.x = (context.x + 1.0); }",
            "loop: set x to x plus 1. end of loop");
    }

    // Full end-to-end test; other tests here just check generated code.
    @Test
    void executeCompiledCode() {
        AppluExecutable<TestContext> executable = AppluCompiler.compile(
            parseProgram("Set z to 3. Set x to y times z."),
            "TestProgram",
            TestContext.class);
        assertEquals("TestProgram", executable.getClass().getName());

        TestContext context = executable.createContext();
        context.set_y(127);
        executable.run(context);
        assertEquals(381, context.get_x());
    }

    public static interface TestContext extends AppluExecutable.ExecutionContext {
        void set_y(double y);

        double get_x();
    }

    // ––––––– Helpers –––––––

    private void assertExpressionCompilesTo(String expectedJavaCode, String appluExpression) {
        assertCompilationEquals(expectedJavaCode, parseExpression(appluExpression));
    }

    private void assertStatementCompilesTo(String expectedJavaCode, String appluStatement) {
        assertCompilationEquals(expectedJavaCode, parseStatement(appluStatement));
    }

    private void assertCompilationEquals(String expectedJavaCode, ASTNode ast) {
        var result = new StringWriter();
        var out = new PrintWriter(result);
        ast.compile(out);
        assertEquals(normalizeWhitespace(expectedJavaCode), normalizeWhitespace(result.toString()));
    }

    // Permits non-significant whitespace differences between expected values in these tests and the
    // actual compiler output
    private String normalizeWhitespace(String code) {
        return code
            .replaceAll("([^A-Za-z]|^)\\s+", "$1")
            .replaceAll("\\s+([^A-Za-z]|^)", "$1")
            .replaceAll("\\s+", " ");
    }
}
