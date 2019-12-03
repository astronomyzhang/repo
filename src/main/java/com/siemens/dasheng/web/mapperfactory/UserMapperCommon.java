package com.siemens.dasheng.web.mapperfactory;

import com.siemens.dasheng.web.enums.DataBaseType;
import com.siemens.dasheng.web.mapper.UserMapper;
import com.siemens.dasheng.web.model.Users;
import com.siemens.dasheng.web.singleton.conf.DataBaseConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Z003W5DZ
 * @Auther: xiaozhi.gu
 * @Date: 6/21/2018 14:21
 * @Description:
 */
@Component
public class UserMapperCommon {

    @Autowired
    private DataBaseConf dataBaseConf;

    @Autowired
    private UserMapper userMapper;

    public Users selectByUsername(String username){
        Users users = new Users();
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            users = userMapper.selectByUsernamePG(username);
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            users = userMapper.selectByUsername(username);
        }
        return users;
    }

}
