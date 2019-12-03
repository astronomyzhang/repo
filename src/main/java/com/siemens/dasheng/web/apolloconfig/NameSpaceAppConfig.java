package com.siemens.dasheng.web.apolloconfig;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.context.annotation.Configuration;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/1/16.
 */
@Configuration
@EnableApolloConfig({"DPP.COMMON-EXTERNAL","DPP.COMMON-INTERNAL"})
public class NameSpaceAppConfig {
}
