/**
 * 
 */
package net.consensys.spring.awesome.statemachine.service;

/**
 * StateMachine service interface
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public interface StateMachine<S extends Enum<S>, E extends Enum<E>, T, I> {

    /**
     * Trigger a transition
     * @param event     Event triggered
     * @param id        ID of the entity
     */
    void onTransition(E event, I id);
    
    /**
     * Trigger a transition
     * @param event     Event triggered
     * @param id        ID of the entity
     * @param context   Optional context object
     */
    void onTransition(E event, I id, Object context);
    
    /**
     * Trigger a transition
     * @param event     Event triggered
     * @param id        ID of the entity
     * @param entity    Entity
     * @param context   Optional context object
     */
    void onTransition(E event, I id, T entity, Object context);
}
