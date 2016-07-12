package de.rwth.idsg.steve.web.validation;

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 22.01.2016
 */
public class EmailCollectionValidator implements ConstraintValidator<EmailCollection, Collection<String>> {

    private static final EmailValidator VALIDATOR = new EmailValidator();

    @Override
    public void initialize(EmailCollection constraintAnnotation) {
        // No-op
    }

    @Override
    public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
        for (String s : value) {
            if (!VALIDATOR.isValid(s, context)) {
                return false;
            }
        }
        return true;
    }
}
