package com.siemens.dasheng.web.conf;

import com.siemens.dasheng.web.util.LoadJarUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * ThirdpartyServiceLoader 三方包解析
 *
 * @author zhangliming
 * @date 2019/10/24
 */
public class ThirdpartyServiceLoader {

    public static final Map<String, Class<?>> SERVICEIMPL_MAP = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String ROOT_JAR_PATH = "D:\\connect-jars\\";

    /**
     * 初始化三方jar的实现服务类
     * @throws Exception
     */
    public static void init3rdpartyService() throws Exception {
        String path = traverseFolder(ROOT_JAR_PATH);
        //通过将给定路径名字符串转换为抽象路径名来创建一个新File实例
        if (StringUtils.isBlank(path)) {
            throw new Exception("no jar file found in the third party jar path.");
        }
        //load jar file
        LoadJarUtil.loadJar(path);
        //通过jarFile和JarEntry得到所有的类
        JarFile jar = new JarFile(path);
        //返回zip文件条目的枚举
        Enumeration<JarEntry> enumFiles = jar.entries();
        JarEntry entry;
        //测试此枚举是否包含更多的元素
        while (enumFiles.hasMoreElements()){
            entry = enumFiles.nextElement();
            if (entry.getName().indexOf("META-INF") < 0) {
                String classFullName = entry.getName();
                if (classFullName.endsWith(".class")) {
                    String className = classFullName.substring(0,classFullName.length()-6).replace("/", ".");
                    Class<?> connectClass = Class.forName(className);
                    if (!connectClass.isInterface()) {
                        String serviceName = className.substring(className.lastIndexOf(".") + 1);
                        SERVICEIMPL_MAP.put(serviceName, connectClass);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String jarpath = traverseFolder(ROOT_JAR_PATH);
        System.out.println(jarpath);
    }


    /**
     * 查找jar文件
     * @param path
     */
    public static String traverseFolder(String path) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                return null;
            } else {
                for (File file2 : files) {
                    String fileName = file2.getName();
                    if (fileName.endsWith(".jar")) {
                        return file2.getAbsolutePath();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据接口类，获取三方已经实现的类
     * @param classType
     * @return
     */
    public static Class<?> getSpecifiedClass(Class<?> classType) {
        for (Class<?> val : SERVICEIMPL_MAP.values()) {
            if (classType.isAssignableFrom(val)) {
                return val;
            }
        }
        return null;
    }
}