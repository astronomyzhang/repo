package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.Users;
import com.siemens.dasheng.web.util.MyMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * @author Z003W5DZ
 * @Auther: xiaozhi.gu
 * @Date: 6/21/2018 14:21
 * @Description:
 */
@Component
public interface UserMapper extends MyMapper<Users> {

    /**
     * select By Username
     *
     * @param username
     * @return
     */
    @Select("select * from (select A.*,rownum rn from ( " +
            "select * from users where userName = #{username}" +
            " )A where rownum <= 1 ) where rn > 0")
    Users selectByUsername(String username);

    /**
     * select By Username PG
     *
     * @param username
     * @return
     */
    @Select("select * from (select A.* from (" +
            "select * from users where userName = #{username}" +
            ")as A offset 0 ) as B limit 1")
    Users selectByUsernamePG(String username);


}