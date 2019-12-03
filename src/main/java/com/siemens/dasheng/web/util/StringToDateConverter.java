package com.siemens.dasheng.web.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * yaming.chen@siemens.com
 *
 * @author chenyaming
 * @date 2016/11/29
 */

public class StringToDateConverter implements Converter<String, Date> {
    private String dateFormat      = "yyyy-MM-dd HH:mm:ss";
    private String shortDateFormat = "yyyy-MM-dd";

    /**
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    @Override
    public Date convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        source = source.trim();
        try {
            String sgin = "-";
            String colon = ":";
            String regex = "^\\d+$";
            if (sgin.contains(source)) {
                SimpleDateFormat formatter;
                if (source.contains(colon)) {
                    formatter = new SimpleDateFormat(dateFormat);
                } else {
                    formatter = new SimpleDateFormat(shortDateFormat);
                }
                Date dtDate = formatter.parse(source);
                return dtDate;
            } else if (source.matches(regex)) {
                return new Date(Long.parseLong(source));
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("parser %s to Date fail", source));
        }
        throw new RuntimeException(String.format("parser %s to Date fail", source));
    }

}

