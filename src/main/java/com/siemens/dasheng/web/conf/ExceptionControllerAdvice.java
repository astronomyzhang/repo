package com.siemens.dasheng.web.conf;

import com.siemens.ofmcommon.enums.ErrorCodeEnum;
import com.siemens.ofmcommon.utils.HttpResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import static com.siemens.dasheng.web.singleton.constant.ModelConstant.IS_SUCCESS;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.MSG;
import static com.siemens.ofmcommon.constant.HttpResponseConstant.DETAIL_ERROR_MSG;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/5/31.
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static String JSON_PARSE_ERROR = "JSON parse error";

    @InitBinder
    public void initBinder() {
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Map errorHandler(Exception ex) {

        if (null != ex.getMessage() && ex.getMessage().contains(JSON_PARSE_ERROR)) {

            Map map = new HashMap(2);
            map.put(DETAIL_ERROR_MSG, ex.getMessage());
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.JSON_FORMAT_ERROR, map);

        }

        log.error("Inner Exception:" + ex.getMessage());
        ex.printStackTrace();

        Map map = new HashMap(2);
        map.put(IS_SUCCESS, false);
        map.put(MSG, "Inner Exception:" + ex.getMessage());
        return map;

    }

}
