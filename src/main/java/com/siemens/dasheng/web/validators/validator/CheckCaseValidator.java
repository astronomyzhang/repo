package com.siemens.dasheng.web.validators.validator;

import com.siemens.dasheng.web.validators.enums.CaseMode;
import com.siemens.dasheng.web.validators.interfaces.CheckCase;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/6/12.
 */
public class CheckCaseValidator implements ConstraintValidator<CheckCase, String> {

    private CaseMode caseMode;
    @Override
    public void initialize(CheckCase checkCase) {
        this.caseMode = checkCase.value();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }

        if (caseMode == CaseMode.UPPER) {
            return s.equals(s.toUpperCase());
        } else {
            return s.equals(s.toLowerCase());
        }
    }

}
