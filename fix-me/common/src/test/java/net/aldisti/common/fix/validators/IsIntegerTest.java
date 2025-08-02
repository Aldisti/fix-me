package net.aldisti.common.fix.validators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IsIntegerTest {
    @Test
    void testValidNoArgument() {
        final IsInteger validator = IsInteger.of();

        assertDoesNotThrow(() -> validator.validate("1"));
        assertDoesNotThrow(() -> validator.validate("123"));
        assertDoesNotThrow(() -> validator.validate("12345678910"));

        assertThrows(ValidatorException.class, () -> validator.validate(null));
        assertThrows(ValidatorException.class, () -> validator.validate(""));
        assertThrows(ValidatorException.class, () -> validator.validate("ciao"));
        assertThrows(ValidatorException.class, () -> validator.validate("1c"));
        assertThrows(ValidatorException.class, () -> validator.validate("a2"));
        assertThrows(ValidatorException.class, () -> validator.validate("12b34"));
    }

    @Test
    void testValidWithArgument() {
        final IsInteger validator = IsInteger.of(3);

        assertDoesNotThrow(() -> validator.validate("012"));
        assertDoesNotThrow(() -> validator.validate("123"));

        assertThrows(ValidatorException.class, () -> validator.validate(null));
        assertThrows(ValidatorException.class, () -> validator.validate(""));
        assertThrows(ValidatorException.class, () -> validator.validate("ciao"));
        assertThrows(ValidatorException.class, () -> validator.validate("1c"));
        assertThrows(ValidatorException.class, () -> validator.validate("a2"));
        assertThrows(ValidatorException.class, () -> validator.validate("12b34"));
        assertThrows(ValidatorException.class, () -> validator.validate("1"));
        assertThrows(ValidatorException.class, () -> validator.validate("12"));
        assertThrows(ValidatorException.class, () -> validator.validate("1234"));
        assertThrows(ValidatorException.class, () -> validator.validate("123456"));
    }
}