package net.aldisti.common.fix.validators;

import java.util.regex.Pattern;

public class IsIntegerValidator extends RegexValidator {

    private IsIntegerValidator() {
        super(Pattern.compile("^\\d+$"));
    }

    private IsIntegerValidator(int size) {
        super(Pattern.compile("^\\d{" + size + "}$"));
    }

    public static IsIntegerValidator of() {
        return new IsIntegerValidator();
    }

    public static IsIntegerValidator of(int size) {
        return new IsIntegerValidator(size);
    }

    @Override
    public void validate(String value) throws ValidatorException {
        super.validate(value);
    }
}
