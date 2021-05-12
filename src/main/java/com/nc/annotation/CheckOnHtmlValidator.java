package com.nc.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckOnHtmlValidator implements ConstraintValidator<CheckOnHtml, String> {
    public static final String HTML_TAGS_REGEX = "^<.+?>$";

    @Override
    public boolean isValid(String startLine, ConstraintValidatorContext constraintValidatorContext) {
        return !startLine.matches(HTML_TAGS_REGEX);
    }
}
