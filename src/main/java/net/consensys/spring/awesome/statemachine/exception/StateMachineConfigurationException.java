/**
 * 
 */
package net.consensys.spring.awesome.statemachine.exception;

/**
 * StateMachineConfigurationException
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class StateMachineConfigurationException extends RuntimeException {

    private static final long serialVersionUID = -1118878855938251305L;

    public StateMachineConfigurationException(String message) {
        super(message);
    }
}
