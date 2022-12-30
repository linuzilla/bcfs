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
@Constraint(validatedBy = CJKStringValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CJKString {
    String message() default "non CJK character found.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
