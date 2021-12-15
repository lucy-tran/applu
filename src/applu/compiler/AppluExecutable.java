package applu.compiler;

import applu.ast.StatementNode;

/**
 * A compiled Applu program. To run it, use the createContext() method to create an object that will
 * hold the values of the program’s variables, then run the program with run().
 * 
 * @see AppluCompiler#compile(StatementNode, String, Class) for detailed usage instructions
 * 
 * @author Paul Cantrell
 */
public interface AppluExecutable<Context extends AppluExecutable.ExecutionContext> {
    Context createContext();

    void run(Context context);

    interface ExecutionContext {
    }
}
