package net.aldisti.common.fix.validators;

public interface TagValueValidator {
    void validate(String value) throws ValidatorException;
}
