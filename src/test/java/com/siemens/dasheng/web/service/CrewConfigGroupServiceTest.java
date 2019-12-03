package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.BaseTest;
import com.siemens.dasheng.web.model.dto.GetSensorGroupResponse;
import com.siemens.dasheng.web.request.GetCrewSensorGroupRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author alaln
 * Created by z0041dpv on 4/19/2019.
 */
public class CrewConfigGroupServiceTest extends BaseTest {

    @Autowired
    private CrewConfigGroupService crewConfigGroupService;



    @Test
    public void getCrewSensorGroupTest() {
        GetCrewSensorGroupRequest getCrewSensorGroupRequest = new GetCrewSensorGroupRequest();

        List<GetSensorGroupResponse> getSensorGroupResponses = crewConfigGroupService.getCrewSensorGroup(getCrewSensorGroupRequest);

        Assert.assertNotNull(getSensorGroupResponses);
    }



}
