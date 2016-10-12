package com.softjourn.vending.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

@Documented
@Constraint(validatedBy = ValidDateImpl.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface ValidDate {

    String message() default "The start date is after the due date (or vice verse), or wrong date format(try yyyy-mm-dd)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
