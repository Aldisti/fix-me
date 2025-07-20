package net.aldisti.common.fix.validators;

import net.aldisti.common.fix.InvalidFixMessage;
import net.aldisti.common.fix.constants.MsgType;

public class IsMsgType implements TagValueValidator {
    private static final IsMsgType INSTANCE = new IsMsgType();

    private IsMsgType() { }

    public static IsMsgType of() {
        return INSTANCE;
    }

    @Override
    public void validate(String value) throws ValidatorException {
        try {
            MsgType.fromValue(value);
        } catch (InvalidFixMessage e) {
            throw new ValidatorException(e.getMessage());
        }
    }
}
