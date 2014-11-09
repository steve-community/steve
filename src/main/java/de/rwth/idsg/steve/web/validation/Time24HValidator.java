package de.rwth.idsg.steve.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Inspired by:
 * http://www.mkyong.com/regular-expressions/how-to-validate-time-in-24-hours-format-with-regular-expression/
 *
 * Time formats that match:
 *  1. “01:00″, “02:00″, “13:00″,
 *  2. “1:00″, “2:00″, “13:01″,
 *  3. “23:59″,”15:00″
 *  4. “00:00″,”0:00″
 *
 * Time formats not matching:
 *  1. “24:00″ – hour is out of range [0-23]
 *  2. “12:60″ – minute is out of range [00-59]
 *  3. “0:0″ – invalid format for minute, at least 2 digits
 *  4. “13:1″ – invalid format for minute, at least 2 digits
 *  5. “101:00″ – hour is out of range [0-23]
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
public class Time24HValidator implements ConstraintValidator<Time24H, String> {

    private Pattern pattern;
    private Matcher matcher;

    private static final String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    @Override
    public void initialize(Time24H time) {
        pattern = Pattern.compile(TIME24HOURS_PATTERN);
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        return string == null || pattern.matcher(string).matches();
    }
}
