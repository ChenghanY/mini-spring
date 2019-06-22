package com.framework.james.web.handler;

import com.framework.james.beans.BeanFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 底层联系Servlet和Controller
 *
 * Spring HandlerManager将Controller的方法抽象成Handler的规范
 * @see HandlerManager
 * Spring 依赖注入的特性，借助BeanFactory实现
 * @see BeanFactory
 */
public class Handler {
    private String uri;

    /** 本次转发消息中所在的Controller */
    private Class<?> controller;

    /** 反射的Method类 */
    private Method method;

    /** 运行参数 上层传入*/
    private String[] args;

    public Handler(String uri, Method method, Class<?> controller, String[] args) {
        this.uri = uri;
        this.method = method;
        this.controller = controller;
        this.args = args;
    }

    /**
     *
     * Handler是Servlet请求的封装，使用handle封装
     * @param req 传入的Servlet请求
     * @param res 传入待赋值的Servlet响应
     * @return todo 无异常仅返回TRUE,有异常向上抛出
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws IOException
     */
    public boolean handle(ServletRequest req, ServletResponse res) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException {
        // 获取Servlet的请求uri
        String requestUri = ((HttpServletRequest) req).getRequestURI();
        if (!uri.equals(requestUri)) {
            return false;
        }
        // 获取上层的运行参数
        Object[] parameters = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            parameters[i] = req.getParameter(args[i]);
        }
        /* 反射拿到当前类的对象，调用该类含有parameters参数的方法
           version 1 : 【不合理】【每次调用都创建】 Object controller = controller.newInstance();
           version 2 : 【Spring】【依赖注入】从Bean工厂内获取Bean，Controller是特殊的Bean
         */
        Object controllerBean = BeanFactory.getBean(controller);
        // 使用反射调用Bean的方法并获得返回值
        Object methodResponse = method.invoke(controllerBean, parameters);

        // 处理Servlet传入的响应，这里直接打印
        res.getWriter().println(methodResponse.toString());
        return true;
    }

}
