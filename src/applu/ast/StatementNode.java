package applu.ast;

import applu.interpreter.EvaluationContext;

/**
 * An Applu abstract syntax subtree that will evaluate to a specific value when the program runs.
 * 
 * Applu only supports double-precision floating point expressions; all ExpressionNodes evaluate to
 * a double.
 * 
 * @author Paul Cantrell
 */
public abstract class StatementNode extends ASTNode {
    /**
     * Runs this statement against the given context.
     *
     * Used to implement the Wordy interpreter.
     *
     * Reports to the evaluation context’s tracer (if present) when expression evaluation is starting,
     * and when it is complete.
     *
     * @param context Provides the values of variables.
     */
    public final void run(EvaluationContext context) {
        context.trace(this, EvaluationContext.Tracer.Phase.STARTED);
        try {
            doRun(context);
        } finally {
            context.trace(this, EvaluationContext.Tracer.Phase.COMPLETED);
        }
    }

    /**
     * Subclasses should implement this to support interpreted execution.
     */
    protected abstract void doRun(EvaluationContext context);
}
