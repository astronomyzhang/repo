package com.siemens.dasheng.web.util;

import java.util.Date;

/**
 * Created by zheng.wenjing@siemens.com on 12/14/2017
 * @author Z003W5DZ
 */
public class ServiceUtil {


    public static String mergeStrForNotIn(String[] objects, String field) {


        String sqlStr = "";

        if (null != objects && objects.length > 0) {

            int subSize = 999;
            int count = objects.length % subSize == 0 ? objects.length / subSize : objects.length / subSize + 1;

            for (int i = 0; i < count; i++) {
                StringBuffer modelidsSql = new StringBuffer("");
                int index = i * subSize;
                int j = 0;
                iterator(objects, subSize, modelidsSql, index, j);
                String objectSql = modelidsSql.toString();
                if (objectSql.length() >= 1) {
                    //get models, 去除字符串末尾的逗号
                    objectSql = field + " not in (" + objectSql.substring(0, objectSql.length() - 1) + ")";
                }
                if ("".equals(sqlStr)) {
                    sqlStr = objectSql;
                } else {
                    sqlStr = sqlStr + " and " + objectSql;
                }
            }

        }

        return sqlStr;
    }

    private static void iterator(String[] objects, int subSize, StringBuffer modelidsSql, int index, int j) {
        while (j < subSize && index < objects.length) {
            if (null != objects[index] && !"".equals(objects[index])) {
                modelidsSql.append(objects[index] + ",");
            }
            j++;
            index++;
        }
    }

    /**
     * 通过给定的list和field拼接in语句，
     * 防止超过in语句后面参数大于1000条
     *
     * @param models, field
     * @return
     */
    public static String mergeStrForIn(String[] models, String field) {

        String sqlStr = "";

        if (null != models && models.length > 0) {

            int subSize = 999;
            int count = models.length % subSize == 0 ? models.length / subSize : models.length / subSize + 1;

            for (int i = 0; i < count; i++) {
                StringBuffer modelidsSql = new StringBuffer("");
                int index = i * subSize;
                int j = 0;
                iterator(models, subSize, modelidsSql, index, j);
                String modelidsSqlStr = modelidsSql.toString();
                if (modelidsSqlStr.length() >= 1) {
                    //get models, 去除字符串末尾的逗号
                    modelidsSqlStr = field + " in (" + modelidsSqlStr.substring(0, modelidsSqlStr.length() - 1) + ")";
                }
                if ("".equals(sqlStr)) {
                    sqlStr = modelidsSqlStr;
                } else {
                    sqlStr = sqlStr + " or " + modelidsSqlStr;
                }
            }
        }

        return sqlStr;
    }


    public static final String DAY = "天";
    public static final String HOUR = "小时";
    public static final String MINUTE = "分钟";
    public static final String SECOND = "秒";

    /**
     * 1天对应的毫秒数
     */
    public static final long ONE_DAY = 1000L * 3600L * 24;

    /**
     * 1小时对应的毫秒数
     */
    public static final long ONE_HOUR = 1000L * 3600L;

    /**
     * 1分钟对应的毫秒数
     */
    public static final long ONE_MINUTES = 60 * 1000L;
    /**
     * 1秒钟对应的毫秒数
     */
    public static final long SECONDS = 1000L;


    /**
     * Convert timestr like '5天1小时32分钟59秒' to timestamp
     *
     * @param timeStr
     * @return
     */
    public static long convertTimeStr2TimeStamp(String timeStr) {

        long ts = 0L;

        // timeStr必须为标准的输入，中间包含“天”，“小时”、“分钟”和“秒”这4个字符串
        if (null != timeStr && timeStr.contains(DAY)
                && timeStr.contains(HOUR)
                && timeStr.contains(MINUTE)
                && timeStr.contains(SECOND)) {

            timeStr = timeStr.replaceAll(DAY, "-").replaceAll(HOUR, "-").replaceAll(MINUTE, "-").replaceAll(SECOND, "-");

            String[] timeArray = timeStr.split("-");

            Integer numfour = 4;
            if (null != timeArray && timeArray.length == numfour) {

                long day = Long.parseLong(timeArray[0]);
                long hour = Long.parseLong(timeArray[1]);
                long minute = Long.parseLong(timeArray[2]);
                long second = Long.parseLong(timeArray[3]);

                ts = day * ONE_DAY + hour * ONE_HOUR + minute * ONE_MINUTES + second * SECONDS;

            }


        }

        return ts;
    }

    /**
     * 获取告警持续时间
     *
     * @param bgtime
     * @param recovertime
     * @param endtime
     * @return
     */
    public static long getDurationtime(Date bgtime, Date recovertime, Date endtime) {
        long durationtime;
        if (null != recovertime) {
            //存在自动恢复时间
            durationtime = recovertime.getTime() - bgtime.getTime();
        } else if (null != endtime) {
            //存在人工干预结束时间
            durationtime = endtime.getTime() - bgtime.getTime();
        } else { //报警未解除
            durationtime = System.currentTimeMillis() - bgtime.getTime();
        }
        return durationtime;
    }

}
