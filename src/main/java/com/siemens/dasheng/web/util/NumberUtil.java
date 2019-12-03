package com.siemens.dasheng.web.util;

import java.util.regex.Pattern;

/**
 * @author allan
 * Created by ofm on 2019/4/4.
 */
public class NumberUtil {

    private final  static Pattern PATTERN = Pattern.compile("^[-\\+]?[\\d]*$");

    /**
     * 判断字符串是否数字
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        return PATTERN.matcher(str).matches();
    }
}
