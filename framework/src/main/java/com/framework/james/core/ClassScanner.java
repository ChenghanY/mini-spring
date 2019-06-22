package com.framework.james.core;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *  Spring 启动时扫描指定目录下的所有类
 */
public class ClassScanner {
    /**
     * 扫描指定包目录下的所有Class文件
     * @param packageName 全包名
     * @return class文件的List
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> scanClasses(String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classList = new ArrayList<>();
        // 包名处理成路径名
        String packagePath = packageName.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // class文件、包、jar文件都属于资源文件，用URL可以定义一个资源
        Enumeration<URL> resources = classLoader.getResources(packagePath);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            // 资源为jar文件时深度遍历class文件，本例gradle打包的项目文件本身就是一个jar文件
            if (resource.getProtocol().endsWith("jar")) {
                JarURLConnection jarURLConnection = (JarURLConnection)resource.openConnection();
                String jarFilePath = jarURLConnection.getJarFile().getName();
                classList.addAll(getClassesFromJar(jarFilePath, packagePath));
            } else {
                // todo 可处理其他资源文件
            }
        }
        return classList;
    }

    /**
     * 根据Jar包路径获取Jar包中的class文件并初始化返回
     * @param jarFilePath
     * @param path
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> getClassesFromJar(String jarFilePath, String path) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        JarFile jarFile = new JarFile(jarFilePath);
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String entryName = jarEntry.getName(); // et:com/mooc/zbs
            if (entryName.startsWith(path) && entryName.endsWith(".class")) {
                // 去除.class后缀
                String classFullName = entryName.replace("/",".")
                        .substring(0, entryName.length() - 6);
                // 获取class全路径，初始化class
                classes.add(Class.forName(classFullName));
            }
        }
        return classes;
    }
}
