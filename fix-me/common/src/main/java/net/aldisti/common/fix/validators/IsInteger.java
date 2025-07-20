package net.aldisti.common.fix.validators;

import java.util.regex.Pattern;

public class IsInteger extends RegexValidator {

    private IsInteger() {
        super(Pattern.compile("^\\d+$"));
    }

    private IsInteger(int size) {
        super(Pattern.compile("^\\d{" + size + "}$"));
    }

    public static IsInteger of() {
        return new IsInteger();
    }

    public static IsInteger of(int size) {
        return new IsInteger(size);
    }

    @Override
    public void validate(String value) throws ValidatorException {
        super.validate(value);
    }
}
