package com.siemens.dasheng.web.thirdparty;

import java.util.List;
import java.util.Map;

/**
 * timeseriesdb 3rd party interface
 * @author zhangliming
 * @date 2019/10/28
 */
public interface TimeSeries3rdPartyService {

    /**
     * Test if the connector could be connected,if success return true or else return false.
     * @return boolean
     * @throws Exception
     */
    boolean testConnector() throws Exception;

    /**
     * Query tag info by the specified regex.
     * @param regex
     * @return List<TagInfo>
     * @throws Exception
     */
    List<TagInfo> selectTagList(String regex) throws Exception;

    /**
     * Insert the tag into the db, if success return true, or else return false.
     * @param tag
     * @return boolean
     * @throws Exception
     */
    boolean insertTagData(String tag) throws Exception;

    /**
     * Batch query realtime data from database by the specified tags.
     * @param tagNameList
     * @return List<DataItem>
     * @throws Exception
     */
    List<DetailItem> batchReadRealtimeData(List<String> tagNameList) throws Exception;

    /**
     * Batch read historical data from database by specified tags and interval and time frame.
     * The result map ,key is tagName, value is List<DetailItem>.
     * @param tagNameList
     * @param timeMapperList
     * @param interval unit:second
     * @return Map<String, List<DetailItem>>
     * @throws Exception
     */
    Map<String, List<DetailItem>> batchReadHistoricalDataWithInterval(List<String> tagNameList, List<TimeInfo> timeMapperList, int interval) throws Exception;


    /**
     * Batch read historical data from database by specified tags and time frame.
     * The result map ,key is tagName, value is List<DetailItem>.
     * @param tagNameList
     * @param timeMapperList
     * @return Map<String, List<DetailItem>>
     * @throws Exception
     */
    Map<String, List<DetailItem>> batchReadHistoricalData(List<String> tagNameList, List<TimeInfo> timeMapperList) throws Exception;

    /**
     * Batch insert tag values into dataBase and return the number that has been inserted suceessfully.
     * @param pointList
     * @return int
     * @throws Exception
     */
    int batchInsertPointList(List<DetailItem> pointList) throws Exception;

}
