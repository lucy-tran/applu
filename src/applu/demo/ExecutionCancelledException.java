package applu.demo;

/**
 * Used by both Playground and ShaderUI to halt execution of an Applu program early in response to
 * user input. (This is essential for the Playground, whose interpreter would otherwise hang
 * indefinitely as soon as the user types syntactically valid code that forms an infinite loop.)
 * 
 * @author Paul Cantrell
 */
public class ExecutionCancelledException extends RuntimeException {
}
