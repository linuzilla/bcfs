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
@Constraint(validatedBy = TwPersonalIdValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TwPersonalId {
    String message() default "ncu.cc.commons.validators.TwPersonalId";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
