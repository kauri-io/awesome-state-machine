/**
 * 
 */
package net.consensys.spring.awesome.statemachine.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.validation.ValidationException;

import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;

import lombok.Getter;
import net.consensys.spring.awesome.statemachine.service.StateMachine;
import net.consensys.spring.awesome.statemachine.service.StateMachineImpl;
import net.consensys.spring.awesome.statemachine.utils.validation.ValidatorUtils;

/**
 * StateMachineConfiguration allows to configure a stateMachine with transitions and an optional CrudRepository (enable/disable persitence storage)
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public abstract class StateMachineConfiguration<S extends Enum<S>, E extends Enum<E>, T, I extends Serializable> {

    private final CrudRepository<T, I> repository;
    private final List<Transition<S, E, T>> transitions;

    /* ****************************************************
     * CONSTRUCTORS
     * **************************************************** */
    
    /**
     * Build the configuration and disable the persistence
     */
    public StateMachineConfiguration() {
        this(null);
    }

    /**
     * Build the configuration and enable the persistence
     * @param repository CrudRepository for persistence
     */
    public StateMachineConfiguration(CrudRepository<T, I> repository) {
        this(repository, null);
    }
    
    /**
     * Build the configuration, enable the persistence and pass transitions
     * @param repository CrudRepository for persistence
     * @param transitions Transitions
     */
    public StateMachineConfiguration(CrudRepository<T, I> repository, List<Transition<S, E, T>> transitions) {
        this.repository = repository;
        this.transitions = Optional.ofNullable(transitions).orElseGet(ArrayList::new);
        this.transitions.forEach(StateMachineConfiguration::validate);
    }

    /* ****************************************************
     * SERVICE
     * **************************************************** */
    
    /**
     * Instantiate a StateMachine service Bean
     * @return StateMachine service
     */
    @Bean
    public StateMachine<S, E, T, I> service() {
        return new StateMachineImpl<>(transitions, repository);
    }
    

    /* ****************************************************
     * VALIDATATION
     * **************************************************** */
    
    /**
     * Validate a transition (mandatory, condition, etc.)
     * @param transition Transition to validate
     */
    private static void validate(Transition<?,?,?> transition) {

        ValidatorUtils.requireNonNull(transition, "transition");
        ValidatorUtils.requireNonNull(transition.getEvent(), "transition.event");
        ValidatorUtils.requireNonNull(transition.getFrom(), "transition.from");
        ValidatorUtils.requireNonEmpty(transition.getTo(), "transition.to");
                
        transition.getTo().stream().forEach( s -> {
            ValidatorUtils.requireNonNull(s.getTo(), "transition.stateto.to");
            if(transition.getTo().size() > 1 && s.getCondition() == null)  
                throw new ValidationException("To condition cannot be null if conditional state");
        });   
    }
    
    
    /* ****************************************************
     * BUILDERS
     * **************************************************** */
    
    /**
     * Add a transition to the configuration
     * @param transition Transition to add
     * @return Transition list
     */
    protected List<Transition<S, E, T>> add(Transition<S, E, T> transition) {
        validate(transition);
        transitions.add(transition);
        return transitions;
    }
    
    /**
     * Build an empty transition
     * @return Empty transition
     */
    protected Transition.Builder<S, E, T> transition() {
        return new Transition.Builder<>();
    }

    /* ****************************************************
     * CONFIGURATION MODEL
     * **************************************************** */
    
    public static class Transition<S extends Enum<S>, E extends Enum<E>, T> {
        
        @Getter E event; // Mandatory
        @Getter S from; // Mandatory
        @Getter List<StateTo<S, T>> to; // Mandatory [1,n]
        @Getter BiConsumer<T, Object> before; // Optional
        @Getter BiConsumer<T, Object> after; // Optional

        private Transition(Builder<S, E, T> b) {
            this.event = b.event;
            this.from = b.from;
            this.to = b.to;
            this.before = b.before;
            this.after = b.after;
        }
        
        public static final class Builder<S extends Enum<S>, E extends Enum<E>, T> {
            E event;
            S from;
            List<StateTo<S, T>> to;
            BiConsumer<T, Object> before;
            BiConsumer<T, Object> after;
            
            public Builder<S, E, T> event(E event) {
                this.event = event;
                return this;
            }
            public Builder<S, E, T> to(S state) {
                return this.to(state, null);
            }
            public Builder<S, E, T> to(S state, BiFunction<T, Object, Boolean> condition) {
                this.to = Optional.ofNullable(this.to).orElseGet(ArrayList::new);
                this.to.add(new StateTo.Builder<S, T>().to(state).condition(condition).build());
                return this;
            }
            public Builder<S, E, T> from(S from) {
                this.from = from;
                return this;
            }
            public Builder<S, E, T> before(BiConsumer<T, Object> before) {
                this.before = before;
                return this;
            }
            public Builder<S, E, T> after(BiConsumer<T, Object> after) {
                this.after = after;
                return this;
            }

            public Transition<S, E, T> build() {
                return new Transition<>(this);
            }
        }  
    }
    
    public static class StateTo<S extends Enum<S>, T> {

        @Getter private S to; // Mandatory
        @Getter private BiFunction<T, Object, Boolean> condition; // Optional if only one StateTo

        private StateTo(Builder<S, T> b) {
            this.to = b.to;
            this.condition = b.condition;
        }
        
        public static final class Builder<S extends Enum<S>, T>{
            S to;
            BiFunction<T, Object, Boolean> condition;

            public Builder<S, T> to(S to) {
                this.to = to;
                return this;
            }
            public Builder<S, T> condition(BiFunction<T, Object, Boolean> condition) {
                this.condition = condition;
                return this;
            }
            
            public StateTo<S, T> build() {
                return new StateTo<>(this);
            }
        }
    }
    
}
