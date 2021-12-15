package applu.interpreter;

/**
 * Causes the Applu interpreter to exit the current innermost nested loop. Thrown by
 * {@link applu.ast.LoopExitNode}.
 * 
 * @author Paul Cantrell
 */
public class LoopExited extends RuntimeException {
}
