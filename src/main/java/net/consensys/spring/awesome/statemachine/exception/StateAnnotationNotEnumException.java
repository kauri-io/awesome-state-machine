/**
 * 
 */
package net.consensys.spring.awesome.statemachine.exception;

/**
 * StateAnnotationNotEnumException
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class StateAnnotationNotEnumException extends RuntimeException {

    private static final long serialVersionUID = -1118878855938251305L;

    public StateAnnotationNotEnumException(String message) {
        super(message);
    }
}
