package com.framework.james.web.handler;

import com.framework.james.web.mvc.Controller;
import com.framework.james.web.mvc.RequestMapping;
import com.framework.james.web.mvc.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring 使用HandlerManager间接管理Controller
 * @see Handler
 * 【step3】http请求
 */
public class HandlerManager {
    /** 持有 */
    public static List<Handler> handlerList = new ArrayList<>();

    /**
     * 解析带有Controller注解的类
     * @param classList 类集合
     */
    public static void resolveMappingHandler(List<Class<?>> classList) {
        for (Class<?> cls : classList) {
            if(cls.isAnnotationPresent(Controller.class)) {
                parseHandlerFromController(cls);
            }
        }
    }

    /**
     * 具体的Controller类转换成Handler对象
     * @param cls
     */
    private static void parseHandlerFromController(Class<?> cls) {
        Method[] methods = cls.getDeclaredMethods();

        // 只操作带有RequestMapping注解的类
        for (Method method : methods) {
            if (!method.isAnnotationPresent(RequestMapping.class)) {
                continue;
            }
            // 通过注解注入uri
            String uri = method.getDeclaredAnnotation(RequestMapping.class).value();
            List<String> paramNameList = new ArrayList<>();
            // 通过注解拿到url参数
            for (Parameter parameter : method.getParameters()) {
                if(parameter.isAnnotationPresent(RequestParam.class)) {
                    paramNameList.add(parameter.getDeclaredAnnotation(RequestParam.class).value());
                }
            }
            String[] params = paramNameList.toArray(new String[paramNameList.size()]);
            // 将Controller转换为Handler
            Handler handler = new Handler(uri, method, cls, params);
            // 存储转化而成的Handler
            HandlerManager.handlerList.add(handler);
        }
    }
}
