package net.aldisti.common.fix.validators;

import java.util.regex.Pattern;

public class RegexValidator implements TagValueValidator {

    protected final Pattern pattern;

    protected RegexValidator(Pattern pattern) {
        this.pattern = pattern;
    }

    public static RegexValidator of(String regex) {
        return new RegexValidator(Pattern.compile(regex));
    }

    @Override
    public void validate(String value) throws ValidatorException {
        if (value == null)
            throw new ValidatorException("Value is null");
        if (!pattern.matcher(value).matches())
            throw new ValidatorException(
                    "Tag value [" + value + "] doesn't match regex [" + pattern.pattern() + "]"
            );
    }
}
