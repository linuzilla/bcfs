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
@Constraint(validatedBy = TwDateValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TwDate {
    String message() default "ncu.cc.commons.validators.TwDate";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
