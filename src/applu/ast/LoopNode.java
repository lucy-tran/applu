package applu.ast;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

import applu.interpreter.EvaluationContext;
import applu.interpreter.LoopExited;

/**
 * Applu's only looping construct, essentially an infinite while loop. Repeatedly runs the `body`
 * statement until it encounters a LoopExitNode.
 * 
 * The interpreter implements this by catching a `LoopExited` exception to exit the loop.
 * 
 * @author Paul Cantrell
 */
public class LoopNode extends StatementNode {
    private final StatementNode body;

    public LoopNode(StatementNode body) {
        this.body = body;
    }

    @Override
    public Map<String, ASTNode> getChildren() {
        return Map.of("body", body);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LoopNode loopNode = (LoopNode) o;
        return body.equals(loopNode.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "LoopNode{body=" + body + '}';
    }

    @Override
    public void doRun(EvaluationContext context) {
        while (true) {
            try {
                body.doRun(context);
            } catch (LoopExited exception) {
                break;
            }
        }
    }

    @Override
    public void compile(PrintWriter out) {
        out.print("while(true) ");
        body.compile(out);
    }
}
