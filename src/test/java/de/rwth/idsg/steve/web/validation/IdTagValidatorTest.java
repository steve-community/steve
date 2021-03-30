package de.rwth.idsg.steve.web.validation;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 22.03.2021
 */
public class IdTagValidatorTest {

    IdTagValidator validator = new IdTagValidator();

    @Test
    public void testNull() {
        Assert.assertTrue(validator.isValid(null, null));
    }

    @Test
    public void testAllLowercaseLetters() {
        Assert.assertTrue(validator.isValid("test", null));
    }

    @Test
    public void testAllUppercaseLetters() {
        Assert.assertTrue(validator.isValid("TEST", null));
    }

    @Test
    public void testMixedCaseLetters() {
        Assert.assertTrue(validator.isValid("TesT", null));
        Assert.assertTrue(validator.isValid("tEst", null));
    }

    @Test
    public void testLettersAndNumbers() {
        Assert.assertTrue(validator.isValid("test12", null));
        Assert.assertTrue(validator.isValid("89test", null));
        Assert.assertTrue(validator.isValid("te9s0t", null));
    }

    @Test
    public void testDot() {
        Assert.assertTrue(validator.isValid(".test", null));
        Assert.assertTrue(validator.isValid("test.", null));
        Assert.assertTrue(validator.isValid("te..st", null));
    }

    @Test
    public void testDash() {
        Assert.assertTrue(validator.isValid("-test", null));
        Assert.assertTrue(validator.isValid("test-", null));
        Assert.assertTrue(validator.isValid("te--st", null));
    }

    @Test
    public void testUnderscore() {
        Assert.assertTrue(validator.isValid("_test", null));
        Assert.assertTrue(validator.isValid("test_", null));
        Assert.assertTrue(validator.isValid("te__st", null));
    }

    /**
     * https://github.com/RWTH-i5-IDSG/steve/issues/475
     */
    @Test
    public void testColon() {
        Assert.assertTrue(validator.isValid(":test", null));
        Assert.assertTrue(validator.isValid("test:", null));
        Assert.assertTrue(validator.isValid("te::st", null));

        Assert.assertTrue(validator.isValid("VID:00XXXXXXXXXX", null));
    }

    @Test
    public void testCombined() {
        Assert.assertTrue(validator.isValid("1t.E-S_:t20", null));
    }

}
