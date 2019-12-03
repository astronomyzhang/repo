package com.siemens.dasheng.web.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 动态加载jar包
 *
 * @author zhangliming
 * @date 2019/10/23
 */
public class LoadJarUtil {

    /**
     * 根据路径加载jar文件
     * @param jarPath
     */
    public static void loadJar(String jarPath) {
        File jarFile = new File(jarPath);
        Method method = null;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        boolean accessible = method.isAccessible();
        try {
            //释放权限
            method.setAccessible(true);
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            URL url = jarFile.toURI().toURL();
            method.invoke(classLoader, url);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //归还权限
            method.setAccessible(accessible);
        }
    }

}
