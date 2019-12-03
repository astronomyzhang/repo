package com.siemens.dasheng.web.service;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.model.dto.AppInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author alaln
 * Created by z0041dpv on 6/10/2019.
 */
public class JsonTest {

    public static void main(String[] args) {
        List<AppInfo> parseResult = JSON.parseArray("[{\n" +
                "            \"id\": 10540,\n" +
                "            \"appid\": \"OFM1137946294596730880\",\n" +
                "            \"name\": null,\n" +
                "            \"secretkey\": \"a57f179555ee0498efd86c004421986d\",\n" +
                "            \"notifyurl\": \"\",\n" +
                "            \"appurl\": \"\",\n" +
                "            \"fullname\": \"后台服务\",\n" +
                "            \"isdelete\": 0,\n" +
                "            \"flag\": 3,\n" +
                "            \"description\": \"\",\n" +
                "            \"typeicon\": null,\n" +
                "            \"hostname\": null,\n" +
                "            \"port\": null,\n" +
                "            \"username\": null,\n" +
                "            \"password\": null,\n" +
                "            \"domain\": null,\n" +
                "            \"protocal\": null,\n" +
                "            \"isshow\": 1,\n" +
                "            \"remoteurl\": \"7bf4258b4ced4d90b8a4c878c7f38943.png\",\n" +
                "            \"clientAPPSubMenuList\": null\n" +
                "        }]", AppInfo.class);

        List<AppInfo> rd =  parseResult.stream().map(info -> {
            info.setName(info.getFullname());
            return info;
        }).collect(Collectors.toList());

        System.out.println(rd);

    }
}
