package com.siemens.dasheng.web.validators.interfaces;

import com.siemens.dasheng.web.validators.enums.CaseMode;
import com.siemens.dasheng.web.validators.validator.CheckCaseValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/6/12.
 */
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckCaseValidator.class)
@Documented
public @interface CheckCase {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    CaseMode value();
}
