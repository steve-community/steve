package de.rwth.idsg.steve.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.01.2016
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Constraint(validatedBy = {ChargeBoxIdValidator.class, ChargeBoxIdListValidator.class})
public @interface ChargeBoxId {

    String message() default "ChargeBox ID cannot contain any whitespace";

    // Required by validation runtime
    Class<?>[] groups() default {};

    // Required by validation runtime
    Class<? extends Payload>[] payload() default {};
}
