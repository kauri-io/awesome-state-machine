package net.consensys.spring.awesome.statemachine.utils.validation;

import java.util.List;
import java.util.Optional;

import javax.validation.ValidationException;

import org.springframework.util.StringUtils;

/**
 * Validation utils methods
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class ValidatorUtils {

    private ValidatorUtils() {}
    
    public static <T> T requireNonNull(T obj) {
        return requireNonNull(obj, obj.getClass().getSimpleName());
    }
    
    public static <T> T requireNonNull(T obj, String fieldName) {
        return Optional.ofNullable(obj).orElseThrow(() -> new ValidationException("Object " + fieldName + " cannot be null"));
    }
    
    public static String requireNonEmpty(String str, String fieldName) {
        requireNonNull(str, fieldName);
        if(StringUtils.isEmpty(str)) {
            throw new ValidationException("String " + fieldName + " cannot be empty");
        }
        return str;
    }
    
    public static <T> List<T> requireNonEmpty(List<T> list, String fieldName) {
        requireNonNull(list, fieldName);
        if(list.isEmpty()) {
            throw new ValidationException("List " + fieldName + " cannot be empty");
        }
        return list;
    }
    
}
