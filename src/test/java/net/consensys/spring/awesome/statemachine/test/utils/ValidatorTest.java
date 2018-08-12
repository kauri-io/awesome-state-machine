/**
 * 
 */
package net.consensys.spring.awesome.statemachine.test.utils;

import java.util.Arrays;
import java.util.List;

import javax.validation.ValidationException;

import org.junit.Test;

import net.consensys.spring.awesome.statemachine.utils.validation.ValidatorUtils;

public class ValidatorTest {

    @Test
    public void requireNonNullOK() {
        String obj = "ddd";
        ValidatorUtils.requireNonNull(obj, "field");
    }
    @Test(expected=ValidationException.class)
    public void requireNonNullKO() {
        ValidatorUtils.requireNonNull(null, "field");
    }
    @Test
    public void requireNonEmptyStringOK() {
        String str = "ddd";
        ValidatorUtils.requireNonEmpty(str, "field");
    }
    @Test(expected=ValidationException.class)
    public void requireNonEmptyStringKO() {
        String str = "";
        ValidatorUtils.requireNonEmpty(str, "field");
    }
    @Test
    public void requireNonEmptyListOK() {
        List<String> list = Arrays.asList("ddd");
        ValidatorUtils.requireNonEmpty(list, "field");
    }
    @Test(expected=ValidationException.class)
    public void requireNonEmptyListKO() {
        List<String> list = Arrays.asList();
        ValidatorUtils.requireNonEmpty(list, "field");
    }
    
}
