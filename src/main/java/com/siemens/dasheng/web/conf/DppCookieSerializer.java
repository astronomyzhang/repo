package com.siemens.dasheng.web.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.web.http.DefaultCookieSerializer;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/5/10.
 */
public class DppCookieSerializer extends DefaultCookieSerializer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final HashSet<String> NO_NEED_FILTER_SET = new HashSet<>();

    static {
        NO_NEED_FILTER_SET.add("/SSO/login");
        NO_NEED_FILTER_SET.add("/login");
    }

    @Override
    public void writeCookieValue(CookieValue cookieValue) {

        boolean canAddCookie = false;

        HttpServletRequest request = cookieValue.getRequest();

        String uri = request.getRequestURI();

        for (String fiterUri : NO_NEED_FILTER_SET) {
            if (uri.contains(fiterUri)) {
                canAddCookie = true;
            }
        }

        if (canAddCookie) {
            log.info("Add Cookie:" + cookieValue.getCookieValue());
            super.writeCookieValue(cookieValue);
        }

    }

}
