/**
 * 
 */
package net.consensys.spring.awesome.statemachine.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.data.repository.CrudRepository;

import lombok.extern.slf4j.Slf4j;
import net.consensys.spring.awesome.statemachine.configuration.StateMachineConfiguration.StateTo;
import net.consensys.spring.awesome.statemachine.configuration.StateMachineConfiguration.Transition;
import net.consensys.spring.awesome.statemachine.exception.BadStateException;
import net.consensys.spring.awesome.statemachine.exception.EntityNotFoundException;
import net.consensys.spring.awesome.statemachine.exception.NoTargetStateException;
import net.consensys.spring.awesome.statemachine.exception.StateMachineConfigurationException;
import net.consensys.spring.awesome.statemachine.utils.lambda.Throwing;
import net.consensys.spring.awesome.statemachine.utils.reflection.ReflectionUtils;
import net.consensys.spring.awesome.statemachine.utils.validation.ValidatorUtils;

/**
 * StateMachine implementation
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Slf4j
public class StateMachineImpl<S extends Enum<S>, E extends Enum<E>, T, I extends Serializable> implements StateMachine<S, E, T, I> {

    /**
     * List of transitions
     */
    private final List<Transition<S, E, T, I>> transitions;
    
    /**
     * beforeAll
     * Function executed before the logic to retrieve the entity from the ID
     * Optional: Only if persistence is enable (repository present)
     */
    private final Optional<Function<I, T>> beforeAll;
    
    /**
     * afterAll
     * Function executed after the logic to persit the entity
     * Optional: Only if persistence is enable (repository present)
     */
    private final Optional<Consumer<T>> afterAll;
    
    public StateMachineImpl(List<Transition<S, E, T, I>> transitions, CrudRepository<T, I> repository) {
        this.transitions = ValidatorUtils.requireNonEmpty(transitions, "transitions");

        this.beforeAll = Optional.ofNullable(repository)
                .map(r -> id -> Optional.ofNullable(r.findOne(id))
                    .orElseThrow(() -> new EntityNotFoundException("Entity [id: " + id + "] not found")));
        
        this.afterAll = Optional.ofNullable(repository)
                .map(r -> r::save);
        
    }

    @Override
    public void onTransition(E event, I id) {
        this.onTransition(event, id, null);
    }


    @Override
    public void onTransition(E event, I id, Object context) {
        

        //////////////////////////////////////////////////////////////////////////////////////
        // Validation
        ValidatorUtils.requireNonNull(event);
        ValidatorUtils.requireNonNull(id);
        if(!beforeAll.isPresent() || !afterAll.isPresent()) {
            throw new StateMachineConfigurationException("A repositoy needs to be configured");
        }
        

        //////////////////////////////////////////////////////////////////////////////////////
        // Check if a transition exists for this events in the configuration
        Optional<Transition<S, E, T, I>> checkTransition = transitions.stream().filter( t -> t.getEvent().equals(event) ).findFirst();
        if(!checkTransition.isPresent()) {
            log.warn("No transition found for the event {}", event);
            return;
        }
        Transition<S, E, T, I> transition = checkTransition.get();
        
        //////////////////////////////////////////////////////////////////////////////////////
        // Execute beforeAll function that returns the entity
        T entity = Optional.ofNullable(transition.getBeforeAll())
            .map(f-> f.apply(id))
            .orElseGet(() -> beforeAll.get().apply(id));
        log.trace("beforeAll -> {}", entity);

        
        //////////////////////////////////////////////////////////////////////////////////////
        // execute
        this.onTransition(event, id, entity, context);

        
        //////////////////////////////////////////////////////////////////////////////////////
        // Execute afterAll function that saves the entity
        if(Optional.ofNullable(transition.getAfterAll()).isPresent()) {
            transition.getAfterAll().accept(entity);
        } else {
            this.afterAll.get().accept(entity);
        }
    }


    @Override
    public void onTransition(E event, I id, T entity, Object context) {

        //////////////////////////////////////////////////////////////////////////////////////
        // Validation
        ValidatorUtils.requireNonNull(event);
        ValidatorUtils.requireNonNull(id);
        ValidatorUtils.requireNonNull(entity);
        


        //////////////////////////////////////////////////////////////////////////////////////
        // Check if a transition exists for this events in the configuration
        Optional<Transition<S, E, T, I>> checkTransition = transitions.stream().filter( t -> t.getEvent().equals(event) ).findFirst();
        if(!checkTransition.isPresent()) {
            log.warn("No transition found for the event {}", event);
            return;
        }
        Transition<S, E, T, I> transition = checkTransition.get();


        //////////////////////////////////////////////////////////////////////////////////////
        // Execute before function
        Optional.ofNullable(transition.getBefore()).ifPresent(Throwing.rethrow(c->c.accept(entity, context)));


        //////////////////////////////////////////////////////////////////////////////////////
        // Check if the instance is at the right state
        if(!transition.getFrom().equals(getState(entity))) {
            throw new BadStateException(id, getState(entity), transition.getFrom(), event);
        }

        
        //////////////////////////////////////////////////////////////////////////////////////
        // Get the next state
        S to;
        if(transition.getTo().size() == 1) {
            to = transition.getTo().get(0).getTo();
        } else {
            Optional<StateTo<S, T>> toCheck = transition.getTo()
                    .stream()
                    .filter( t -> t.getCondition().apply(entity, context) )
                    .findFirst();
            if(!toCheck.isPresent()) {
                throw new NoTargetStateException(id, getState(entity), event);
            }

            to = toCheck.get().getTo();
        }

        
        //////////////////////////////////////////////////////////////////////////////////////
        // Change state
        setState(entity, to);


        //////////////////////////////////////////////////////////////////////////////////////
        // Execute after function
        Optional.ofNullable(transition.getAfter()).ifPresent(Throwing.rethrow(c->c.accept(entity, context)));
    }


    /* ****************************************************
     * GET/SET STATE
     * **************************************************** */
    
    private void setState(T entity, S state) {
        ValidatorUtils.requireNonNull(entity, "entity");
        ValidatorUtils.requireNonNull(state, "state");
        Field field = ReflectionUtils.findStateField(entity);
        ReflectionUtils.callSetter(entity, field, state);
    }

    @SuppressWarnings("unchecked")
    private S getState(T entity) {
        ValidatorUtils.requireNonNull(entity, "entity");
        Field field = ReflectionUtils.findStateField(entity);
        return (S) ReflectionUtils.callGetter(entity, field); 
    }


}
