package com.siemens.dasheng.web.validators.interfaces;

import com.siemens.dasheng.web.validators.enums.CharacterType;
import com.siemens.dasheng.web.validators.validator.CharacterOnlyContainerValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author wang.liu@siemens.com
 * Created by Liu Wang on 2018/8/31.
 * 除过自定义 CharacterType[] 中的类型外， 出现其他类型会返回 false.
 */

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CharacterOnlyContainerValidator.class)
@Documented
public @interface CharacterOnlyContainer {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    CharacterType[] value();
}
