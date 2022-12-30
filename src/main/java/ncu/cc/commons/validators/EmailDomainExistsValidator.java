package ncu.cc.commons.validators;

import ncu.cc.commons.utils.DNSUtil;
import ncu.cc.commons.utils.StackTraceUtil;
import ncu.cc.commons.utils.StringUtil;

import javax.naming.NamingException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class EmailDomainExistsValidator implements ConstraintValidator<EmailDomainExists,String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtil.isNullOrEmpty(s)) {
            return false;
        } else {
            String[] emailAddr = s.split("@");

            if (emailAddr.length == 2) {
                try {
                    return DNSUtil.validEmailDomain(emailAddr[1]);
                } catch (NamingException e) {
                    StackTraceUtil.print1(e.getMessage());
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
