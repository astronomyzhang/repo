package com.siemens.dasheng.web.log;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.log.common.BaseLogObject;
import com.siemens.dasheng.web.model.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * LogInfo
 *
 * @author xuxin
 * @date 2019/1/3
 */
@Service
public class LogInfo {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String LOGIN = "LOGIN";

    public static final String LOGOUT= "LOGOUT";


    /**
     * 通用的方法
     * @param request
     */
    public void general(HttpServletRequest request) {

    }

    /**
     * 用户登录信息 退出信息
     */
    public  void sercurity(Users u, String type) {
        BaseLogObject object = new BaseLogObject();
        object.setEmployeenum(u.getEmployeenum());
        object.setUserName(u.getUsername());
        object.setOperateType(type);
        writte(object);
    }

    /**
     * 操作业务日志
     * @param request
     */
    public  void  activity(HttpServletRequest request, String msg) {

    }
    /**
     * 打印日志
     * @param object
     */
    public  void writte( Object object) {
        if (null != object) {
            logger.info(JSON.toJSONString(object));
        }
    }
}
