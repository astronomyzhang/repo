package com.siemens.dasheng.web.validators.validator;

import com.siemens.dasheng.web.singleton.constant.CommonConstant;

import com.siemens.dasheng.web.validators.interfaces.NumberBytesLength;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.UnsupportedEncodingException;

/**
 * @author liming
 * @Date: 11/3/2018 9:46 AM
 */
@Component
public class  NumberrBytesLengthValidator implements ConstraintValidator<NumberBytesLength, Long> {

    private int bytesLength;

    @Override
    public void initialize(NumberBytesLength numberBytesLength) {
        bytesLength = numberBytesLength.value();

    }

    @Override
    public boolean isValid(Long s, ConstraintValidatorContext constraintValidatorContext) {
        return doValid(s, this.bytesLength);
    }

    private boolean doValid(Long s, int bytesLength) {

        try {
            if(StringUtils.isEmpty(s)){
                return true;
            }
            if (s > bytesLength) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
