/**
 * 
 */
package net.consensys.spring.awesome.statemachine.utils.reflection;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import net.consensys.spring.awesome.statemachine.annotation.State;
import net.consensys.spring.awesome.statemachine.exception.EntityHasNoStateAnnotationException;
import net.consensys.spring.awesome.statemachine.exception.StateAnnotationNotEnumException;
import net.consensys.spring.awesome.statemachine.exception.StateMachineUnexpectedException;

/**
 * Java Reflection utils methods
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class ReflectionUtils {
    
    private ReflectionUtils() {}
    
    public static <T> Field findStateField(T entity) {

        try {
            for(Field field: entity.getClass().getDeclaredFields()){
                if(field.isAnnotationPresent(State.class)){
                    if(!field.getType().isEnum()) {
                        throw new StateAnnotationNotEnumException("Field " + field.getName() + " is not an enum");
                    }
                    return field;
                } 
            }
            throw new EntityHasNoStateAnnotationException("Can't find annotation @State in class " + entity.getClass());

        } catch (IllegalArgumentException e) {
            throw new StateMachineUnexpectedException(e);
        } 
    }

    public static void callSetter(Object obj, Field field, Object value) {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(field.getName(), obj.getClass());
            pd.getWriteMethod().invoke(obj, value);
        } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new StateMachineUnexpectedException(e);
        }
    }

    public static Object callGetter(Object obj, Field field) {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(field.getName(), obj.getClass());
            return pd.getReadMethod().invoke(obj);
        } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          throw new StateMachineUnexpectedException(e);
        }
    }
}
