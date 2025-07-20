package net.aldisti.common.fix.validators;

public class IsEnum implements TagValueValidator {

    private final Class<? extends Enum<?>> clazz;

    protected IsEnum(Class<? extends Enum<?>> clazz) {
        this.clazz = clazz;
    }

    public static IsEnum of(Class<? extends Enum<?>> clazz) {
        return new IsEnum(clazz);
    }

    @Override
    public void validate(String value) throws ValidatorException {
        try {
            Enum.valueOf(clazz.asSubclass(Enum.class), value);
        } catch (IllegalArgumentException e) {
            throw new ValidatorException(
                    "Tag value [" + value + "] is not " + clazz.getSimpleName()
            );
        }
    }
}
