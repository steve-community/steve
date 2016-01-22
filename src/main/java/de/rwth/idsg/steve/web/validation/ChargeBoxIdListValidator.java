package de.rwth.idsg.steve.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.01.2016
 */
public class ChargeBoxIdListValidator implements ConstraintValidator<ChargeBoxId, List<String>> {

    private static final ChargeBoxIdValidator VALIDATOR = new ChargeBoxIdValidator();

    @Override
    public void initialize(ChargeBoxId constraintAnnotation) {
        // No-op
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        for (String s : value) {
            if (!VALIDATOR.isValid(s, context)) {
                return false;
            }
        }
        return true;
    }
}
