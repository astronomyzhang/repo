package com.siemens.dasheng.web.mapper;


import com.siemens.dasheng.web.model.Sensor;
import com.siemens.dasheng.web.util.MyMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2018/11/21
 */
@Component
public interface SensorMapper extends MyMapper<Sensor> {

    /**
     * 通过测点id拼接的in语句，批量查询测点列表
     * @param pointIdList
     * @return
     */
    @Select("select * from SENSOR t where id in (${pointIdList}) ")
    List<Sensor> selectPointListByPointIdList(@Param("pointIdList") String pointIdList);


    /**
     * 分页获取测点oracle
     * @param crewid
     * @param regex
     * @param orderString
     * @param sortOrder
     * @param start
     * @param length
     * @return
     */
    @Select("select * from (select A.*,rownum rn from ( " +
            "select * from SENSOR t " +
            "where t.sensorkks = #{crewid} and id||title||rangeunit like '%'||#{regex}||'%' " +
            "order by ${orderString} ${sortOrder} " +
            " )A where rownum <= #{start} + #{length}) where rn > #{start}")
    List<Sensor> selectSensorListForSearch(@Param("crewid") String crewid,
                                           @Param("regex") String regex,
                                           @Param("orderString") String orderString,
                                           @Param("sortOrder") String sortOrder,
                                           @Param("start") Integer start,
                                           @Param("length") Integer length);

    /**
     * 分页获得测点 pg
     * @param crewid
     * @param regex
     * @param orderString
     * @param sortOrder
     * @param start
     * @param length
     * @return
     */

    @Select("select * from (" +
            "select A.* from (" +
            "select * from SENSOR t where t.sensorkks = #{crewid} and id||title||rangeunit like '%'||#{regex}||'%'" +
            "order by ${orderString} ${sortOrder}" +
            ") as A offset ${start}) as B limit ${length}")
    List<Sensor> selectSensorListForSearchPG(@Param("crewid") String crewid,
                                             @Param("regex") String regex,
                                             @Param("orderString") String orderString,
                                             @Param("sortOrder") String sortOrder,
                                             @Param("start") Integer start,
                                             @Param("length") Integer length);

    /**
     *  // 测点验证相关新增的接口 begin -----------
     * @param equipmentid
     * @return
     */
    @Select("select s.id, s.figurenumber, s.sensorkks, s.description, s.title, s.location, s.type, s.mediumtype, s.iotype, \n" +
            "    s.signaltype, s.connectedsystem, s.powersupplier, s.isolation, s.rangeunit, s.rangeup, s.rangedown, \n" +
            "    s.tendency, s.io1, s.io2, s.connectiontype, s.signaleffectiveway, s.reserve01, s.reserve02, s.reserve03 from sensor s " +
            "    where s.equipmentid = #{equipmentid} and reserve03 = '1' ")
    List<Sensor> selectListByEquipmentId(String equipmentid);


    /**
     * 通过报警id获取测点
     * @param alarmid
     * @return
     */
    @Select("select * from (select A.*,rownum rn from ( " +
            "select sensor.* from sensor sensor,trendwarning_measuringpoint mspoint,alarm alarm" +
            " where alarm.id = #{alarmid} and alarm.pointid = mspoint.id and mspoint.sensorid = sensor.id"+
            " )A where rownum <= 1 ) where rn > 0")
    Sensor selectByAlarmid(@Param("alarmid") Integer alarmid);

    /**
     * 通过报警id获取测点 pg
     * @param alarmid
     * @return
     */
    @Select("select * from (select A.* from (" +
            "select sensor.* from sensor sensor,trendwarning_measuringpoint mspoint,alarm alarm" +
            " where alarm.id = #{alarmid} and alarm.pointid = mspoint.id and mspoint.sensorid = sensor.id" +
            ")as A offset 0) as B limit 1")
    Sensor selectByAlarmidPG(@Param("alarmid") Integer alarmid);



    /**
     * 删除当前测点
     * @param siecode
     */
    @Delete("delete from sensor where id=#{id}")
    void deleteSensor(@Param("id") String siecode);

    /**
     * 批量删除
     * @param siecodes
     */
    void deleteBatch(@Param("siecodes") List<String> siecodes);

    /**
     * 更新测点
     * @param id
     * @param title
     * @param sensorExistId
     */
    @Update("update sensor set id = #{newID} , title = #{title} where id = #{oldId}")
    void updateSensorById(@Param("newID") String id, @Param("title") String title, @Param("oldId") String sensorExistId);

    /**
     * 获得所有的测点id
     * @return
     */
    @Select("select id from sensor")
    Set<String> queryAllSiecode();

    /**
     * 获取测点数量
     * @return
     */
    @Select("select count(*) from SENSOR t")
    int getLocalSensor();


    /**
     * selectSensorListByTagList
     * @param tagList
     * @return
     */
    List<Sensor> selectSensorListByTagList(@Param("tagList") List<String> tagList);
}
