package ncu.cc.commons.validators;

import ncu.cc.commons.utils.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class TwPersonalIdValidator implements ConstraintValidator<TwPersonalId,String> {
    private static final int []multiply = { 1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1 };

    private static int getCode(char ch) {
        switch (ch) {
            case 'I': return 34;
            case 'O': return 35;
            case 'W': return 32;
            case 'Z': return 33;
            case 'X': return 30;
            case 'Y': return 31;
            default:
                int code = ch - 'A' + 10;
                if (code > 17) code--;
                if (code > 22) code--;
                return code;
        }
    }

    private static int getD(char code) {
        if (code >= '0' && code <= '9') {
            return code - '0';
        } else {
            return getCode(code);
        }
    }

    @Override
    public boolean isValid(String personalId, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtil.isNullOrEmpty(personalId)) {
            return false;
        } else {
            String pid = personalId.trim().toUpperCase();

            if (!pid.matches("^[A-Z][A-Z0-9]\\d{8}$")) return false;

            int[] list = new int[multiply.length];
            char[] chars = pid.toCharArray();
            int code = getCode(chars[0]);
            int x2 = code % 10;
            list[0] = (code - x2) / 10;
            list[1] = x2;

            for (int i = 1; i <= 9; i++) {
                list[i + 1] = getD(chars[i]) % 10;
            }

            int sum = 0;

            for (int i = 0; i < multiply.length; i++) {
                sum += list[i] * multiply[i];
            }

            if (sum % 10 != 0) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public void initialize(TwPersonalId constraintAnnotation) {

    }
}
