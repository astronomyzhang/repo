package com.siemens.dasheng.web.validators.interfaces;

import com.siemens.dasheng.web.validators.enums.CharacterType;
import com.siemens.dasheng.web.validators.validator.CharacterStartWithValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author wang.liu@siemens.com
 * Created by Liu Wang on 2018/8/31.
 * 字符串开始字符除过 CharacterType 定义外， 包含其他会返回 false
 */

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CharacterStartWithValidator.class)
@Documented
public @interface CharacterStartWith {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    CharacterType[] value();
}
