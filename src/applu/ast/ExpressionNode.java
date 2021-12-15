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
public abstract class ExpressionNode extends ASTNode {
    /**
     * Evaluates this expression using the given variable values.
     * 
     * Used to implement the Applu interpreter.
     * 
     * Reports to the evaluation context’s tracer (if present) that expression evaluation is starting,
     * and reports the resulting value when evaluation is complete.
     * 
     * @param context Provides the values of variables this expression can use.
     * @return The result of evaluating this expression.
     */
    public final double evaluate(EvaluationContext context) {
        context.trace(this, EvaluationContext.Tracer.Phase.STARTED);
        Double result = null;
        try {
            result = doEvaluate(context);
            return result;
        } finally {
            context.trace(this, EvaluationContext.Tracer.Phase.COMPLETED, result);
        }
    }

    /**
     * Subclasses should implement this to support interpreted execution.
     */
    protected abstract double doEvaluate(EvaluationContext context);
}
