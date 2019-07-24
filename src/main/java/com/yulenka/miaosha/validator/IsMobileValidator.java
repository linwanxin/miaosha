package com.yulenka.miaosha.validator;

import com.yulenka.miaosha.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 1:05 2019/7/23
 **/
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {
    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if(required){
          return ValidatorUtil.isMobile(value);
      }else {
          if(StringUtils.isEmpty(value)){
              return true;
          }else {
              return ValidatorUtil.isMobile(value);
          }
      }
    }
}
