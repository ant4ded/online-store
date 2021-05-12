package com.nc.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckPriceValidator implements ConstraintValidator<CheckPrice, String> {
    @Override
    public boolean isValid(String price, ConstraintValidatorContext constraintValidatorContext) {
        double priceDouble;
        try {
            priceDouble = Double.parseDouble(price);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return priceDouble > 0;
    }
}
