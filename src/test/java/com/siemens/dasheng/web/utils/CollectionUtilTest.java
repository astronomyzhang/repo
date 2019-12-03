package com.siemens.dasheng.web.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author alaln
 * Created by z0041dpv on 4/22/2019.
 */
public class CollectionUtilTest {

    public static void main(String[] args) {

        List<String> firstValue =new ArrayList<>();
        firstValue.add("TIME_SERIES_DATA#REAL_TIME_AND_ARCHIVED#OSIPI#SQLDASV2012_WINDOWS#localhost#10.192.10.136#");
        firstValue.add("13");

        List<String> endValue =new ArrayList<>();
        endValue.add("TIME_SERIES_DATA#REAL_TIME_AND_ARCHIVED#OSIPI#SQLDASV2012_WINDOWS#localhost#10.192.10.136#");
        endValue.add("133");

        System.out.println(Collections.disjoint(firstValue, endValue));

        List<String> listA = new ArrayList<>();
        List<String> listB = new ArrayList<>();
        listA.add("1");
        listA.add("2");
        listA.add("3");
        listA.add("5");

        listB.add("5");


        listA.removeAll(listB);
        System.out.println(listA);


        List<String> list1 = new ArrayList();
        list1.add("1111");
        list1.add("2222");
        list1.add("3333");

        List<String> list2 = new ArrayList();
        list2.add("3333");
        list2.add("4444");
        list2.add("1111");

        // 交集
        List<String> intersection = list1.stream().filter(item -> list2.contains(item)).collect(Collectors.toList());
        System.out.println("---得到交集 intersection---");
        intersection.parallelStream().forEach(System.out :: println);

        Map<Long, String> dd = new HashMap<>();

        Map<Long, String> resultMap = new HashMap<>();
        resultMap.put(1L,  "111");
        resultMap.put(2L,  "222");

        Map<Long, String> resultMap1 = new HashMap<>();
        resultMap1.put(1L,  "111");
        resultMap1.put(3L,  "333");

        dd.putAll(resultMap);
        dd.putAll(resultMap1);

        System.out.println(dd);
    }



}
