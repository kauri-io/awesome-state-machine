package net.consensys.spring.awesome.statemachine.utils.lambda;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Throwing {

    private Throwing() {}

    public static <T1> Consumer<T1> rethrow(final ThrowingConsumer<T1> consumer) {
        return consumer;
    }

    public static <T1, R> Function<T1, R> rethrowFunc(final ThrowingFunction<T1, R> function) {
        return function;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void sneakyThrow(Throwable ex) throws E {
        throw (E) ex;
    }
    
    public static class LambdaRuntimeException extends RuntimeException {
        private static final long serialVersionUID = -1118878855938251305L;
        public LambdaRuntimeException(Throwable e) { super(e); }
    }
    
    public static class LambdaException extends Exception {
        private static final long serialVersionUID = -1118878855938251305L;
        public LambdaException(Throwable e) { super(e); }
    }


}
