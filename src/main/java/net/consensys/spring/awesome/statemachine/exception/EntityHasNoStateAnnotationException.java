/**
 * 
 */
package net.consensys.spring.awesome.statemachine.exception;

/**
 * EntityHasNoStateAnnotationException
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class EntityHasNoStateAnnotationException extends RuntimeException {

    private static final long serialVersionUID = -1118878855938251305L;

    public EntityHasNoStateAnnotationException(String message) {
        super(message);
    }
}
