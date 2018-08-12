package net.consensys.spring.awesome.statemachine.utils.lambda;

import java.util.function.Consumer;

import net.consensys.spring.awesome.statemachine.utils.lambda.Throwing.LambdaException;

@FunctionalInterface
public interface ThrowingConsumer<T1> extends Consumer<T1> {

    @Override
    default void accept(final T1 e1) {
        try {
            accept0(e1);
        } catch (Exception ex) {
            Throwing.sneakyThrow(ex);
        }
    }

    void accept0(T1 e1) throws LambdaException;

}