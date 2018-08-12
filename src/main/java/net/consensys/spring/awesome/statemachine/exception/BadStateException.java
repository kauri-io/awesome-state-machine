/**
 * 
 */
package net.consensys.spring.awesome.statemachine.exception;

/**
 * BadStateException
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class BadStateException extends RuntimeException {

    private static final long serialVersionUID = -1118878855938251305L;

    public <I, S extends Enum<S>, E extends Enum<E>> BadStateException(I id, S current, S expected, E event) {
        super(String.format("Entity [id: %s] is in state %s whilst state %s is expected for event %s", id, current, expected,  event));
    }
}
