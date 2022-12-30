package ncu.cc.commons.validators;

import ncu.cc.commons.utils.FormatUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class TwDateValidator implements ConstraintValidator<TwDate,String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return FormatUtil.twDateFormatter(s) != null;
    }
}
