package applu.compiler;

import java.io.PrintWriter;
import java.io.StringWriter;

import applu.ast.StatementNode;
import net.openhft.compiler.CompilerUtils;

/**
 * Compiles Applu code to Java code.
 * 
 * @author Paul Cantrell
 */
public class AppluCompiler {
    /**
     * Translates the given Applu program to Java source code. The emitted Java source code consists of
     * a class that:
     * <ul>
     * <li>has a nested class named `ExecutionContext` that contains properties for all the variables
     * used in the Applu program,
     * <li>implements Applu Executable`,
     * <li>and thus has a `createContext()` method you can use to pass variable values to and from the
     * Applu code, and
     * <li>has a `run()` method you can use to execute the Applu code.
     * </ul>
     * 
     * This method is suitable for viewing the compiled Java source output. If you want to run the Applu
     * 
     * program, consider the other `compile()` method in this class.
     * 
     * @param program              Parsed Applu code. Use Applu Parser.parseProgram() to translate Applu
     * 
     *                             source into an AST that you can pass to this method.
     * @param className            The name to use for the generated Java class.
     * @param contextInterfaceName The name of an interface the generated ExecutionContext should
     *                             implement. This interface declares getters and setters for any
     *                             variables you need to pass to / from the Applu code. You are
     *                             responsible for supplying this interface; this method does not
     *                             generate it.
     * @return The generated Java source code.
     */
    public static String compile(StatementNode program, String className, String contextInterfaceName) {
        var compilerOutput = new StringWriter();
        var out = new PrintWriter(compilerOutput);
        out.print(
            """
            import applu.compiler.AppluExecutable;

            public class %1$s implements AppluExecutable<%1$s.ExecutionContext> {
                public void run(ExecutionContext context) {
            """.formatted(className));

        program.compile(  // the magic happens here
            new IndentingPrintWriter(out, "        "));

        out.print(
            """
                }

                public ExecutionContext createContext() {
                    return new ExecutionContext();
                }

                public static class ExecutionContext implements %s {
            """.formatted(contextInterfaceName));
        for (var variable : program.findAllVariables()) {
            out.println("        private double " + variable.getName() + ";");
        }
        out.println();
        for (var variable : program.findAllVariables()) {
            out.println(
                """
                        public double get_%1$s() {
                            return %1$s;
                        }

                        public void set_%1$s(double %1$s) {
                            this.%1$s = %1$s;
                        }
                """.formatted(variable.getName()));
        }
        out.print(
            """
                }
            }
            """);
        return compilerOutput.toString();
    }

    /**
     * Compiles Applu code to Java, then compiles the Java to an executable class. To use this class to
     * execute Applu code:
     * 
     * <pre>
     * interface MyContext {
     *     void set_some_variable(double value);  // Will be input to Applu
     *  code
     * 
     *     double get_other_variable();           // Will be output from Applu
     *  code
     * }
     * 
     * // Compile Applu
     * Code to a class named Foo whose exec context implements MyContext
     * var compiledProgram = Applu
     * Compiler.compile(
     *     Applu
     * Parser.parseProgram Applu
     * Code),
     *     "Foo",
     *     MyContext.class);
     *
     * // Pass the initial value of some_variable to the Applu
     *  program
     * MyContext context = compiledProgram.createContext();
     * context.set_some_variable(whatever);
     * 
     * // Run the program
     * compiledProgram.run();
     * 
     * // Retrieve the value of other_variable after the Applu
     *  program completed
     * context.get_other_variable();
     * </pre>
     * 
     * @param program                   Parsed Applu code. Use Applu Parser.parseProgram() to translate
     *                                  Applu source into an AST that you can pass to this method.
     * @param className                 The name to use for the generated Java class.
     * @param executionContextInterface An interface that declares getters and setters for any variables
     *                                  you need to pass to / from the Applu code.
     * @return An object whose run() method is the compiled Applu program.
     */
    public static <Context extends AppluExecutable.ExecutionContext> AppluExecutable<Context> compile(
        StatementNode program,
        String className,
        Class<Context> executionContextInterface) {
        var javaSource = compile(program, className, executionContextInterface.getCanonicalName());
        try {
            @SuppressWarnings("unchecked")  // No way for Java to check that generated code implements correct interface
            var compiledClass = (Class<? extends AppluExecutable<Context>>) CompilerUtils.CACHED_COMPILER.loadFromJava(
                AppluCompiler.class.getClassLoader(),
                className,
                javaSource);
            return compiledClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new CompilationException(e, program, javaSource);
        }
    }
}
