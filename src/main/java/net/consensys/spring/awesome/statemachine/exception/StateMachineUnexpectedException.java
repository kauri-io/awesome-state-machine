/**
 * 
 */
package net.consensys.spring.awesome.statemachine.exception;

/**
 * StateMachineUnexpectedException
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class StateMachineUnexpectedException extends RuntimeException {

    private static final long serialVersionUID = -1118878855938251305L;

    public StateMachineUnexpectedException(Throwable e) {
        super(e);
    }
}
