package ncu.cc.commons.validators;

import ncu.cc.commons.utils.CJKCharacter;
import ncu.cc.commons.utils.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class CJKStringValidator implements ConstraintValidator<CJKString,String> {
    private boolean isCJKCharacter(char ch) {
        int codepoint = (int) ch;

        for (CJKCharacter.Block block: CJKCharacter.CJK_BLOCKS) {
            for (CJKCharacter.Range range: block.getRanges()) {
                if (range.getFrom() <= codepoint && codepoint <= range.getTo()) {
                    return true;
                }
            }
        }

//        StackTraceUtil.print1(ch + " is not a CJK character, codepoint = " + Integer.toHexString(codepoint));

        return false;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtil.isNullOrEmpty(s)) {
            return false;
        } else {
            for (char ch : s.trim().toCharArray()) {
                if (!isCJKCharacter(ch)) {
                    return false;
                }
            }
            return true;
        }
    }
}
