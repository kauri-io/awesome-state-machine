/**
 * 
 */
package net.consensys.spring.awesome.statemachine.exception;

/**
 * NoTargetStateException
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class NoTargetStateException extends RuntimeException {

    private static final long serialVersionUID = -1118878855938251305L;

    public <I, S extends Enum<S>, E extends Enum<E>> NoTargetStateException(I id, S current, E event) {
        super(String.format("No target state found for Entity [id: %s] - event %s - current state %s", id, event, current));
    }
}