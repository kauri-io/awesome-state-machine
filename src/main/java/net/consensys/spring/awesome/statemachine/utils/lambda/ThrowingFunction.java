package net.consensys.spring.awesome.statemachine.utils.lambda;

import java.util.function.Function;

import net.consensys.spring.awesome.statemachine.utils.lambda.Throwing.LambdaException;
import net.consensys.spring.awesome.statemachine.utils.lambda.Throwing.LambdaRuntimeException;

@FunctionalInterface
public interface ThrowingFunction<T1, R> extends Function<T1, R> {

    @Override
    default R apply(final T1 e1) {
        try {
            return apply0(e1);
        } catch (Exception ex) {
            throw new LambdaRuntimeException(ex);
        }
    }

    R apply0(T1 e1) throws LambdaException;

}