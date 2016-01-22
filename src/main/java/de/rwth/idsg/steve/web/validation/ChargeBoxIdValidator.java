package de.rwth.idsg.steve.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.01.2016
 */
public class ChargeBoxIdValidator implements ConstraintValidator<ChargeBoxId, String> {

    private static final String REGEX = "\\S+";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @Override
    public void initialize(ChargeBoxId idTag) {
        // No-op
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        return string == null || PATTERN.matcher(string).matches();
    }
}
