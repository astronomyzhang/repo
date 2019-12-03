package com.siemens.dasheng.web.validators.validator;

import com.siemens.dasheng.web.validators.enums.CharacterType;
import com.siemens.dasheng.web.validators.interfaces.CharacterStartWith;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wang.liu@siemens.com
 * Created by Liu Wang on 2018/8/31.
 */

public class CharacterStartWithValidator implements ConstraintValidator<CharacterStartWith, String> {

    private CharacterType[] characterTypes;

    @Autowired
    private CharacterOnlyContainerValidator characterOnlyContainerValidator;
    @Override
    public void initialize(CharacterStartWith characterStartWith) {
        this.characterTypes = characterStartWith.value();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return doValid(s, this.characterTypes);
    }

    public boolean doValid(String s, CharacterType[] characterTypes) {

        String prefixPattern = "^[";
        String suffixPattern = "]";
        StringBuilder pattern = new StringBuilder(prefixPattern);

        pattern  = characterOnlyContainerValidator.patternBody(pattern, characterTypes);

        pattern.append(suffixPattern);

        Pattern pattern1 = Pattern.compile(pattern.toString());

        Matcher matcher = pattern1.matcher(s);

        if(matcher.find()){
            return true;
        }
       return false;

    }
}
