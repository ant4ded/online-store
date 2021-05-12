package com.nc.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckOnNumberValidator implements ConstraintValidator<CheckOnNumber, String> {
    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        return !name.matches(".*[0-9].*");
    }
}
