package com.siemens.dasheng.web.validators.validator;

import com.siemens.dasheng.web.validators.enums.CharacterType;
import com.siemens.dasheng.web.validators.interfaces.CharacterOnlyContainer;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author wang.liu@siemens.com
 * Created by Liu Wang on 2018/8/31.
 */

@Component
public class CharacterOnlyContainerValidator implements ConstraintValidator<CharacterOnlyContainer, String> {

    private CharacterType[] characterTypes;

    @Override
    public void initialize(CharacterOnlyContainer characterOnlyContainer) {

        this.characterTypes = characterOnlyContainer.value();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        return doValid(s, this.characterTypes);

    }

    public boolean doValid(String s, CharacterType[] characterTypes){
        String prefixPattern = "[";
        String suffixPattern = "]+";
        StringBuilder pattern = new StringBuilder(prefixPattern);

        pattern = patternBody(pattern, characterTypes);

        pattern.append(suffixPattern);

        return s.matches(pattern.toString());
    }

    public StringBuilder patternBody(StringBuilder pattern, CharacterType[] characterTypes) {

        for(CharacterType characterType: characterTypes){

            if(CharacterType.UPPER_CASE.equals(characterType)){

                String up = "A-Z";
                pattern.append(up);
            }

            if(CharacterType.LOWER_CASE.equals(characterType)){

                String low = "a-z";
                pattern.append(low);
            }
            if(CharacterType.NUMBER.equals(characterType)){

                String num = "0-9";
                pattern.append(num);
            }
            if(CharacterType.SYMBOL_UNDERLINE.equals(characterType)){

                String underline = "_";
                pattern.append(underline);
            }

            if(CharacterType.SYMBOL_AND.equals(characterType)){

                String and = "&";
                pattern.append(and);
            }

            if(CharacterType.SYMBOL_JIN.equals(characterType)){

                String jin = "#";
                pattern.append(jin);
            }
        }

        return pattern;
    }
}
