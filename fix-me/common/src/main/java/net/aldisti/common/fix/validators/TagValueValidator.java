package net.aldisti.common.fix.validators;

/**
 * Interface that defines a validator for the {@link net.aldisti.common.fix.Message}
 * attributes.
 */
public interface TagValueValidator {
    void validate(String value) throws ValidatorException;
}
