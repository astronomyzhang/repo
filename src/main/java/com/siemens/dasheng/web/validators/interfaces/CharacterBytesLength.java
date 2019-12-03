package com.siemens.dasheng.web.validators.interfaces;

import com.siemens.dasheng.web.validators.validator.CharacterBytesLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author liming
 * @Date: 11/3/2018 9:44 AM
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CharacterBytesLengthValidator.class)
@Documented
public @interface CharacterBytesLength {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value();
}
