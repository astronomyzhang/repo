package com.siemens.dasheng.web.interceptor.sessioncheck;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.model.Users;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.siemens.dasheng.web.singleton.constant.ControllerConstant.USER_SESSION;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/6/5.
 */
@Component
public class HttpSessionCheck implements HandlerInterceptor {

    @Resource(name = "redisOPCTemplate")
    private StringRedisTemplate redisTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String UNDEFINED = "undefined";
    public static final String NULL = "null";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        boolean passed = true;

        HttpSession session = request.getSession();

        // check userSession in the session
        Users loginUser = (Users) session.getAttribute(USER_SESSION);
        if (null == loginUser) {

            // try to get dppToken and reset session
            String dppToken = request.getHeader("dpp-token");
            if (null != dppToken) {
                if (!(StringUtils.equals(dppToken,UNDEFINED) || StringUtils.equals(dppToken,NULL))) {
                    String usersResult = redisTemplate.opsForValue().get("dpp-token-namespce:" + dppToken);
                    if (!StringUtils.isBlank(usersResult)) {
                        // set token and userInfo to httpSession
                        session.setAttribute("dppSessionToken", dppToken);
                        session.setAttribute(USER_SESSION, JSON.parseObject(usersResult, Users.class));
                    }
                }
            }

        }

        return passed;

    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
