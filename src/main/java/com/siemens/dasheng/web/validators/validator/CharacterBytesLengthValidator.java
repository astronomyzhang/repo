package com.siemens.dasheng.web.validators.validator;

import com.siemens.dasheng.web.singleton.constant.CommonConstant;
import com.siemens.dasheng.web.validators.interfaces.CharacterBytesLength;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.UnsupportedEncodingException;

/**
 * @author liming
 * @Date: 11/3/2018 9:46 AM
 */
@Component
public class CharacterBytesLengthValidator implements ConstraintValidator<CharacterBytesLength, String> {

    private int bytesLength;

    @Override
    public void initialize(CharacterBytesLength characterBytesLength) {
        bytesLength = characterBytesLength.value();

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return doValid(s, this.bytesLength);
    }

    private boolean doValid(String s, int bytesLength) {

        try {
            if (StringUtils.isNotBlank(s) && s.getBytes(CommonConstant.UTF8).length > bytesLength) {
                return false;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
