/**
 * 
 */
package net.consensys.spring.awesome.statemachine.exception;

/**
 * EntityNotFoundException
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class EntityNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -1118878855938251305L;

    public EntityNotFoundException(String message) {
        super(message);
    }
}
