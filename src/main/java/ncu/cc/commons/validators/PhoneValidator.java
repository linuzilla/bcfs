package ncu.cc.commons.validators;

import ncu.cc.commons.utils.FormatUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public boolean isValid(String phoneNo, ConstraintValidatorContext constraintValidatorContext) {
        return FormatUtil.twPhoneFormatter(phoneNo, "%s-%s") != null;
    }
}
