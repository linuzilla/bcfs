package ncu.cc.commons.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = EmailDomainExistsValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailDomainExists {
    String message() default "email domain not exists.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
